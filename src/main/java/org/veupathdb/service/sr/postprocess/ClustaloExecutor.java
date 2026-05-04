package org.veupathdb.service.sr.postprocess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gusdb.fgputil.runtime.RuntimeUtil;
import org.veupathdb.service.sr.SrtServiceOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Executor for running clustal-omega (clustalo) binary.
 *
 * Handles process execution, output capture, timeout enforcement, and error handling.
 */
public class ClustaloExecutor {

  private static final Logger LOG = LogManager.getLogger(ClustaloExecutor.class);

  private final String clustaloBinaryPath;
  private final int timeoutSeconds;

  /**
   * Create a ClustaloExecutor with the specified configuration.
   *
   * @param clustaloBinaryPath Path to the clustalo binary executable
   * @param timeoutSeconds Timeout in seconds for clustalo execution
   */
  public ClustaloExecutor(String clustaloBinaryPath, int timeoutSeconds) {
    this.clustaloBinaryPath = clustaloBinaryPath;
    this.timeoutSeconds = timeoutSeconds;
  }

  /**
   * Execute clustalo with the given parameters.
   *
   * @param inputFile Input FASTA file
   * @param outputFile Output file for alignment
   * @param outputFormat Output format (e.g., "clustal", "fasta", "phylip")
   * @param guideTreeFile Optional guide tree output file (null if not needed)
   * @throws IOException if execution fails
   * @throws ClustaloException if clustalo returns non-zero exit code or times out
   */
  public void execute(
      File inputFile,
      File outputFile,
      String outputFormat,
      File guideTreeFile
  ) throws IOException, ClustaloException {

    List<String> command = new ArrayList<>();
    command.add(clustaloBinaryPath);
    command.add("--infile=" + inputFile.getAbsolutePath());
    command.add("--outfile=" + outputFile.getAbsolutePath());
    command.add("--outfmt=" + outputFormat);
    command.add("--force"); // Overwrite output files
    command.add("-v"); // Verbose output

    // Add guide tree output if requested
    if (guideTreeFile != null) {
      command.add("--guidetree-out=" + guideTreeFile.getAbsolutePath());
    }

    LOG.info("Executing clustalo: " + String.join(" ", command));

    StringBuilder output = new StringBuilder();
    Optional<Integer> exitValue = RuntimeUtil.executeSubprocess(
        command,
        Collections.emptyMap(),                   // no extra environment
        Optional.empty(),                         // input is read from file, not stdin
        line -> {
          LOG.debug("clustalo: " + line);         // log stdout/stderr at debug level
          output.append(line).append("\n");       // collect output for logging on error
        },
        Optional.empty(),                         // join stdout/stderr
        Optional.of(Duration.of(                  // timeout the subprocess
            timeoutSeconds, ChronoUnit.SECONDS))
    );

    if (exitValue.isEmpty()) {
      throw new ClustaloException("Clustalo execution timed out after " + timeoutSeconds + " seconds");
    }
    if (exitValue.get() != 0) {
      throw new ClustaloException("Clustalo failed with exit code " + exitValue.get() + ". Output:\n" + output);
    }
    LOG.info("Clustalo completed successfully");
  }

  /**
   * Exception thrown when clustalo execution fails.
   */
  public static class ClustaloException extends Exception {
    public ClustaloException(String message) {
      super(message);
    }

    public ClustaloException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
