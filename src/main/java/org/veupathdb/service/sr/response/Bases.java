package org.veupathdb.service.sr.response;

import htsjdk.tribble.bed.BEDFeature;
import htsjdk.tribble.annotation.Strand;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.util.SequenceUtil;

public class Bases {

  public static byte[] getBasesForBedFeature(IndexedFastaSequenceFile sequences, BEDFeature feature){

    return getBasesForBedFeature(sequences.getSubsequenceAt(feature.getContig(), feature.getStart(), feature.getEnd()), feature);
  }

  // for unit test
  static byte[] getBasesForBedFeature(ReferenceSequence subsequence, BEDFeature feature){

    var bases = subsequence.getBases();
    var exons = feature.getExons();

    if(exons.size() > 0){
      var newLength =  exons.stream().mapToInt(e -> e.getCodingLength()).sum();
      var newBases = new byte[newLength];
      int newBasesOffset = 0;
      for (var e: exons){
        var src = bases;
        var srcPos = e.getCdStart() - 1; // tribble is 1-based but Java arrays are 0-based
        var dest = newBases;
        var destPos = newBasesOffset;
        var length = e.getCodingLength();
        System.arraycopy(src, srcPos, dest, destPos, length);
        newBasesOffset += length;
      }
      bases = newBases;
    }
    if(feature.getStrand().equals(Strand.NEGATIVE)){
      SequenceUtil.reverseComplement(bases);
    }
    return bases;
  }

}
