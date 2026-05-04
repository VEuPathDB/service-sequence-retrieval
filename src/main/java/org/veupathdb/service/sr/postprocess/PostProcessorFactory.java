package org.veupathdb.service.sr.postprocess;

import org.veupathdb.service.sr.SrtServiceOptions;
import org.veupathdb.service.sr.generated.model.*;
import org.veupathdb.service.sr.postprocess.genetree.GeneTreeProcessor;
import org.veupathdb.service.sr.postprocess.msa.MsaProcessor;

/**
 * Factory for creating PostProcessor instances based on type and options.
 */
public class PostProcessorFactory {

  /**
   * Create a PostProcessor for the given type and options.
   *
   * @param postProcessType The type of post-processing
   * @param msaOptions Options for MSA (required if type is MSA)
   * @param geneTreeOptions Options for GENETREE (required if type is GENETREE)
   * @param options Application configuration
   * @param context Processing context (SYNC or ASYNC) - affects timeout and resource limits
   * @return A PostProcessor instance
   * @throws IllegalArgumentException if the type/options combination is invalid
   */
  public static PostProcessor create(
      PostProcessType postProcessType,
      MsaOptions msaOptions,
      GeneTreeOptions geneTreeOptions,
      SrtServiceOptions options,
      ProcessingContext context
  ) {
    if (postProcessType == null) {
      throw new IllegalArgumentException("postProcessType cannot be null");
    }

    return switch (postProcessType) {
      case MSA -> {
        if (msaOptions == null) {
          throw new IllegalArgumentException(
              "msaOptions required when postProcess is MSA");
        }
        yield new MsaProcessor(msaOptions, options, context);
      }
      case GENETREE -> {
        if (geneTreeOptions == null) {
          throw new IllegalArgumentException(
              "geneTreeOptions required when postProcess is GENETREE");
        }
        yield new GeneTreeProcessor(geneTreeOptions, options, context);
      }
    };
  }
}
