package org.veupathdb.service.sr.postprocess.orthomcl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.veupathdb.service.sr.generated.model.OrthomclMsaFormat;
import org.veupathdb.service.sr.generated.model.OrthomclMsaOptions;
import org.veupathdb.service.sr.generated.model.OrthomclMsaOptionsImpl;
import org.veupathdb.service.sr.postprocess.ClustaloExecutor;
import org.veupathdb.service.sr.postprocess.PostProcessResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrthomclMsaProcessorTest {

  @TempDir
  Path tempDir;

  private ClustaloExecutor mockExecutor;
  private File testInputFasta;
  private File mockAlignmentClustal;
  private File mockAlignmentFasta;
  private File mockGuideTree;

  @BeforeEach
  void setUp() throws IOException {
    mockExecutor = mock(ClustaloExecutor.class);

    // Load test resources
    ClassLoader classLoader = getClass().getClassLoader();
    testInputFasta = tempDir.resolve("test-input.fasta").toFile();
    Files.copy(
      classLoader.getResourceAsStream("org/veupathdb/service/sr/postprocess/msa/test-input.fasta"),
      testInputFasta.toPath(),
      StandardCopyOption.REPLACE_EXISTING
    );

    // Load mock outputs
    mockAlignmentClustal = tempDir.resolve("mock-alignment-clustal.txt").toFile();
    Files.copy(
      classLoader.getResourceAsStream("org/veupathdb/service/sr/postprocess/msa/mock-alignment-clustal.txt"),
      mockAlignmentClustal.toPath(),
      StandardCopyOption.REPLACE_EXISTING
    );

    mockAlignmentFasta = tempDir.resolve("mock-alignment-fasta.txt").toFile();
    Files.copy(
      classLoader.getResourceAsStream("org/veupathdb/service/sr/postprocess/msa/mock-alignment-fasta.txt"),
      mockAlignmentFasta.toPath(),
      StandardCopyOption.REPLACE_EXISTING
    );

    mockGuideTree = tempDir.resolve("mock-guidetree.dnd").toFile();
    Files.copy(
      classLoader.getResourceAsStream("org/veupathdb/service/sr/postprocess/msa/mock-guidetree.dnd"),
      mockGuideTree.toPath(),
      StandardCopyOption.REPLACE_EXISTING
    );
  }

  @Test
  void testProcessWithClustalFormat() throws Exception {
    // Setup
    OrthomclMsaOptions options = new OrthomclMsaOptionsImpl();
    options.setFormat(OrthomclMsaFormat.CLUSTAL);

    // Mock clustalo execution to create output files
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      File guideTreeFile = invocation.getArgument(3);

      // Copy mock files to the expected output locations
      Files.copy(mockAlignmentClustal.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      Files.copy(mockGuideTree.toPath(), guideTreeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
      any(File.class),  // input
      any(File.class),  // output
      eq("clustal"),    // format
      any(File.class),  // guide tree
      eq("--residuenumber"),
      eq("--output-order=tree-order")
    );

    OrthomclMsaProcessor processor = new OrthomclMsaProcessor(options, mockExecutor);

    // Execute
    PostProcessResult result = processor.process(testInputFasta);

    // Verify
    assertNotNull(result);
    assertEquals("text/html", result.getContentType());

    String html = new String(result.getContent());
    assertTrue(html.contains("<!DOCTYPE html>"));
    assertTrue(html.contains("CLUSTAL O"));
    assertTrue(html.contains(".dnd file"));
    assertTrue(html.contains("seq1"));
    assertTrue(html.contains("seq2"));
    assertTrue(html.contains("seq3"));

    // Verify additional files
    assertTrue(result.getAdditionalFiles().containsKey("guidetree.dnd"));

    // Verify clustalo was called correctly
    verify(mockExecutor).execute(
      any(File.class),
      any(File.class),
      eq("clustal"),
      any(File.class),
      eq("--residuenumber"),
      eq("--output-order=tree-order")
    );
  }

  @Test
  void testProcessWithFastaFormat() throws Exception {
    // Setup
    OrthomclMsaOptions options = new OrthomclMsaOptionsImpl();
    options.setFormat(OrthomclMsaFormat.FASTA);

    // Mock clustalo execution
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      File guideTreeFile = invocation.getArgument(3);

      Files.copy(mockAlignmentFasta.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      Files.copy(mockGuideTree.toPath(), guideTreeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
      any(File.class),
      any(File.class),
      eq("fasta"),
      any(File.class),
      eq("--residuenumber"),
      eq("--output-order=tree-order")
    );

    OrthomclMsaProcessor processor = new OrthomclMsaProcessor(options, mockExecutor);

    // Execute
    PostProcessResult result = processor.process(testInputFasta);

    // Verify - fasta format should return plain text, not HTML
    assertNotNull(result);
    assertEquals("text/plain", result.getContentType());

    String fasta = new String(result.getContent());
    assertTrue(fasta.contains(">seq1"));
    assertTrue(fasta.contains(">seq2"));
    assertTrue(fasta.contains(">seq3"));
    assertFalse(fasta.contains("<!DOCTYPE html>"));
  }

  @Test
  void testProcessWithPhylipFormat() throws Exception {
    // Setup
    OrthomclMsaOptions options = new OrthomclMsaOptionsImpl();
    options.setFormat(OrthomclMsaFormat.PHYLIP);

    // Mock clustalo execution
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      File guideTreeFile = invocation.getArgument(3);

      // Create simple phylip-format output
      String phylipContent = "  3  53\nseq1       ATGCGATCGA TCGATCGATC GATCGATCGA TCGATCGATC GATCGATCGA TCG\nseq2       ATGCGATCGA T-GATCGATC GATCGATCGA TCGATCGATC GATCGATCGA TCG\nseq3       ATGCGATCGA TCGATCGAT- GATCGATCGA TCGATCGATC GATCGATCGA TCG\n";
      Files.writeString(outputFile.toPath(), phylipContent);
      Files.copy(mockGuideTree.toPath(), guideTreeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
      any(File.class),
      any(File.class),
      eq("phylip"),
      any(File.class),
      eq("--residuenumber"),
      eq("--output-order=tree-order")
    );

    OrthomclMsaProcessor processor = new OrthomclMsaProcessor(options, mockExecutor);

    // Execute
    PostProcessResult result = processor.process(testInputFasta);

    // Verify
    assertNotNull(result);
    assertEquals("text/plain", result.getContentType());

    String phylip = new String(result.getContent());
    assertTrue(phylip.contains("seq1"));
  }

  @Test
  void testClustaloExecutionFailure() throws Exception {
    // Setup
    OrthomclMsaOptions options = new OrthomclMsaOptionsImpl();
    options.setFormat(OrthomclMsaFormat.CLUSTAL);

    // Mock clustalo execution to throw exception
    doThrow(new ClustaloExecutor.ClustaloException("Clustalo failed"))
      .when(mockExecutor).execute(
        any(File.class),
        any(File.class),
        any(String.class),
        any(File.class),
        any(String.class),
        any(String.class)
      );

    OrthomclMsaProcessor processor = new OrthomclMsaProcessor(options, mockExecutor);

    // Execute and expect exception
    assertThrows(IOException.class, () -> processor.process(testInputFasta));
  }

  @Test
  void testTreeDataProcessing() throws Exception {
    // Setup
    OrthomclMsaOptions options = new OrthomclMsaOptionsImpl();
    options.setFormat(OrthomclMsaFormat.CLUSTAL);

    // Create test tree with colons
    String testTree = "(seq1:0.1,seq2:0.2,(seq3:0.15):0.05);";
    File testTreeFile = tempDir.resolve("test-tree.dnd").toFile();
    Files.writeString(testTreeFile.toPath(), testTree);

    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      File guideTreeFile = invocation.getArgument(3);

      Files.copy(mockAlignmentClustal.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      Files.copy(testTreeFile.toPath(), guideTreeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
      any(File.class),
      any(File.class),
      eq("clustal"),
      any(File.class),
      eq("--residuenumber"),
      eq("--output-order=tree-order")
    );

    OrthomclMsaProcessor processor = new OrthomclMsaProcessor(options, mockExecutor);

    // Execute
    PostProcessResult result = processor.process(testInputFasta);

    // Verify tree data is included
    String html = new String(result.getContent());
    assertTrue(html.contains(testTree));

    // Verify guide tree file is in additional files
    byte[] treeData = result.getAdditionalFiles().get("guidetree.dnd");
    assertNotNull(treeData);
    String treeContent = new String(treeData);
    assertTrue(treeContent.contains(testTree));
  }
}
