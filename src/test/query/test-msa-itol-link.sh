#!/bin/sh

# Functional test for MSA with iTOL link feature
# Tests that clustal_dnd format returns HTML with either:
# - A valid iTOL hyperlink, OR
# - An error message about invalid phylogenetic tree

echo "Testing MSA with iTOL link feature..."

RESPONSE=$(curl -s -X POST "http://localhost:8080/sequences/protein" \
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

# Test 1: Response should be HTML
if ! echo "$RESPONSE" | grep -q "<!DOCTYPE html>"; then
  echo "FAIL: Response is not HTML"
  exit 1
fi
echo "PASS: Response is HTML"

# Test 2: Response should contain CLUSTAL alignment
if ! echo "$RESPONSE" | grep -q "CLUSTAL O"; then
  echo "FAIL: Response does not contain CLUSTAL alignment"
  exit 1
fi
echo "PASS: Response contains CLUSTAL alignment"

# Test 3: Response should contain guide tree section
if ! echo "$RESPONSE" | grep -q "Guide Tree (.dnd format)"; then
  echo "FAIL: Response does not contain guide tree section"
  exit 1
fi
echo "PASS: Response contains guide tree section"

# Test 4: Response MUST contain a working iTOL link (not error message)
HAS_ITOL_LINK=$(echo "$RESPONSE" | grep -c "Click here to view a phylogenetic tree")
HAS_ERROR_MSG=$(echo "$RESPONSE" | grep -c "does not produce a valid iTOL phylogenetic tree")

if [ "$HAS_ERROR_MSG" -gt 0 ]; then
  echo "FAIL: Response contains error message - iTOL upload is not working"
  echo "This test expects iTOL upload to succeed with valid tree data"
  exit 1
fi

if [ "$HAS_ITOL_LINK" -eq 0 ]; then
  echo "FAIL: Response does not contain iTOL hyperlink"
  echo "Response excerpt:"
  echo "$RESPONSE" | head -50
  exit 1
fi

echo "PASS: Response contains iTOL hyperlink"

# Test 5: Verify the link points to a valid iTOL tree (not home page)
ITOL_URL=$(echo "$RESPONSE" | grep -o 'href="https://itol.embl.de/[^"]*"' | head -1 | sed 's/href="//;s/"//')
if [ -z "$ITOL_URL" ]; then
  echo "FAIL: iTOL link found but URL could not be extracted"
  exit 1
fi

# Check if it's not just the home page
if [ "$ITOL_URL" = "https://itol.embl.de" ] || [ "$ITOL_URL" = "https://itol.embl.de/" ]; then
  echo "FAIL: iTOL link points to home page instead of tree"
  exit 1
fi

echo "PASS: iTOL link points to tree: $ITOL_URL"

# Test 6: Verify guide tree data exists in response
if ! echo "$RESPONSE" | grep -A5 "Guide Tree (.dnd format)" | grep -q "("; then
  echo "FAIL: Guide tree data appears to be missing or empty"
  exit 1
fi
echo "PASS: Guide tree data is present"

echo ""
echo "All tests passed!"
exit 0
