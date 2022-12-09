package org.veupathdb.service.sr.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.*;

import org.veupathdb.service.sr.service.SequenceRetrievalService;
import org.veupathdb.service.sr.generated.model.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;

import org.veupathdb.service.sr.TestQueries;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.generated.model.StartOffset;
import org.veupathdb.service.sr.generated.model.SequencesSequenceTypeFileFormatPostMultipartFormDataImpl;
import org.veupathdb.service.sr.generated.model.UploadMethod;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;


public class FeatureAdapterTest {

  @Test
  public void testReadBed() throws Exception {
    var features = FeatureAdapter.readBed(new FileInputStream(TestQueries.proteinBed()), StartOffset.ZERO);
    assertEquals(features.size(), 1);
    assertEquals(features.get(0).getContig(), "EHI7A_117830-t26_1-p1");
    assertEquals(features.get(0).getStart(), 1);
    assertEquals(features.get(0).getEnd(), 7);
    assertEquals(features.get(0).getName(), "");

  }

  @Test
  public void testReadBedGenomic() throws Exception {
    var features = FeatureAdapter.readBed(new FileInputStream(TestQueries.firstThreeHundredBasesBed6()), StartOffset.ZERO);
    assertEquals(features.size(), 1);
    assertEquals(features.get(0).getContig(), "KB823016");
    assertEquals(features.get(0).getStart(), 1);
    assertEquals(features.get(0).getEnd(), 300);
    assertEquals(features.get(0).getName(), "gene_id");
    assertEquals(features.get(0).getStrand().encodeAsChar(), '+');
  }

  @Test
  public void testReadBadJawn() {
    Throwable exception = assertThrows(Exception.class, () -> FeatureAdapter.readBed(new ByteArrayInputStream("\tBad jawn\n".getBytes("UTF-8")), StartOffset.ZERO));
    assertEquals("Error when parsing line: \tBad jawn", exception.getMessage());

  }
}
