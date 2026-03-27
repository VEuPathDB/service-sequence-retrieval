# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is a Java microservice for retrieving DNA/protein sequences from indexed FASTA files based on genomic coordinates (BED/GFF3 format). Part of the VEuPathDB bioinformatics platform.

## Essential Commands

### Prerequisites
- Java 21+
- Set environment variables: `GITHUB_USERNAME` and `GITHUB_TOKEN` (required for VEuPathDB maven packages)
- Docker (for local development)

### Build Pipeline
**Critical**: Commands must run in this order for initial build:
```bash
./gradlew clean generate-jaxrs jar test
```

### Individual Commands
```bash
# Generate JAX-RS code from RAML API specification
./gradlew generate-jaxrs

# Build fat JAR
./gradlew shadowJar

# Run tests
./gradlew test

# Build Docker image
./gradlew build-docker
```

### Local Development
```bash
cd docker-compose
cp env.sample .env
# Edit .env to configure paths and credentials
./runLocal.sh   # Starts PostgreSQL, RabbitMQ, MinIO, and the service
```

### Testing
```bash
# Unit tests
./gradlew test

# Integration tests (requires running service)
bash src/test/query/query-genomic-bed.sh
```

## High-Level Architecture

### Technology Stack
- **Language**: Java 21
- **Build**: Gradle 8.7 with Kotlin DSL
- **Web Framework**: JAX-RS with Jersey 4.0.2
- **API Design**: RAML 1.0 (API-first, generates code)
- **Async Infrastructure**: PostgreSQL (job queue), RabbitMQ (message queue), MinIO (S3-compatible storage)
- **Bioinformatics**: htsjdk 2.24.1 (Samtools library)
- **Key Libraries**:
  - `org.veupathdb.lib:jaxrs-container-core:10.0.3` (container framework)
  - `org.veupathdb.lib:compute-platform:1.8.6` (async job execution)

### Architectural Pattern
Layered REST service with both synchronous (streaming) and asynchronous (queued) sequence retrieval.

### Core Components

**Entry Point** (`Main.java`):
- Extends VEuPathDB container base class
- Initializes AsyncPlatform with database, queue, and storage connections
- Configuration loaded from environment variables via `AsyncOptions.java`

**Resource Layer** (REST controllers in `service/`):
- `SequenceRetrievalService`: Synchronous endpoints - returns FASTA sequences immediately via streaming
- `SequenceRetrievalAsyncService`: Asynchronous endpoints - returns job ID, queues background work
- `JobController`: Job status and result retrieval

**Business Logic Layer** (`reference/` and `response/`):
- `ReferenceDAOFactory`: Singleton managing multiple sequence types (GENOMIC, PROTEIN, EST, POPSET)
- `ReferenceDAO`: Core data access - wraps SQLite index and FASTA file, uses htsjdk's `IndexedFastaSequenceFile`
- `ReferenceSequenceSpec`: Per-reference configuration (max sequences, max bases, strand awareness)
- `FeatureAdapter`: Converts BED/GFF3 formats to htsjdk BEDFeature objects
- `StreamSequences`: Streams FASTA output efficiently without buffering entire results
- `Deflines` and `Bases`: Generate FASTA headers and extract sequence data

**Async Processing Layer** (`async/`):
- `WriteFeaturesJob`: Executes sequence retrieval jobs, writes results to S3
- `MyExecutorFactory`: Creates job executors for compute-platform
- Uses MD5 hash-based job IDs for automatic deduplication

### Key Design Patterns

**API-First Design**:
- RAML specification (`api.raml`) drives code generation
- All code under `generated/` is auto-generated - never edit directly
- Modify RAML instead, then regenerate with `./gradlew generate-jaxrs`

**Streaming Architecture**:
- Consumer/OutputStream pattern throughout
- No buffering of entire results in memory
- Critical for large genomic queries

**Lazy Index Loading**:
- SQLite-backed FASTA index (not loaded into RAM)
- `FastaSequenceIndexStub` provides on-demand lookups
- Scales to genomes with millions of sequences

**Multi-Reference Support**:
- Factory pattern for different sequence types
- Each type independently configured via environment variables
- Validation rules per reference (stranded vs. unstranded)

## Code Organization

```
org.veupathdb.service.sr/
├── Main.java                          # Application entry point
├── Resources.java                     # JAX-RS resource registration
├── AsyncOptions.java                  # Configuration from environment
│
├── service/                           # REST controllers
│   ├── SequenceRetrievalService       # Sync endpoints
│   ├── SequenceRetrievalAsyncService  # Async endpoints
│   └── JobController                  # Job management
│
├── reference/                         # Data access layer
│   ├── ReferenceDAOFactory            # Factory for sequence types
│   ├── ReferenceDAO                   # Main data access
│   └── ReferenceSequenceSpec          # Reference configuration
│
├── response/                          # Response generation
│   ├── StreamSequences                # Main streaming logic
│   ├── Deflines                       # FASTA header generation
│   └── Bases                          # Sequence extraction
│
├── async/                             # Async job processing
│   ├── WriteFeaturesJob               # Job executor
│   └── MyExecutorFactory              # Executor factory
│
├── util/                              # Utilities
│   └── FeatureAdapter                 # BED/GFF3 format conversions
│
└── generated/                         # Auto-generated from RAML
    ├── model/                         # API data models
    ├── resources/                     # JAX-RS resource interfaces
    └── support/                       # Support classes
```

## Critical Development Notes

### Generated Code Convention
- All code under `generated/` is auto-generated from `api.raml`
- **Never edit generated code directly** - modify RAML instead
- Service implementations extend/implement generated interfaces
- Run `./gradlew generate-jaxrs` after RAML changes

### Sequence Coordinate Systems
- **BED format**: 0-based half-open [start, end)
- **GFF3 format**: 1-based closed [start, end]
- htsjdk handles conversions internally
- Service supports configurable start offset for BED files

### Stranded vs. Unstranded References
- **Genomic/EST**: stranded - supports +/- strand queries
- **Protein**: unstranded - strand must be NONE
- Validation enforced at `ReferenceSequenceSpec` level
- Incorrect strand specification will cause request rejection

### Configuration Management
Each sequence type requires 5 environment variables:
- `{TYPE}_FASTA_FILE` - Path to FASTA file
- `{TYPE}_INDEX_FILE` - Path to SQLite index
- `{TYPE}_MAX_SEQUENCES_PER_REQUEST` - Request limit
- `{TYPE}_MAX_TOTAL_BASES_PER_REQUEST` - Base count limit
- `{TYPE}_IS_STRANDED` - Boolean for strand support

Master list: `ALL_REFERENCE_SEQUENCE_NAMES` (comma-separated sequence type names)

### Index File Format
- Standard samtools .fai format, converted to SQLite for efficient queries
- Conversion script: `scripts/index_to_sqlite3.sh`
- Columns: name, length, offset, linebases, linewidth
- Required for all FASTA files served by the service

### VEuPathDB Ecosystem Dependencies
- Requires GitHub token for VEuPathDB maven packages
- Uses custom container framework (`jaxrs-container-core`)
- Integrates with compute-platform for async processing
- Part of larger bioinformatics infrastructure
