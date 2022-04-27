package org.veupathdb.service.sequence.util;

import org.veupathdb.service.sequence.generated.model.Feature;
import org.veupathdb.service.sequence.generated.model.DeflineFormat;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.lang.StringBuilder;
import java.io.IOException;
import java.util.Optional;

public class FastaUtil {

  public static final char HEADER_START_CHAR = '>';

  public static final char QUERY_TO_REGION_CHAR = ' ';

  public static final char CONTIG_TO_COORDS_CHAR = ':';

  public static final char START_TO_END_CHAR = '-';

  public static final char BEFORE_STRAND_CHAR= '(';

  public static final char AFTER_STRAND_CHAR= ')';

  private static final char LINE_SEPARATOR_CHR = '\n';

  private static final Charset CHARSET = Charset.forName("UTF-8");

  private static final byte[] LINE_SEPARATOR = String.valueOf(LINE_SEPARATOR_CHR).getBytes(CHARSET);

  private static String deflineForFeature(Feature feature, DeflineFormat deflineFormat, boolean mentionStrand){
    var result = new StringBuilder(200);
    result.append(HEADER_START_CHAR);
    switch(deflineFormat) {
      case QUERYONLY:
        result.append(Optional.ofNullable(feature.getQuery()).orElse(""));
        break;

      case QUERYANDREGION:
        result.append(Optional.ofNullable(feature.getQuery()).orElse(""));
        result.append(QUERY_TO_REGION_CHAR);
      case REGIONONLY:
        result.append(feature.getContig());
        result.append(CONTIG_TO_COORDS_CHAR);
        result.append(feature.getStart());
        result.append(START_TO_END_CHAR);
        result.append(feature.getEnd());
        if(mentionStrand){
          result.append(BEFORE_STRAND_CHAR);
          result.append(feature.getStrand());
          result.append(AFTER_STRAND_CHAR);
        }
        break;
    }
    return result.toString();
  }

/*
 * appends sequence to stream
  customized from htsjdk.samtools.reference.FastaReferenceWriter;
*/
  public static void appendSequenceToStream(OutputStream outputStream, Feature feature, DeflineFormat deflineFormat, byte[] bases, int basesPerLine, boolean mentionStrand) throws IOException {

    var defline = deflineForFeature(feature, deflineFormat, mentionStrand);
    outputStream.write(defline.getBytes(CHARSET));
    outputStream.write(LINE_SEPARATOR);

    final int to = bases.length;
    int next = 0;
    int currentLineBasesCount = 0;
    while( next < to ){
      if (currentLineBasesCount == basesPerLine) {
        outputStream.write(LINE_SEPARATOR);
        currentLineBasesCount = 0;
      }
      final int nextLength = Math.min(to - next, basesPerLine - currentLineBasesCount);
      outputStream.write(bases, next, nextLength);
      currentLineBasesCount += nextLength;
      next += nextLength;
    }
    outputStream.write(LINE_SEPARATOR);
  }

}
