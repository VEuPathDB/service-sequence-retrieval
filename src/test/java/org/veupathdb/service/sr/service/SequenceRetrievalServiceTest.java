package org.veupathdb.service.sr.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

import org.veupathdb.service.sr.service.SequenceRetrievalService;
import org.veupathdb.service.sr.generated.model.*;

import org.veupathdb.service.sr.util.Sequences;
import org.veupathdb.service.sr.config.TestReferenceSequenceConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;

import org.veupathdb.service.sr.TestFiles;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.generated.model.StartOffset;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;


public class SequenceRetrievalServiceTest {

  @BeforeEach
  public void setUp(){
    Sequences.initialize(new TestReferenceSequenceConfig());
  }

  @Test
  public void testJsonGenomic() throws Exception {
    var json = """
{"features": [{"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q1", "strand": "POSITIVE"}, {"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q2", "strand": "NEGATIVE"}, {"contig": "CDFG01000013.1" , "start": 1, "end": 7, "query": "Q3", "strand": "NONE"}], "deflineFormat": "QUERYANDREGION", "basesPerLine": 60, "forceStrandedness": true}';
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
{"features": [{"contig": "EHI7A_117830-t26_1-p1" , "start": 1, "end": 7, "query": "QUERY" }], "deflineFormat": "QUERYANDREGION", "basesPerLine": 60, "forceStrandedness": false}'
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
    var forceStrandedness = false;
    var basesPerLine = 60;
    var startOffset = StartOffset.ZERO;
    var uploadMethodStr = "FILE";
    var file = TestFiles.proteinBed();
    var meta = (FormDataContentDisposition) null;
    var url = "";
    var response = new SequenceRetrievalService().postSequencesBySequenceTypeAndFileFormat(sequenceTypeStr, fileFormatStr, deflineFormat, forceStrandedness, basesPerLine, startOffset, uploadMethodStr, file, meta, url);
    
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
    var forceStrandedness = false;
    var basesPerLine = 60;
    var startOffset = (StartOffset) null;
    var uploadMethodStr = "FILE";
    var file = TestFiles.geneGff3();
    var meta = (FormDataContentDisposition) null;
    var url = "";
    var response = new SequenceRetrievalService().postSequencesBySequenceTypeAndFileFormat(sequenceTypeStr, fileFormatStr, deflineFormat, forceStrandedness, basesPerLine, startOffset, uploadMethodStr, file, meta, url);
    
    assertEquals(response.getStatus(), 200);

  }

  private void testJson(String json, String sequenceTypeStr, String expected) throws Exception {

    var sequencePostRequest = new ObjectMapper().readValue(json, SequencePostRequest.class);

    var response = new SequenceRetrievalService().postSequencesBySequenceType(sequenceTypeStr, sequencePostRequest);
    assertEquals(bodyText(response), expected);
  }

  private static String bodyText(Response response) throws Exception {
    var e = (org.veupathdb.service.sr.generated.model.PlainTextFastaResponseStream) response.getEntity();
    var baos = new ByteArrayOutputStream();
    e.write(baos);
    return baos.toString();
  }

}

