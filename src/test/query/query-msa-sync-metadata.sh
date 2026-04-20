#!/bin/sh

# Test synchronous MSA with clustal format and metadata from file URL
# The output should be text/plain with TSV metadata prepended to clustal alignment
#
# Note: The metadata file is mounted in the container at /sequenceFiles/test-msa-metadata.tsv
# It is copied from src/test/resources/veupathdb/service/sequence/reference/ which is
# mounted to /sequenceFiles in the docker-compose setup.

# Path to metadata file inside the container
METADATA_URL="file:///sequenceFiles/test-msa-metadata.tsv"

echo "Using metadata URL (container path): $METADATA_URL"
echo "---"

curl -X POST "http://localhost:8080/sequences/protein" \
  -H 'Content-Type: application/json' \
  --no-buffer \
  --data "{
    \"features\": [
      {\"contig\": \"EHI7A_117830-t26_1-p1\", \"start\": 1, \"end\": 100, \"query\": \"SEQ1\"},
      {\"contig\": \"EHI7A_117830-t26_1-p1\", \"start\": 50, \"end\": 150, \"query\": \"SEQ2\"},
      {\"contig\": \"EHI7A_117830-t26_1-p1\", \"start\": 100, \"end\": 200, \"query\": \"SEQ3\"}
    ],
    \"deflineFormat\": \"QUERYONLY\",
    \"basesPerLine\": 60,
    \"postProcess\": \"MSA\",
    \"msaOptions\": {
      \"format\": \"clustal\",
      \"metadataUrl\": \"$METADATA_URL\"
    }
  }"

echo ""
echo "---"
echo "Expected output:"
echo "  - Content should start with TSV header: ID<tab>organism<tab>sample_type<tab>location"
echo "  - Should contain metadata rows for SEQ1, SEQ2, SEQ3"
echo "  - Should have double newline separator"
echo "  - Should contain CLUSTAL alignment after metadata"
