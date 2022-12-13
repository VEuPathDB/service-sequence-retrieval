#!/bin/sh

curl -X POST 'localhost:8080/sequences/genomic/bed?startOffset=ZERO&DeflineFormat=QUERYREGION' \
-H 'Content-Type:multipart/form-data' \
--form 'uploadMethod="file"' \
--form 'file=@"src/test/resources/veupathdb/service/sequence/query/contig_first_300_bases.bed6"'

curl -X POST 'localhost:8080/sequences/genomic/bed?startOffset=ZERO&DeflineFormat=QUERYREGION' \
-H 'Content-Type:multipart/form-data' \
--form 'uploadMethod="file"' \
--form 'file=@"src/test/resources/veupathdb/service/sequence/query/contig_first_300_bases_backwards.bed6"'
