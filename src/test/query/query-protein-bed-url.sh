#!/bin/sh

curl -X POST 'localhost:8080/sequences/protein/bed?startOffset=ZERO&DeflineFormat=QUERYREGION&forceStrandedness=false' \
-H 'Content-Type:multipart/form-data' \
--form 'uploadMethod="url"' \
--form 'url=https://raw.githubusercontent.com/VEuPathDB/service-sequence-retrieval/main/src/test/resources/veupathdb/service/sequence/query/protein.bed'
