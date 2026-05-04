package org.veupathdb.service.sr.postprocess.msa;

import htsjdk.tribble.bed.BEDFeature;
import jakarta.ws.rs.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for parsing and validating metadata from TSV files.
 *
 * Metadata format:
 * - Tab-delimited with header row
 * - First column is always the ID (regardless of column name)
 * - Remaining columns are metadata fields
 */
public class MetadataParser {

  private static final Logger LOG = LogManager.getLogger(MetadataParser.class);

  // Metadata fetch limits - hardcoded as they're unlikely to change across deployments
  private static final int METADATA_MAX_SIZE_BYTES = 2 * 1024 * 1024; // 2MB
  private static final int METADATA_CONNECTION_TIMEOUT_MS = 30000; // 30 seconds
  private static final int METADATA_READ_TIMEOUT_MS = 30000; // 30 seconds

  /**
   * Result of parsing metadata from TSV.
   * Preserves both row and column order from the original TSV file.
   */
  public static class ParsedMetadata {
    private final String[] fieldNames;
    private final Map<String, Map<String, String>> data;

    public ParsedMetadata(String[] fieldNames, Map<String, Map<String, String>> data) {
      this.fieldNames = fieldNames;
      this.data = data;
    }

    public String[] getFieldNames() {
      return fieldNames;
    }

    public Map<String, Map<String, String>> getData() {
      return data;
    }
  }

