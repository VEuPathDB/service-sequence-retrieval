#!/bin/sh

# End-to-end test: percentActg filtering + MSA post-processing.
#
# Requests an MSA (fasta format) over 3 sequences from the "lowactg" reference
# type: GOOD01 and GOOD02 are ~97% similar, 100% ACTG sequences; BAD01 is the
# same length but only 20% ACTG (mostly N's). Without percentActg, BAD01 would
# be handed to clustalo and could crash or corrupt the alignment. With
# percentActg set above BAD01's actual percentage, it should be silently
# skipped, and the aligned output should contain GOOD01/GOOD02 but not BAD01.

RESPONSE=$(curl --silent -w "\nHTTP_STATUS:%{http_code}" -X POST "http://localhost:8080/sequences/lowactg" \
  -H 'Content-Type: application/json' \
  --data '{
    "features": [
      {"contig": "GOOD01", "start": 1, "end": 60, "query": "GOOD01"},
      {"contig": "GOOD02", "start": 1, "end": 60, "query": "GOOD02"},
      {"contig": "BAD01", "start": 1, "end": 60, "query": "BAD01"}
    ],
    "deflineFormat": "QUERYONLY",
    "basesPerLine": 60,
    "percentActg": 50,
    "postProcess": "MSA",
    "msaOptions": {
      "format": "fasta"
    }
  }')

HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS" | cut -d':' -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_STATUS/d')

echo "$BODY"
echo ""
echo "HTTP Status: $HTTP_STATUS"
echo ""

PASS=1

if [ "$HTTP_STATUS" = "200" ]; then
  echo "PASS: got 200 OK"
else
  echo "FAIL: expected 200, got $HTTP_STATUS"
  PASS=0
fi

if echo "$BODY" | grep -q "GOOD01"; then
  echo "PASS: alignment includes GOOD01"
else
  echo "FAIL: alignment is missing GOOD01"
  PASS=0
fi

if echo "$BODY" | grep -q "GOOD02"; then
  echo "PASS: alignment includes GOOD02"
else
  echo "FAIL: alignment is missing GOOD02"
  PASS=0
fi

if echo "$BODY" | grep -q "BAD01"; then
  echo "FAIL: alignment includes BAD01, but it should have been filtered out by percentActg"
  PASS=0
else
  echo "PASS: alignment correctly excludes BAD01"
fi

echo ""
if [ "$PASS" = "1" ]; then
  echo "RESULT: PASS"
  exit 0
else
  echo "RESULT: FAIL"
  exit 1
fi
