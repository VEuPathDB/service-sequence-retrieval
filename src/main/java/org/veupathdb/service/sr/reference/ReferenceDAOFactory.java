package org.veupathdb.service.sr.reference;

import org.gusdb.fgputil.runtime.Environment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

/*
 * Create ReferenceDAOs as configured for the service
 *
 * On startup: ReferenceDAOFactory.init()
 * In each web request: ReferenceDAOFactory.get(sequenceType)
 */
public class ReferenceDAOFactory {
  
  private static final String ALL_REFERENCE_SEQUENCE_NAMES = "ALL_REFERENCE_SEQUENCE_NAMES";

  // environment variable suffixes (used for each available sequence type)
  private static final String SUFFIX_FASTA_FILE = "_FASTA_FILE";
  private static final String SUFFIX_INDEX_FILE = "_INDEX_FILE";
  private static final String SUFFIX_MAX_SEQUENCES_PER_REQUEST = "_MAX_SEQUENCES_PER_REQUEST";
  private static final String SUFFIX_MAX_TOTAL_BASES_PER_REQUEST = "_MAX_TOTAL_BASES_PER_REQUEST";
  private static final String SUFFIX_IS_STRANDED = "_IS_STRANDED";

  // populated in unit tests
  protected static Map<String, ReferenceDAO> instances;

  public static ReferenceDAO get(String sequenceType) {
    return Objects.requireNonNull(Objects.requireNonNull(instances, "Error: get called before init").get(sequenceType.toLowerCase()), "Sequence file not available for sequence type: " + sequenceType);
  }

  public static void init(){
    instances = new HashMap<String, ReferenceDAO>();
    var sequenceNames = Arrays.asList(Environment.getRequiredVar(ALL_REFERENCE_SEQUENCE_NAMES).split(","));
    if (sequenceNames.size() == 0){
      throw new RuntimeException("Required list: ALL_REFERENCE_SEQUENCE_NAMES=<eg. genomic,protein>");
    }
    for (var sequenceName: sequenceNames){
      instances.put(sequenceName.toLowerCase(), daoFromEnvVars(sequenceName));
    }
  }

  public static ReferenceDAO daoFromEnvVars(String sequenceName){
    return new ReferenceDAO(
      new ReferenceSequenceSpec(
        sequenceName,  
        longVar(sequenceName + SUFFIX_MAX_SEQUENCES_PER_REQUEST),
        longVar(sequenceName + SUFFIX_MAX_TOTAL_BASES_PER_REQUEST),
        booleanVar(sequenceName + SUFFIX_IS_STRANDED)
      ),
      pathVar(sequenceName + SUFFIX_INDEX_FILE),
      pathVar(sequenceName + SUFFIX_FASTA_FILE)
      );
  }

  private static Path pathVar(String k){
    Path result = Paths.get(Environment.getRequiredVar(k));
    if( ! Files.exists(result)){
      throw new RuntimeException("Resource corresponding to var " + k + " does not exist: " + result.toString());
    }
    return result;
  }
  private static long longVar(String k) {
    var s = Environment.getRequiredVar(k);
    // support scentific notation for integer values that can fit in a Long
    return new BigDecimal(s).longValueExact();
  }

  private static boolean booleanVar(String k){
    var s = Environment.getRequiredVar(k);
    if("true".equals(s.toLowerCase())){
      return true;
    } else if ("false".equals(s.toLowerCase())){
      return false;
    } else {
      throw new RuntimeException("Value for property " + k + " should be true/false, got: " + s);
    }
  }
}
