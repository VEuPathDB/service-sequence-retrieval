package org.veupathdb.service.sr.postprocess.genetree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.veupathdb.service.sr.SrtServiceOptions;
import org.veupathdb.service.sr.generated.model.GeneTreeOptions;
import org.veupathdb.service.sr.postprocess.FastTreeExecutor;
import org.veupathdb.service.sr.postprocess.MafftExecutor;
import org.veupathdb.service.sr.postprocess.PostProcessResult;
import org.veupathdb.service.sr.postprocess.PostProcessor;
import org.veupathdb.service.sr.postprocess.ProcessingContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Post-processor for gene tree generation.
 *
 * Uses mafft for multiple sequence alignment followed by fasttree for phylogenetic tree construction.
 * Always outputs Newick format trees as plain text.
 */
public class GeneTreeProcessor implements PostProcessor {

  private static final Logger LOG = LogManager.getLogger(GeneTreeProcessor.class);

  private final GeneTreeOptions options;
  private final MafftExecutor mafftExecutor;
  private final FastTreeExecutor fastTreeExecutor;

  /**
   * Production constructor.
   *
   * @param options Gene tree-specific options
   * @param config Application configuration
   * @param context Processing context (SYNC or ASYNC) - determines timeout
   */
  public GeneTreeProcessor(GeneTreeOptions options, SrtServiceOptions config, ProcessingContext context) {
    this(options,
        new MafftExecutor(
            config.getMafftBinaryPath(),
            context == ProcessingContext.ASYNC
                ? config.getGeneTreeAsyncTimeoutSeconds()
                : config.getGeneTreeSyncTimeoutSeconds()
        ),
        new FastTreeExecutor(
            config.getFastTreeBinaryPath(),
            context == ProcessingContext.ASYNC
                ? config.getGeneTreeAsyncTimeoutSeconds()
                : config.getGeneTreeSyncTimeoutSeconds()
        )
    );
  }

  /**
   * Constructor for testing with injectable executors.
   */
  public GeneTreeProcessor(GeneTreeOptions options, MafftExecutor mafftExecutor, FastTreeExecutor fastTreeExecutor) {
    this.options = options;
    this.mafftExecutor = mafftExecutor;
    this.fastTreeExecutor = fastTreeExecutor;
  }

  @Override
  public PostProcessResult process(File fastaInput) throws IOException {
    // Create temp files for intermediate alignment and final tree output
    File alignmentFile = File.createTempFile("alignment-", ".fasta");
    File treeFile = File.createTempFile("tree-", ".newick");

    try {
      // Step 1: Run mafft for multiple sequence alignment
      LOG.info("Running mafft for multiple sequence alignment");
      try {
        mafftExecutor.execute(fastaInput, alignmentFile);
      } catch (MafftExecutor.MafftException e) {
        throw new IOException("Mafft execution failed", e);
      }

      // Step 2: Run fasttree to generate phylogenetic tree
      LOG.info("Running fasttree to generate phylogenetic tree");
      try {
        fastTreeExecutor.execute(alignmentFile, treeFile);
      } catch (FastTreeExecutor.FastTreeException e) {
        throw new IOException("FastTree execution failed", e);
      }

      // Read tree content
      byte[] treeContent = Files.readAllBytes(treeFile.toPath());

      LOG.info("Gene tree generation completed successfully");

      // Return tree as plain text (Newick format)
      return new PostProcessResult("text/plain", treeContent);

    } finally {
      // Clean up temp files
      alignmentFile.delete();
      treeFile.delete();
    }
  }
}
