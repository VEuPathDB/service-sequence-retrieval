package org.veupathdb.service.sr.postprocess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MafftExecutorTest {

  @TempDir
  Path tempDir;

  private File inputFile;
  private File outputFile;

  @BeforeEach
  void setUp() throws IOException {
    // Create temp files for testing
    inputFile = tempDir.resolve("input.fasta").toFile();
    Files.writeString(inputFile.toPath(), ">seq1\nATGC\n>seq2\nATGC\n>seq3\nATGC\n");

    outputFile = tempDir.resolve("output.fasta").toFile();
  }

  @Test
  void testMafftExecutorCreation() {
    MafftExecutor executor = new MafftExecutor("/usr/bin/mafft", 300);
    assertNotNull(executor);
  }

  @Test
  void testExecuteWithNonExistentBinary() {
    MafftExecutor executor = new MafftExecutor("/nonexistent/mafft", 300);

    assertThrows(Exception.class, () ->
      executor.execute(inputFile, outputFile, "protein", SequenceStats.of(List.of()))
    );
  }

  @Test
  void testExecuteWithInvalidInputFile() {
    MafftExecutor executor = new MafftExecutor("/usr/bin/mafft", 300);

    File nonExistentInput = tempDir.resolve("nonexistent.fasta").toFile();

    assertThrows(Exception.class, () ->
      executor.execute(nonExistentInput, outputFile, "protein", SequenceStats.of(List.of()))
    );
  }

  @Test
  void testMafftExceptionMessage() {
    MafftExecutor.MafftException exception =
      new MafftExecutor.MafftException("Test error message");

    assertEquals("Test error message", exception.getMessage());
  }

  @Test
  void testMafftExceptionWithCause() {
    IOException cause = new IOException("Original error");
    MafftExecutor.MafftException exception =
      new MafftExecutor.MafftException("Wrapped error", cause);

    assertEquals("Wrapped error", exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testBuildCommandClustalWithGuideTree() {
    MafftExecutor executor = new MafftExecutor("/usr/bin/mafft", 300);
    assertEquals(
        List.of("/usr/bin/mafft", "--auto", "--thread", "4", "--clustalout", "--treeout",
            inputFile.getAbsolutePath()),
        executor.buildCommand(inputFile, "clustal", true));
  }

  @Test
  void testBuildCommandPhylipAndFasta() {
    MafftExecutor executor = new MafftExecutor("/usr/bin/mafft", 300);
    assertTrue(executor.buildCommand(inputFile, "phylip", false).contains("--phylipout"));
    assertEquals(
        List.of("/usr/bin/mafft", "--auto", "--thread", "4", inputFile.getAbsolutePath()),
        executor.buildCommand(inputFile, "fasta", false));
  }

  @Test
  void testBuildCommandRejectsUnsupportedFormat() {
    MafftExecutor executor = new MafftExecutor("/usr/bin/mafft", 300);
    assertThrows(IllegalArgumentException.class, () -> executor.buildCommand(inputFile, "msf", false));
  }

  @Test
  void testStripLeafIndexPrefixes() {
    String mafftTree = "((\n1_SEQ1\n:0.48900,\n2_SEQ2\n:0.48900):0.03285,\n3_SEQ3\n:0.52185);\n";
    assertEquals(
        "((\nSEQ1\n:0.48900,\nSEQ2\n:0.48900):0.03285,\nSEQ3\n:0.52185);\n",
        MafftExecutor.stripLeafIndexPrefixes(mafftTree));
  }

  @Test
  void testStripLeafIndexPrefixesKeepsRestOfName() {
    assertEquals("(A_1:0.1,12_B:0.2);", MafftExecutor.stripLeafIndexPrefixes("(1_A_1:0.1,2_12_B:0.2);"));
  }
}
