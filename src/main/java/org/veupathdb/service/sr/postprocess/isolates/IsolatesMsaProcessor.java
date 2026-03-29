package org.veupathdb.service.sr.postprocess.isolates;

import org.veupathdb.service.sr.AsyncOptions;
import org.veupathdb.service.sr.generated.model.IsolatesMsaOptions;
import org.veupathdb.service.sr.postprocess.ClustaloExecutor;
import org.veupathdb.service.sr.postprocess.PostProcessResult;
import org.veupathdb.service.sr.postprocess.PostProcessor;
import org.veupathdb.service.sr.postprocess.ProcessingContext;

import java.io.File;
import java.io.IOException;

/**
 * Post-processor for Isolates multiple sequence alignment.
 *
 * TODO: Implementation pending - define business logic and output formats.
 */
public class IsolatesMsaProcessor implements PostProcessor {

  private final IsolatesMsaOptions options;
  private final ClustaloExecutor clustaloExecutor;

  /**
   * Production constructor.
   *
   * @param options MSA-specific options
   * @param config Application configuration
   * @param context Processing context (SYNC or ASYNC) - determines timeout
   */
  public IsolatesMsaProcessor(IsolatesMsaOptions options, AsyncOptions config, ProcessingContext context) {
    this(options, new ClustaloExecutor(
      config.getClustaloBinaryPath(),
      context == ProcessingContext.ASYNC ? config.getClustaloAsyncTimeoutSeconds() : config.getClustaloSyncTimeoutSeconds()
    ));
  }

  /**
   * Constructor for testing with injectable ClustaloExecutor.
   */
  public IsolatesMsaProcessor(IsolatesMsaOptions options, ClustaloExecutor clustaloExecutor) {
    this.options = options;
    this.clustaloExecutor = clustaloExecutor;
  }

  @Override
  public PostProcessResult process(File fastaInput) throws IOException {
    throw new UnsupportedOperationException(
      "IsolatesMSA post-processing is not yet implemented. " +
      "Please define the business logic and output formats for this processor.");
  }
}
