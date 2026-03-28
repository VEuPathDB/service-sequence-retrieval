#!/bin/sh

# Test asynchronous MSA with clustal format output

doCurl() {
  curl --silent -X POST "http://localhost:8080/sequences-async/protein" \
    -H 'Content-Type: application/json' \
    --no-buffer \
    --data '{
      "features": [
        {"contig": "EHI7A_117830-t26_1-p1", "start": 1, "end": 100, "query": "SEQ1"},
        {"contig": "EHI7A_117830-t26_1-p1", "start": 50, "end": 150, "query": "SEQ2"},
        {"contig": "EHI7A_117830-t26_1-p1", "start": 100, "end": 200, "query": "SEQ3"},
        {"contig": "EHI7A_117830-t26_1-p1", "start": 150, "end": 250, "query": "SEQ4"},
        {"contig": "EHI7A_117830-t26_1-p1", "start": 200, "end": 300, "query": "SEQ5"}
      ],
      "deflineFormat": "QUERYONLY",
      "basesPerLine": 60,
      "postProcess": "orthomclMSA",
      "orthomclMsaOptions": {
        "format": "clustal"
      }
    }'
}

echo "Submitting async MSA job..."
jobId=$(doCurl | jq -r .jobID)
echo "Job ID: $jobId"

echo "Waiting for job to complete..."
sleep 5

echo -e "\n=== Job Status ==="
curl --silent "http://localhost:8080/jobs/$jobId" | jq

echo -e "\n=== Available Files ==="
curl --silent "http://localhost:8080/jobs/$jobId/files" | jq

echo -e "\n=== Output File ==="
curl --silent "http://localhost:8080/jobs/$jobId/files/output"

echo -e "\n=== Guide Tree (if available) ==="
curl --silent "http://localhost:8080/jobs/$jobId/files/guidetree.dnd" 2>/dev/null || echo "(Guide tree not available)"
