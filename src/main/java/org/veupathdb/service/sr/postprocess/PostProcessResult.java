package org.veupathdb.service.sr.postprocess;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Result of post-processing a FASTA file.
 *
 * Contains the primary output content along with optional additional files
 * (e.g., guide trees, metadata) and content type information.
 */
public class PostProcessResult {

  private final String contentType;
  private final byte[] content;
  private final Map<String, byte[]> additionalFiles;

  /**
   * Create a post-process result.
   *
   * @param contentType The MIME type of the primary content (e.g., "text/html", "text/plain")
   * @param content The primary output content
   * @param additionalFiles Optional map of additional files (filename -> content)
   */
  public PostProcessResult(String contentType, byte[] content, Map<String, byte[]> additionalFiles) {
    this.contentType = contentType;
    this.content = content;
    this.additionalFiles = additionalFiles != null ? new HashMap<>(additionalFiles) : Collections.emptyMap();
  }

  /**
   * Create a post-process result with no additional files.
   *
   * @param contentType The MIME type of the primary content
   * @param content The primary output content
   */
  public PostProcessResult(String contentType, byte[] content) {
    this(contentType, content, null);
  }

  public String getContentType() {
    return contentType;
  }

  public byte[] getContent() {
    return content;
  }

  public Map<String, byte[]> getAdditionalFiles() {
    return Collections.unmodifiableMap(additionalFiles);
  }
}
