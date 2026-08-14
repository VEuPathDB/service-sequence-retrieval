package org.veupathdb.service.sr.postprocess.msa;

import htsjdk.tribble.bed.BEDFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gusdb.fgputil.FormatUtil;
import org.veupathdb.service.sr.SrtServiceOptions;
import org.veupathdb.service.sr.generated.model.MsaFormat;
import org.veupathdb.service.sr.generated.model.MsaOptions;
import org.veupathdb.service.sr.postprocess.ClustaloExecutor;
import org.veupathdb.service.sr.postprocess.PostProcessResult;
import org.veupathdb.service.sr.postprocess.PostProcessor;
import org.veupathdb.service.sr.postprocess.ProcessingContext;

import jakarta.ws.rs.BadRequestException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Unified post-processor for multiple sequence alignment.
 *
 * Runs clustal-omega with guide tree generation and optionally creates
 * HTML output with iTOL phylogenetic tree visualization.
 *
 * Supports both orthomcl and isolates MSA use cases.
 */
public class MsaProcessor implements PostProcessor {

  private static final Logger LOG = LogManager.getLogger(MsaProcessor.class);

  private static final MsaFormat DEFAULT_FORMAT = MsaFormat.CLUSTAL;

  private final MsaOptions options;
  private final MsaFormat format;
  private final ClustaloExecutor clustaloExecutor;
  private final String itolBaseUrl;

  /**
   * Production constructor.
   *
   * @param options MSA-specific options
   * @param config Application configuration
   * @param context Processing context (SYNC or ASYNC) - determines timeout
   */
  public MsaProcessor(MsaOptions options, SrtServiceOptions config, ProcessingContext context) {
    this(options,
        new ClustaloExecutor(
            config.getClustaloBinaryPath(),
            context == ProcessingContext.ASYNC
                ? config.getClustaloAsyncTimeoutSeconds()
                : config.getClustaloSyncTimeoutSeconds()
        ),
        config.getItolBaseUrl()
    );
  }

  /**
   * Constructor for testing with injectable ClustaloExecutor and iTOL URL.
   */
  public MsaProcessor(
      MsaOptions options,
      ClustaloExecutor clustaloExecutor,
      String itolBaseUrl) {
    this.options = options;
    this.format = Optional.ofNullable(options.getFormat()).orElse(DEFAULT_FORMAT);
    this.clustaloExecutor = clustaloExecutor;
    this.itolBaseUrl = itolBaseUrl;
  }

  @Override
  public PostProcessResult process(File fastaInput, List<BEDFeature> features) throws IOException {
    // Validate metadata URL usage
    validateMetadataUrl();

    // Get format - use clustal for clustal_dnd since clustalo doesn't have that format
    String clustaloFormat = format == MsaFormat.CLUSTALDND ? "clustal" : format.getValue();

    // Create temp files for output
    File alignmentFile = File.createTempFile("alignment-", ".txt");
    // Only create guide tree file if needed for CLUSTALDND format
    File guideTreeFile = (format == MsaFormat.CLUSTALDND)
        ? File.createTempFile("guidetree-", ".dnd")
        : null;

    try {
      // Run clustalo
      try {
        clustaloExecutor.execute(
            fastaInput,
            alignmentFile,
            clustaloFormat,
            guideTreeFile
        );
      } catch (ClustaloExecutor.ClustaloException e) {
        throw new IOException("Clustalo execution failed", e);
      }

      // Generate response based on format
      if (format == MsaFormat.CLUSTAL && options.getMetadataUrl() != null) {
        // Clustal with metadata TSV prepended (complex streaming)
        LOG.info("Prepending metadata TSV to clustal output");

        // 1. Extract IDs from features
        var featureIds = MetadataParser.extractIdsFromFeatures(features);

        // 2. Fetch and parse metadata from URL (with preserved order)
        MetadataParser.ParsedMetadata parsedMetadata =
            MetadataParser.parseMetadataFromUrl(options.getMetadataUrl());

        // 3. Strict validation - throws BadRequestException on mismatch
        MetadataParser.validateMetadataIds(parsedMetadata.getData(), featureIds);

        // 4. Stream metadata TSV followed by alignment
        return streamMetadataWithAlignment(parsedMetadata, alignmentFile, guideTreeFile);
      } else if (format == MsaFormat.CLUSTAL && options.getMetadataUrl() == null) {
        // Plain text clustal (simple streaming)
        return new PostProcessResult("text/plain",
            os -> Files.copy(alignmentFile.toPath(), os),
            List.of(alignmentFile));
      } else if (format == MsaFormat.CLUSTALDND) {
        // HTML with iTOL tree link (complex streaming)
        return generateHtmlWithItol(alignmentFile, guideTreeFile);
      } else {
        // Other formats: plain text (simple streaming)
        return new PostProcessResult("text/plain",
            os -> Files.copy(alignmentFile.toPath(), os),
            List.of(alignmentFile));
      }

    } finally {
      // Temp files are managed by PostProcessResult for cleanup after streaming
      // No cleanup here
    }
  }

