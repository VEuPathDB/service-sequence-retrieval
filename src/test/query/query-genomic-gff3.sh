curl --location --request POST 'localhost:8080/sequences/genomic/gff3?startOffset=ZERO&DeflineFormat=QUERYREGION&forceStrandedness=true' \
--form 'uploadMethod="file"' \
--form 'file=@"src/test/query/gene.gff3"'
