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
  private static final String DNASEQ_TYPE = "dnaseq";
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
   * Execute mafft to perform multiple sequence alignment.
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
    command.add("--auto");
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
    // dnaseq IDs are "<strain>:<location>", with the same location on every sequence.
    // Drop the location from the names mafft sees so alignment and tree labels are the strain.
    File mafftInput = inputFile;
    boolean tempInput = DNASEQ_TYPE.equalsIgnoreCase(sequenceType);
    if (tempInput) {
      mafftInput = File.createTempFile("mafft-input-", ".fasta");
    }

    // mafft writes the guide tree next to the input file as "<input>.tree"
    File mafftTreeFile = new File(mafftInput.getAbsolutePath() + ".tree");
    try {
      if (tempInput) {
        stripCommonLocationSuffix(inputFile, mafftInput);
      }
      List<String> command = buildCommand(mafftInput, outputFormat, guideTreeFile != null);
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
      if (tempInput) {
        Files.deleteIfExists(mafftInput.toPath());
      }
    }
  }

  /**
   * Write a copy of a FASTA file with the location removed from each sequence ID. IDs look like
   * "strain:location" (e.g. "OHP111:Pf3D7_11_v3:1282966-1306696:f") where the location is shared
   * by every sequence, so it is found as the longest common ID suffix beginning at a ':'.
   * Strain names may themselves contain ':'. Text after the ID on a header line is kept.
   */
  static void stripCommonLocationSuffix(File fastaIn, File fastaOut) throws IOException {
    List<String> lines = Files.readAllLines(fastaIn.toPath(), StandardCharsets.UTF_8);
    List<String> ids = new ArrayList<>();
    for (String line : lines) {
      if (line.startsWith(">")) {
        ids.add(headerId(line));
      }
    }
    String suffix = commonLocationSuffix(ids);
    List<String> out = new ArrayList<>(lines.size());
    for (String line : lines) {
      if (line.startsWith(">") && !suffix.isEmpty()) {
        String id = headerId(line);
        line = ">" + id.substring(0, id.length() - suffix.length()) + line.substring(1 + id.length());
      }
      out.add(line);
    }
    Files.write(fastaOut.toPath(), out, StandardCharsets.UTF_8);
  }

  private static String headerId(String headerLine) {
    int end = 1;
    while (end < headerLine.length() && !Character.isWhitespace(headerLine.charAt(end))) {
      end++;
    }
    return headerLine.substring(1, end);
  }

  /**
   * @return the longest suffix shared by all IDs that starts with ':' and leaves every ID
   *     non-empty, or "" if there is none (including when there are fewer than two IDs)
   */
  static String commonLocationSuffix(List<String> ids) {
    if (ids.size() < 2) {
      return "";
    }
    String first = ids.get(0);
    int common = first.length();
    for (String id : ids) {
      int n = 0;
      while (n < common && n < id.length()
          && id.charAt(id.length() - 1 - n) == first.charAt(first.length() - 1 - n)) {
        n++;
      }
      common = n;
    }
    String suffix = first.substring(first.length() - common);
    int colon = suffix.indexOf(':');
    if (colon < 0) {
      return "";
    }
    suffix = suffix.substring(colon);
    for (String id : ids) {
      if (id.length() <= suffix.length()) {
        return "";
      }
    }
    return suffix;
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
   * (clustalo sizes this automatically). Use the longest header line in the input.
   */
  private static void addNameLength(List<String> command, File inputFile) throws IOException {
    int longest = longestNameLength(inputFile);
    if (longest > 0) {
      command.add("--namelength");
      command.add(String.valueOf(longest));
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
