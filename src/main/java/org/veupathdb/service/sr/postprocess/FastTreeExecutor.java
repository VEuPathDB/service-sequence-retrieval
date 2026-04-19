package org.veupathdb.service.sr.postprocess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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

    ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectInput(alignmentFile); // Read from alignment file
    pb.redirectOutput(outputFile); // Write tree to output file
    pb.redirectErrorStream(false); // Keep stderr separate for logging

    Process process = pb.start();

    // Capture stderr for logging
    StringBuilder stderrOutput = new StringBuilder();
    Thread stderrReader = new Thread(() -> {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          stderrOutput.append(line).append("\n");
          LOG.debug("fasttree: " + line);
        }
      } catch (IOException e) {
        LOG.warn("Error reading fasttree stderr", e);
      }
    });
    stderrReader.start();

    // Wait for process with timeout
    boolean completed;
    try {
      completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      process.destroyForcibly();
      throw new FastTreeException("FastTree execution interrupted", e);
    }

    if (!completed) {
      process.destroyForcibly();
      throw new FastTreeException(
        "FastTree execution timed out after " + timeoutSeconds + " seconds");
    }

    // Wait for stderr reader to finish
    try {
      stderrReader.join(1000);
    } catch (InterruptedException e) {
      LOG.warn("Interrupted while waiting for stderr reader", e);
    }

    int exitCode = process.exitValue();
    if (exitCode != 0) {
      throw new FastTreeException(
        "FastTree failed with exit code " + exitCode + ". Error output:\n" + stderrOutput.toString());
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
