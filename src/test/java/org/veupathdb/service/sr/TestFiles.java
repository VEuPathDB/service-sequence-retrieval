package org.veupathdb.service.sr;

import java.io.File;
public class TestFiles {

  public static final File geneGff3(){
    return fileFromResource("veupathdb/service/sequence/query/gene.gff3");
  }
  public static final File firstThreeHundredBasesBed6(){
    return fileFromResource("veupathdb/service/sequence/query/contig_first_300_bases.bed6");
  }

  public static final File firstAndLastHundredFromFirstThreeHundredBasesBed12(){
    return fileFromResource("veupathdb/service/sequence/query/contig_first_and_last_hundred_from_first_300_bases.bed12");
  }

  public static final File proteinBed(){
    return fileFromResource("veupathdb/service/sequence/query/protein.bed");
  }

  private static final File fileFromResource(String resourcePath){
    var url = TestFiles.class.getClassLoader().getResource(resourcePath);
    try {
      return new File(url.toURI());
    } catch (Exception e){
      throw new RuntimeException(e);
    }
  }

}
