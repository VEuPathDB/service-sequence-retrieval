# Implementation Plan: Add MSA Post-Processing Support

## Overview
Add support for multiple sequence alignment (MSA) and other post-processing types to the sequence retrieval service. The architecture follows a pipeline: **FASTA generation → optional post-processing → output**.

---

## 1. RAML API Changes

### 1.1 Add New Types to `schema/url/sequence.raml`

**Add PostProcessType enum:**
```raml
PostProcessType:
  type: string
  enum: ['orthomclMSA', 'isolatesMSA', 'geneTree']
```

**Add output format enums (each post-process type has its own):**
```raml
OrthomclMsaFormat:
  type: string
  enum: ['fasta', 'clustal', 'msf', 'phylip', 'selex', 'stockholm', 'vienna']

IsolatesMsaFormat:
  type: string
  # TBD - to be defined later

GeneTreeFormat:
  type: string
  # TBD - to be defined later
```

**Add options types:**
```raml
OrthomclMsaOptions:
  type: object
  additionalProperties: false
  properties:
    format:
      type: OrthomclMsaFormat
      default: 'clustal'

IsolatesMsaOptions:
  type: object
  additionalProperties: false
  properties:
    format:
      type: IsolatesMsaFormat
      # default TBD

GeneTreeOptions:
  type: object
  additionalProperties: false
  properties:
    format:
      type: GeneTreeFormat
      # default TBD
```

**Update SequencePostRequest:**
```raml
SequencePostRequest:
  additionalProperties: false
  properties:
    features:
      type: array
      items: Feature
    deflineFormat?:
      type: DeflineFormat
      default: 'REGIONONLY'
    basesPerLine?:
      type: integer
      default: 60
    postProcess?:
      type: PostProcessType
    orthomclMsaOptions?:
      type: OrthomclMsaOptions
    isolatesMsaOptions?:
      type: IsolatesMsaOptions
    geneTreeOptions?:
      type: GeneTreeOptions
```

**Update SequenceRetrievalSpec** (for async jobs):
Add same postProcess and options fields.

### 1.2 Update Response Types

For clustal format in orthomclMSA, response is HTML. Update `api.raml`:
```raml
/sequences/{sequenceType}:
  post:
    responses:
      200:
        body:
          text/x-fasta:
            type: lib.PlainTextFastaResponse
          text/html:
            type: any  # HTML response for clustal format MSA
```

---

## 2. Java Class Architecture

### 2.1 Post-Processor Interface

**Create `org.veupathdb.service.sr.postprocess.PostProcessor` interface:**
```java
public interface PostProcessor {
  PostProcessResult process(File fastaInput) throws IOException;
}

public class PostProcessResult {
  private final String contentType;  // "text/html" or "text/plain"
  private final byte[] content;
  private final Map<String, byte[]> additionalFiles; // for .dnd, etc.

  // constructor, getters
}
```

### 2.2 Clustalo Executor

**Create `org.veupathdb.service.sr.postprocess.ClustaloExecutor`:**
- Executes clustalo binary via ProcessBuilder
- Parameters: input file, output format, output file
- Captures stdout/stderr for error handling
- Throws exception if clustalo fails or times out

### 2.3 Post-Processor Implementations

**Create `org.veupathdb.service.sr.postprocess.orthomcl.OrthomclMsaProcessor`:**
- Implements PostProcessor
- Uses ClustaloExecutor with flags: `--residuenumber --output-order=tree-order --guidetree-out --force`
- For clustal format:
  - Parse .dnd file, manipulate colons for iTOL
  - POST to `https://itol.embl.de/upload.cgi`
  - Generate HTML with iTOL link + alignment + .dnd
- For other formats: return plain text alignment

**Create `org.veupathdb.service.sr.postprocess.isolates.IsolatesMsaProcessor`:**
- Placeholder implementation (to be defined later)
- Similar structure to OrthomclMsaProcessor

**Create `org.veupathdb.service.sr.postprocess.genetree.GeneTreeProcessor`:**
- Placeholder implementation (to be defined later)

### 2.4 Post-Processor Factory

