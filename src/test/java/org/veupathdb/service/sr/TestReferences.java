package org.veupathdb.service.sr;

import org.veupathdb.service.sr.reference.ReferenceDAO;
import org.veupathdb.service.sr.reference.ReferenceDAOFactory;
import org.veupathdb.service.sr.reference.ReferenceSequenceSpec;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
public class TestReferences extends ReferenceDAOFactory {
    

  public static final ReferenceSequenceSpec genomeSpec = new ReferenceSequenceSpec("genome", 10, 10000, true);
  public static final ReferenceDAO genomeDao = new ReferenceDAO(genomeSpec, ref("Genome.fa.fai.sqlite"), ref("Genome.fa"));

  public static final ReferenceSequenceSpec proteinSpec = new ReferenceSequenceSpec("protein", 3, 1_000_000, false);
  public static final ReferenceDAO proteinDao = new ReferenceDAO(proteinSpec, ref("AnnotatedProteins.fa.fai.sqlite"), ref("AnnotatedProteins.fa"));

  public static final ReferenceSequenceSpec estSpec = new ReferenceSequenceSpec("est", 10, 10000, true);
  public static final ReferenceDAO estDao = new ReferenceDAO(estSpec, ref("ESTs.fa.fai.sqlite"), ref("ESTs.fa"));

  public static final ReferenceSequenceSpec popsetSpec = new ReferenceSequenceSpec("popset", 10, 10000, true);
  public static final ReferenceDAO popsetDao = new ReferenceDAO(popsetSpec, ref("Isolates.fa.fai.sqlite"), ref("Isolates.fa"));

  public static void setUp(){
    instances = new HashMap<>();
    instances.put("genomic", genomeDao);
    instances.put("protein", proteinDao);
    instances.put("est", estDao);
    instances.put("popset", popsetDao);
  }

  private static Path ref(String name){
    var resourcePath = "veupathdb/service/sequence/reference/" + name;
    var resource = ReferenceDAOFactory.class.getClassLoader().getResource(resourcePath);
    if(resource != null){
      return Paths.get(resource.getFile());
    } else {
      throw new RuntimeException("Test resource not found: " + resourcePath);
    }
  }

}
