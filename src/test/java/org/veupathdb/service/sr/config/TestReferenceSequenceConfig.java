package org.veupathdb.service.sr.config;

import org.veupathdb.service.sr.generated.model.SequenceType;

public class TestReferenceSequenceConfig extends ReferenceSequenceConfig {
    

  public TestReferenceSequenceConfig(){
    ReferenceSequenceTypeSpec genomicTypeConfig = new ReferenceSequenceTypeSpec();
    genomicTypeConfig.setFastaFile(ref("Genome.fa"));
    genomicTypeConfig.setIndexFile(ref("Genome.fa.fai.sqlite"));
    genomicTypeConfig.setMaxSequencesPerRequest(10);
    genomicTypeConfig.setMaxTotalBasesPerRequest(10000);
    genomicTypeConfig.setDisallowReverseComplement(false);
    ReferenceSequenceTypeSpec proteinTypeConfig = new ReferenceSequenceTypeSpec();
    proteinTypeConfig.setFastaFile(ref("AnnotatedProteins.fa"));
    proteinTypeConfig.setIndexFile(ref("AnnotatedProteins.fa.fai.sqlite"));
    proteinTypeConfig.setMaxSequencesPerRequest(3);
    proteinTypeConfig.setMaxTotalBasesPerRequest(1_000_000);
    proteinTypeConfig.setDisallowReverseComplement(true);
    put(SequenceType.GENOMIC, genomicTypeConfig);
    put(SequenceType.PROTEIN, proteinTypeConfig);
  }

  private String ref(String name){
    var folder = "veupathdb/service/sequence/reference";
    return getClass().getClassLoader().getResource(folder + "/" + name).getFile();
  }

}
