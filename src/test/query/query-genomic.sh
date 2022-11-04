#!/bin/sh

curl -X POST "http://localhost:8080/sequences/genomic"  -H 'Content-Type: application/json' --no-buffer \
  --data '{"features": [{"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q1", "strand": "POSITIVE"}, {"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q2", "strand": "NEGATIVE"}, {"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q3", "strand": "NONE"}], "deflineFormat": "QUERYANDREGION", "basesPerLine": 60, "forceStrandedness": true}'

