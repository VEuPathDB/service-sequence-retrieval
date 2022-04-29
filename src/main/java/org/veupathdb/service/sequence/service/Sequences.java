package org.veupathdb.service.sequence.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.veupathdb.service.sequence.model.Config;
import org.veupathdb.service.sequence.model.ReferenceSequenceSpec;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.FastaSequenceIndex;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class Sequences {
  private static final Logger LOG = LogManager.getLogger(Sequences.class);

  private static Map<String, ReferenceSequenceSpec> sequenceSpecs;
  private static Map<String, FastaSequenceIndex> sequenceIndexes;
  private static Map<String, IndexedFastaSequenceFile> sequenceFiles;

  public static void initialize(Config config){
    sequenceSpecs = new HashMap<>();
    sequenceIndexes = new HashMap<>();
    sequenceFiles = new HashMap<>();
    for(var spec: config.getReferenceSequenceSpecs()){
      var name = spec.getName();
      LOG.info("Initializing reference sequence: " + name);
      sequenceSpecs.put(name, spec);
      var index = new FastaSequenceIndex(Paths.get(spec.getIndex()));
      sequenceIndexes.put(name, index);
      var file = new IndexedFastaSequenceFile(Paths.get(spec.getFasta()), index);
      sequenceFiles.put(name, file);
    }
  }



  public static ReferenceSequenceSpec getSequenceSpec(String sequenceType){
    return Objects.requireNonNull(sequenceSpecs.get(sequenceType), "Sequence spec not available for sequence type: " + sequenceType);
  }

  public static FastaSequenceIndex getSequenceIndex(String sequenceType){
    return Objects.requireNonNull(sequenceIndexes.get(sequenceType), "Sequence index not available for sequence type: " + sequenceType);
  }

  public static IndexedFastaSequenceFile getSequenceFile(String sequenceType){
    return Objects.requireNonNull(sequenceFiles.get(sequenceType), "Sequence file not available for sequence type: " + sequenceType);
  }

}
