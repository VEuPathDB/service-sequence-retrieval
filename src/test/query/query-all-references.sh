#!/bin/sh

curl -X POST "http://localhost:8080/sequences/genomic"  -H 'Content-Type: application/json' --no-buffer \
  --data '{"features": [{"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q1", "strand": "POSITIVE"}, {"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q2", "strand": "NEGATIVE"}, {"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q3", "strand": "NONE"}], "deflineFormat": "QUERYANDREGION", "basesPerLine": 60}'
echo ""
curl -X POST "http://localhost:8080/sequences/protein"  -H 'Content-Type: application/json' --no-buffer \
  --data '{"features": [{"contig": "EHI7A_117830-t26_1-p1" , "start": 1, "end": 7, "query": "QUERY" }], "deflineFormat": "QUERYANDREGION", "basesPerLine": 60}'
echo ""
curl -X POST "http://localhost:8080/sequences/est"  -H 'Content-Type: application/json' --no-buffer \
  --data '{"features": [{"contig": "BE636532" , "start": 1, "end": 7, "query": "Q1", "strand": "POSITIVE"}], "deflineFormat": "QUERYANDREGION", "basesPerLine": 60}'
echo ""
curl -X POST "http://localhost:8080/sequences/popset"  -H 'Content-Type: application/json' --no-buffer \
  --data '{"features": [{"contig": "KF927025" , "start": 1, "end": 7, "query": "Q1", "strand": "POSITIVE"}], "deflineFormat": "QUERYANDREGION", "basesPerLine": 60}'
