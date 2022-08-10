package org.veupathdb.service.sr.config;

import org.gusdb.fgputil.runtime.Environment;
import org.veupathdb.service.sr.generated.model.SequenceType;

import java.math.BigDecimal;
import java.util.HashMap;

public class ReferenceSequenceConfig extends HashMap<SequenceType, ReferenceSequenceTypeSpec> {

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
      populate(_instance);
    }
    return _instance;
  }

  // overrideable for unit tests
  protected ReferenceSequenceConfig() {

  }

  // loads config for each supported sequence type from environment vars
  private static void populate(ReferenceSequenceConfig self) {
    for (SequenceType type : SequenceType.values()) {
      ReferenceSequenceTypeSpec typeConfig = new ReferenceSequenceTypeSpec();
      typeConfig.setFastaFile(Environment.getRequiredVar(type.name() + SUFFIX_FASTA_FILE));
      typeConfig.setIndexFile(Environment.getRequiredVar(type.name() + SUFFIX_INDEX_FILE));
      typeConfig.setMaxSequencesPerRequest(toLong(Environment.getRequiredVar(type.name() + SUFFIX_MAX_SEQUENCES_PER_REQUEST)));
      typeConfig.setMaxTotalBasesPerRequest(toLong(Environment.getRequiredVar(type.name() + SUFFIX_MAX_TOTAL_BASES_PER_REQUEST)));
      typeConfig.setDisallowReverseComplement(Boolean.parseBoolean(Environment.getRequiredVar(type.name() + SUFFIX_DISALLOW_REVERSE_COMPLEMENT)));
      self.put(type, typeConfig);
    }
  }

  private static long toLong(String s) {
    // support scentific notation for integer values that can fit in a Long
    return new BigDecimal(s).longValueExact();
  }
}