**Create `org.veupathdb.service.sr.postprocess.PostProcessorFactory`:**
```java
public class PostProcessorFactory {
  public static PostProcessor create(PostProcessType type, Object options) {
    return switch(type) {
      case ORTHOMCL_MSA -> new OrthomclMsaProcessor((OrthomclMsaOptions) options);
      case ISOLATES_MSA -> new IsolatesMsaProcessor((IsolatesMsaOptions) options);
      case GENE_TREE -> new GeneTreeProcessor((GeneTreeOptions) options);
    };
  }
}
```

---

## 3. Configuration Changes

### 3.1 Add to AsyncOptions.java

```java
@Option(
  names = "--clustalo-binary-path",
  defaultValue = "${env:CLUSTALO_BINARY_PATH}",
  description = "Path to clustalo binary",
  arity = "1",
  required = true)
private String clustaloBinaryPath;

@Option(
  names = "--msa-sync-max-sequences",
  defaultValue = "${env:MSA_SYNC_MAX_SEQUENCES}",
  description = "Maximum sequences allowed for synchronous MSA requests",
  arity = "1")
private Integer msaSyncMaxSequences;
private static final int DEFAULT_MSA_SYNC_MAX_SEQUENCES = 20;

@Option(
  names = "--clustalo-timeout-seconds",
  defaultValue = "${env:CLUSTALO_TIMEOUT_SECONDS}",
  description = "Timeout for clustalo execution",
  arity = "1")
private Integer clustaloTimeoutSeconds;
private static final int DEFAULT_CLUSTALO_TIMEOUT_SECONDS = 300;

// getters...
```

### 3.2 Environment Variables

Add to `docker-compose/env.sample`:
```
CLUSTALO_BINARY_PATH=/usr/bin/clustalo
MSA_SYNC_MAX_SEQUENCES=20
CLUSTALO_TIMEOUT_SECONDS=300
```

---

## 4. Service Layer Changes

### 4.1 Update SequenceRetrievalService.java (Sync)

**Add validation and processing:**
```java
private void validateMsaRequest(SequencePostRequest entity, List<BEDFeature> features) {
  if (entity.getPostProcess() != null) {
    int maxSeq = AsyncOptions.getInstance().getMsaSyncMaxSequences();
    if (features.size() > maxSeq) {
      throw new BadRequestException(
        "Too many sequences for synchronous MSA (" + features.size() +
        "). Maximum is " + maxSeq + ". Use async endpoint instead.");
    }
  }
}

private Consumer<OutputStream> processWithPostProcessing(
    Consumer<OutputStream> fastaStream,
    PostProcessType postProcessType,
    Object options) throws IOException {

  // Write FASTA to temp file
  File tempFasta = File.createTempFile("seq-retrieval", ".fasta");
  try (FileOutputStream fos = new FileOutputStream(tempFasta)) {
    fastaStream.accept(fos);
  }

  // Run post-processor
  PostProcessor processor = PostProcessorFactory.create(postProcessType, options);
  PostProcessResult result = processor.process(tempFasta);

  tempFasta.delete();

  // Return consumer that writes the result
  return (os) -> {
    try {
      os.write(result.getContent());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  };
}
```

**Update endpoint methods to check postProcess field and call processing.**

### 4.2 Update SequenceRetrievalAsyncService.java

Add postProcess fields to SequenceRetrievalSpec when creating job specs:
```java
spec.setPostProcess(entity.getPostProcess());
spec.setOrthomclMsaOptions(entity.getOrthomclMsaOptions());
// ... other options
```

### 4.3 Update WriteFeaturesJob.java

**Add post-processing after FASTA generation:**
```java
@Override
public JobResult execute(JobContext jobContext) {
  var jobSpec = Json.parse(jobContext.getConfig(), SequenceRetrievalSpecImpl.class);

  // ... existing FASTA generation code ...

  // Write FASTA to temp file
  File tempFasta = File.createTempFile("job-fasta", ".fasta");
  try (FileOutputStream fos = new FileOutputStream(tempFasta)) {
    stream.accept(fos);
  }

  // Check for post-processing
  if (jobSpec.getPostProcess() != null) {
    Object options = getOptionsForPostProcessType(jobSpec);
    PostProcessor processor = PostProcessorFactory.create(
      jobSpec.getPostProcess(), options);
    PostProcessResult result = processor.process(tempFasta);

    // Write result to workspace
    jobContext.getWorkspace().write("output", result.getContent());

    // Write additional files (e.g., .dnd)
    for (Map.Entry<String, byte[]> entry : result.getAdditionalFiles().entrySet()) {
      jobContext.getWorkspace().write(entry.getKey(), entry.getValue());
    }
  } else {
    // Write FASTA directly
    jobContext.getWorkspace().write("output",
      Files.readAllBytes(tempFasta.toPath()));
  }

  tempFasta.delete();
  return JobResult.success("output");
}
```

