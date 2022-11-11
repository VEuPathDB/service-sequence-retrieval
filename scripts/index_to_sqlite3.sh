#!/bin/bash

if ! ( [ "$1" -a "$2" ] ) ; then
  echo "Usage: $0 input.fai output.fai.sqlite"
  exit 1
fi

rm -f "$2"

sqlite3 "$2" <<EOF
create table faidx (
  name text not null,
  length number not null,
  offset number not null,
  linebases number,
  linewidth number
);
create unique index faidx_name on faidx (name);
.separator '	'
.import $1 faidx
EOF
