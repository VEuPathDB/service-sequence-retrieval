package org.veupathdb.service.sr.util;

import java.io.File;
import java.util.Scanner;
import java.io.FileInputStream;

import org.veupathdb.service.sr.generated.model.StartOffset;

import htsjdk.tribble.FeatureCodec;
import htsjdk.tribble.bed.BEDCodec;
import htsjdk.tribble.gff.Gff3Codec;

import org.veupathdb.service.sr.generated.model.Feature;
import org.veupathdb.service.sr.generated.model.FeatureImpl;

import java.util.List;
import java.util.ArrayList;
public class ConvertFeatures {
  
  public static List<Feature> featuresFromBed(File file, StartOffset startOffset){
    try(var scanner = new Scanner(file)) {
      var codec = getBedCodec(startOffset);
      var result = new ArrayList<Feature>();
      while(scanner.hasNextLine()){
        var htsjdkFeature = codec.decode(scanner.nextLine());
        Feature feature = new FeatureImpl();
        feature.setContig(htsjdkFeature.getContig());
        feature.setStart(htsjdkFeature.getStart());
        feature.setEnd(htsjdkFeature.getEnd());
        feature.setQuery(htsjdkFeature.getName());
        feature.setStrand(ourStrand(htsjdkFeature.getStrand()));
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
  public static List<Feature> featuresFromGff3(File file){
    try(var fileInputStream = new FileInputStream(file)) {
      var codec = new Gff3Codec();
      var result = new ArrayList<Feature>();
      var lineIterator = codec.makeSourceFromStream(fileInputStream);
      while(lineIterator.hasNext()){
        var htsjdkFeature = codec.decode(lineIterator);
        Feature feature = new FeatureImpl();
        feature.setContig(htsjdkFeature.getContig());
        feature.setStart(htsjdkFeature.getStart());
        feature.setEnd(htsjdkFeature.getEnd());
        feature.setQuery(htsjdkFeature.getName());
        feature.setStrand(ourStrand(htsjdkFeature.getStrand()));
        result.add(feature);
      }
      return result;
    } catch (Exception e){
      throw new RuntimeException(e);
    }
  }

  private static String ourStrand(htsjdk.tribble.annotation.Strand tribbleStrand){
    switch(tribbleStrand){
      case NEGATIVE:
        return "-";
      case POSITIVE:
        return "+";
      case NONE:
        return ".";
    }
    return null;
  }

  private static BEDCodec getBedCodec(StartOffset startOffset){
    switch(startOffset){
      case ZERO:
        return new BEDCodec(BEDCodec.StartOffset.ZERO);
      case ONE:
        return new BEDCodec(BEDCodec.StartOffset.ONE);
    }
    return null;
  }


}
