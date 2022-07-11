curl -X POST \
  -v \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@local/gene.gff3' \
  "http://localhost:8080/sequenceForGff3/genomic?deflineFormat=query_and_region" 

