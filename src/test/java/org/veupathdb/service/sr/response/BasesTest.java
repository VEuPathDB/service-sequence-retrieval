package org.veupathdb.service.sr.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

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
import org.veupathdb.service.sr.generated.model.SequenceType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import htsjdk.tribble.bed.BEDFeature;
import htsjdk.tribble.bed.SimpleBEDFeature;
import htsjdk.tribble.bed.FullBEDFeature;
import htsjdk.tribble.annotation.Strand;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public class BasesTest {

  @Test
  public void testGetBases() throws Exception {
    var sequences = TestReferences.genomeSequences;
    var feature = new SimpleBEDFeature(1,4, "CDFG01000062.1");

    var baos = new ByteArrayOutputStream();
    baos.write(Bases.getBasesForBedFeature(sequences, feature));
    assertEquals("GCTG", baos.toString());
  }

  @Test
  public void testGetBasesRc() throws Exception {
    var sequences = TestReferences.genomeSequences;
    var feature = new SimpleBEDFeature(1,4, "CDFG01000062.1");
    feature.setStrand(Strand.NEGATIVE);

    var baos = new ByteArrayOutputStream();
    baos.write(Bases.getBasesForBedFeature(sequences, feature));
    assertEquals("CAGC", baos.toString());
  }

  @Test
  public void testGetBasesExons() throws Exception {
    var sequences = TestReferences.genomeSequences;
    var feature = new FullBEDFeature("CDFG01000062.1", 1,4);
    feature.addExon(feature.new Exon(1,2));
    feature.addExon(feature.new Exon(4,4));

    var baos = new ByteArrayOutputStream();
    baos.write(Bases.getBasesForBedFeature(sequences, feature));
    assertEquals("GCG", baos.toString());
  }
}
