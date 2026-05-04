#!/bin/sh

# Test validation: metadataUrl should only work with clustal format

echo "Test 1: metadataUrl with fasta format (should fail with 400)"
echo "============================================================"
RESPONSE=$(curl --silent -w "\nHTTP_STATUS:%{http_code}" -X POST "http://localhost:8080/sequences/protein" \
  -H 'Content-Type: application/json' \
  --data '{
    "features": [
      {"contig": "EHI7A_117830-t26_1-p1", "start": 1, "end": 100, "query": "SEQ1"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 50, "end": 150, "query": "SEQ2"}
    ],
    "deflineFormat": "QUERYONLY",
    "basesPerLine": 60,
    "postProcess": "MSA",
    "msaOptions": {
      "format": "fasta",
      "metadataUrl": "https://example.com/metadata.tsv"
    }
  }')

HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS" | cut -d':' -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_STATUS/d')

if [ "$HTTP_STATUS" = "400" ]; then
  echo "✓ PASS: Got expected 400 Bad Request"
  echo "Error message: $BODY"
else
  echo "✗ FAIL: Expected 400, got $HTTP_STATUS"
  echo "Response: $BODY"
fi

echo ""
echo "Test 2: metadataUrl with clustal_dnd format (should fail with 400)"
echo "===================================================================="
RESPONSE=$(curl --silent -w "\nHTTP_STATUS:%{http_code}" -X POST "http://localhost:8080/sequences/protein" \
  -H 'Content-Type: application/json' \
  --data '{
    "features": [
      {"contig": "EHI7A_117830-t26_1-p1", "start": 1, "end": 100, "query": "SEQ1"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 50, "end": 150, "query": "SEQ2"}
    ],
    "deflineFormat": "QUERYONLY",
    "basesPerLine": 60,
    "postProcess": "MSA",
    "msaOptions": {
      "format": "clustal_dnd",
      "metadataUrl": "https://example.com/metadata.tsv"
    }
  }')

HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS" | cut -d':' -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_STATUS/d')

if [ "$HTTP_STATUS" = "400" ]; then
  echo "✓ PASS: Got expected 400 Bad Request"
  echo "Error message: $BODY"
else
  echo "✗ FAIL: Expected 400, got $HTTP_STATUS"
  echo "Response: $BODY"
fi

echo ""
echo "Test 3: metadataUrl with clustal format (should succeed or warn)"
echo "================================================================="
RESPONSE=$(curl --silent -w "\nHTTP_STATUS:%{http_code}" -X POST "http://localhost:8080/sequences/protein" \
  -H 'Content-Type: application/json' \
  --data '{
    "features": [
      {"contig": "EHI7A_117830-t26_1-p1", "start": 1, "end": 100, "query": "SEQ1"},
      {"contig": "EHI7A_117830-t26_1-p1", "start": 50, "end": 150, "query": "SEQ2"}
    ],
    "deflineFormat": "QUERYONLY",
    "basesPerLine": 60,
    "postProcess": "MSA",
    "msaOptions": {
      "format": "clustal",
      "metadataUrl": "https://example.com/metadata.tsv"
    }
  }')

HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS" | cut -d':' -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_STATUS/d')

if [ "$HTTP_STATUS" = "200" ]; then
  echo "✓ PASS: Got 200 OK (metadata HTML not yet implemented, returns plain text)"
  echo "Output length: $(echo "$BODY" | wc -c) bytes"
else
  echo "✗ FAIL: Expected 200, got $HTTP_STATUS"
  echo "Response: $BODY"
fi

echo ""
echo "Test 4: clustal_dnd without metadataUrl (should succeed)"
echo "========================================================="
RESPONSE=$(curl --silent -w "\nHTTP_STATUS:%{http_code}" -X POST "http://localhost:8080/sequences/protein" \
  -H 'Content-Type: application/json' \
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
      "format": "clustal_dnd"
    }
  }')

HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS" | cut -d':' -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_STATUS/d')

if [ "$HTTP_STATUS" = "200" ]; then
  echo "✓ PASS: Got 200 OK"
  # Check for HTML markers
  if echo "$BODY" | grep -q "<!DOCTYPE html>"; then
    echo "✓ Output contains HTML"
  else
    echo "✗ Output does not contain HTML"
  fi
  if echo "$BODY" | grep -q "Guide Tree"; then
    echo "✓ Output contains guide tree section"
  else
    echo "✗ Output missing guide tree section"
  fi
else
  echo "✗ FAIL: Expected 200, got $HTTP_STATUS"
  echo "Response: $BODY"
fi

echo ""
echo "All validation tests complete!"
