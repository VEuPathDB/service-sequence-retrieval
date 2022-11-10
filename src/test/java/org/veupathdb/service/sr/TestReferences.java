package org.veupathdb.service.sr;

import org.veupathdb.service.sr.reference.ReferenceDAO;
import org.veupathdb.service.sr.reference.ReferenceDAOFactory;
import org.veupathdb.service.sr.reference.ReferenceSequenceSpec;
import org.veupathdb.service.sr.reference.FastaSequenceIndexSqlite;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
public class TestReferences extends ReferenceDAOFactory {
    

  public static final ReferenceSequenceSpec genomeSpec = new ReferenceSequenceSpec("genome", 10, 10000, true);
  public static final FastaSequenceIndexSqlite genomeIndex = new FastaSequenceIndexSqlite(ref("Genome.fa.fai.sqlite"));
  public static final IndexedFastaSequenceFile genomeSequences = new IndexedFastaSequenceFile(ref("Genome.fa"), genomeIndex);
  public static final ReferenceDAO genomeDao = new ReferenceDAO(genomeSpec, ref("Genome.fa.fai.sqlite"), ref("Genome.fa"));

  public static final ReferenceSequenceSpec proteinSpec = new ReferenceSequenceSpec("protein", 3, 1_000_000, false);
  public static final FastaSequenceIndexSqlite proteinIndex = new FastaSequenceIndexSqlite(ref("AnnotatedProteins.fa.fai.sqlite"));
  public static final IndexedFastaSequenceFile proteinSequences = new IndexedFastaSequenceFile(ref("AnnotatedProteins.fa"), proteinIndex);
  public static final ReferenceDAO proteinDao = new ReferenceDAO(proteinSpec, ref("AnnotatedProteins.fa.fai.sqlite"), ref("AnnotatedProteins.fa"));

  public static void setUp(){
    instances = new HashMap<>();
    instances.put("genomic", genomeDao);
    instances.put("protein", proteinDao);
  }

  private static Path ref(String name){
    var folder = "veupathdb/service/sequence/reference";
    return Paths.get(ReferenceDAOFactory.class.getClassLoader().getResource(folder + "/" + name).getFile());
  }

}
