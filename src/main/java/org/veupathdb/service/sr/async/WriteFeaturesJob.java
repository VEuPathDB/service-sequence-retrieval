package org.veupathdb.service.sr.async;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.lib.compute.platform.job.JobContext;
import org.veupathdb.lib.compute.platform.job.JobExecutor;
import org.veupathdb.lib.compute.platform.job.JobResult;
import org.veupathdb.lib.jackson.Json;
import org.veupathdb.service.sr.SrtServiceOptions;
import org.veupathdb.service.sr.postprocess.PostProcessResult;
import org.veupathdb.service.sr.postprocess.PostProcessor;
import org.veupathdb.service.sr.postprocess.PostProcessorFactory;
import org.veupathdb.service.sr.postprocess.ProcessingContext;
import org.veupathdb.service.sr.util.FeatureAdapter;
import org.veupathdb.service.sr.reference.ReferenceDAOFactory;
import org.veupathdb.service.sr.generated.model.PostProcessType;
import org.veupathdb.service.sr.generated.model.SequenceRetrievalSpec;
import org.veupathdb.service.sr.generated.model.SequenceRetrievalSpecImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.net.URL;

import htsjdk.tribble.bed.BEDFeature;

public class WriteFeaturesJob implements JobExecutor {
  @NotNull
  @Override
  public JobResult execute(@NotNull JobContext jobContext) {
    var jobSpec = Json.parse(jobContext.getConfig(), SequenceRetrievalSpecImpl.class);

    var sequenceType = jobSpec.getSequenceType();
    var fileFormat = jobSpec.getFileFormat();
    var deflineFormat = jobSpec.getDeflineFormat();
    var basesPerLine = jobSpec.getBasesPerLine();

    List<BEDFeature> features;

    if(jobSpec.getFeatures()!= null){
      features = FeatureAdapter.toBEDFeatures(jobSpec.getFeatures());
    } else {
      try (InputStream fileStream = switch (jobSpec.getUploadMethod()) {
        case FILE -> new ByteArrayInputStream(jobSpec.getFeaturesStr().getBytes(StandardCharsets.UTF_8));
        case URL -> new URL(jobSpec.getFeaturesUrl()).openStream();
      }) {
      features = switch (fileFormat) {
        case BED -> FeatureAdapter.readBed(fileStream, jobSpec.getStartOffset());
        case GFF3 -> FeatureAdapter.readGff3AndConvertToBed(fileStream);
      };
      } catch (IOException e){
        throw new RuntimeException("Unable to complete file processing", e);
      }
    }

    // Validate async post-processing sequence limits
    if (jobSpec.getPostProcess() != null) {
      SrtServiceOptions options = org.veupathdb.service.sr.Main.getOptions();

      switch (jobSpec.getPostProcess()) {
        case MSA -> {
          int maxSequences = options.getMsaAsyncMaxSequences();
          if (features.size() > maxSequences) {
            return JobResult.failure(
              "Too many sequences for asynchronous MSA request (" + features.size() + " sequences). " +
              "Maximum allowed is " + maxSequences + "."
            );
          }
        }
        case GENETREE -> {
          // Minimum 3 sequences required for gene tree generation
          if (features.size() < 3) {
            return JobResult.failure(
              "Too few sequences for gene tree generation (" + features.size() + " sequences). " +
              "Minimum required is 3 sequences."
            );
          }
          int maxSequences = options.getGeneTreeAsyncMaxSequences();
          if (features.size() > maxSequences) {
            return JobResult.failure(
              "Too many sequences for asynchronous gene tree request (" + features.size() + " sequences). " +
              "Maximum allowed is " + maxSequences + "."
            );
          }
        }
      }
    }

    var stream = ReferenceDAOFactory.get(sequenceType).validateAndPrepareResponse(features, deflineFormat, basesPerLine);

    // Write FASTA to temp file for post-processing
    File tempFasta;
    try {
      tempFasta = File.createTempFile("job-fasta-", ".fasta");
    } catch (IOException e) {
      return JobResult.failure("Failed to create temp file: " + e.getMessage());
    }

    try {
      try (FileOutputStream fos = new FileOutputStream(tempFasta)) {
        stream.accept(fos);
      } catch (IOException e) {
        return JobResult.failure("Failed to write FASTA to temp file: " + e.getMessage());
      }

      // Check if post-processing is requested
      if (jobSpec.getPostProcess() != null) {
        return executeWithPostProcessing(jobContext, jobSpec, tempFasta, features);
      } else {
        // No post-processing - write FASTA directly
        byte[] fastaContent;
        try {
          fastaContent = Files.readAllBytes(tempFasta.toPath());
        } catch (IOException e) {
          return JobResult.failure("Failed to read FASTA from temp file: " + e.getMessage());
        }
        jobContext.getWorkspace().write("output", new String(fastaContent, StandardCharsets.UTF_8));
        return JobResult.success("output");
      }
    } finally {
      tempFasta.delete();
    }
  }

  /**
   * Execute job with post-processing.
   */
  private JobResult executeWithPostProcessing(
      JobContext jobContext,
      SequenceRetrievalSpec jobSpec,
      File tempFasta,
      List<BEDFeature> features) {

    try {
      // Create post-processor
      SrtServiceOptions options = org.veupathdb.service.sr.Main.getOptions();
      PostProcessor processor = PostProcessorFactory.create(
          jobSpec.getPostProcess(),
          jobSpec.getMsaOptions(),
          jobSpec.getGeneTreeOptions(),
          options,
          ProcessingContext.ASYNC
      );

      // Process
      PostProcessResult result = processor.process(tempFasta, features);

      // Write primary output
      jobContext.getWorkspace().write("output", new String(result.getContent(), StandardCharsets.UTF_8));

      // Write additional files (e.g., guide tree)
      Map<String, byte[]> additionalFiles = result.getAdditionalFiles();
      for (Map.Entry<String, byte[]> entry : additionalFiles.entrySet()) {
        jobContext.getWorkspace().write(entry.getKey(),
          new String(entry.getValue(), StandardCharsets.UTF_8));
      }

      // Build list of all output files (primary + additional)
      List<String> outputFiles = new ArrayList<>();
      outputFiles.add("output");
      outputFiles.addAll(additionalFiles.keySet());

      return JobResult.success(outputFiles);
    } catch (IOException e) {
      return JobResult.failure("Post-processing failed: " + e.getMessage());
    } catch (Exception e) {
      return JobResult.failure("Unexpected error during post-processing: " + e.getMessage());
    }
  }
}
