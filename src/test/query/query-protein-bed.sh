#!/bin/sh

curl -X POST 'localhost:8080/sequences/protein/bed?startOffset=ZERO&DeflineFormat=QUERYREGION&forceStrandedness=false' \
-H 'Content-Type:multipart/form-data' \
--form 'uploadMethod="file"' \
--form 'file=@"src/test/resources/veupathdb/service/sequence/query/protein.bed"'
