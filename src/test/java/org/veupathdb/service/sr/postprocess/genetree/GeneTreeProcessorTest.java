package org.veupathdb.service.sr.postprocess.genetree;

import htsjdk.tribble.bed.BEDFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.veupathdb.service.sr.generated.model.GeneTreeOptions;
import org.veupathdb.service.sr.generated.model.GeneTreeOptionsImpl;
import org.veupathdb.service.sr.postprocess.FastTreeExecutor;
import org.veupathdb.service.sr.postprocess.MafftExecutor;
import org.veupathdb.service.sr.postprocess.PostProcessResult;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeneTreeProcessorTest {

  @TempDir
  Path tempDir;

  private MafftExecutor mockMafftExecutor;
  private FastTreeExecutor mockFastTreeExecutor;
  private File testInputFasta;
  private List<BEDFeature> emptyFeatures;

  @BeforeEach
  void setUp() throws IOException {
    mockMafftExecutor = mock(MafftExecutor.class);
    mockFastTreeExecutor = mock(FastTreeExecutor.class);
    emptyFeatures = Collections.emptyList();

    // Create test input FASTA
    testInputFasta = tempDir.resolve("test-input.fasta").toFile();
    Files.writeString(testInputFasta.toPath(),
      ">seq1\nATGCATGC\n>seq2\nATGCTTGC\n>seq3\nATGCGGGC\n");
  }

  @Test
  void testProcessGeneTree() throws Exception {
    GeneTreeOptions options = new GeneTreeOptionsImpl();

    // Mock mafft execution to create alignment file
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      Files.writeString(outputFile.toPath(),
        ">seq1\nATGCATGC\n>seq2\nATGCTTGC\n>seq3\nATGCGGGC\n");
      return null;
    }).when(mockMafftExecutor).execute(
        any(File.class),
        any(File.class)
    );

    // Mock fasttree execution to create tree file
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      Files.writeString(outputFile.toPath(),
        "(seq1:0.5,seq2:0.3,seq3:0.2);");
      return null;
    }).when(mockFastTreeExecutor).execute(
        any(File.class),
        any(File.class)
    );

    GeneTreeProcessor processor = new GeneTreeProcessor(
      options, mockMafftExecutor, mockFastTreeExecutor);
    PostProcessResult result = processor.process(testInputFasta, emptyFeatures);

    // Verify plain text output (Newick format)
    assertEquals("text/plain", result.getContentType());
    String output = new String(result.getContent(), StandardCharsets.UTF_8);
    assertTrue(output.contains("seq1"));
    assertTrue(output.contains("seq2"));
    assertTrue(output.contains("seq3"));

    // Verify both executors were called
    verify(mockMafftExecutor).execute(eq(testInputFasta), any(File.class));
    verify(mockFastTreeExecutor).execute(any(File.class), any(File.class));
  }

  @Test
  void testProcessThrowsExceptionOnMafftFailure() throws Exception {
    GeneTreeOptions options = new GeneTreeOptionsImpl();

    // Mock mafft to throw exception
    doThrow(new MafftExecutor.MafftException("Mafft failed"))
        .when(mockMafftExecutor).execute(any(File.class), any(File.class));

    GeneTreeProcessor processor = new GeneTreeProcessor(
      options, mockMafftExecutor, mockFastTreeExecutor);

    assertThrows(IOException.class, () -> processor.process(testInputFasta, emptyFeatures));

    // Verify mafft was called but fasttree was not
    verify(mockMafftExecutor).execute(eq(testInputFasta), any(File.class));
    verify(mockFastTreeExecutor, never()).execute(any(File.class), any(File.class));
  }

  @Test
  void testProcessThrowsExceptionOnFastTreeFailure() throws Exception {
    GeneTreeOptions options = new GeneTreeOptionsImpl();

    // Mock mafft execution to succeed
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      Files.writeString(outputFile.toPath(),
        ">seq1\nATGCATGC\n>seq2\nATGCTTGC\n>seq3\nATGCGGGC\n");
      return null;
    }).when(mockMafftExecutor).execute(any(File.class), any(File.class));

    // Mock fasttree to throw exception
    doThrow(new FastTreeExecutor.FastTreeException("FastTree failed"))
        .when(mockFastTreeExecutor).execute(any(File.class), any(File.class));

    GeneTreeProcessor processor = new GeneTreeProcessor(
      options, mockMafftExecutor, mockFastTreeExecutor);

    assertThrows(IOException.class, () -> processor.process(testInputFasta, emptyFeatures));

    // Verify both executors were called
    verify(mockMafftExecutor).execute(eq(testInputFasta), any(File.class));
    verify(mockFastTreeExecutor).execute(any(File.class), any(File.class));
  }
}
