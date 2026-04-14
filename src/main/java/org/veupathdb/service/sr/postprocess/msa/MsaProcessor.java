package org.veupathdb.service.sr.postprocess.msa;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.Map;

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

  private final MsaOptions options;
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
  public MsaProcessor(MsaOptions options, ClustaloExecutor clustaloExecutor, String itolBaseUrl) {
    this.options = options;
    this.clustaloExecutor = clustaloExecutor;
    this.itolBaseUrl = itolBaseUrl;
  }

  @Override
  public PostProcessResult process(File fastaInput) throws IOException {
    // Validate metadata URL usage
    validateMetadataUrl();

    // Get format - use clustal for clustal_dnd since clustalo doesn't have that format
    MsaFormat format = options.getFormat();
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

      // Read alignment content
      byte[] alignmentContent = Files.readAllBytes(alignmentFile.toPath());

      // Generate response based on format
      if (format == MsaFormat.CLUSTAL && options.getMetadataUrl() != null) {
        // Future: clustal with metadata tooltips (HTML)
        // For now, return plain text
        // TODO: Implement metadata fetching and HTML tooltip generation
        LOG.warn("Metadata URL provided but metadata HTML generation not yet implemented");
        return new PostProcessResult("text/plain", alignmentContent);
      } else if (format == MsaFormat.CLUSTAL && options.getMetadataUrl() == null) {
        // Plain text clustal
        return new PostProcessResult("text/plain", alignmentContent);
      } else if (format == MsaFormat.CLUSTALDND) {
        // HTML with iTOL tree link - read tree data
        String treeData = Files.readString(guideTreeFile.toPath(), StandardCharsets.UTF_8);
        return generateHtmlWithItol(alignmentContent, treeData);
      } else {
        // Other formats: plain text
        return new PostProcessResult("text/plain", alignmentContent);
      }

    } finally {
      // Clean up temp files
      alignmentFile.delete();
      if (guideTreeFile != null) {
        guideTreeFile.delete();
      }
    }
  }

  /**
   * Validate metadata URL usage - only allowed with clustal format.
   */
  private void validateMetadataUrl() {
    if (options.getMetadataUrl() != null && options.getFormat() != MsaFormat.CLUSTAL) {
      throw new BadRequestException(
          "metadataUrl is only supported with 'clustal' format. " +
              "Current format: " + options.getFormat().getValue()
      );
    }
  }

  /**
   * Generate HTML response with iTOL tree link and alignment.
   * Used for clustal_dnd format.
   */
  private PostProcessResult generateHtmlWithItol(byte[] alignmentContent, String treeData)
      throws IOException {

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

    // Generate HTML
    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html>\n");
    html.append("<html>\n<head>\n");
    html.append("<meta charset=\"UTF-8\">\n");
    html.append("<title>Multiple Sequence Alignment</title>\n");
    html.append("</head>\n<body>\n");

    // Add iTOL link if available
    if (itolUrl != null) {
      html.append("<h3><a href=\"").append(itolUrl).append("\" target=\"_blank\">")
          .append("Click here to view a phylogenetic tree of the alignment.")
          .append("</a></h3>\n");
    } else {
      // No valid iTOL URL - show message
      html.append("<h3>(.dnd file does not produce a valid iTOL phylogenetic tree)</h3>\n");
    }

    // Add alignment
    html.append("<pre>\n");
    String alignmentText = new String(alignmentContent, StandardCharsets.UTF_8);

    // Process alignment - highlight header line
    String[] lines = alignmentText.split("\n");
    for (String line : lines) {
      if (line.startsWith("CLUSTAL O")) {
        html.append("<h3>").append(escapeHtml(line)).append("</h3>\n");
      } else {
        html.append(escapeHtml(line)).append("\n");
      }
    }
    html.append("</pre>\n");

    // Add guide tree data
    html.append("<hr>\n");
    html.append("<h4>Guide Tree (.dnd format)</h4>\n");
    html.append("<pre>");
    html.append(escapeHtml(treeData));
    html.append("</pre>\n");

    html.append("</body>\n</html>\n");

    // Store guide tree as additional file
    Map<String, byte[]> additionalFiles = new HashMap<>();
    additionalFiles.put("guidetree.dnd", treeData.getBytes(StandardCharsets.UTF_8));

    return new PostProcessResult("text/html", html.toString().getBytes(StandardCharsets.UTF_8),
        additionalFiles);
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

  /**
   * Escape HTML special characters.
   */
  private String escapeHtml(String text) {
    return text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }
}
