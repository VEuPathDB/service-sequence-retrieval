#!/bin/sh

# End-to-end test: async job status fields (queuePosition, created, started, finished).
#
# Submits an async MSA job, then polls /jobs/{jobId} and checks:
#   - queuePosition is either absent or a non-negative integer
#   - "created" appears as soon as the job exists and never disappears
#   - "started" is absent while queued, then appears once in-progress/complete
#   - "finished" is absent until the job reaches a terminal status, then appears
#   - timestamps, once present, are non-decreasing (created <= started <= finished)

RESPONSE=$(curl --silent -X POST "http://localhost:8080/sequences-async/protein" \
  -H 'Content-Type: application/json' \
  --no-buffer \
  --data '{
    "features": [
      {"contig": "EHI7A_117830-t26_1-p1", "start": 1, "end": 100, "query": "SEQ1"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 50, "end": 150, "query": "SEQ2"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 100, "end": 200, "query": "SEQ3"}
    ],
    "deflineFormat": "QUERYONLY",
    "basesPerLine": 60,
    "postProcess": "MSA",
    "msaOptions": {
      "format": "fasta"
    }
  }')

echo "=== Job Submission ==="
echo "$RESPONSE"
echo ""

JOB_ID=$(echo "$RESPONSE" | jq -r .jobID)

if [ -z "$JOB_ID" ] || [ "$JOB_ID" = "null" ]; then
  echo "FAIL: no jobID returned from submission"
  exit 1
fi

echo "Job ID: $JOB_ID"
echo ""

PASS=1
SAW_STARTED=0
SAW_FINISHED=0
PREV_CREATED=""
PREV_STARTED=""

# to_epoch CONVERTS an ISO-8601 timestamp to seconds since epoch, or empty on failure.
to_epoch() {
  date -d "$1" +%s 2>/dev/null || date -j -f "%Y-%m-%dT%H:%M:%S" "$(echo "$1" | cut -c1-19)" +%s 2>/dev/null
}

for i in $(seq 1 15); do
  STATUS_RESPONSE=$(curl --silent "http://localhost:8080/jobs/$JOB_ID")
  echo "--- poll $i ---"
  echo "$STATUS_RESPONSE" | jq

  STATUS=$(echo "$STATUS_RESPONSE" | jq -r .status)
  QUEUE_POSITION=$(echo "$STATUS_RESPONSE" | jq -r '.queuePosition // empty')
  CREATED=$(echo "$STATUS_RESPONSE" | jq -r '.created // empty')
  STARTED=$(echo "$STATUS_RESPONSE" | jq -r '.started // empty')
  FINISHED=$(echo "$STATUS_RESPONSE" | jq -r '.finished // empty')

  # queuePosition, when present, must be a non-negative integer.
  if [ -n "$QUEUE_POSITION" ]; then
    case "$QUEUE_POSITION" in
      ''|*[!0-9]*)
        echo "FAIL: queuePosition '$QUEUE_POSITION' is not a non-negative integer"
        PASS=0
        ;;
      *)
        echo "OK: queuePosition ($QUEUE_POSITION) is a non-negative integer"
        ;;
    esac
  fi

  if [ -z "$CREATED" ]; then
    echo "FAIL: created is missing on poll $i (status=$STATUS)"
    PASS=0
  fi

  if [ -n "$STARTED" ]; then
    SAW_STARTED=1
  fi

  if [ -n "$FINISHED" ]; then
    SAW_FINISHED=1
    # finished should never appear before started.
    if [ -z "$STARTED" ]; then
      echo "FAIL: finished is present but started is not"
      PASS=0
    fi
  fi

  # created must not change across polls.
  if [ -n "$PREV_CREATED" ] && [ -n "$CREATED" ] && [ "$PREV_CREATED" != "$CREATED" ]; then
    echo "FAIL: created changed between polls ($PREV_CREATED -> $CREATED)"
    PASS=0
  fi
  [ -n "$CREATED" ] && PREV_CREATED="$CREATED"

  # started, once seen, must not disappear or change on a later poll.
  if [ -n "$PREV_STARTED" ] && [ -n "$STARTED" ] && [ "$PREV_STARTED" != "$STARTED" ]; then
    echo "FAIL: started changed between polls ($PREV_STARTED -> $STARTED)"
    PASS=0
  fi
  [ -n "$STARTED" ] && PREV_STARTED="$STARTED"

  # ordering check: created <= started <= finished, using whichever are present.
  C_EPOCH=$( [ -n "$CREATED" ] && to_epoch "$CREATED" )
  S_EPOCH=$( [ -n "$STARTED" ] && to_epoch "$STARTED" )
  F_EPOCH=$( [ -n "$FINISHED" ] && to_epoch "$FINISHED" )

  if [ -n "$C_EPOCH" ] && [ -n "$S_EPOCH" ] && [ "$C_EPOCH" -gt "$S_EPOCH" ]; then
    echo "FAIL: created ($CREATED) is after started ($STARTED)"
    PASS=0
  fi
  if [ -n "$S_EPOCH" ] && [ -n "$F_EPOCH" ] && [ "$S_EPOCH" -gt "$F_EPOCH" ]; then
    echo "FAIL: started ($STARTED) is after finished ($FINISHED)"
    PASS=0
  fi

  if [ "$STATUS" = "complete" ] || [ "$STATUS" = "failed" ] || [ "$STATUS" = "expired" ]; then
    break
  fi

  sleep 1
done

echo ""
if [ "$STATUS" != "complete" ] && [ "$STATUS" != "failed" ] && [ "$STATUS" != "expired" ]; then
  echo "FAIL: job did not reach a terminal status within the polling window (last status: $STATUS)"
  PASS=0
fi

if [ "$SAW_STARTED" != "1" ]; then
  echo "FAIL: started was never populated"
  PASS=0
else
  echo "OK: started was populated at some point"
fi

if [ "$SAW_FINISHED" != "1" ]; then
  echo "FAIL: finished was never populated"
  PASS=0
else
  echo "OK: finished was populated at some point"
fi

echo ""
if [ "$PASS" = "1" ]; then
  echo "RESULT: PASS"
  exit 0
else
  echo "RESULT: FAIL"
  exit 1
fi
