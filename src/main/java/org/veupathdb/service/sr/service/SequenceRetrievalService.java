package org.veupathdb.service.sr.service;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gusdb.fgputil.IoUtil;
import org.gusdb.fgputil.Timer;
import org.veupathdb.service.sr.SrtServiceOptions;
import org.veupathdb.service.sr.generated.model.*;
import org.veupathdb.service.sr.generated.resources.SequencesSequenceType;
import org.veupathdb.service.sr.postprocess.PostProcessResult;
import org.veupathdb.service.sr.postprocess.PostProcessor;
import org.veupathdb.service.sr.postprocess.PostProcessorFactory;
import org.veupathdb.service.sr.postprocess.ProcessingContext;
import org.veupathdb.service.sr.reference.PreparedResponse;
import org.veupathdb.service.sr.reference.ReferenceDAOFactory;
import org.veupathdb.service.sr.util.EnumUtil;
import org.veupathdb.service.sr.util.FeatureAdapter;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;

public class SequenceRetrievalService implements SequencesSequenceType {

  private static final Logger LOG = LogManager.getLogger(SequenceRetrievalService.class);

  private static final DeflineFormat DEFAULT_DEFLINE_FORMAT = DeflineFormat.REGIONONLY;
  private static final int DEFAULT_BASES_PER_LINE = 60;
  private static final int NULL_PERCENT_ACTG = 0;

  /**
   * Extend generated streamer class so we can log processing duration
   */
  private static class StreamerWithLogging extends PlainTextFastaResponseStream {
    public StreamerWithLogging(Consumer<OutputStream> streamer) {
      super(streamer);
    }
    @Override
    public void write(OutputStream output) throws IOException, WebApplicationException {
      Timer timer = new Timer();
      super.write(output);
      LOG.info("Took " + timer.getElapsedStringAndRestart() + " to retrieve and stream sequences.");
    }
  }

  @Override
  public PostSequencesBySequenceTypeResponse postSequencesBySequenceType(String sequenceType, SequencePostRequest entity) {

    var deflineFormat = Optional.ofNullable(entity.getDeflineFormat()).orElse(DEFAULT_DEFLINE_FORMAT);
    var basesPerLine = Optional.ofNullable(entity.getBasesPerLine()).orElse(DEFAULT_BASES_PER_LINE);
    var percentActg = "protein".equalsIgnoreCase(sequenceType)
        ? NULL_PERCENT_ACTG
        : Optional.ofNullable(entity.getPercentActg()).orElse(NULL_PERCENT_ACTG);
    validatePercentActgRange(percentActg);

    var features = FeatureAdapter.toBEDFeatures(entity.getFeatures());

    // Validate post-processing sync request limits
    if (entity.getPostProcess() != null) {
      validatePostProcessSyncRequest(entity.getPostProcess(), features.size());
    }

    var preparedResponse = ReferenceDAOFactory.get(sequenceType).validateAndPrepareResponse(features, deflineFormat, basesPerLine, percentActg);

    // Check if post-processing is requested
    if (entity.getPostProcess() != null) {
      try {
        return handlePostProcessing(preparedResponse, entity, sequenceType);
      } catch (IOException e) {
        throw new RuntimeException("Post-processing failed", e);
      }
    }

    return PostSequencesBySequenceTypeResponse.respond200WithTextXFasta(new StreamerWithLogging(preparedResponse.stream()));

  }

  /**
   * Validate that percentActg (if non-default) falls within the documented 0-100 range.
   */
  private static void validatePercentActgRange(int percentActg) {
    if (percentActg < 0 || percentActg > 100) {
      throw new BadRequestException("percentActg must be between 0 and 100, got: " + percentActg);
    }
  }

