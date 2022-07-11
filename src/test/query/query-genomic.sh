curl -X POST "http://localhost:8080/sequence/genomic"  -H 'Content-Type: application/json' --no-buffer \
  --data '{"features": [{"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q1", "strand": "+"}, {"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q2", "strand": "-"}, {"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q3", "strand": "."}], "deflineFormat": "query_and_region", "basesPerLine": 60, "forceStrandedness": true}'

