# `percentActg` request option — design spec

Date: 2026-09-17
Status: approved, ready for implementation

## Problem

The MSA postprocess (clustal-omega, invoked via `ClustaloExecutor`) crashes
when an input sequence contains too many non-ACTG characters (e.g. long runs
of `N`, ambiguity codes, masked/garbage regions). There is currently no way
for a client to filter such sequences out before they reach clustalo — the
first the caller hears about the problem is a clustalo failure.

## Goal

Add a `percentActg` option to the primary sequence-retrieval endpoint
(`SequencePostRequest`) that lets the client specify a minimum required
percentage of A/C/T/G bases a sequence must have to be included in the
response / passed on to postprocessing. Sequences below the threshold are
dropped rather than causing a downstream crash.

The value must reach `StreamSequences.java` (currently around line 35, the
per-feature loop that computes `bases` for each requested feature) since
that is the single shared choke point used by both plain FASTA retrieval and
the MSA/GENETREE postprocess input-building path.

## Non-goals

- No change to the async endpoint's multipart/BED/GFF3 upload path
  (`postSequencesBySequenceTypeAndFileFormat`) — that path has no JSON
  request body to add a field to and isn't in scope. It will always behave
  as if `percentActg` were unset (no filtering).
- No new server-side config (`SrtServiceOptions`) — this is a per-request
  value, not a deployment setting.
- No UI work. The frontend will not offer this option for protein sequence
  requests; server-side handling of protein is a defensive backstop, not the
  primary mechanism (see "PROTEIN handling" below).

## Behavior

### Definition

`percentActg` of a sequence = `100 * count(bases matching [ACTGactg]) / length(bases)`,
case-insensitive. All other characters (N, ambiguity codes, gaps, U, etc.)
count against the percentage. Empty sequences are treated as 100% (never
filtered) to avoid surprising behavior on zero-length edge cases.

### Threshold semantics

- `percentActg` is an optional integer field on `SequencePostRequest`,
  default `0`.
- `0` (the default) means **no filtering** — current behavior is fully
  preserved for all existing clients that don't send this field.
- When `percentActg > 0`, any requested feature whose computed ACTG
  percentage is strictly less than `percentActg` is **skipped**: it is not
  written to the FASTA output and does not participate in postprocessing.
  The rest of the request proceeds normally. This is a request-scoped
  filter, not a hard validation error — one low-quality sequence should not
  block the others.
- Skipped features are logged (INFO level, one line per skipped feature
  including its name/id) so the drop is observable without failing the
  request.

### PROTEIN handling

There is no sequence-type enum in this codebase. Sequence types are
configured dynamically via the `ALL_REFERENCE_SEQUENCE_NAMES` environment
variable (see `ReferenceDAOFactory`), and identified at request time only by
the lowercase `sequenceType` path parameter used to look up a `ReferenceDAO`
(e.g. `"genomic"`, `"protein"`, `"est"`). There is no code today that reads
a `{TYPE}_REFERENCE_TYPE` env var mentioned in project docs — despite the
name, it is not consumed anywhere in `ReferenceDAOFactory` or elsewhere, so
it cannot be relied on to identify PROTEIN.

Given that, "is this a protein request" is determined by **case-insensitive
name matching** against the literal `sequenceType` path parameter:

```java
boolean isProtein = "protein".equalsIgnoreCase(sequenceType);
```

- If `isProtein` is true, `percentActg` is ignored entirely (forced to `0`)
  regardless of what the client sent, since ACTG filtering is meaningless
  for amino acid sequences.
- Any other/future sequence type name (`genomic`, `est`, whatever gets added
  later) honors the client's `percentActg` value (or the `0` default).
- This check happens once, in `SequenceRetrievalService`, before the value
  is passed down — `ReferenceDAO` and `StreamSequences` remain unaware of
  sequence-type semantics and just receive a plain `int`.

## Data flow

Mirrors the existing `basesPerLine` plumbing exactly:

```
SequencePostRequest.percentActg (RAML/generated model)
  -> SequenceRetrievalService.postSequencesBySequenceType(sequenceType, entity)
       - resolves effective percentActg (0 if isProtein, else entity value or 0)
  -> ReferenceDAOFactory.get(sequenceType).validateAndPrepareResponse(
         features, deflineFormat, basesPerLine, percentActg)
  -> ReferenceDAO.validateAndPrepareResponse(..., percentActg)
  -> StreamSequences.write(outputStream, sequenceFile, features,
         deflineFormat, requestedBasesPerLine, percentActg)
       - per feature: compute bases (existing Bases.getBasesForBedFeature)
       - if percentActg > 0 and Bases.percentActg(bases) < percentActg:
           log + skip this feature (no defline/bases written)
         else: write as today
```

## Components touched

1. **`schema/url/sequence.raml`**
   Add to `SequencePostRequest`:
   ```yaml
   percentActg?:
     type: integer
     default: 0
     description: |
       Minimum percentage (0-100) of A/C/T/G bases a sequence must contain
       to be included in the response. Sequences below this threshold are
       skipped. Default 0 disables filtering. Ignored for protein sequence
       types.
   ```
   Regenerate via `./gradlew generate-jaxrs` — **never** edit
   `generated/` by hand.

2. **`SequenceRetrievalService.java`**
   In `postSequencesBySequenceType`, compute the effective `percentActg`
   using the `isProtein` name check above, and pass it into
   `validateAndPrepareResponse`. No change needed in
   `postSequencesBySequenceTypeAndFileFormat` (out of scope — always passes
   `0`).

3. **`ReferenceDAO.java`**
   `validateAndPrepareResponse` gains an `int percentActg` parameter,
   forwarded unchanged to `StreamSequences.write`.

4. **`StreamSequences.java`**
   `write(...)` gains an `int percentActg` parameter. In the per-feature
   loop, after computing `bases`, apply the skip check described above
   before calling `appendSequenceToStream`.

5. **`Bases.java`**
   New static helper:
   ```java
   public static double percentActg(byte[] bases) {
     if (bases.length == 0) return 100.0;
     int count = 0;
     for (byte b : bases) {
       switch (b) {
         case 'A': case 'a': case 'C': case 'c':
         case 'T': case 't': case 'G': case 'g':
           count++;
       }
     }
     return 100.0 * count / bases.length;
   }
   ```

## Testing

- **Unit tests for `Bases.percentActg`**: mixed-case ACTG, all-N sequence,
  empty array, sequence with ambiguity codes.
- **Unit tests for `StreamSequences.write`**: a feature below threshold is
  skipped when `percentActg > 0`; the same feature is included when
  `percentActg == 0`; multiple features where only the low-quality one is
  dropped and the rest stream normally.
- **Service-level test** (if existing test infra covers
  `SequenceRetrievalService`): confirm a request with
  `sequenceType=protein` and `percentActg=90` behaves identically to one
  with `percentActg` omitted (i.e., the value is ignored).

## Open items / explicit assumptions

- `{TYPE}_REFERENCE_TYPE`, documented in `CLAUDE.md` as part of the 5-var
  config pattern, is not read by any code path found in this repo. This
  spec relies on the `sequenceType` path parameter / configured name string
  instead. If `{TYPE}_REFERENCE_TYPE` turns out to be consumed elsewhere
  (e.g. by a downstream service reading the same env vars), that's outside
  this service's code and doesn't affect this design.
