curl -X POST "http://localhost:8080/sequences/protein"  -H 'Content-Type: application/json' --no-buffer \
  --data '{"features": [{"contig": "EHI7A_117830-t26_1-p1" , "start": 1, "end": 700000, "query": "QUERY" }, {"contig": "bad-contig", "start": 1, "end": 10} ], "deflineFormat": "QUERYANDREGION", "basesPerLine": 60, "forceStrandedness": false}'