  /**
   * Parse TSV metadata from URL.
   * First column is always the ID (regardless of header name).
   * Preserves both row and column order from the original TSV file.
   * Supports http, https, and file protocols.
   *
   * @param urlString URL to fetch metadata from (http://, https://, or file://)
   * @return ParsedMetadata containing field names and data with preserved order
   * @throws IOException if URL fetch or parsing fails
   * @throws BadRequestException if metadata format is invalid
   */
  public static ParsedMetadata parseMetadataFromUrl(String urlString)
      throws IOException, BadRequestException {

    LOG.info("Fetching metadata from URL: " + urlString);

    // Validate URL scheme
    URL url;
    try {
      url = new URL(urlString);
    } catch (Exception e) {
      throw new BadRequestException("Invalid metadata URL: " + e.getMessage());
    }

    String protocol = url.getProtocol().toLowerCase();
    if (!protocol.equals("http") && !protocol.equals("https") && !protocol.equals("file")) {
      throw new BadRequestException(
          "Invalid metadata URL scheme: " + protocol + ". Only http, https, and file are supported.");
    }

    // Fetch metadata (with timeout for HTTP/HTTPS)
    InputStream inputStream;
    if (protocol.equals("http") || protocol.equals("https")) {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(METADATA_CONNECTION_TIMEOUT_MS);
      connection.setReadTimeout(METADATA_READ_TIMEOUT_MS);
      connection.setRequestMethod("GET");

      int responseCode;
      try {
        responseCode = connection.getResponseCode();
      } catch (IOException e) {
        throw new IOException("Failed to fetch metadata from URL: " + e.getMessage(), e);
      }

      if (responseCode != 200) {
        throw new IOException(
            "Failed to fetch metadata from URL: HTTP " + responseCode + " " + connection.getResponseMessage());
      }

      inputStream = connection.getInputStream();
    } else {
      // file:// protocol
      try {
        inputStream = url.openStream();
      } catch (IOException e) {
        throw new IOException("Failed to read metadata from file URL: " + e.getMessage(), e);
      }
    }

    // Parse metadata line-by-line with size limit
    // Use LinkedHashMap to preserve row insertion order
    Map<String, Map<String, String>> metadata = new LinkedHashMap<>();
    String[] metadataFieldNames = null;
    int headerColumnCount = 0;
    int rowNumber = 0;
    int totalBytes = 0;

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      String line;

      while ((line = reader.readLine()) != null) {
        rowNumber++;
        totalBytes += line.getBytes(StandardCharsets.UTF_8).length + 1; // +1 for newline

        if (totalBytes > METADATA_MAX_SIZE_BYTES) {
          throw new BadRequestException(
              "Metadata file exceeds maximum size of " + METADATA_MAX_SIZE_BYTES + " bytes");
        }

        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty()) {
          continue; // Skip empty lines
        }

        // First non-empty line is the header
        if (metadataFieldNames == null) {
          metadataFieldNames = parseHeaderLine(trimmedLine);
          headerColumnCount = metadataFieldNames.length + 1; // +1 for ID column
          continue;
        }

        // Parse data row
        Map.Entry<String, Map<String, String>> rowData =
            parseDataLine(trimmedLine, metadataFieldNames, headerColumnCount, rowNumber);

        String id = rowData.getKey();
        Map<String, String> rowMetadata = rowData.getValue();

        // Check for duplicate IDs
        if (metadata.containsKey(id)) {
          throw new BadRequestException("Invalid metadata format: duplicate ID '" + id + "' found");
        }

        metadata.put(id, rowMetadata);
      }
    }

    // Validation
    if (metadataFieldNames == null) {
      throw new BadRequestException("Metadata file is empty");
    }

    if (metadata.isEmpty()) {
      throw new BadRequestException("Metadata file contains no data rows");
    }

    LOG.info("Parsed metadata for " + metadata.size() + " IDs with " +
        metadataFieldNames.length + " metadata fields");

    return new ParsedMetadata(metadataFieldNames, metadata);
  }

  /**
   * Parse header line from TSV metadata.
   * First column is ID, remaining columns are metadata field names.
   *
   * @param headerLine Header line (tab-delimited)
   * @return Array of metadata field names (excludes first ID column)
   * @throws BadRequestException if header format is invalid
   */
  static String[] parseHeaderLine(String headerLine) throws BadRequestException {
    String[] headers = headerLine.split("\t");

    if (headers.length < 2) {
      throw new BadRequestException(
          "Metadata file must have at least 2 columns (ID + at least one metadata field). Found " +
              headers.length + " column(s).");
    }

    // Metadata field names are columns 2+ (exclude first ID column)
    return Arrays.copyOfRange(headers, 1, headers.length);
  }

  /**
   * Parse a data row from TSV metadata.
   * First column is ID, remaining columns are metadata values.
   *
   * @param dataLine Data line (tab-delimited)
   * @param metadataFieldNames Array of metadata field names from header
   * @param expectedColumnCount Expected number of columns (from header)
   * @param rowNumber Row number (for error messages)
   * @return Map entry of ID -> metadata map
   * @throws BadRequestException if row format is invalid
   */
  static Map.Entry<String, Map<String, String>> parseDataLine(
      String dataLine,
      String[] metadataFieldNames,
      int expectedColumnCount,
      int rowNumber) throws BadRequestException {

    String[] values = dataLine.split("\t", -1); // -1 to preserve empty trailing fields

    if (values.length != expectedColumnCount) {
      throw new BadRequestException(
          "Invalid metadata format: row " + rowNumber + " has " + values.length +
              " columns but header has " + expectedColumnCount + " columns");
    }

    String id = values[0].trim();
    if (id.isEmpty()) {
      throw new BadRequestException("Invalid metadata format: row " + rowNumber + " has empty ID");
    }

    // Build metadata map for this ID (columns 2+)
    // Use LinkedHashMap to preserve column order
    Map<String, String> rowMetadata = new LinkedHashMap<>();
    for (int j = 0; j < metadataFieldNames.length; j++) {
      rowMetadata.put(metadataFieldNames[j], values[j + 1].trim());
    }

    return Map.entry(id, rowMetadata);
  }

  /**
   * Validate that metadata IDs exactly match feature IDs.
   *
   * @param metadata Parsed metadata map
   * @param featureIds Set of IDs from features
   * @throws BadRequestException if IDs don't match exactly
   */
  public static void validateMetadataIds(
      Map<String, Map<String, String>> metadata,
      Set<String> featureIds) throws BadRequestException {

    Set<String> metadataIds = metadata.keySet();

    // Find IDs in metadata but not in features
    Set<String> extraMetadataIds = new HashSet<>(metadataIds);
    extraMetadataIds.removeAll(featureIds);

    // Find IDs in features but not in metadata
    Set<String> missingMetadataIds = new HashSet<>(featureIds);
    missingMetadataIds.removeAll(metadataIds);

    // Build error message if there are mismatches
    if (!extraMetadataIds.isEmpty() || !missingMetadataIds.isEmpty()) {
      StringBuilder error = new StringBuilder("Metadata validation failed:");

      if (!extraMetadataIds.isEmpty()) {
        List<String> sortedExtra = new ArrayList<>(extraMetadataIds);
        Collections.sort(sortedExtra);
        error.append("\n  - Metadata contains ")
            .append(extraMetadataIds.size())
            .append(" ID(s) not in feature set: ")
            .append(sortedExtra);
      }

      if (!missingMetadataIds.isEmpty()) {
        List<String> sortedMissing = new ArrayList<>(missingMetadataIds);
        Collections.sort(sortedMissing);
        error.append("\n  - Feature set contains ")
            .append(missingMetadataIds.size())
            .append(" ID(s) not in metadata: ")
            .append(sortedMissing);
      }

      throw new BadRequestException(error.toString());
    }

    LOG.info("Metadata validation passed: all " + featureIds.size() + " feature IDs have metadata");
  }

  /**
   * Extract IDs from BEDFeature list.
   *
   * @param features List of BEDFeatures
   * @return Set of IDs (from BEDFeature.getName())
   */
  public static Set<String> extractIdsFromFeatures(List<BEDFeature> features) {
    return features.stream()
        .map(BEDFeature::getName)
        .collect(Collectors.toSet());
  }
}
