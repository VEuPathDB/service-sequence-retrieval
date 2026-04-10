package org.veupathdb.service.sr.postprocess.msa;

import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.veupathdb.service.sr.generated.model.MsaFormat;
import org.veupathdb.service.sr.generated.model.MsaOptions;
import org.veupathdb.service.sr.generated.model.MsaOptionsImpl;
import org.veupathdb.service.sr.postprocess.ClustaloExecutor;
import org.veupathdb.service.sr.postprocess.PostProcessResult;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MsaProcessorTest {

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
    URL inputUrl = classLoader.getResource("org/veupathdb/service/sr/postprocess/msa/test-input.fasta");
    URL clustalUrl = classLoader.getResource("org/veupathdb/service/sr/postprocess/msa/mock-alignment-clustal.txt");
    URL fastaUrl = classLoader.getResource("org/veupathdb/service/sr/postprocess/msa/mock-alignment-fasta.txt");
    URL treeUrl = classLoader.getResource("org/veupathdb/service/sr/postprocess/msa/mock-guidetree.dnd");

    assertNotNull(inputUrl, "Test input FASTA not found");
    assertNotNull(clustalUrl, "Mock clustal alignment not found");
    assertNotNull(fastaUrl, "Mock fasta alignment not found");
    assertNotNull(treeUrl, "Mock guide tree not found");

    testInputFasta = new File(inputUrl.getFile());
    mockAlignmentClustal = new File(clustalUrl.getFile());
    mockAlignmentFasta = new File(fastaUrl.getFile());
    mockGuideTree = new File(treeUrl.getFile());
  }

  @Test
  void testProcessClustalPlainText() throws Exception {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.CLUSTAL);

    // Mock clustalo execution to create output files
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      File guideTreeFile = invocation.getArgument(3);

      Files.copy(mockAlignmentClustal.toPath(), outputFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);
      Files.copy(mockGuideTree.toPath(), guideTreeFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
        any(File.class),
        any(File.class),
        eq("clustal"),
        any(File.class)
    );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");
    PostProcessResult result = processor.process(testInputFasta);

    // Verify plain text output for clustal without metadata
    assertEquals("text/plain", result.getContentType());
    String output = new String(result.getContent(), StandardCharsets.UTF_8);
    assertTrue(output.contains("CLUSTAL"));

    // Verify clustalo was called
    verify(mockExecutor).execute(
        eq(testInputFasta),
        any(File.class),
        eq("clustal"),
        any(File.class)
    );
  }

  @Test
  void testProcessClustalDndHtml() throws Exception {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.CLUSTALDND);

    // Mock clustalo execution
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      File guideTreeFile = invocation.getArgument(3);

      Files.copy(mockAlignmentClustal.toPath(), outputFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);
      Files.copy(mockGuideTree.toPath(), guideTreeFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
        any(File.class),
        any(File.class),
        eq("clustal"),  // clustal_dnd uses clustal format
        any(File.class)
    );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");
    PostProcessResult result = processor.process(testInputFasta);

    // Verify HTML output
    assertEquals("text/html", result.getContentType());
    String html = new String(result.getContent(), StandardCharsets.UTF_8);

    // Check HTML structure
    assertTrue(html.contains("<!DOCTYPE html>"));
    assertTrue(html.contains("<html>"));
    assertTrue(html.contains("<title>Multiple Sequence Alignment</title>"));
    assertTrue(html.contains("<pre>"));
    assertTrue(html.contains("CLUSTAL"));

    // Check for guide tree section
    assertTrue(html.contains("Guide Tree"));
    assertTrue(html.contains(".dnd format"));

    // Verify additional files
    assertTrue(result.getAdditionalFiles().containsKey("guidetree.dnd"));
    String treeContent = new String(result.getAdditionalFiles().get("guidetree.dnd"),
        StandardCharsets.UTF_8);
    assertFalse(treeContent.isEmpty());

    // Verify clustalo was called with clustal format (not clustal_dnd)
    verify(mockExecutor).execute(
        eq(testInputFasta),
        any(File.class),
        eq("clustal"),
        any(File.class)
    );
  }

  @Test
  void testProcessFastaFormat() throws Exception {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.FASTA);

    // Mock clustalo execution
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      File guideTreeFile = invocation.getArgument(3);

      Files.copy(mockAlignmentFasta.toPath(), outputFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);
      Files.copy(mockGuideTree.toPath(), guideTreeFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
        any(File.class),
        any(File.class),
        eq("fasta"),
        any(File.class)
    );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");
    PostProcessResult result = processor.process(testInputFasta);

    // Verify plain text output
    assertEquals("text/plain", result.getContentType());
    String output = new String(result.getContent(), StandardCharsets.UTF_8);
    assertTrue(output.startsWith(">"));

    verify(mockExecutor).execute(
        eq(testInputFasta),
        any(File.class),
        eq("fasta"),
        any(File.class)
    );
  }

  @Test
  void testProcessPhylipFormat() throws Exception {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.PHYLIP);

    // Mock clustalo execution
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      File guideTreeFile = invocation.getArgument(3);

      // Write simple phylip format
      Files.writeString(outputFile.toPath(), "3 100\nseq1  ACGT\nseq2  ACGT\nseq3  ACGT\n");
      Files.copy(mockGuideTree.toPath(), guideTreeFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
        any(File.class),
        any(File.class),
        eq("phylip"),
        any(File.class)
    );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");
    PostProcessResult result = processor.process(testInputFasta);

    // Verify plain text output
    assertEquals("text/plain", result.getContentType());

    verify(mockExecutor).execute(
        eq(testInputFasta),
        any(File.class),
        eq("phylip"),
        any(File.class)
    );
  }

  @Test
  void testMetadataUrlValidationWithClustal() throws Exception {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.CLUSTAL);
    options.setMetadataUrl("https://example.com/metadata.tsv");

    // Mock clustalo execution
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      File guideTreeFile = invocation.getArgument(3);

      Files.copy(mockAlignmentClustal.toPath(), outputFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);
      Files.copy(mockGuideTree.toPath(), guideTreeFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
        ArgumentMatchers.any(),
        ArgumentMatchers.any(),
        ArgumentMatchers.any(),
        ArgumentMatchers.any()
    );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");

    // Should not throw - metadataUrl is allowed with clustal
    // Currently returns plain text (metadata HTML not yet implemented)
    PostProcessResult result = processor.process(testInputFasta);
    assertNotNull(result);
  }

  @Test
  void testMetadataUrlValidationWithFastaThrows() {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.FASTA);
    options.setMetadataUrl("https://example.com/metadata.tsv");

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");

    // Should throw BadRequestException
    Exception exception = assertThrows(BadRequestException.class, () -> {
      processor.process(testInputFasta);
    });

    assertTrue(exception.getMessage().contains("metadataUrl"));
    assertTrue(exception.getMessage().contains("clustal"));
    assertTrue(exception.getMessage().contains("fasta"));
  }

  @Test
  void testMetadataUrlValidationWithClustalDndThrows() {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.CLUSTALDND);
    options.setMetadataUrl("https://example.com/metadata.tsv");

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");

    // Should throw BadRequestException
    Exception exception = assertThrows(BadRequestException.class, () -> {
      processor.process(testInputFasta);
    });

    assertTrue(exception.getMessage().contains("metadataUrl"));
    assertTrue(exception.getMessage().contains("clustal"));
  }

  @Test
  void testClustaloExecutionFailure() throws Exception {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.CLUSTAL);

    // Mock clustalo to throw exception
    doThrow(new ClustaloExecutor.ClustaloException("Clustalo failed with exit code 1"))
        .when(mockExecutor).execute(
            any(File.class),
            any(File.class),
            any(String.class),
            any(File.class)
        );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");

    // Should wrap in IOException
    Exception exception = assertThrows(IOException.class, () -> {
      processor.process(testInputFasta);
    });

    assertTrue(exception.getMessage().contains("Clustalo execution failed"));
  }

  @Test
  void testHtmlEscaping() throws Exception {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.CLUSTALDND);

    // Create alignment with special characters
    String alignmentWithSpecialChars = "CLUSTAL O(1.2.4)\n\n" +
        "seq<1>  ACGT&ACGT  10\n" +
        "seq\"2\"  ACGT'ACGT  10\n";

    // Mock clustalo execution
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      File guideTreeFile = invocation.getArgument(3);

      Files.writeString(outputFile.toPath(), alignmentWithSpecialChars);
      Files.copy(mockGuideTree.toPath(), guideTreeFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
        any(File.class),
        any(File.class),
        eq("clustal"),
        any(File.class)
    );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");
    PostProcessResult result = processor.process(testInputFasta);

    String html = new String(result.getContent(), StandardCharsets.UTF_8);

    // Verify HTML escaping
    assertTrue(html.contains("&lt;"));  // < escaped
    assertTrue(html.contains("&gt;"));  // > escaped
    assertTrue(html.contains("&amp;"));  // & escaped
    assertTrue(html.contains("&quot;") || html.contains("&#39;"));  // quotes escaped
  }

  @Test
  void testConfigurableItolUrl() throws Exception {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.CLUSTALDND);

    String customItolUrl = "https://custom-itol.example.com";

    // Mock clustalo execution
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      File guideTreeFile = invocation.getArgument(3);

      Files.copy(mockAlignmentClustal.toPath(), outputFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);
      Files.copy(mockGuideTree.toPath(), guideTreeFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
        any(File.class),
        any(File.class),
        eq("clustal"),
        any(File.class)
    );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, customItolUrl);
    PostProcessResult result = processor.process(testInputFasta);

    // Note: iTOL upload will fail in tests (no network), but the custom URL
    // should be used in the upload attempt
    String html = new String(result.getContent(), StandardCharsets.UTF_8);

    // Verify HTML was generated (even if iTOL upload failed)
    assertTrue(html.contains("<!DOCTYPE html>"));
    assertTrue(html.contains("Multiple Sequence Alignment"));
  }
}
