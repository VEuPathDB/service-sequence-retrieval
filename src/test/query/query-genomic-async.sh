#!/bin/sh

doCurl() {
  curl --silent -X POST "http://localhost:8080/sequences-async/genomic"  -H 'Content-Type: application/json' --no-buffer \
  --data '{"features": [{"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q1", "strand": "POSITIVE"}, {"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q2", "strand": "NEGATIVE"}, {"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q3", "strand": "NONE"}], "deflineFormat": "QUERYANDREGION", "basesPerLine": 60, "forceStrandedness": true}'
}

jobId=$(doCurl | jq -r .jobID)

curl --silent "http://localhost:8080/jobs/$jobId/files/output"