  /**
   * Stream metadata TSV content followed by alignment output.
   * Converts metadata map to TSV format and streams it with a double newline separator,
   * then streams the alignment file. Preserves both row and column order from the original TSV file.
   *
   * @param parsedMetadata Parsed metadata with field names and data
   * @param alignmentFile Clustal alignment file
   * @param guideTreeFile Guide tree file (may be null)
   * @return PostProcessResult with streaming content
   */
  private PostProcessResult streamMetadataWithAlignment(
      MetadataParser.ParsedMetadata parsedMetadata,
      File alignmentFile,
      File guideTreeFile) {

    List<File> tempFiles = guideTreeFile != null
        ? List.of(alignmentFile, guideTreeFile)
        : List.of(alignmentFile);

    return new PostProcessResult("text/plain", os -> {
      String[] fieldNames = parsedMetadata.getFieldNames();
      Map<String, Map<String, String>> metadata = parsedMetadata.getData();

      if (!metadata.isEmpty()) {
        // Write header row: ID followed by field names in original order
        StringBuilder header = new StringBuilder("ID");
        for (String fieldName : fieldNames) {
          header.append("\t").append(fieldName);
        }
        header.append("\n");
        os.write(header.toString().getBytes(StandardCharsets.UTF_8));

        // Write data rows in original order (LinkedHashMap preserves insertion order)
        for (Map.Entry<String, Map<String, String>> entry : metadata.entrySet()) {
          String id = entry.getKey();
          Map<String, String> fields = entry.getValue();

          StringBuilder row = new StringBuilder(id);
          // Use field names array to ensure correct column order
          for (String fieldName : fieldNames) {
            row.append("\t").append(fields.get(fieldName));
          }
          row.append("\n");
          os.write(row.toString().getBytes(StandardCharsets.UTF_8));
        }
      }

      // Write double newline separator
      os.write("\n".getBytes(StandardCharsets.UTF_8));

      // Stream alignment file
      Files.copy(alignmentFile.toPath(), os);
    }, tempFiles);
  }

  /**
   * Validate metadata URL usage - only allowed with clustal format.
   */
  private void validateMetadataUrl() {
    if (options.getMetadataUrl() != null && format != MsaFormat.CLUSTAL) {
      throw new BadRequestException(
          "metadataUrl is only supported with 'clustal' format. " +
              "Current format: " + format.getValue()
      );
    }
  }

  /**
   * Generate HTML response with iTOL tree link and alignment.
   * Used for clustal_dnd format. Streams HTML generation to avoid memory overhead.
   */
  private PostProcessResult generateHtmlWithItol(File alignmentFile, File guideTreeFile)
      throws IOException {

    // Read tree data for iTOL upload and additional file
    // Tree files are small, so reading into memory is acceptable here
    String treeData = Files.readString(guideTreeFile.toPath(), StandardCharsets.UTF_8);

    // Validate tree data before uploading
    String itolUrl = null;
    if (treeData == null || treeData.trim().isEmpty()) {
      LOG.warn("Guide tree data is empty, skipping iTOL upload");
    } else {
      // Process tree data for iTOL
      String processedTreeData = processTreeDataForItol(treeData);

      // Try to upload to iTOL
      try {
        itolUrl = uploadToItol(processedTreeData);

        // Validate that we got a real tree URL, not just the iTOL home page
        if (itolUrl != null && !itolUrl.equals(itolBaseUrl) && !itolUrl.equals(itolBaseUrl + "/")) {
          LOG.info("Successfully uploaded tree to iTOL: " + itolUrl);
        } else {
          LOG.warn("iTOL upload returned home page URL, tree upload likely failed");
          itolUrl = null;
        }
      } catch (IOException e) {
        LOG.warn("Failed to upload tree to iTOL, continuing without tree link", e);
      }
    }

    // Store guide tree as additional file
    Map<String, byte[]> additionalFiles = new HashMap<>();
    additionalFiles.put("guidetree.dnd", treeData.getBytes(StandardCharsets.UTF_8));

    // Capture itolUrl for use in lambda
    final String finalItolUrl = itolUrl;
    final String finalTreeData = treeData;

    // Stream HTML generation
    return new PostProcessResult("text/html", os -> {
      // Write HTML header
      os.write("<!DOCTYPE html>\n".getBytes(StandardCharsets.UTF_8));
      os.write("<html>\n<head>\n".getBytes(StandardCharsets.UTF_8));
      os.write("<meta charset=\"UTF-8\">\n".getBytes(StandardCharsets.UTF_8));
      os.write("<title>Multiple Sequence Alignment</title>\n".getBytes(StandardCharsets.UTF_8));
      os.write("</head>\n<body>\n".getBytes(StandardCharsets.UTF_8));

      // Add iTOL link if available
      if (finalItolUrl != null) {
        String link = "<h3><a href=\"" + finalItolUrl + "\" target=\"_blank\">" +
            "Click here to view a phylogenetic tree of the alignment." +
            "</a></h3>\n";
        os.write(link.getBytes(StandardCharsets.UTF_8));
      } else {
        // No valid iTOL URL - show message
        os.write("<h3>(.dnd file does not produce a valid iTOL phylogenetic tree)</h3>\n"
            .getBytes(StandardCharsets.UTF_8));
      }

      // Start alignment section
      os.write("<pre>\n".getBytes(StandardCharsets.UTF_8));

      // Stream alignment line-by-line with HTML escaping
      try (BufferedReader reader = Files.newBufferedReader(alignmentFile.toPath(), StandardCharsets.UTF_8)) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.startsWith("CLUSTAL O")) {
            os.write(("<h3>" + FormatUtil.escapeHtml(line) + "</h3>\n").getBytes(StandardCharsets.UTF_8));
          } else {
            os.write((FormatUtil.escapeHtml(line) + "\n").getBytes(StandardCharsets.UTF_8));
          }
        }
      }

