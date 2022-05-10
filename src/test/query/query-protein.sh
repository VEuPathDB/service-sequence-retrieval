curl -X POST "http://localhost:8080/sequence/protein"  -H 'Content-Type: application/json' --no-buffer \
  --data '{"features": [{"contig": "EHI7A_117830-t26_1-p1" , "start": 1, "end": 7, "query": "QUERY" }], "deflineFormat": "query_and_region", "basesPerLine": 60}'

