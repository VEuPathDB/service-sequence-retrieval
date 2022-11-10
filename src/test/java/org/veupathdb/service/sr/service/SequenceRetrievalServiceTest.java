package org.veupathdb.service.sr.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

import org.veupathdb.service.sr.service.SequenceRetrievalService;
import org.veupathdb.service.sr.generated.model.*;

import org.veupathdb.service.sr.TestReferences;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;

import org.veupathdb.service.sr.TestQueries;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.generated.model.StartOffset;
import org.veupathdb.service.sr.generated.model.SequencesSequenceTypeFileFormatPostMultipartFormDataImpl;
import org.veupathdb.service.sr.generated.model.UploadMethod;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;


public class SequenceRetrievalServiceTest {

  @BeforeEach
  public void setUp(){
    TestReferences.setUp();
  }

  @Test
  public void testJsonGenomic() throws Exception {
    var json = """
{"features": [{"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q1", "strand": "POSITIVE"}, {"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q2", "strand": "NEGATIVE"}, {"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q3", "strand": "NONE"}], "deflineFormat": "QUERYANDREGION", "basesPerLine": 60}';
""";
    var sequenceTypeStr = "GENOMIC";
    var expected = """
>Q1 CDFG01000013.1:1-7(+)
CTCGCCC
>Q2 CDFG01000013.1:1-7(-)
GGGCGAG
>Q3 CDFG01000013.1:1-7
CTCGCCC
""";
    testJson(json, sequenceTypeStr, expected);
  }

  @Test
  public void testJsonProtein() throws Exception {
  var json = """
{"features": [{"contig": "EHI7A_117830-t26_1-p1" , "start": 1, "end": 7, "query": "QUERY" }], "deflineFormat": "QUERYANDREGION", "basesPerLine": 60}'
""";
  var sequenceTypeStr = "PROTEIN";
  var expected = """
>QUERY EHI7A_117830-t26_1-p1:1-7
MSLTDQI
""";
    testJson(json, sequenceTypeStr, expected);
  }
  @Test
  public void testBedProtein() throws Exception {
    var sequenceTypeStr = "PROTEIN";
    var fileFormatStr = "BED";
    var deflineFormat = DeflineFormat.QUERYANDREGION;
    var basesPerLine = 60;
    var startOffset = StartOffset.ZERO;
    var entity = new SequencesSequenceTypeFileFormatPostMultipartFormDataImpl();
    entity.setUploadMethod(UploadMethod.FILE);
    entity.setFile(TestQueries.proteinBed());
    var response = new SequenceRetrievalService().postSequencesBySequenceTypeAndFileFormat(sequenceTypeStr, fileFormatStr, deflineFormat, basesPerLine, startOffset, entity);
    
    var expected = """
> EHI7A_117830-t26_1-p1:1-7
MSLTDQI
""";

    assertEquals(bodyText(response), expected);
  }

  @Test
  public void testGff3Genomic() throws Exception {
    var sequenceTypeStr = "GENOMIC";
    var fileFormatStr = "GFF3";
    var deflineFormat = DeflineFormat.QUERYANDREGION;
    var basesPerLine = 60;
    var startOffset = (StartOffset) null;
    var entity = new SequencesSequenceTypeFileFormatPostMultipartFormDataImpl();
    entity.setUploadMethod(UploadMethod.FILE);
    entity.setFile(TestQueries.geneGff3());
    var response = new SequenceRetrievalService().postSequencesBySequenceTypeAndFileFormat(sequenceTypeStr, fileFormatStr, deflineFormat, basesPerLine, startOffset, entity);
    assertEquals(response.getStatus(), 200);
  }

  @Test
  public void testBedGene() throws Exception {
    var sequenceTypeStr = "GENOMIC";
    var fileFormatStr = "BED";
    var deflineFormat = DeflineFormat.QUERYANDREGION;
    var basesPerLine = 100;
    var startOffset = StartOffset.ZERO;
    var entity = new SequencesSequenceTypeFileFormatPostMultipartFormDataImpl();


    entity.setUploadMethod(UploadMethod.FILE);
    entity.setFile(TestQueries.firstThreeHundredBasesBed6());
    var response = new SequenceRetrievalService().postSequencesBySequenceTypeAndFileFormat(sequenceTypeStr, fileFormatStr, deflineFormat, basesPerLine, startOffset, entity);
    assertEquals(response.getStatus(), 200);
    var expected = bodyText(response).split("\n");
    assertEquals(expected.length, 4);
    assertEquals(expected[0], ">gene_id KB823016:1-300(+)");
    assertEquals(expected[1].length(), 100);
    assertEquals(expected[1], "TATTGTATTTATTATAGCATTCCCTTATCCATTTCTAATTCAAATACATCATAATCAAATAATAACAAAGAGAGTCAATACTTGATTTGTACTAGGGTTT");
    assertEquals(expected[2].length(), 100);
    assertEquals(expected[3].length(), 100);

    var secondEntity = new SequencesSequenceTypeFileFormatPostMultipartFormDataImpl();
    secondEntity.setUploadMethod(UploadMethod.FILE);
    secondEntity.setFile(TestQueries.firstAndLastHundredFromFirstThreeHundredBasesBed12());
    var secondResponse = new SequenceRetrievalService().postSequencesBySequenceTypeAndFileFormat(sequenceTypeStr, fileFormatStr, deflineFormat, basesPerLine, startOffset, secondEntity);
    assertEquals(secondResponse.getStatus(), 200);
    var secondExpected = bodyText(secondResponse).split("\n");
    assertEquals(secondExpected.length, 3);
    assertEquals(secondExpected[1].length(), 100);
    assertEquals(secondExpected[2].length(), 100);
    assertEquals(secondExpected[0], expected[0]);

    assertEquals(secondExpected[1], expected[1]);
    assertEquals(secondExpected[2], expected[3]);

  }

  private void testJson(String json, String sequenceTypeStr, String expected) throws Exception {

    var sequencePostRequest = new ObjectMapper().readValue(json, SequencePostRequest.class);

    var response = new SequenceRetrievalService().postSequencesBySequenceType(sequenceTypeStr, sequencePostRequest);
    assertEquals(expected, bodyText(response));
  }

  private static String bodyText(Response response) throws Exception {
    var e = (org.veupathdb.service.sr.generated.model.PlainTextFastaResponseStream) response.getEntity();
    var baos = new ByteArrayOutputStream();
    e.write(baos);
    return baos.toString();
  }

}

