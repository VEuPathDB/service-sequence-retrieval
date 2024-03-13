package org.veupathdb.service.sr.response;

import htsjdk.tribble.bed.SimpleBEDFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.veupathdb.service.sr.generated.model.DeflineFormat;

public class DeflinesTest {

  @Test
  public void testEndOfSequenceTruncated() {
    final SimpleBEDFeature bedFeature = new SimpleBEDFeature(0, 50, "x");
    bedFeature.setName("My Bed Feature");
    final String defLine = Deflines.deflineForFeature(bedFeature, DeflineFormat.QUERYONLY, 49);
    Assertions.assertEquals(">My Bed Feature | error_code=TRUNCATED_END_OF_SEQUENCE", defLine);
  }

  @Test
  public void testStartAfterEndTruncated() {
    final SimpleBEDFeature bedFeature = new SimpleBEDFeature(51, 50, "x");
    bedFeature.setName("My Bed Feature");
    final String defLine = Deflines.deflineForFeature(bedFeature, DeflineFormat.QUERYONLY, 10000);
    Assertions.assertEquals(">My Bed Feature | error_code=TRUNCATED_NEGATIVE_LENGTH", defLine);
  }
}
