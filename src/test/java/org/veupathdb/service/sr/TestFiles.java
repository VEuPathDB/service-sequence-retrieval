package org.veupathdb.service.sr;

import java.io.InputStream;
public class TestFiles {

  public static final InputStream geneGff3(){
    return TestFiles.class.getClassLoader().getResourceAsStream("veupathdb/service/sequence/query/gene.gff3");
  }

  public static final InputStream proteinBed(){
    return TestFiles.class.getClassLoader().getResourceAsStream("veupathdb/service/sequence/query/protein.bed");
  }

}
