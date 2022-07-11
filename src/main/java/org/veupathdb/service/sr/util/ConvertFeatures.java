package org.veupathdb.service.sr.util;

import htsjdk.tribble.bed.BEDCodec;
import htsjdk.tribble.gff.Gff3Codec;
import org.veupathdb.service.sr.generated.model.Feature;
import org.veupathdb.service.sr.generated.model.FeatureImpl;
import org.veupathdb.service.sr.generated.model.StartOffset;
import org.veupathdb.service.sr.generated.model.Strand;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConvertFeatures {
  
  public static List<Feature> featuresFromBed(InputStream in, StartOffset startOffset){
    try (var scanner = new Scanner(in)) {
      var codec = getBedCodec(startOffset);
      var result = new ArrayList<Feature>();
      while(scanner.hasNextLine()){
        var htsjdkFeature = codec.decode(scanner.nextLine());
        Feature feature = new FeatureImpl();
        feature.setContig(htsjdkFeature.getContig());
        feature.setStart(htsjdkFeature.getStart());
        feature.setEnd(htsjdkFeature.getEnd());
        feature.setQuery(htsjdkFeature.getName());
        feature.setStrand(translateStrandEnum(htsjdkFeature.getStrand()));
        result.add(feature);
      }
      return result;
    } catch (Exception e){
      throw new RuntimeException(e);
    }
  }

  /* 
   * Read gff3
   * differences to reading .bed:
   * Gff3Feature and BedFeature don't have getName in their common interface
   * also gff3 is not read line by line, but feature by feature
  */ 
  public static List<Feature> featuresFromGff3(InputStream in) throws IOException {
    var codec = new Gff3Codec();
    var result = new ArrayList<Feature>();
    var lineIterator = codec.makeSourceFromStream(in);
    while(lineIterator.hasNext()){
      var htsjdkFeature = codec.decode(lineIterator);
      if (htsjdkFeature != null) {
        Feature feature = new FeatureImpl();
        feature.setContig(htsjdkFeature.getContig());
        feature.setStart(htsjdkFeature.getStart());
        feature.setEnd(htsjdkFeature.getEnd());
        feature.setQuery(htsjdkFeature.getName());
        feature.setStrand(translateStrandEnum(htsjdkFeature.getStrand()));
        result.add(feature);
      }
    }
    return result;
  }

  private static Strand translateStrandEnum(htsjdk.tribble.annotation.Strand tribbleStrand){
    return switch (tribbleStrand) {
      case POSITIVE -> Strand.POSITIVE;
      case NEGATIVE -> Strand.NEGATIVE;
      case NONE -> Strand.NONE;
    };
  }

  private static BEDCodec getBedCodec(StartOffset startOffset){
    return switch (startOffset) {
      case ZERO -> new BEDCodec(BEDCodec.StartOffset.ZERO);
      case ONE -> new BEDCodec(BEDCodec.StartOffset.ONE);
    };
  }

}
