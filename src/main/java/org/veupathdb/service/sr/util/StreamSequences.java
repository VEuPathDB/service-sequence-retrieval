package org.veupathdb.service.sr.util;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.util.SequenceUtil;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.generated.model.Feature;
import org.veupathdb.service.sr.generated.model.Strand;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;

public class StreamSequences {

 private static byte[] sequenceForFeature(IndexedFastaSequenceFile sequences, Feature feature, boolean forceStrandedness){
    var referenceSequence = sequences.getSubsequenceAt(feature.getContig(), feature.getStart(), feature.getEnd());
    var bases = referenceSequence.getBases();
    if(forceStrandedness && Strand.NEGATIVE.equals(feature.getStrand())){
      SequenceUtil.reverseComplement(bases);
    }
    return bases;
  }

  public static Consumer<OutputStream> responseStream(IndexedFastaSequenceFile sequences, List<Feature> features, DeflineFormat deflineFormat, boolean forceStrandedness, int requestedBasesPerLine){

    int basesPerLine = requestedBasesPerLine > 0 ? requestedBasesPerLine : Integer.MAX_VALUE;
    return outputStream -> {
      try (var buf = new BufferedOutputStream(outputStream)) {
        for (var feature : features) {
          var bases = sequenceForFeature(sequences, feature, forceStrandedness);
          var mentionStrand = forceStrandedness && (Strand.NEGATIVE.equals(feature.getStrand()) || Strand.POSITIVE.equals(feature.getStrand()));

          FastaUtil.appendSequenceToStream(buf, feature, deflineFormat, bases, basesPerLine, mentionStrand);
          buf.flush();
        }
      }
      catch (IOException e) {
        throw new RuntimeException("Unable to stream sequences response", e);
      }
    };
  }
}
