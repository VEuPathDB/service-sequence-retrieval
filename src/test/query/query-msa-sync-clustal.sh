#!/bin/sh

# Test synchronous MSA with clustal format output (returns HTML)
curl -X POST "http://localhost:8080/sequences/protein" \
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
      "format": "clustal"
    }
  }'
