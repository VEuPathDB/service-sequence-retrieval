package org.veupathdb.service.sr.postprocess.msa;

import htsjdk.tribble.bed.BEDFeature;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

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
  private List<BEDFeature> emptyFeatures;

  @BeforeEach
  void setUp() throws IOException {
    mockExecutor = mock(ClustaloExecutor.class);
    emptyFeatures = Collections.emptyList();

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

    // Mock clustalo execution to create output files (no guide tree for plain clustal)
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);

      Files.copy(mockAlignmentClustal.toPath(), outputFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
        any(File.class),
        any(File.class),
        eq("clustal"),
        isNull()
    );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");
    PostProcessResult result = processor.process(testInputFasta, emptyFeatures);

    // Verify plain text output for clustal without metadata
    assertEquals("text/plain", result.getContentType());

    // Use writeContent for streaming results
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    result.writeContent(baos);
    result.cleanup();
    String output = baos.toString(StandardCharsets.UTF_8);

    assertTrue(output.contains("CLUSTAL"));

    // Verify clustalo was called with null guide tree file
    verify(mockExecutor).execute(
        eq(testInputFasta),
        any(File.class),
        eq("clustal"),
        isNull()
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
    PostProcessResult result = processor.process(testInputFasta, emptyFeatures);

    // Verify HTML output
    assertEquals("text/html", result.getContentType());

    // Use writeContent for streaming results
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    result.writeContent(baos);
    result.cleanup();
    String html = baos.toString(StandardCharsets.UTF_8);

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

    // Mock clustalo execution (no guide tree for fasta format)
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);

      Files.copy(mockAlignmentFasta.toPath(), outputFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
        any(File.class),
        any(File.class),
        eq("fasta"),
        isNull()
    );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");
    PostProcessResult result = processor.process(testInputFasta, emptyFeatures);

    // Verify plain text output
    assertEquals("text/plain", result.getContentType());

    // Use writeContent for streaming results
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    result.writeContent(baos);
    result.cleanup();
    String output = baos.toString(StandardCharsets.UTF_8);

    assertTrue(output.startsWith(">"));

    verify(mockExecutor).execute(
        eq(testInputFasta),
        any(File.class),
        eq("fasta"),
        isNull()
    );
  }

  @Test
  void testProcessPhylipFormat() throws Exception {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.PHYLIP);

    // Mock clustalo execution (no guide tree for phylip format)
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);

      // Write simple phylip format
      Files.writeString(outputFile.toPath(), "3 100\nseq1  ACGT\nseq2  ACGT\nseq3  ACGT\n");

      return null;
    }).when(mockExecutor).execute(
        any(File.class),
        any(File.class),
        eq("phylip"),
        isNull()
    );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");
    PostProcessResult result = processor.process(testInputFasta, emptyFeatures);

    // Verify plain text output
    assertEquals("text/plain", result.getContentType());

    verify(mockExecutor).execute(
        eq(testInputFasta),
        any(File.class),
        eq("phylip"),
        isNull()
    );
  }

  @Test
  void testClustalWithoutMetadata() throws Exception {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.CLUSTAL);
    // No metadataUrl - should return plain text

    // Mock clustalo execution (no guide tree for plain clustal)
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);

      Files.copy(mockAlignmentClustal.toPath(), outputFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);

      return null;
    }).when(mockExecutor).execute(
        ArgumentMatchers.any(),
        ArgumentMatchers.any(),
        ArgumentMatchers.any(),
        isNull()
    );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");

    PostProcessResult result = processor.process(testInputFasta, emptyFeatures);
    assertNotNull(result);
    assertEquals("text/plain", result.getContentType());

    // Use writeContent for streaming results
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    result.writeContent(baos);
    result.cleanup();
    String output = baos.toString(StandardCharsets.UTF_8);

    assertTrue(output.contains("CLUSTAL"));
  }

  @Test
  void testMetadataUrlValidationWithFastaThrows() {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.FASTA);
    options.setMetadataUrl("https://example.com/metadata.tsv");

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");

    // Should throw BadRequestException
    Exception exception = assertThrows(BadRequestException.class, () -> {
      processor.process(testInputFasta, emptyFeatures);
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
      processor.process(testInputFasta, emptyFeatures);
    });

    assertTrue(exception.getMessage().contains("metadataUrl"));
    assertTrue(exception.getMessage().contains("clustal"));
  }

  @Test
  void testClustaloExecutionFailure() throws Exception {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.CLUSTAL);

    // Mock clustalo to throw exception (CLUSTAL format doesn't use guide tree)
    doThrow(new ClustaloExecutor.ClustaloException("Clustalo failed with exit code 1"))
        .when(mockExecutor).execute(
            any(File.class),
            any(File.class),
            any(String.class),
            isNull()
        );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");

    // Should wrap in IOException
    Exception exception = assertThrows(IOException.class, () -> {
      processor.process(testInputFasta, emptyFeatures);
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
    PostProcessResult result = processor.process(testInputFasta, emptyFeatures);

    // Use writeContent for streaming results
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    result.writeContent(baos);
    result.cleanup();
    String html = baos.toString(StandardCharsets.UTF_8);

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
    PostProcessResult result = processor.process(testInputFasta, emptyFeatures);

    // Note: iTOL upload will fail in tests (no network), but the custom URL
    // should be used in the upload attempt
    // Use writeContent for streaming results
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    result.writeContent(baos);
    result.cleanup();
    String html = baos.toString(StandardCharsets.UTF_8);

    // Verify HTML was generated (even if iTOL upload failed)
    assertTrue(html.contains("<!DOCTYPE html>"));
    assertTrue(html.contains("Multiple Sequence Alignment"));
  }

  @Test
  void testClustalWithMetadataFromFileUrl() throws Exception {
    MsaOptions options = new MsaOptionsImpl();
    options.setFormat(MsaFormat.CLUSTAL);

    // Get file URL for test metadata (shared with functional tests)
    ClassLoader classLoader = getClass().getClassLoader();
    URL metadataUrl = classLoader.getResource("veupathdb/service/sequence/reference/test-msa-metadata.tsv");
    assertNotNull(metadataUrl, "Test metadata file not found");

    // Convert to file:// URL
    String fileUrl = metadataUrl.toString();
    options.setMetadataUrl(fileUrl);

    // Create mock features with IDs matching the metadata file (SEQ1, SEQ2, SEQ3)
    BEDFeature feature1 = mock(BEDFeature.class);
    when(feature1.getName()).thenReturn("SEQ1");
    BEDFeature feature2 = mock(BEDFeature.class);
    when(feature2.getName()).thenReturn("SEQ2");
    BEDFeature feature3 = mock(BEDFeature.class);
    when(feature3.getName()).thenReturn("SEQ3");

    List<BEDFeature> features = List.of(feature1, feature2, feature3);

    // Mock clustalo execution
    doAnswer(invocation -> {
      File outputFile = invocation.getArgument(1);
      Files.copy(mockAlignmentClustal.toPath(), outputFile.toPath(),
          StandardCopyOption.REPLACE_EXISTING);
      return null;
    }).when(mockExecutor).execute(
        any(File.class),
        any(File.class),
        eq("clustal"),
        isNull()
    );

    MsaProcessor processor = new MsaProcessor(options, mockExecutor, "https://itol.embl.de");
    PostProcessResult result = processor.process(testInputFasta, features);

    // Verify plain text output
    assertEquals("text/plain", result.getContentType());

    // Use writeContent for streaming results
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    result.writeContent(baos);
    result.cleanup();
    String output = baos.toString(StandardCharsets.UTF_8);

    // Verify metadata TSV is at the top
    assertTrue(output.startsWith("ID\t"), "Output should start with TSV header");
    assertTrue(output.contains("organism\tsample_type\tlocation"), "Should contain metadata column headers");
    assertTrue(output.contains("SEQ1\tEntamoeba histolytica\tisolate\tlaboratory"), "Should contain SEQ1 metadata");
    assertTrue(output.contains("SEQ2\tEntamoeba histolytica\tclinical\tfield_site_A"), "Should contain SEQ2 metadata");
    assertTrue(output.contains("SEQ3\tEntamoeba histolytica\treference\tgenome_project"), "Should contain SEQ3 metadata");

    // Verify double newline separator and clustal alignment follows
    assertTrue(output.contains("\n\nCLUSTAL"), "Should have double newline before CLUSTAL header");
    assertTrue(output.contains("CLUSTAL O"), "Should contain clustal alignment");

    // Verify metadata appears before alignment
    int metadataPos = output.indexOf("SEQ1\tEntamoeba");
    int clustalPos = output.indexOf("CLUSTAL");
    assertTrue(metadataPos < clustalPos, "Metadata should appear before clustal alignment");
  }
}
