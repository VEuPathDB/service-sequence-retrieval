#!/usr/bin/env python3
"""Create a .sqlite database with a `faidx` table and import a .fai file into it.

Python replacement for the sqlite3-CLI version; needs only the built-in sqlite3 module.
Column names follow http://www.htslib.org/doc/faidx.html
Table name and columns are expected by the service.

Usage: fai2sqlite.py input.fai output.fai.sqlite
"""
import os
import sqlite3
import sys

if len(sys.argv) != 3:
    sys.exit(f"Usage: {sys.argv[0]} input.fai output.fai.sqlite")

src, dst = sys.argv[1], sys.argv[2]

if not os.path.isfile(src):
    sys.exit(f"Input not found: {src}")
if os.path.abspath(src) == os.path.abspath(dst):
    sys.exit("Output must be different from input")


def rows(path):
    with open(path) as f:
        for lineno, line in enumerate(f, 1):
            line = line.rstrip("\n")
            if not line:
                continue
            fields = line.split("\t")
            if len(fields) != 5:
                sys.exit(f"{path}:{lineno}: expected 5 tab-separated fields, got {len(fields)}")
            name, *nums = fields
            try:
                yield (name, *map(int, nums))
            except ValueError:
                sys.exit(f"{path}:{lineno}: non-numeric value in {fields[1:]}")


# Build into a temp file, then rename, so the service never sees a half-built database.
tmp = dst + ".tmp"
if os.path.exists(tmp):
    os.remove(tmp)

con = sqlite3.connect(tmp)
try:
    with con:
        con.execute("""
            create table faidx (
              name text not null,
              length number not null,
              offset number not null,
              linebases number,
              linewidth number
            )""")
        con.execute("create unique index faidx_name on faidx (name)")
        con.executemany("insert into faidx values (?, ?, ?, ?, ?)", rows(src))
    count = con.execute("select count(*) from faidx").fetchone()[0]
finally:
    con.close()

os.replace(tmp, dst)
print(f"Imported {count} sequences into {dst}")
