package org.veupathdb.service.sr.model;

import org.gusdb.fgputil.runtime.Environment;
import org.veupathdb.service.sr.generated.model.SequenceType;

import java.util.HashMap;

public class ReferenceSequenceConfig extends HashMap<SequenceType, ReferenceSequenceSpec> {

  // environment variable suffixes (used for each available sequence type)
  private static final String SUFFIX_FASTA_FILE = "_FASTA_FILE";
  private static final String SUFFIX_INDEX_FILE = "_INDEX_FILE";
  private static final String SUFFIX_MAX_SEQUENCES_PER_REQUEST = "_MAX_SEQUENCES_PER_REQUEST";
  private static final String SUFFIX_MAX_TOTAL_BASES_PER_REQUEST = "_MAX_TOTAL_BASES_PER_REQUEST";
  private static final String SUFFIX_DISALLOW_REVERSE_COMPLEMENT = "_DISALLOW_REVERSE_COMPLEMENT";

  // singleton pattern
  private static ReferenceSequenceConfig _instance;
  public static synchronized ReferenceSequenceConfig getInstance() {
    if (_instance == null) {
      _instance = new ReferenceSequenceConfig();
    }
    return _instance;
  }

  // loads config for each supported sequence type from environment vars
  private ReferenceSequenceConfig() {
    for (SequenceType type : SequenceType.values()) {
      ReferenceSequenceSpec typeConfig = new ReferenceSequenceSpec();
      typeConfig.setFastaFile(Environment.getRequiredVar(type.name() + SUFFIX_FASTA_FILE));
      typeConfig.setIndexFile(Environment.getRequiredVar(type.name() + SUFFIX_INDEX_FILE));
      typeConfig.setMaxSequencesPerRequest(Integer.parseInt(Environment.getRequiredVar(type.name() + SUFFIX_MAX_SEQUENCES_PER_REQUEST)));
      typeConfig.setMaxTotalBasesPerRequest(Integer.parseInt(Environment.getRequiredVar(type.name() + SUFFIX_MAX_TOTAL_BASES_PER_REQUEST)));
      typeConfig.setDisallowReverseComplement(Boolean.parseBoolean(Environment.getOptionalVar(type.name() + SUFFIX_DISALLOW_REVERSE_COMPLEMENT, Boolean.TRUE.toString())));
      put(type, typeConfig);
    }
  }
}
