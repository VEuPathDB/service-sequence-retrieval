package org.veupathdb.service.sr;

import java.io.File;
public class TestFiles {

  public static final File geneGff3(){
    var url = TestFiles.class.getClassLoader().getResource("veupathdb/service/sequence/query/gene.gff3");
    try {
      return new File(url.toURI());
    } catch (Exception e){
      return null;
    }
  }

  public static final File proteinBed(){
    var url = TestFiles.class.getClassLoader().getResource("veupathdb/service/sequence/query/protein.bed");
    try {
      return new File(url.toURI());
    } catch (Exception e){
      return null;
    }
  }

}
