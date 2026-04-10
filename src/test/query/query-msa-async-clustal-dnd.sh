#!/bin/sh

# Test asynchronous MSA with clustal_dnd format
# This will submit a job, wait for completion, and retrieve the results

echo "Submitting async MSA job with clustal_dnd format..."

# Submit job
JOB_RESPONSE=$(curl --silent -X POST "http://localhost:8080/sequences-async/protein" \
  -H 'Content-Type: application/json' \
  --data '{
    "features": [
      {"contig": "EHI7A_117830-t26_1-p1", "start": 1, "end": 100, "query": "SEQ1"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 50, "end": 150, "query": "SEQ2"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 100, "end": 200, "query": "SEQ3"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 150, "end": 250, "query": "SEQ4"}
    ],
    "deflineFormat": "QUERYONLY",
    "basesPerLine": 60,
    "postProcess": "MSA",
    "msaOptions": {
      "format": "clustal_dnd"
    }
  }')

JOB_ID=$(echo $JOB_RESPONSE | grep -o '"jobID":"[^"]*"' | cut -d'"' -f4)

if [ -z "$JOB_ID" ]; then
  echo "Failed to submit job"
  echo "Response: $JOB_RESPONSE"
  exit 1
fi

echo "Job submitted with ID: $JOB_ID"

# Poll for job completion
MAX_ATTEMPTS=30
ATTEMPT=0
while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
  STATUS_RESPONSE=$(curl --silent "http://localhost:8080/jobs/$JOB_ID")
  STATUS=$(echo $STATUS_RESPONSE | grep -o '"status":"[^"]*"' | cut -d'"' -f4)

  echo "Job status: $STATUS"

  if [ "$STATUS" = "complete" ]; then
    echo "Job completed successfully!"
    break
  elif [ "$STATUS" = "failed" ]; then
    echo "Job failed!"
    echo "Response: $STATUS_RESPONSE"
    exit 1
  elif [ "$STATUS" = "expired" ]; then
    echo "Job expired!"
    exit 1
  fi

  ATTEMPT=$((ATTEMPT + 1))
  sleep 2
done

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
  echo "Job did not complete within timeout"
  exit 1
fi

# List available files
echo ""
echo "Available files:"
curl --silent "http://localhost:8080/jobs/$JOB_ID/files" | grep -o '"[^"]*"' | tr -d '"'

# Download main output (HTML)
echo ""
echo "Main output (first 500 chars):"
curl --silent "http://localhost:8080/jobs/$JOB_ID/files/output" | head -c 500
echo ""

# Download guide tree
echo ""
echo "Guide tree (.dnd file):"
curl --silent "http://localhost:8080/jobs/$JOB_ID/files/guidetree.dnd"
echo ""
