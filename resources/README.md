# Third-Party Bioinformatics Tools

This directory contains source code for third-party bioinformatics tools that are compiled during the Docker build process.

## Included Tools

### Clustal Omega 1.2.4
- **File**: `clustal-omega-1.2.4.tar.gz`
- **License**: GNU GPL v2+ (see `clustal-omega-LICENSE.txt`)
- **Source**: https://github.com/GSLBiotech/clustal-omega
- **Description**: Multiple sequence alignment tool for protein and DNA/RNA
- **Compilation**: Compiled from source during Docker build using autoconf/make

### MAFFT 7.525
- **File**: `mafft-7.525-without-extensions-src.tgz`
- **License**: BSD (see `mafft-LICENSE.txt`)
- **Source**: https://mafft.cbrc.jp/alignment/software/
- **Description**: Multiple sequence alignment program for protein/DNA/RNA
- **Compilation**: Compiled from source during Docker build using make

### FastTree 2.1
- **File**: `FastTree.c`
- **License**: GNU GPL v2+ (see `FastTree-LICENSE.txt`)
- **Source**: http://www.microbesonline.org/fasttree/
- **Description**: Approximately-maximum-likelihood phylogenetic tree inference
- **Compilation**: Single C file compiled with gcc during Docker build

## Rationale for Including Source Code

These source files are included in the repository for the following reasons:

1. **Reproducibility**: Ensures consistent versions across all builds
2. **Build reliability**: Works even if upstream sources become unavailable
3. **License compliance**: Redistributing source code is explicitly permitted (and encouraged) by both BSD and GPL licenses
4. **Transparency**: Users can inspect exactly what code is being compiled

## License Compliance

All tools are open-source and permit source redistribution:
- **Clustal Omega**: GPL v2+ allows redistribution with source code
- **MAFFT**: BSD license allows redistribution with copyright notice
- **FastTree**: GPL v2+ allows redistribution with source code

License files are included alongside each tool's source code.
