package org.veupathdb.service.sr.response;

import htsjdk.tribble.bed.SimpleBEDFeature;
import org.junit.jupiter.api.Test;
import org.veupathdb.service.sr.TestReferences;
import org.veupathdb.service.sr.generated.model.DeflineFormat;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StreamSequencesTest {

  // NonActgContig.fa / LOWACTG01 is a fixed 10-base fixture "ACNNNNNNNN" (20% ACTG).
  private static final String LOW_ACTG_CONTIG = "LOWACTG01";

  @Test
  public void testPercentActgZeroDisablesFiltering() throws Exception {
    var feature = new SimpleBEDFeature(1, 10, LOW_ACTG_CONTIG);
    var output = write(List.of(feature), 0);
    assertEquals(">LOWACTG01:1-10\nACNNNNNNNN\n", output);
  }

  @Test
  public void testFeatureAtThresholdIsIncluded() throws Exception {
    var feature = new SimpleBEDFeature(1, 10, LOW_ACTG_CONTIG);
    // fixture is exactly 20% ACTG; threshold of exactly 20 must still include it (not-strictly-less-than)
    var output = write(List.of(feature), 20);
    assertEquals(">LOWACTG01:1-10\nACNNNNNNNN\n", output);
  }

  @Test
  public void testFeatureBelowThresholdIsSkipped() throws Exception {
    var feature = new SimpleBEDFeature(1, 10, LOW_ACTG_CONTIG);
    // fixture is exactly 20% ACTG; threshold of 21 must exclude it
    var output = write(List.of(feature), 21);
    assertEquals("", output);
  }

  private String write(List<SimpleBEDFeature> features, int percentActg) throws Exception {
    var baos = new ByteArrayOutputStream();
    TestReferences.lowActgDao
        .validateAndPrepareResponse(List.copyOf(features), DeflineFormat.REGIONONLY, 60, percentActg)
        .accept(baos);
    return baos.toString();
  }
}
