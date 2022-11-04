#!/bin/sh

curl -X POST 'localhost:8080/sequences/genomic/gff3?startOffset=ZERO&DeflineFormat=QUERYREGION&forceStrandedness=true' \
-H 'Content-Type:multipart/form-data' \
--form 'uploadMethod="file"' \
--form 'file=@"src/test/resources/veupathdb/service/sequence/query/gene.gff3"' \