---

## 5. Deployment: Install clustalo

### 5.1 Update Dockerfile

**Add clustalo installation:**
```dockerfile
# In builder stage, after existing RUN commands:
RUN apk add --no-cache clustal-omega

# Verify installation
RUN clustalo --version
```

### 5.2 Update docker-compose.dev.yml

Add environment variable:
```yaml
environment:
  - CLUSTALO_BINARY_PATH=/usr/bin/clustalo
  - MSA_SYNC_MAX_SEQUENCES=20
  - CLUSTALO_TIMEOUT_SECONDS=300
```

---

## 6. Validation and Error Handling

### 6.1 Request Validation

**Create `org.veupathdb.service.sr.validation.PostProcessValidator`:**
- Validate postProcess type has corresponding options
- Validate mutually exclusive options (can't have both orthomclMsaOptions and isolatesMsaOptions)
- Validate options values (format enum, etc.)

### 6.2 Error Cases

- **Sync MSA too large:** Return 400 with helpful message directing to async
- **Clustalo execution failure:** Return 500 with clustalo stderr
- **Clustalo timeout:** Return 500 with timeout message
- **iTOL service failure:** Log warning, return alignment without iTOL link
- **Missing clustalo binary:** Fail fast at startup with clear error

---

## 7. Testing Strategy

### 7.1 Unit Tests
- PostProcessor implementations with mock clustalo output
- Validation logic
- HTML generation for orthomclMSA clustal format

### 7.2 Integration Tests
- Small sequence sets (2-5 sequences) via sync endpoints
- Verify output formats (fasta, clustal, phylip, etc.)
- Test async endpoint with larger sets
- Test sync rejection of large requests

### 7.3 Manual Testing
- Test iTOL integration with real data
- Verify HTML output renders correctly
- Test all output formats

---

## 8. Documentation Updates

### 8.1 Update CLAUDE.md
- Add section on post-processing architecture
- Document clustalo integration
- List post-process types and their options

### 8.2 Update readme.adoc
- Document new API fields
- Provide examples of MSA requests
- Document configuration variables

---

## Implementation Order

1. **RAML changes** → regenerate code
2. **Configuration** → add AsyncOptions fields
3. **Post-processor architecture** → interface, factory, ClustaloExecutor
4. **OrthomclMsaProcessor** → full implementation with iTOL
5. **Service layer integration** → sync and async endpoints
6. **WriteFeaturesJob updates** → async processing
7. **Validation** → request validation
8. **Dockerfile** → install clustalo
9. **Placeholder processors** → IsolatesMsa, GeneTree (minimal implementations)
10. **Testing** → unit and integration tests
11. **Documentation** → update CLAUDE.md and readme.adoc

---

## Key Implementation Notes

### Clustalo Integration
- **Binary:** clustal-omega (`clustalo` command)
- **Output formats:** `--outfmt={a2m=fa[sta],clu[stal],msf,phy[lip],selex,st[ockholm],vie[nna]}`
- **OrthomclMSA flags:** `--residuenumber --output-order=tree-order --guidetree-out --force`

### OrthomclMSA Business Logic (from Perl CGI)
- Run clustalo with guide tree output
- Process .dnd file: manipulate colons for iTOL compatibility
  - Reverse string, replace first `:` with `%`, replace remaining `:` with `_`, reverse back, replace `%` with `:`
- POST tree data to `https://itol.embl.de/upload.cgi`
- Parse response location header to get iTOL URL
- For **clustal format only:** Generate HTML with:
  - iTOL link in header
  - Alignment content
  - Raw .dnd file at bottom
- For **other formats:** Return plain text alignment

### Backwards Compatibility
- All postProcess fields are optional
- Service maintains current behavior when postProcess is omitted
- Existing clients continue working unchanged

This keeps the service backwards compatible (postProcess is optional) while adding extensible post-processing support.
