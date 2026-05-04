package org.veupathdb.service.sr.postprocess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FastTreeExecutorTest {

  @TempDir
  Path tempDir;

  private File alignmentFile;
  private File outputFile;

  @BeforeEach
  void setUp() throws IOException {
    // Create temp files for testing
    alignmentFile = tempDir.resolve("alignment.fasta").toFile();
    Files.writeString(alignmentFile.toPath(), ">seq1\nATGC\n>seq2\nATGC\n>seq3\nATGC\n");

    outputFile = tempDir.resolve("tree.newick").toFile();
  }

  @Test
  void testFastTreeExecutorCreation() {
    FastTreeExecutor executor = new FastTreeExecutor("/usr/bin/fasttree", 300);
    assertNotNull(executor);
  }

  @Test
  void testExecuteWithNonExistentBinary() {
    FastTreeExecutor executor = new FastTreeExecutor("/nonexistent/fasttree", 300);

    assertThrows(Exception.class, () ->
      executor.execute(alignmentFile, outputFile)
    );
  }

  @Test
  void testExecuteWithInvalidInputFile() {
    FastTreeExecutor executor = new FastTreeExecutor("/usr/bin/fasttree", 300);

    File nonExistentInput = tempDir.resolve("nonexistent.fasta").toFile();

    assertThrows(Exception.class, () ->
      executor.execute(nonExistentInput, outputFile)
    );
  }

  @Test
  void testFastTreeExceptionMessage() {
    FastTreeExecutor.FastTreeException exception =
      new FastTreeExecutor.FastTreeException("Test error message");

    assertEquals("Test error message", exception.getMessage());
  }

  @Test
  void testFastTreeExceptionWithCause() {
    IOException cause = new IOException("Original error");
    FastTreeExecutor.FastTreeException exception =
      new FastTreeExecutor.FastTreeException("Wrapped error", cause);

    assertEquals("Wrapped error", exception.getMessage());
    assertEquals(cause, exception.getCause());
  }
}
