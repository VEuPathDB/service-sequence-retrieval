package org.veupathdb.service.sr.postprocess;

import org.veupathdb.service.sr.AsyncOptions;
import org.veupathdb.service.sr.generated.model.*;
import org.veupathdb.service.sr.postprocess.orthomcl.OrthomclMsaProcessor;
import org.veupathdb.service.sr.postprocess.isolates.IsolatesMsaProcessor;
import org.veupathdb.service.sr.postprocess.genetree.GeneTreeProcessor;

/**
 * Factory for creating PostProcessor instances based on type and options.
 */
public class PostProcessorFactory {

  /**
   * Create a PostProcessor for the given type and options.
   *
   * @param postProcessType The type of post-processing
   * @param orthomclMsaOptions Options for orthomclMSA (required if type is ORTHOMCL_MSA)
   * @param isolatesMsaOptions Options for isolatesMSA (required if type is ISOLATES_MSA)
   * @param geneTreeOptions Options for geneTree (required if type is GENE_TREE)
   * @param options Application configuration
   * @return A PostProcessor instance
   * @throws IllegalArgumentException if the type/options combination is invalid
   */
  public static PostProcessor create(
      PostProcessType postProcessType,
      OrthomclMsaOptions orthomclMsaOptions,
      IsolatesMsaOptions isolatesMsaOptions,
      GeneTreeOptions geneTreeOptions,
      AsyncOptions options
  ) {
    if (postProcessType == null) {
      throw new IllegalArgumentException("postProcessType cannot be null");
    }

    return switch (postProcessType) {
      case ORTHOMCLMSA -> {
        if (orthomclMsaOptions == null) {
          throw new IllegalArgumentException(
            "orthomclMsaOptions required when postProcess is orthomclMSA");
        }
        yield new OrthomclMsaProcessor(orthomclMsaOptions, options);
      }
      case ISOLATESMSA -> {
        if (isolatesMsaOptions == null) {
          throw new IllegalArgumentException(
            "isolatesMsaOptions required when postProcess is isolatesMSA");
        }
        yield new IsolatesMsaProcessor(isolatesMsaOptions, options);
      }
      case GENETREE -> {
        if (geneTreeOptions == null) {
          throw new IllegalArgumentException(
            "geneTreeOptions required when postProcess is geneTree");
        }
        yield new GeneTreeProcessor(geneTreeOptions, options);
      }
    };
  }
}
