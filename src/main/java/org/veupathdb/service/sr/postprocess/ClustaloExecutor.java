package org.veupathdb.service.sr.postprocess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.veupathdb.service.sr.AsyncOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
   * @param additionalArgs Optional additional arguments
   * @throws IOException if execution fails
   * @throws ClustaloException if clustalo returns non-zero exit code or times out
   */
  public void execute(
      File inputFile,
      File outputFile,
      String outputFormat,
      File guideTreeFile,
      String... additionalArgs
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

    // Add any additional arguments
    for (String arg : additionalArgs) {
      command.add(arg);
    }

    LOG.info("Executing clustalo: " + String.join(" ", command));

    ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectErrorStream(true); // Merge stderr into stdout

    Process process = pb.start();

    // Capture output in separate thread
    StringBuilder output = new StringBuilder();
    Thread outputReader = new Thread(() -> {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          output.append(line).append("\n");
          LOG.debug("clustalo: " + line);
        }
      } catch (IOException e) {
        LOG.warn("Error reading clustalo output", e);
      }
    });
    outputReader.start();

    // Wait for process with timeout
    boolean completed;
    try {
      completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      process.destroyForcibly();
      throw new ClustaloException("Clustalo execution interrupted", e);
    }

    if (!completed) {
      process.destroyForcibly();
      throw new ClustaloException(
        "Clustalo execution timed out after " + timeoutSeconds + " seconds");
    }

    // Wait for output reader to finish
    try {
      outputReader.join(5000);
    } catch (InterruptedException e) {
      LOG.warn("Output reader thread interrupted", e);
    }

    int exitCode = process.exitValue();
    if (exitCode != 0) {
      throw new ClustaloException(
        "Clustalo failed with exit code " + exitCode + ". Output:\n" + output);
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
