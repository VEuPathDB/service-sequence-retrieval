package org.veupathdb.service.sr.postprocess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
      executor.execute(inputFile, outputFile)
    );
  }

  @Test
  void testExecuteWithInvalidInputFile() {
    MafftExecutor executor = new MafftExecutor("/usr/bin/mafft", 300);

    File nonExistentInput = tempDir.resolve("nonexistent.fasta").toFile();

    assertThrows(Exception.class, () ->
      executor.execute(nonExistentInput, outputFile)
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
}
