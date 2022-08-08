curl --location --request POST 'localhost:8080/sequences/protein/bed?startOffset=ZERO&DeflineFormat=QUERYREGION&forceStrandedness=false' \
--form 'uploadMethod="file"' \
--form 'file=@"src/test/query/protein.bed"'
