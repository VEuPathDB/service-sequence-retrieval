# Service Sequence Retrieval - Essential Context

A Java microservice for retrieving DNA/protein sequences from indexed FASTA files based on genomic coordinates. Part of the VEuPathDB bioinformatics platform.

## Quick Start

**Prerequisites:**
- Java 21+
- Environment variables: `GITHUB_USERNAME` and `GITHUB_TOKEN` (for VEuPathDB maven packages)

**Critical Build Order:**
```bash
./gradlew clean generate-jaxrs jar test
```
This specific order is required on initial build. Skipping steps causes cryptic errors.

**Local Development:**
```bash
cd docker-compose
cp env.sample .env
./runLocal.sh
```

## Critical Conventions

### API-First Design
- RAML 1.0 specification (`api.raml`) generates code
- **NEVER edit the `generated/` directory** - changes will be overwritten
- Workflow: Modify RAML → Run `./gradlew generate-jaxrs` → Implement interfaces

### FASTA Index Format
- Requires **SQLite indexes**, not just standard `.fai` files
- Conversion: `scripts/index_to_sqlite3.sh <fasta_file>`
- Rationale: Indexes can be too large for RAM; SQLite enables lazy loading

### Configuration Pattern
Each sequence type requires **5 environment variables** with specific suffixes:
- `{TYPE}_FASTA_FILE` - Path to FASTA file
- `{TYPE}_INDEX_FILE` - Path to SQLite index
- `{TYPE}_ORGANISM` - Organism name
- `{TYPE}_BUILD` - Genome build
- `{TYPE}_REFERENCE_TYPE` - Type identifier

Plus master list: `ALL_REFERENCE_SEQUENCE_NAMES` (comma-separated type names)

Example: `GENOMIC_FASTA_FILE`, `GENOMIC_INDEX_FILE`, etc.

## Key Architectural Decisions

### Streaming Architecture
- No buffering of entire results in memory
- Critical for large genomic queries (can be gigabytes)
- Results streamed directly to client via JAX-RS StreamingOutput

### Stranded vs. Unstranded Validation
- **GENOMIC/EST**: Stranded (supports +/- strand specification)
- **PROTEIN**: Unstranded (strand must be NONE)
- Validation enforced at API layer

### MSA (Multiple Sequence Alignment) Limits
Uses clustal-omega (CPU/memory intensive):
- **Sync requests**: 20 sequences (default), 30s timeout
- **Async requests**: 1000 sequences, 1800s timeout
- Configurable via `MSA_SYNC_MAX_SEQS` / `MSA_SYNC_TIMEOUT_SECONDS`

## Important Gotchas

1. **GitHub Authentication**: VEuPathDB packages require GitHub token in environment
2. **Build Order Matters**: Initial build must follow exact order above
3. **Generated Code**: Always regenerate after RAML changes - don't skip this step
4. **Index Files**: Standard samtools `.fai` indexes won't work - must use SQLite format
5. **Memory Constraints**: Async jobs run in separate queue to avoid blocking sync requests

## Project Structure Highlights

- `api.raml` - Source of truth for API contract
- `src/main/java/org/veupathdb/service/srt/service/SrtServiceOptions.java` - Configuration management
- `src/main/java/org/veupathdb/service/srt/repo/` - FASTA access via htsjdk
- `scripts/index_to_sqlite3.sh` - Index conversion utility
