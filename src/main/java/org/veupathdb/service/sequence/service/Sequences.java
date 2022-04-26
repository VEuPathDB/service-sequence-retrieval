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

    var sequenceGenomicFaidx = Paths.get(options.getSequenceGenomicFaidx().orElseThrow(()->new RuntimeException("Missing argument for sequenceGenomicFaidx")));
    var sequenceGenomicFasta = Paths.get(options.getSequenceGenomicFasta().orElseThrow(()->new RuntimeException("Missing argument for sequenceGenomicFasta")));
    var sequenceGenomicIndex = new FastaSequenceIndex(sequenceGenomicFaidx);
    genomicSequence = new IndexedFastaSequenceFile(sequenceGenomicFasta, sequenceGenomicIndex);

  }

}
