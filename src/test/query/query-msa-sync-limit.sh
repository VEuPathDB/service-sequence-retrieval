#!/bin/sh

# Test synchronous MSA with too many sequences (should return 400 error)
# Default limit is 20 sequences, this sends 25

echo "Attempting sync MSA with 25 sequences (limit is 20)..."
echo "Should receive 400 Bad Request error..."
echo ""

curl -X POST "http://localhost:8080/sequences/protein" \
  -H 'Content-Type: application/json' \
  --no-buffer \
  -w "\nHTTP Status: %{http_code}\n" \
  --data '{
    "features": [
      {"contig": "EHI7A_117830-t26_1-p1", "start": 1, "end": 50, "query": "SEQ1"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 51, "end": 100, "query": "SEQ2"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 101, "end": 150, "query": "SEQ3"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 151, "end": 200, "query": "SEQ4"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 201, "end": 250, "query": "SEQ5"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 251, "end": 300, "query": "SEQ6"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 301, "end": 350, "query": "SEQ7"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 351, "end": 400, "query": "SEQ8"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 401, "end": 450, "query": "SEQ9"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 451, "end": 500, "query": "SEQ10"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 501, "end": 550, "query": "SEQ11"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 551, "end": 600, "query": "SEQ12"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 601, "end": 650, "query": "SEQ13"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 651, "end": 700, "query": "SEQ14"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 701, "end": 750, "query": "SEQ15"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 751, "end": 800, "query": "SEQ16"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 801, "end": 850, "query": "SEQ17"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 851, "end": 900, "query": "SEQ18"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 901, "end": 950, "query": "SEQ19"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 951, "end": 1000, "query": "SEQ20"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 1001, "end": 1050, "query": "SEQ21"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 1051, "end": 1100, "query": "SEQ22"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 1101, "end": 1150, "query": "SEQ23"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 1151, "end": 1200, "query": "SEQ24"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 1201, "end": 1250, "query": "SEQ25"}
    ],
    "deflineFormat": "QUERYONLY",
    "basesPerLine": 60,
    "postProcess": "MSA",
    "msaOptions": {
      "format": "clustal"
    }
  }'
