package org.veupathdb.service.sr.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.FastaSequenceIndexEntry;
import org.veupathdb.service.sr.generated.model.SequenceType;
import org.veupathdb.service.sr.config.TestReferenceSequenceConfig;

import java.util.HashSet;
import java.util.Set;

public class SequencesTest {

  @Test
  public void testLoadContigs(){
    Sequences.initialize(new TestReferenceSequenceConfig());
    var genomicIndex = Sequences.getSequenceIndex(SequenceType.GENOMIC);

    var contigs = new HashSet<String>();
    var it = genomicIndex.iterator();
    while(it.hasNext()){
       contigs.add(it.next().getContig());
    }
    assertEquals(contigs, Set.of("CDFG01000062.1", "CDFG01000031.1", "CDFG01000056.1", "CDFG01000033.1", "CDFG01000013.1", "CDFG01000024.1", "CDFG01000025.1", "CDFG01000035.1", "CDFG01000017.1", "CDFG01000028.1", "KB823016"
));

  }

}
