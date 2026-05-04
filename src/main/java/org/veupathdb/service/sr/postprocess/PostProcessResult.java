package org.veupathdb.service.sr.postprocess;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of post-processing a FASTA file.
 *
 * Contains streaming output content along with optional additional files (e.g., guide trees)
 * and content type information. Uses streaming to avoid loading large outputs into memory.
 */
public class PostProcessResult {

  private final String contentType;
  private final StreamingContent streamingContent;
  private final Map<String, byte[]> additionalFiles; // used for, eg, .dnd file in clustalo output
  private final List<File> tempFilesToCleanup;

  /**
   * Functional interface for streaming content to an OutputStream.
   */
  @FunctionalInterface
  public interface StreamingContent {
    void write(OutputStream os) throws IOException;
  }

  /**
   * Create a post-process result with streaming content.
   *
   * @param contentType The MIME type of the primary content (e.g., "text/html", "text/plain")
   * @param streamingContent Function that writes content to an OutputStream
   * @param additionalFiles Optional map of additional files (filename -> content)
   * @param tempFiles Optional list of temporary files to cleanup after streaming
   */
  public PostProcessResult(String contentType, StreamingContent streamingContent, Map<String, byte[]> additionalFiles, List<File> tempFiles) {
    this.contentType = contentType;
    this.streamingContent = streamingContent;
    this.additionalFiles = additionalFiles != null ? new HashMap<>(additionalFiles) : Collections.emptyMap();
    this.tempFilesToCleanup = tempFiles != null ? new ArrayList<>(tempFiles) : Collections.emptyList();
  }

  /**
   * Create a post-process result with streaming content and no additional files.
   *
   * @param contentType The MIME type of the primary content
   * @param streamingContent Function that writes content to an OutputStream
   * @param tempFiles Optional list of temporary files to cleanup after streaming
   */
  public PostProcessResult(String contentType, StreamingContent streamingContent, List<File> tempFiles) {
    this(contentType, streamingContent, null, tempFiles);
  }

  /**
   * Create a post-process result with streaming content, no additional files, and no temp files.
   *
   * @param contentType The MIME type of the primary content
   * @param streamingContent Function that writes content to an OutputStream
   */
  public PostProcessResult(String contentType, StreamingContent streamingContent) {
    this(contentType, streamingContent, null, null);
  }

  /**
   * Cleanup temporary files associated with this result.
   * Should be called after content has been streamed/written.
   */
  public void cleanup() {
    for (File tempFile : tempFilesToCleanup) {
      if (tempFile != null && tempFile.exists()) {
        tempFile.delete();
      }
    }
  }

  public String getContentType() {
    return contentType;
  }

  /**
   * Write content to the provided OutputStream.
   *
   * @param os OutputStream to write to
   * @throws IOException if writing fails
   */
  public void writeContent(OutputStream os) throws IOException {
    streamingContent.write(os);
  }

  public Map<String, byte[]> getAdditionalFiles() {
    return Collections.unmodifiableMap(additionalFiles);
  }
}
