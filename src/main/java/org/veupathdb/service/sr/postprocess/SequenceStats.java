package org.veupathdb.service.sr.postprocess;

import htsjdk.tribble.bed.BEDFeature;

import java.util.List;

/**
 * Sequence count/length stats for a post-processing request, for logging.
 */
public class SequenceStats {

  public final int numSeqs;
  public final int maxSeqLength;
  public final int avgSeqLength;

  private SequenceStats(int numSeqs, int maxSeqLength, int avgSeqLength) {
    this.numSeqs = numSeqs;
    this.maxSeqLength = maxSeqLength;
    this.avgSeqLength = avgSeqLength;
  }

  public static SequenceStats of(List<BEDFeature> features) {
    int numSeqs = features.size();
    int maxSeqLength = 0;
    long totalSeqLength = 0;
    for (BEDFeature feature : features) {
      int length = feature.getEnd() - feature.getStart() + 1;
      maxSeqLength = Math.max(maxSeqLength, length);
      totalSeqLength += length;
    }
    int avgSeqLength = numSeqs == 0 ? 0 : (int) (totalSeqLength / numSeqs);
    return new SequenceStats(numSeqs, maxSeqLength, avgSeqLength);
  }

  @Override
  public String toString() {
    return "numSeqs=" + numSeqs + " maxSeqLength=" + maxSeqLength + " avgSeqLength=" + avgSeqLength;
  }
}
