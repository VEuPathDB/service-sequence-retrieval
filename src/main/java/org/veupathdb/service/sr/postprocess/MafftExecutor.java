package org.veupathdb.service.sr.postprocess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gusdb.fgputil.Timer;
import org.gusdb.fgputil.runtime.RuntimeUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

/**
 * Executor for running mafft binary.
 *
 * Handles process execution, output capture, timeout enforcement, and error handling.
 */
public class MafftExecutor {

  private static final Logger LOG = LogManager.getLogger(MafftExecutor.class);

  private static final int THREADS = 4;
  private static final int NAME_LENGTH_PADDING = 3;
  private static final Pattern LEAF_INDEX_PREFIX = Pattern.compile("(?<=[(,])(\\s*)\\d+_");

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
   * Execute mafft to perform multiple sequence alignment for the GENETREE pipeline.
   * Uses {@code --retree 2 --amino} rather than {@code --auto} to speed up alignment.
   *
   * @param inputFile Input FASTA file
   * @param outputFile Output file for alignment
   * @param sequenceType Sequence type being aligned, for logging
   * @param stats Sequence count/length stats for the sequences being aligned, for logging
   * @throws IOException if execution fails
   * @throws MafftException if mafft returns non-zero exit code or times out
   */
  public void execute(
      File inputFile,
      File outputFile,
      String sequenceType,
      SequenceStats stats
  ) throws IOException, MafftException {
    List<String> command = new ArrayList<>();
    command.add(mafftBinaryPath);
    command.add("--retree");
    command.add("2");
    command.add("--amino");
    command.add("--quiet");
    command.add("--anysymbol");
    command.add(inputFile.getAbsolutePath());
    run(command, outputFile, sequenceType, stats);
  }

  /**
   * Execute mafft with a chosen output format and optional guide tree output.
   *
   * @param inputFile Input FASTA file
   * @param outputFile Output file for alignment
   * @param outputFormat One of "fasta", "clustal", "phylip"
   * @param guideTreeFile Optional guide tree output file (null if not needed)
   * @param sequenceType Sequence type being aligned, for logging
   * @param stats Sequence count/length stats for the sequences being aligned, for logging
   * @throws IOException if execution fails
   * @throws MafftException if mafft returns non-zero exit code or times out
   */
  public void execute(
      File inputFile,
      File outputFile,
      String outputFormat,
      File guideTreeFile,
      String sequenceType,
      SequenceStats stats
  ) throws IOException, MafftException {
    List<String> command = buildCommand(inputFile, outputFormat, guideTreeFile != null);

    // mafft writes the guide tree next to the input file as "<input>.tree"
    File mafftTreeFile = new File(inputFile.getAbsolutePath() + ".tree");
    try {
      run(command, outputFile, sequenceType, stats);
      if (guideTreeFile != null) {
        if (!mafftTreeFile.exists()) {
          throw new MafftException("Mafft did not produce a guide tree file");
        }
        Files.move(mafftTreeFile.toPath(), guideTreeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        String tree = Files.readString(guideTreeFile.toPath(), StandardCharsets.UTF_8);
        Files.writeString(guideTreeFile.toPath(), stripLeafIndexPrefixes(tree), StandardCharsets.UTF_8);
      }
    } finally {
      Files.deleteIfExists(mafftTreeFile.toPath());
    }
  }

  /**
   * mafft labels guide tree leaves "<input order>_<name>" (e.g. "1_SEQ1"); remove the
   * "<input order>_" prefix so labels match the sequence names, as with clustalo.
   * Leaf labels are the only tokens that directly follow a '(' or ','.
   */
  static String stripLeafIndexPrefixes(String tree) {
    return LEAF_INDEX_PREFIX.matcher(tree).replaceAll("$1");
  }

  List<String> buildCommand(File inputFile, String outputFormat, boolean guideTree) throws IOException {
    List<String> command = new ArrayList<>();
    command.add(mafftBinaryPath);
    command.add("--auto");
    command.add("--quiet");
    command.add("--anysymbol");
    command.add("--preservecase");
    command.add("--thread");
    command.add(String.valueOf(THREADS));
    switch (outputFormat) {
      case "clustal" -> {
        addNameLength(command, inputFile);
        command.add("--clustalout");
      }
      case "phylip" -> {
        addNameLength(command, inputFile);
        command.add("--phylipout");
      }
      case "fasta" -> { }  // mafft's default output
      default -> throw new IllegalArgumentException("Unsupported mafft output format: " + outputFormat);
    }
    if (guideTree) {
      command.add("--treeout");
    }
    command.add(inputFile.getAbsolutePath());
    return command;
  }

  /**
   * mafft truncates sequence names in clustal and phylip output unless told how long they are
   * (clustalo sizes this automatically). Use the longest header line in the input plus some
   * extra whitespace.
   */
  private static void addNameLength(List<String> command, File inputFile) throws IOException {
    int longest = longestNameLength(inputFile);
    if (longest > 0) {
      command.add("--namelength");
      command.add(String.valueOf(longest + NAME_LENGTH_PADDING));
    }
  }

  static int longestNameLength(File fastaFile) throws IOException {
    int longest = 0;
    try (BufferedReader reader = Files.newBufferedReader(fastaFile.toPath(), StandardCharsets.UTF_8)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith(">")) {
          longest = Math.max(longest, line.substring(1).strip().length());
        }
      }
    }
    return longest;
  }

  private void run(
      List<String> command,
      File outputFile,
      String sequenceType,
      SequenceStats stats
  ) throws IOException, MafftException {

    LOG.info("Executing mafft: " + String.join(" ", command));

    Timer timer = new Timer();
    StringBuilder stderrOutput = new StringBuilder();
    Optional<Integer> exitValue = RuntimeUtil.executeSubprocess(
        command,
        Collections.emptyMap(),                   // no extra environment
        Optional.empty(),                         // input is read from file, not stdin
        line -> {
          LOG.trace("mafft: " + line);            // log stderr at trace level
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
      // the output is carried by the exception, which callers log with its cause
      LOG.error("mafft failed: sequenceType=" + sequenceType + " " + stats
          + " exitCode=" + exitValue.get());
      throw new MafftException("Mafft failed with exit code " + exitValue.get() + ". Error output:\n" + stderrOutput);
    }
    LOG.info("mafft execution: sequenceType=" + sequenceType + " " + stats
        + " wallTime=" + timer.getElapsedString());
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