  @Override
  public PostSequencesBySequenceTypeAndFileFormatResponse postSequencesBySequenceTypeAndFileFormat(
      String sequenceType,
      String fileFormatStr,
      DeflineFormat deflineFormat,
      Integer basesPerLine,
      StartOffset startOffset,
      SequencesSequenceTypeFileFormatPostMultipartFormData entity){

      var uploadMethod = entity.getUploadMethod();
      var file = entity.getFile();
      var url = entity.getUrl();

    // throw not found since these are path params
    var fileFormat = EnumUtil.validate(fileFormatStr, SupportedFileFormat.values(), NotFoundException::new);

    Timer timer = new Timer();
    LOG.info("Beginning input data fetch of submission type " + uploadMethod +
        ", data type " + fileFormat + " to input data located at " +
        (uploadMethod == UploadMethod.FILE ? file.getAbsoluteFile().toString() : url));
    try (InputStream fileStream = switch (uploadMethod) {
      case FILE -> new FileInputStream(file);
      case URL -> new URL(url).openStream();
    }) {
      LOG.info("Took " + timer.getElapsedStringAndRestart() + " to open stream to input data.");
      var features = switch (fileFormat) {
        case BED -> FeatureAdapter.readBed(fileStream, startOffset);
        case GFF3 -> FeatureAdapter.readGff3AndConvertToBed(fileStream);
      };

      LOG.info("Took " + timer.getElapsedStringAndRestart() + " to read features from input data.");
      var preparedResponse = ReferenceDAOFactory.get(sequenceType).validateAndPrepareResponse(features, deflineFormat, basesPerLine, NULL_PERCENT_ACTG);

      LOG.info("Took " + timer.getElapsedStringAndRestart() + " to prepare to stream response.");
      return PostSequencesBySequenceTypeAndFileFormatResponse.respond200WithTextXFasta(new StreamerWithLogging(preparedResponse.stream()));

    } catch (IOException e) {
      throw new RuntimeException("Unable to complete file processing", e);
    }
  }

  /**
   * Validate that the number of sequences is within the limits for synchronous post-processing requests.
   */
  private void validatePostProcessSyncRequest(PostProcessType postProcessType, int sequenceCount) {
    SrtServiceOptions options = org.veupathdb.service.sr.Main.getOptions();

    switch (postProcessType) {
      case MSA -> {
        int maxSequences = options.getMsaSyncMaxSequences();
        if (sequenceCount > maxSequences) {
          throw new BadRequestException(
            "Too many sequences for synchronous MSA request (" + sequenceCount + " sequences). " +
            "Maximum allowed is " + maxSequences + ". " +
            "Please use the async endpoint (/sequences-async) for larger requests.");
        }
      }
      case GENETREE -> {
        // Minimum 3 sequences required for gene tree generation
        if (sequenceCount < 3) {
          throw new BadRequestException(
            "Too few sequences for gene tree generation (" + sequenceCount + " sequences). " +
            "Minimum required is 3 sequences.");
        }
        int maxSequences = options.getGeneTreeSyncMaxSequences();
        if (sequenceCount > maxSequences) {
          throw new BadRequestException(
            "Too many sequences for synchronous gene tree request (" + sequenceCount + " sequences). " +
            "Maximum allowed is " + maxSequences + ". " +
            "Please use the async endpoint (/sequences-async) for larger requests.");
        }
      }
    }
  }

  /**
   * Handle post-processing of FASTA output.
   */
  private PostSequencesBySequenceTypeResponse handlePostProcessing(
      PreparedResponse preparedResponse,
      SequencePostRequest entity,
      String sequenceType) throws IOException {

    // Write FASTA to temp file
    File tempFasta = File.createTempFile("seq-retrieval-", ".fasta");
    try {
      try (FileOutputStream fos = new FileOutputStream(tempFasta)) {
        preparedResponse.stream().accept(fos);
      }

      // Use the features that actually survived percentActg filtering (and were therefore
      // actually written to tempFasta) rather than the original, unfiltered request list.
      // Using the unfiltered list here would hand post-processing a feature/metadata set that
      // doesn't match the FASTA it's about to read, which can throw confusing errors (MSA
      // metadata ID mismatches) or silently diverge.
      var features = preparedResponse.getSurvivedFeatures();

      if (features.isEmpty()) {
        int requestedCount = entity.getFeatures().size();
        throw new BadRequestException(
            "All " + requestedCount + " requested sequences were filtered out by percentActg; nothing to process.");
      }

      // Create post-processor
      SrtServiceOptions options = org.veupathdb.service.sr.Main.getOptions();
      PostProcessor processor = PostProcessorFactory.create(
          entity.getPostProcess(),
          entity.getMsaOptions(),
          entity.getGeneTreeOptions(),
          options,
          ProcessingContext.SYNC,
          sequenceType
      );

      // Process
      PostProcessResult result = processor.process(tempFasta, features);

      // Return appropriate response based on content type (all use streaming)
      return switch (result.getContentType()) {
        case "text/html" -> PostSequencesBySequenceTypeResponse.respond200WithTextHtml(
          new StreamerWithLogging(os -> {
            try {
              result.writeContent(os);
              result.cleanup();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }));
        case "text/plain" -> PostSequencesBySequenceTypeResponse.respond200WithTextPlain(
          new StreamerWithLogging(os -> {
            try {
              result.writeContent(os);
              result.cleanup();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }));
        default -> PostSequencesBySequenceTypeResponse.respond200WithTextXFasta(
          new StreamerWithLogging(os -> {
            try {
              result.writeContent(os);
              result.cleanup();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }));
      };
    } finally {
      tempFasta.delete();
    }
  }
}
