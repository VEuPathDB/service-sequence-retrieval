package org.veupathdb.service.sr.response;

import org.veupathdb.service.sr.generated.model.DeflineFormat;
import htsjdk.samtools.util.Locatable;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.lang.StringBuilder;
import java.io.IOException;
import java.util.Optional;

import htsjdk.tribble.annotation.Strand;
import htsjdk.tribble.bed.BEDFeature;

public class Deflines {

  public static final char HEADER_START_CHAR = '>';

  public static final char QUERY_TO_REGION_CHAR = ' ';

  public static final char CONTIG_TO_COORDS_CHAR = ':';

  public static final char START_TO_END_CHAR = '-';

  public static final char BEFORE_STRAND_CHAR= '(';

  public static final char AFTER_STRAND_CHAR= ')';

  public static String deflineForFeature(BEDFeature feature, DeflineFormat deflineFormat){
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
        if(! feature.getStrand().equals(Strand.NONE)){
          result.append(BEFORE_STRAND_CHAR);
          result.append(feature.getStrand().encodeAsChar());
          result.append(AFTER_STRAND_CHAR);
        }
        break;
    }
    return result.toString();
  }
}
