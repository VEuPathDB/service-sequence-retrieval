package org.veupathdb.service.sr.postprocess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.veupathdb.service.sr.AsyncOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClustaloExecutorTest {

  @TempDir
  Path tempDir;

  private AsyncOptions mockOptions;
  private File inputFile;
  private File outputFile;
  private File guideTreeFile;

  @BeforeEach
  void setUp() throws IOException {
    mockOptions = mock(AsyncOptions.class);
    when(mockOptions.getClustaloBinaryPath()).thenReturn("/usr/bin/clustalo");
    when(mockOptions.getClustaloTimeoutSeconds()).thenReturn(300);

    // Create temp files for testing
    inputFile = tempDir.resolve("input.fasta").toFile();
    Files.writeString(inputFile.toPath(), ">seq1\nATGC\n>seq2\nATGC\n");

    outputFile = tempDir.resolve("output.txt").toFile();
    guideTreeFile = tempDir.resolve("guide.dnd").toFile();
  }

  @Test
  void testClustaloExecutorCreation() {
    ClustaloExecutor executor = new ClustaloExecutor(mockOptions);
    assertNotNull(executor);
  }

  @Test
  void testExecuteWithNonExistentBinary() {
    when(mockOptions.getClustaloBinaryPath()).thenReturn("/nonexistent/clustalo");

    ClustaloExecutor executor = new ClustaloExecutor(mockOptions);

    assertThrows(Exception.class, () ->
      executor.execute(inputFile, outputFile, "clustal", guideTreeFile)
    );
  }

  @Test
  void testExecuteWithInvalidInputFile() {
    ClustaloExecutor executor = new ClustaloExecutor(mockOptions);

    File nonExistentInput = tempDir.resolve("nonexistent.fasta").toFile();

    assertThrows(Exception.class, () ->
      executor.execute(nonExistentInput, outputFile, "clustal", guideTreeFile)
    );
  }

  @Test
  void testClustaloExceptionMessage() {
    ClustaloExecutor.ClustaloException exception =
      new ClustaloExecutor.ClustaloException("Test error message");

    assertEquals("Test error message", exception.getMessage());
  }

  @Test
  void testClustaloExceptionWithCause() {
    IOException cause = new IOException("Original error");
    ClustaloExecutor.ClustaloException exception =
      new ClustaloExecutor.ClustaloException("Wrapped error", cause);

    assertEquals("Wrapped error", exception.getMessage());
    assertEquals(cause, exception.getCause());
  }
}
