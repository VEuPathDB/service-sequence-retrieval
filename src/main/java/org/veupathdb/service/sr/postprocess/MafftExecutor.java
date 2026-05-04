package org.veupathdb.service.sr.postprocess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gusdb.fgputil.runtime.RuntimeUtil;

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

    StringBuilder stderrOutput = new StringBuilder();
    Optional<Integer> exitValue = RuntimeUtil.executeSubprocess(
        command,
        Collections.emptyMap(),                   // no extra environment
        Optional.empty(),                         // input is read from file, not stdin
        line -> {
          LOG.debug("mafft: " + line);            // log stderr at debug level
          stderrOutput.append(line).append("\n"); // collect output for logging on error
        },
        Optional.of(outputFile),                  // write to output file from stdout
        Optional.of(Duration.of(                  // timeout the subprocess
            timeoutSeconds, ChronoUnit.SECONDS))
    );

    if (exitValue.isEmpty()) {
      throw new MafftException("Mafft execution timed out after " + timeoutSeconds + " seconds");
    }
    if (exitValue.get() != 0) {
      throw new MafftException("Mafft failed with exit code " + exitValue.get() + ". Error output:\n" + stderrOutput);
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
