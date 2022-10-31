package org.veupathdb.service.sr.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.veupathdb.service.sr.generated.model.SequenceType;
import org.veupathdb.service.sr.config.ReferenceSequenceConfig;
import org.veupathdb.service.sr.config.ReferenceSequenceTypeSpec;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.FastaSequenceIndex;

import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class Sequences {

  private static final Logger LOG = LogManager.getLogger(Sequences.class);

  private static Map<SequenceType, ReferenceSequenceTypeSpec> sequenceSpecs;
  private static Map<SequenceType, FastaSequenceIndex> sequenceIndexes;
  private static Map<SequenceType, IndexedFastaSequenceFile> sequenceFiles;

  public static void initialize(ReferenceSequenceConfig config){
    sequenceSpecs = new HashMap<>();
    sequenceIndexes = new HashMap<>();
    sequenceFiles = new HashMap<>();
    for (Map.Entry<SequenceType, ReferenceSequenceTypeSpec> spec: config.entrySet()) {
      SequenceType name = spec.getKey();
      LOG.info("Initializing reference sequence: " + spec.getKey() + ". Heap status: "+heapStatus());
      sequenceSpecs.put(name, spec.getValue());
      var index = new FastaSequenceIndex(Paths.get(spec.getValue().getIndexFile()));
      sequenceIndexes.put(name, index);
      var file = new IndexedFastaSequenceFile(Paths.get(spec.getValue().getFastaFile()), index);
      sequenceFiles.put(name, file);
    }
    LOG.info("Initialized reference sequences. Heap status: " + heapStatus());
  }

  public static ReferenceSequenceTypeSpec getSequenceSpec(SequenceType sequenceType){
    return Objects.requireNonNull(sequenceSpecs.get(sequenceType), "Sequence spec not available for sequence type: " + sequenceType);
  }

  public static FastaSequenceIndex getSequenceIndex(SequenceType sequenceType){
    return Objects.requireNonNull(sequenceIndexes.get(sequenceType), "Sequence index not available for sequence type: " + sequenceType);
  }

  public static IndexedFastaSequenceFile getSequenceFile(SequenceType sequenceType){
    return Objects.requireNonNull(sequenceFiles.get(sequenceType), "Sequence file not available for sequence type: " + sequenceType);
  }

  // https://stackoverflow.com/a/44603197
  private static String heapStatus(){
    long heapSize = Runtime.getRuntime().totalMemory();
    long heapMaxSize = Runtime.getRuntime().maxMemory();
    long heapFreeSize = Runtime.getRuntime().freeMemory();
    return "heap size = " + formatSize(heapSize) + ", heap max size = " + formatSize(heapMaxSize) + ", current usage = " + formatSize(heapSize - heapFreeSize);
  }

  private static String formatSize(long v) {
    if (v < 1024) return v + " B";
    int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
    return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
  }
}
