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
 * Executor for running mafft binary.
 *
 * Handles process execution, output capture, timeout enforcement, and error handling.
 */
public class MafftExecutor {

  private static final Logger LOG = LogManager.getLogger(MafftExecutor.class);

  private final String mafftBinaryPath;
  private final int timeoutSeconds;

  /**
   * Create a MafftExecutor with the specified configuration.
   *
   * @param mafftBinaryPath Path to the mafft binary executable
   * @param timeoutSeconds Timeout in seconds for mafft execution
   */
  public MafftExecutor(String mafftBinaryPath, int timeoutSeconds) {
    this.mafftBinaryPath = mafftBinaryPath;
    this.timeoutSeconds = timeoutSeconds;
  }

  /**
   * Execute mafft to perform multiple sequence alignment.
   *
   * @param inputFile Input FASTA file
   * @param outputFile Output file for alignment
   * @throws IOException if execution fails
   * @throws MafftException if mafft returns non-zero exit code or times out
   */
  public void execute(File inputFile, File outputFile) throws IOException, MafftException {

    List<String> command = new ArrayList<>();
    command.add(mafftBinaryPath);
    command.add("--auto");
    command.add("--anysymbol");
    command.add(inputFile.getAbsolutePath());

    LOG.info("Executing mafft: " + String.join(" ", command));

    ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectOutput(outputFile); // Redirect stdout to output file
    pb.redirectErrorStream(false); // Keep stderr separate for logging

    Process process = pb.start();

    // Capture stderr for logging
    StringBuilder stderrOutput = new StringBuilder();
    Thread stderrReader = new Thread(() -> {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          stderrOutput.append(line).append("\n");
          LOG.debug("mafft: " + line);
        }
      } catch (IOException e) {
        LOG.warn("Error reading mafft stderr", e);
      }
    });
    stderrReader.start();

    // Wait for process with timeout
    boolean completed;
    try {
      completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      process.destroyForcibly();
      throw new MafftException("Mafft execution interrupted", e);
    }

    if (!completed) {
      process.destroyForcibly();
      throw new MafftException(
        "Mafft execution timed out after " + timeoutSeconds + " seconds");
    }

    // Wait for stderr reader to finish
    try {
      stderrReader.join(1000);
    } catch (InterruptedException e) {
      LOG.warn("Interrupted while waiting for stderr reader", e);
    }

    int exitCode = process.exitValue();
    if (exitCode != 0) {
      throw new MafftException(
        "Mafft failed with exit code " + exitCode + ". Error output:\n" + stderrOutput.toString());
    }

    LOG.info("Mafft completed successfully");
  }

  /**
   * Exception thrown when mafft execution fails.
   */
  public static class MafftException extends Exception {
    public MafftException(String message) {
      super(message);
    }

    public MafftException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
