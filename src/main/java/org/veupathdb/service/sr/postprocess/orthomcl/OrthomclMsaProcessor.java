package org.veupathdb.service.sr.postprocess.orthomcl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.veupathdb.service.sr.AsyncOptions;
import org.veupathdb.service.sr.generated.model.OrthomclMsaOptions;
import org.veupathdb.service.sr.postprocess.ClustaloExecutor;
import org.veupathdb.service.sr.postprocess.PostProcessResult;
import org.veupathdb.service.sr.postprocess.PostProcessor;
import org.veupathdb.service.sr.postprocess.ProcessingContext;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Post-processor for OrthoMCL multiple sequence alignment.
 *
 * Runs clustal-omega with guide tree generation and optionally creates
 * an HTML page with iTOL phylogenetic tree visualization.
 */
public class OrthomclMsaProcessor implements PostProcessor {

  private static final Logger LOG = LogManager.getLogger(OrthomclMsaProcessor.class);

  private static final String ITOL_UPLOAD_URL = "https://itol.embl.de/upload.cgi";

  private final OrthomclMsaOptions options;
  private final ClustaloExecutor clustaloExecutor;

  /**
   * Production constructor.
   *
   * @param options MSA-specific options
   * @param config Application configuration
   * @param context Processing context (SYNC or ASYNC) - determines timeout
   */
  public OrthomclMsaProcessor(OrthomclMsaOptions options, AsyncOptions config, ProcessingContext context) {
    this(options, new ClustaloExecutor(
      config.getClustaloBinaryPath(),
      context == ProcessingContext.ASYNC ? config.getClustaloAsyncTimeoutSeconds() : config.getClustaloSyncTimeoutSeconds()
    ));
  }

  /**
   * Constructor for testing with injectable ClustaloExecutor.
   */
  public OrthomclMsaProcessor(OrthomclMsaOptions options, ClustaloExecutor clustaloExecutor) {
    this.options = options;
    this.clustaloExecutor = clustaloExecutor;
  }

  @Override
  public PostProcessResult process(File fastaInput) throws IOException {
    // Create temp files for output
    File alignmentFile = File.createTempFile("alignment-", ".txt");
    File guideTreeFile = File.createTempFile("guidetree-", ".dnd");

    try {
      // Get output format from options
      String format = options.getFormat().toString().toLowerCase();

      // Run clustalo with OrthoMCL-specific flags
      try {
        clustaloExecutor.execute(
          fastaInput,
          alignmentFile,
          format,
          guideTreeFile,
          "--residuenumber",
          "--output-order=tree-order"
        );
      } catch (ClustaloExecutor.ClustaloException e) {
        throw new IOException("Clustalo execution failed", e);
      }

      // Read alignment content
      byte[] alignmentContent = Files.readAllBytes(alignmentFile.toPath());

      // If format is clustal, generate HTML with iTOL integration
      if ("clustal".equals(format)) {
        return generateHtmlResponse(alignmentContent, guideTreeFile);
      } else {
        // For other formats, return plain text
        return new PostProcessResult("text/plain", alignmentContent);
      }

    } finally {
      // Clean up temp files
      alignmentFile.delete();
      guideTreeFile.delete();
    }
  }

  /**
   * Generate HTML response with iTOL tree link and alignment.
   */
  private PostProcessResult generateHtmlResponse(byte[] alignmentContent, File guideTreeFile)
      throws IOException {

    // Read and process guide tree
    String treeData = Files.readString(guideTreeFile.toPath(), StandardCharsets.UTF_8);
    String processedTreeData = processTreeDataForItol(treeData);

    // Try to upload to iTOL
    String itolUrl = null;
    try {
      itolUrl = uploadToItol(processedTreeData);
      LOG.info("Successfully uploaded tree to iTOL: " + itolUrl);
    } catch (IOException e) {
      LOG.warn("Failed to upload tree to iTOL, continuing without tree link", e);
    }

    // Generate HTML
    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html>\n");
    html.append("<html>\n<head>\n");
    html.append("<meta charset=\"UTF-8\">\n");
    html.append("<title>OrthoMCL Multiple Sequence Alignment</title>\n");
    html.append("</head>\n<body>\n");

    // Add iTOL link if available
    if (itolUrl != null) {
      html.append("<h3><a href=\"").append(itolUrl).append("\" target=\"_blank\">")
          .append("Click here to view a phylogenetic tree of the alignment.")
          .append("</a></h3>\n");
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
    html.append("<pre>.dnd file\n\n");
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
    URL url = new URL(ITOL_UPLOAD_URL);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    try {
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

      // Send tree data as form parameter
      String postData = "ttext=" + URLEncoder.encode(treeData, StandardCharsets.UTF_8);

      try (OutputStream os = conn.getOutputStream()) {
        os.write(postData.getBytes(StandardCharsets.UTF_8));
      }

      int responseCode = conn.getResponseCode();
      if (responseCode != 302 && responseCode != 200) {
        throw new IOException("iTOL upload failed with response code: " + responseCode);
      }

      // Get redirect location
      String location = conn.getHeaderField("Location");
      if (location != null) {
        return "https://itol.embl.de/" + location;
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
