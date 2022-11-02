package org.veupathdb.service.sr.util;

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

  private static byte[] sequenceForFeature(IndexedFastaSequenceFile sequences, BEDFeature feature, boolean forceStrandedness){
    var referenceSequence = sequences.getSubsequenceAt(feature.getContig(), feature.getStart(), feature.getEnd());
    var bases = referenceSequence.getBases();
    if(feature instanceof FullBEDFeature){
      var exons = ((FullBEDFeature) feature).getExons();
      var length =  exons.stream().mapToInt(e -> e.getCodingLength()).sum();
      var newBases = new byte[length];
      int newBasesOffset = 0;
      for (var e: exons){
        System.arraycopy(bases, e.getCdStart(), newBases, newBasesOffset, e.getCodingLength());
        newBasesOffset += e.getCodingLength();
      }
      bases = newBases;
    }
    if(forceStrandedness && feature.getStrand().equals(Strand.NEGATIVE)){
      SequenceUtil.reverseComplement(bases);
    }
    return bases;
  }

  public static Consumer<OutputStream> responseStream(IndexedFastaSequenceFile sequences, List<BEDFeature> features, DeflineFormat deflineFormat, boolean forceStrandedness, int requestedBasesPerLine){

    int basesPerLine = requestedBasesPerLine > 0 ? requestedBasesPerLine : Integer.MAX_VALUE;
    return outputStream -> {
      try (var buf = new BufferedOutputStream(outputStream)) {
        var mentionStrand = forceStrandedness;
        for (var feature : features) {
          var bases = sequenceForFeature(sequences, feature, forceStrandedness);
          var defline = Deflines.deflineForFeature(feature, deflineFormat, mentionStrand);

          appendSequenceToStream(buf, defline, bases, basesPerLine);
          buf.flush();
        }
      }
      catch (IOException e) {
        throw new RuntimeException("Unable to stream sequences response", e);
      }
    };
  }

  /*
   * appends sequence to stream
   * customized from htsjdk.samtools.reference.FastaReferenceWriter;
   */
  public static void appendSequenceToStream(OutputStream outputStream, String defline, byte[] bases, int basesPerLine) throws IOException {
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
