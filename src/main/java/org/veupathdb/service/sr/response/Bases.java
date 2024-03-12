package org.veupathdb.service.sr.response;

import htsjdk.tribble.bed.BEDFeature;
import htsjdk.tribble.annotation.Strand;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.util.SequenceUtil;
import htsjdk.tribble.bed.FullBEDFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bases {
  private static final Logger LOG = LogManager.getLogger(Bases.class);

  public static byte[] getBasesForBedFeature(IndexedFastaSequenceFile sequences, BEDFeature feature) {
    // Don't ask SAMTools for a feature whose end exceeds the length of the sequence.
    long sequenceEnd = Math.min(sequences.getIndex().getIndexEntry(feature.getContig()).getSize(), feature.getEnd());

    // Don't ask SAMTools for a negative-length sequence. These are truncated and an empty sequence is returned.
    if (feature.getStart() > feature.getEnd()) {
      return new byte[0];
    }

    return getBasesForBedFeature(sequences.getSubsequenceAt(feature.getContig(), feature.getStart(), sequenceEnd), feature);
  }

  // for unit test
  static byte[] getBasesForBedFeature(ReferenceSequence subsequence, BEDFeature feature) {
    LOG.debug("Retrieving base pairs for subsequence " + subsequence.getName());

    var bases = subsequence.getBases();
    var basesStart = feature.getStart();
    var exons = feature.getExons();

    if (exons.size() > 0) {
      var newLength = exons.stream().mapToInt(FullBEDFeature.Exon::getCodingLength).sum();
      var newBases = new byte[newLength];
      int newBasesOffset = 0;
      for (var e : exons) {
        var src = bases;
        var srcPos = e.getCdStart() - basesStart;
        var dest = newBases;
        var destPos = newBasesOffset;
        var length = e.getCodingLength();
        System.arraycopy(src, srcPos, dest, destPos, length);
        newBasesOffset += length;
      }
      bases = newBases;
    }
    if (feature.getStrand().equals(Strand.NEGATIVE)) {
      SequenceUtil.reverseComplement(bases);
    }
    return bases;
  }

}
