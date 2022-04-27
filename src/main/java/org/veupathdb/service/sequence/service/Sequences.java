package org.veupathdb.service.sequence.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.veupathdb.service.sequence.model.config.ExtOptions;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.FastaSequenceIndex;

import java.nio.file.Paths;
import java.nio.file.Path;

public class Sequences {
  private static final Logger LOG = LogManager.getLogger(Sequences.class);

  public static IndexedFastaSequenceFile genomicSequence;

  public static void initialize(ExtOptions options){
    LOG.info("Initializing sequences");

    genomicSequence = readFromPaths(
       options.getSequenceGenomicFasta().orElseThrow(()->new RuntimeException("Missing argument for sequenceGenomicFasta")),
       options.getSequenceGenomicFaidx().orElseThrow(()->new RuntimeException("Missing argument for sequenceGenomicFaidx"))
    );
  }

  public static IndexedFastaSequenceFile readFromPaths(String fasta, String faidx){
    return new IndexedFastaSequenceFile(
      Paths.get(fasta),
      new FastaSequenceIndex((Paths.get(faidx)))
    );
  }

}
