package org.veupathdb.service.sequence.util;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.util.SequenceUtil;
import htsjdk.samtools.reference.FastaReferenceWriterBuilder;

import org.veupathdb.service.sequence.generated.model.Feature;
import org.veupathdb.service.sequence.generated.model.DeflineFormat;

import java.util.List;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.OutputStream;
import java.io.IOException;

import org.veupathdb.service.sequence.util.FastaUtil;


public class StreamSequences {


  public static byte[] sequenceForFeature(IndexedFastaSequenceFile sequences, Feature feature, boolean forceStrandedness){
    var referenceSequence = sequences.getSubsequenceAt(feature.getContig(), feature.getStart(), feature.getEnd());
    var bases = referenceSequence.getBases();
    if(forceStrandedness && feature.getStrand().equals('-')){
      SequenceUtil.reverseComplement(bases);
    }
    return bases;
  }

  public static StreamingOutput responseStream(IndexedFastaSequenceFile sequences, List<Feature> features, DeflineFormat deflineFormat, boolean forceStrandedness){

    int basesPerLine = 60;
    return new StreamingOutput(){
      @Override
      public void write(OutputStream outputStream) throws IOException {
        for(var feature: features){
          var bases = sequenceForFeature(sequences, feature, forceStrandedness);
          FastaUtil.appendSequenceToStream(outputStream, feature, deflineFormat, bases, basesPerLine);
        }
      }
    };
  }
}