      os.write("</pre>\n".getBytes(StandardCharsets.UTF_8));

      // Add guide tree data
      os.write("<hr>\n".getBytes(StandardCharsets.UTF_8));
      os.write("<h4>Guide Tree (.dnd format)</h4>\n".getBytes(StandardCharsets.UTF_8));
      os.write("<pre>".getBytes(StandardCharsets.UTF_8));
      String escapedTreeData = finalTreeData
          .replace("&", "&amp;")
          .replace("<", "&lt;")
          .replace(">", "&gt;");
      os.write(escapedTreeData.getBytes(StandardCharsets.UTF_8));
      os.write("</pre>\n".getBytes(StandardCharsets.UTF_8));

      // Write HTML footer
      os.write("</body>\n</html>\n".getBytes(StandardCharsets.UTF_8));
    }, additionalFiles, List.of(alignmentFile, guideTreeFile));
  }

  /**
   * Process tree data for iTOL compatibility.
   * Based on the Perl logic: reverse string, replace first : with %, replace remaining : with _,
   * reverse back, replace % with :
   */
  private String processTreeDataForItol(String treeData) {
    StringBuilder processed = new StringBuilder();
    for (String line : treeData.split("\n")) {
      if (line.trim().isEmpty()) {
        continue;
      }
      // Reverse the line
      String reversed = new StringBuilder(line).reverse().toString();
      // Replace first : with %
      reversed = reversed.replaceFirst(":", "%");
      // Replace remaining : with _
      reversed = reversed.replace(":", "_");
      // Reverse back
      String result = new StringBuilder(reversed).reverse().toString();
      // Replace % with :
      result = result.replace("%", ":");
      processed.append(result).append("\n");
    }
    return processed.toString();
  }

  /**
   * Upload tree data to iTOL service.
   *
   * @return URL of the uploaded tree
   */
  private String uploadToItol(String treeData) throws IOException {
    String uploadUrl = itolBaseUrl + "/upload.cgi";
    URL url = URI.create(uploadUrl).toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    try {
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      conn.setInstanceFollowRedirects(false);  // Don't follow redirects automatically

      // Use multipart/form-data like the Perl example
      String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
      conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

      // Build multipart/form-data body
      StringBuilder body = new StringBuilder();
      body.append("--").append(boundary).append("\r\n");
      body.append("Content-Disposition: form-data; name=\"ttext\"\r\n\r\n");
      body.append(treeData).append("\r\n");
      body.append("--").append(boundary).append("--\r\n");

      byte[] bodyBytes = body.toString().getBytes(StandardCharsets.UTF_8);
      LOG.debug("iTOL upload boundary: " + boundary);
      LOG.debug("iTOL upload body length: " + bodyBytes.length);

      try (OutputStream os = conn.getOutputStream()) {
        os.write(bodyBytes);
      }

      int responseCode = conn.getResponseCode();
      LOG.debug("iTOL response code: " + responseCode);
      if (responseCode != 302 && responseCode != 200) {
        throw new IOException("iTOL upload failed with response code: " + responseCode);
      }

      // Get redirect location
      String location = conn.getHeaderField("Location");
      if (location != null) {
        return itolBaseUrl + "/" + location;
      } else {
        // If no redirect, read response body
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream()))) {
          StringBuilder response = new StringBuilder();
          String line;
          while ((line = reader.readLine()) != null) {
            response.append(line);
          }
          LOG.debug("iTOL response: " + response);
          throw new IOException("iTOL upload did not return expected redirect");
        }
      }
    } finally {
      conn.disconnect();
    }
  }
}
