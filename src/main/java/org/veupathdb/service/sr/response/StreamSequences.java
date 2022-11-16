package org.veupathdb.service.sr.response;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.util.SequenceUtil;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.generated.model.Feature;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;

import htsjdk.tribble.bed.FullBEDFeature;
import htsjdk.tribble.bed.BEDFeature;
import htsjdk.tribble.annotation.Strand;
import htsjdk.samtools.util.Locatable;
import htsjdk.samtools.util.Interval;
import java.nio.charset.Charset;

public class StreamSequences {

  private static final Charset CHARSET = Charset.forName("UTF-8");

  private static final byte[] LINE_SEPARATOR = String.valueOf("\n").getBytes(CHARSET);

  public static void write(OutputStream outputStream, IndexedFastaSequenceFile sequences, List<BEDFeature> features, DeflineFormat deflineFormat, int requestedBasesPerLine){
    int basesPerLine = requestedBasesPerLine > 0 ? requestedBasesPerLine : Integer.MAX_VALUE;
    try (var buf = new BufferedOutputStream(outputStream)) {
      for (var feature : features) {
        var defline = Deflines.deflineForFeature(feature, deflineFormat);
        var bases = Bases.getBasesForBedFeature(sequences, feature);

        appendSequenceToStream(buf, defline, bases, basesPerLine);
      }
    }
    catch (IOException e) {
      throw new RuntimeException("Unable to stream sequences response", e);
    }
  }

  /*
   * appends sequence to stream
   * customized from htsjdk.samtools.reference.FastaReferenceWriter;
   */
  private static void appendSequenceToStream(OutputStream outputStream, String defline, byte[] bases, int basesPerLine) throws IOException {
    outputStream.write(defline.getBytes(CHARSET));
    outputStream.write(LINE_SEPARATOR);

    final int totalBasesCount = bases.length;
    int writtenBasesCount = 0;
    int currentLineBasesCount = 0;
    while( writtenBasesCount < totalBasesCount ){
      if (currentLineBasesCount == basesPerLine) {
        outputStream.write(LINE_SEPARATOR);
        currentLineBasesCount = 0;
      }
      final int nextLength = Math.min(totalBasesCount - writtenBasesCount, basesPerLine - currentLineBasesCount);
      outputStream.write(bases, writtenBasesCount, nextLength);
      currentLineBasesCount += nextLength;
      writtenBasesCount += nextLength;
    }
    outputStream.write(LINE_SEPARATOR);
  }
}
