package org.veupathdb.service.sr.response;

import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.tribble.annotation.Strand;
import htsjdk.tribble.bed.BEDFeature;
import htsjdk.tribble.bed.FullBEDFeature;
import htsjdk.tribble.bed.SimpleBEDFeature;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasesTest {

  String name = "CDFG01000062.1";
  ReferenceSequence subsequence = new ReferenceSequence(name, -1, "GCTG".getBytes());

  private void sequenceForFeatureIs(BEDFeature feature, String expected) throws Exception {
    var baos = new ByteArrayOutputStream();
    baos.write(Bases.getBasesForBedFeature(subsequence, feature));
    assertEquals(expected, baos.toString());
  }

  @Test
  public void testGetBases() throws Exception {
    var feature = new SimpleBEDFeature(1,4, name);
    sequenceForFeatureIs(feature, "GCTG");
  }

  @Test
  public void testGetBasesRc() throws Exception {
    var feature = new SimpleBEDFeature(1,4, name);
    feature.setStrand(Strand.NEGATIVE);
    sequenceForFeatureIs(feature, "CAGC");
  }

  @Test
  public void testGetBasesNotStartingAtOneWithOneExon() throws Exception {
    var feature = new FullBEDFeature(name, 1001,1004);
    feature.addExon(feature.new Exon(1001,1004));
    sequenceForFeatureIs(feature, "GCTG");
  }

  @Test
  public void testGetBasesExons() throws Exception {
    var feature = new FullBEDFeature(name, 1,4);
    feature.addExon(feature.new Exon(1,2));
    feature.addExon(feature.new Exon(4,4));
    sequenceForFeatureIs(feature, "GCG");
  }
}
