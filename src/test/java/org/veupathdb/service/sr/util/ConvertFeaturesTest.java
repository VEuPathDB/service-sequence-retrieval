package org.veupathdb.service.sr.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

import org.veupathdb.service.sr.util.ConvertFeatures;
import org.veupathdb.service.sr.TestFiles;

public class ConvertFeaturesTest {

  @Test
  public void testGff3() throws IOException {

    var result = ConvertFeatures.featuresFromGff3(TestFiles.geneGff3());

    // all top level features - for now
    assertEquals(2, result.size());
  }




}
