curl -X POST \
  -v \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@local/protein.bed' \
  "http://localhost:8080/sequenceForBed/protein?startOffset=ONE&deflineFormat=query_and_region" 

