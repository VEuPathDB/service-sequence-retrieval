package org.veupathdb.service.sequence.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.FastaSequenceIndex;

import org.veupathdb.service.sequence.service.Sequences;

@DisplayName("StreamSequences")
class StreamSequences {

  @Test
  @DisplayName("Read a sequence")
  public void test1() throws IOException {
    IndexedFastaSequenceFile sequence = Sequences.readFromPaths(
      "src/test/resources/veupathdb/service/sequence/reference/Genome.fa",
      "src/test/resources/veupathdb/service/sequence/reference/Genome.fa.fai");

    assertEquals(sequence, null);
  }

}
