package org.veupathdb.service.sr.postprocess;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gusdb.fgputil.runtime.RuntimeUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Executor for running fasttree binary.
 *
 * Handles process execution, output capture, timeout enforcement, and error handling.
 */
public class FastTreeExecutor {

  private static final Logger LOG = LogManager.getLogger(FastTreeExecutor.class);

  private final String fastTreeBinaryPath;
  private final int timeoutSeconds;

  /**
   * Create a FastTreeExecutor with the specified configuration.
   *
   * @param fastTreeBinaryPath Path to the fasttree binary executable
   * @param timeoutSeconds Timeout in seconds for fasttree execution
   */
  public FastTreeExecutor(String fastTreeBinaryPath, int timeoutSeconds) {
    this.fastTreeBinaryPath = fastTreeBinaryPath;
    this.timeoutSeconds = timeoutSeconds;
  }

  /**
   * Execute fasttree to generate a phylogenetic tree from an alignment.
   *
   * @param alignmentFile Input alignment file (from mafft)
   * @param outputFile Output file for the phylogenetic tree (Newick format)
   * @throws IOException if execution fails
   * @throws FastTreeException if fasttree returns non-zero exit code or times out
   */
  public void execute(File alignmentFile, File outputFile) throws IOException, FastTreeException {

    List<String> command = new ArrayList<>();
    command.add(fastTreeBinaryPath);
    command.add("-mlnni");
    command.add("4");

    LOG.info("Executing fasttree: " + String.join(" ", command));

    StringBuilder stderrOutput = new StringBuilder();
    Optional<Integer> exitValue = RuntimeUtil.executeSubprocess(
        command,
        Collections.emptyMap(),                   // no extra environment
        Optional.of(alignmentFile),               // read from alignment file
        line -> {
          LOG.debug("fasttree: " + line);         // log stderr at debug level
          stderrOutput.append(line).append("\n"); // collect output for logging on error
        },
        Optional.of(outputFile),                  // write tree to output file from stdout
        Optional.of(Duration.of(                  // timeout the subprocess
            timeoutSeconds, ChronoUnit.SECONDS))
    );

    if (exitValue.isEmpty()) {
      throw new FastTreeException("FastTree execution timed out after " + timeoutSeconds + " seconds");
    }
    if (exitValue.get() != 0) {
      throw new FastTreeException("FastTree failed with exit code " + exitValue.get() + ". Error output:\n" + stderrOutput);
    }
    LOG.info("FastTree completed successfully");
  }

  /**
   * Exception thrown when fasttree execution fails.
   */
  public static class FastTreeException extends Exception {
    public FastTreeException(String message) {
      super(message);
    }

    public FastTreeException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
