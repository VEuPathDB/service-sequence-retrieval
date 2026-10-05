#!/bin/bash

# Functional test: when an aligner fails, the service returns an error and the aligner's
# error output is written to the service log. This script checks the response; you then
# check the log by hand.
#
# Input: the "lowactg" reference type. The first 10 bases of BAD01 are all N, and two
# different slices of them are requested, so the aligner is handed sequences made only of N.
# Clustalo is known to fail on that. Mafft accepts all-N input, so it is expected NOT to
# fail here and will report FAIL below. To exercise mafft's failure logging, temporarily
# replace the mafft binary in the container with a fake one that prints to stderr and exits 1:
#   mv /opt/conda/bin/mafft /opt/conda/bin/mafft.real
#   printf '#!/bin/sh\necho "fake mafft: simulated failure" >&2\nexit 1\n' > /opt/conda/bin/mafft
#   chmod +x /opt/conda/bin/mafft
# run this script again, then restore it:  mv /opt/conda/bin/mafft.real /opt/conda/bin/mafft
#
# Sync:  expects HTTP 500.
# Async: expects the job to end with status "failed".
#
# Afterwards, look in the service log for (per aligner):
#   - "mafft failed: ..." / "clustalo failed: ..."   (from the executor, with the aligner output)
#   - "Post-processing failed"                        (from WriteFeaturesJob / the sync service,
#                                                      with the full stack trace and cause)

BASE_URL="http://localhost:8080"

request_body() {
  # $1 = aligner
  cat <<EOF
{
  "features": [
    {"contig": "BAD01", "start": 1, "end": 10, "query": "ALLN1"},
    {"contig": "BAD01", "start": 2, "end": 9, "query": "ALLN2"}
  ],
  "deflineFormat": "QUERYONLY",
  "basesPerLine": 60,
  "postProcess": "MSA",
  "msaOptions": {
    "aligner": "$1",
    "format": "fasta"
  }
}
EOF
}

PASS=1

check_sync() {
  local aligner=$1
  echo "=== sync, aligner=$aligner ==="
  local status
  status=$(request_body "$aligner" | curl --silent --output /dev/null -w "%{http_code}" \
    -X POST "$BASE_URL/sequences/lowactg" -H 'Content-Type: application/json' --data @-)
  if [ "$status" = "500" ]; then
    echo "PASS: got 500; check the log for '$aligner failed' and 'Post-processing failed'"
  else
    echo "FAIL: expected 500, got $status (the aligner did not fail on this input, so there is no error to find in the log)"
    PASS=0
  fi
  echo ""
}

check_async() {
  local aligner=$1
  echo "=== async, aligner=$aligner ==="
  local response job_id
  response=$(request_body "$aligner" | curl --silent \
    -X POST "$BASE_URL/sequences-async/lowactg" -H 'Content-Type: application/json' --data @-)
  job_id=$(echo "$response" | grep -o '"jobID":"[^"]*"' | cut -d'"' -f4)
  if [ -z "$job_id" ]; then
    echo "FAIL: could not submit job. Response: $response"
    PASS=0
    echo ""
    return
  fi
  echo "Job submitted with ID: $job_id"

  local status="" attempt=0
  while [ $attempt -lt 30 ]; do
    status=$(curl --silent "$BASE_URL/jobs/$job_id" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
    echo "Job status: $status"
    if [ "$status" = "complete" ] || [ "$status" = "failed" ] || [ "$status" = "expired" ]; then
      break
    fi
    attempt=$((attempt + 1))
    sleep 2
  done

  if [ "$status" = "failed" ]; then
    echo "PASS: job failed; check the log for '$aligner failed' and 'Post-processing failed'"
  else
    echo "FAIL: expected status 'failed', got '$status' (the aligner did not fail on this input, so there is no error to find in the log)"
    PASS=0
  fi
  echo ""
}

for aligner in clustalo mafft; do
  check_sync "$aligner"
  check_async "$aligner"
done

if [ "$PASS" = "1" ]; then
  echo "RESULT: PASS (now check the service log for the error text)"
  exit 0
else
  echo "RESULT: FAIL"
  exit 1
fi
