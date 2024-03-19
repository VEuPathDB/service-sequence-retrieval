package org.veupathdb.service.sr.response;

import org.veupathdb.service.sr.generated.model.DeflineFormat;

import java.lang.StringBuilder;

import htsjdk.tribble.annotation.Strand;
import htsjdk.tribble.bed.BEDFeature;

public class Deflines {

  public static final char HEADER_START_CHAR = '>';

  public static final char QUERY_TO_REGION_CHAR = ' ';

  public static final char CONTIG_TO_COORDS_CHAR = ':';

  public static final char START_TO_END_CHAR = '-';

  public static final char BEFORE_STRAND_CHAR = '(';

  public static final char AFTER_STRAND_CHAR = ')';

  public static final String NEGATIVE_LENGTH_MESSAGE = " | error_code=NEGATIVE_LENGTH";

  public static final String NOT_REQUESTED_LENGTH_MESSAGE = " | error_code=NOT_REQUESTED_LENGTH";

  /**
   *
   * @param feature A BEDFeature to construct a Fasta defline string for. Note that the name field of the feature
   *                is included as the "QUERY" in the defline.
   * @param deflineFormat Specification for what data to include in the defline: Query, Region, or both.
   * @param indexLength The length of the sequence, used to validate the feature.
   * @return A defline constructed from the incoming BEDFeature given the defline format spec.
   */
  public static String deflineForFeature(BEDFeature feature, DeflineFormat deflineFormat, long indexLength) {
    var result = new StringBuilder(200);
    result.append(HEADER_START_CHAR);
    switch(deflineFormat) {
      case QUERYONLY:
        result.append(feature.getName());
        break;
      case QUERYANDREGION:
        result.append(feature.getName());
        result.append(QUERY_TO_REGION_CHAR);
      case REGIONONLY:
        result.append(feature.getContig());
        result.append(CONTIG_TO_COORDS_CHAR);
        result.append(feature.getStart());
        result.append(START_TO_END_CHAR);
        result.append(feature.getEnd());
        if (!feature.getStrand().equals(Strand.NONE)) {
          result.append(BEFORE_STRAND_CHAR);
          result.append(feature.getStrand().encodeAsChar());
          result.append(AFTER_STRAND_CHAR);
        }
        break;
    }

    // If the sequence is invalid, append an indicator to the defline to indicate to users.
    // This is included in the defline instead of erroring out, as we prefer to fail open to provide
    // what sequence we can to clients.
    if (feature.getEnd() < feature.getStart()) {
      result.append(NEGATIVE_LENGTH_MESSAGE);
    } else if (feature.getEnd() > indexLength) {
      result.append(NOT_REQUESTED_LENGTH_MESSAGE);
    }
    return result.toString();
  }
}
