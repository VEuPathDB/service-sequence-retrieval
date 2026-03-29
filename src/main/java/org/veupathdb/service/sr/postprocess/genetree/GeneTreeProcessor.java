package org.veupathdb.service.sr.postprocess.genetree;

import org.veupathdb.service.sr.AsyncOptions;
import org.veupathdb.service.sr.generated.model.GeneTreeOptions;
import org.veupathdb.service.sr.postprocess.ClustaloExecutor;
import org.veupathdb.service.sr.postprocess.PostProcessResult;
import org.veupathdb.service.sr.postprocess.PostProcessor;
import org.veupathdb.service.sr.postprocess.ProcessingContext;

import java.io.File;
import java.io.IOException;

/**
 * Post-processor for gene tree generation.
 *
 * TODO: Implementation pending - define business logic and output formats.
 */
public class GeneTreeProcessor implements PostProcessor {

  private final GeneTreeOptions options;
  private final ClustaloExecutor clustaloExecutor;

  /**
   * Production constructor.
   *
   * @param options Gene tree-specific options
   * @param config Application configuration
   * @param context Processing context (SYNC or ASYNC) - determines timeout
   */
  public GeneTreeProcessor(GeneTreeOptions options, AsyncOptions config, ProcessingContext context) {
    this(options, new ClustaloExecutor(
      config.getClustaloBinaryPath(),
      context == ProcessingContext.ASYNC ? config.getClustaloAsyncTimeoutSeconds() : config.getClustaloSyncTimeoutSeconds()
    ));
  }

  /**
   * Constructor for testing with injectable ClustaloExecutor.
   */
  public GeneTreeProcessor(GeneTreeOptions options, ClustaloExecutor clustaloExecutor) {
    this.options = options;
    this.clustaloExecutor = clustaloExecutor;
  }

  @Override
  public PostProcessResult process(File fastaInput) throws IOException {
    throw new UnsupportedOperationException(
      "GeneTree post-processing is not yet implemented. " +
      "Please define the business logic and output formats for this processor.");
  }
}
