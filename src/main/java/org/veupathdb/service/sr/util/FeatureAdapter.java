package org.veupathdb.service.sr.util;

import htsjdk.samtools.util.Interval;

import htsjdk.tribble.bed.BEDCodec;
import htsjdk.tribble.gff.Gff3Codec;

import htsjdk.tribble.bed.BEDFeature;
import htsjdk.tribble.bed.SimpleBEDFeature;
import htsjdk.tribble.annotation.Strand;

import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collection;
import java.io.InputStream;
import java.io.IOException;
import java.util.Optional;
public class FeatureAdapter {

  public static List<BEDFeature> toBEDFeatures(List<org.veupathdb.service.sr.generated.model.Feature> features){
    return features.stream().map(FeatureAdapter::toBEDFeature).collect(Collectors.toList());
  }

  private static BEDFeature toBEDFeature(org.veupathdb.service.sr.generated.model.Feature feature){
    var result = new SimpleBEDFeature(feature.getStart(), feature.getEnd(), feature.getContig());
    result.setStrand(toStrand(feature.getStrand()));
    result.setName(feature.getQuery());
    return result;
  }

  private static Strand toStrand(org.veupathdb.service.sr.generated.model.Strand s){
    return switch(Optional.ofNullable(s).orElse(org.veupathdb.service.sr.generated.model.Strand.NONE)){
      case POSITIVE -> Strand.POSITIVE;
      case NEGATIVE -> Strand.NEGATIVE;
      case NONE -> Strand.NONE;
    };
  }
  private static BEDCodec getBedCodec(org.veupathdb.service.sr.generated.model.StartOffset startOffset){
    return switch (startOffset) {
      case ZERO -> new BEDCodec(BEDCodec.StartOffset.ZERO);
      case ONE -> new BEDCodec(BEDCodec.StartOffset.ONE);
    };
  }

  public static List<BEDFeature> readBed(InputStream in, org.veupathdb.service.sr.generated.model.StartOffset startOffset){
    var scanner = new Scanner(in);
    var codec = getBedCodec(startOffset);
    var result = new ArrayList<BEDFeature>();
    while(scanner.hasNextLine()){
      var line = scanner.nextLine();
      // our .beds are tab-separated and can have spaces in fields
      try {
        result.add(codec.decode(line.split("\t")));
      } catch (Exception e){
        throw new RuntimeException("Error when parsing line: " + line, e);
      }
    }
    return result;
  }

  /* 
   * Read gff3
   * differences to reading .bed:
   * Gff3Feature and BedFeature don't have getName in their common interface
   * also gff3 is not read line by line, but feature by feature
  */ 
  public static List<BEDFeature> readGff3AndConvertToBed(InputStream in) {
    var codec = new Gff3Codec();
    var result = new ArrayList<BEDFeature>();
    try {
      var lineIterator = codec.makeSourceFromStream(in);
      while(! codec.isDone(lineIterator)){
        var gff3Feature = codec.decode(lineIterator);
        if (gff3Feature != null && gff3Feature.isTopLevelFeature()) {

          // bed format is zero based half open;  need to adjust the start coord (not end) as gff will be 1 based closed
          int zeroBasedStart = gff3Feature.getStart() - 1;

          var bedFeature = new SimpleBEDFeature(zeroBasedStart, gff3Feature.getEnd(), gff3Feature.getContig());
          bedFeature.setStrand(gff3Feature.getStrand());
          bedFeature.setName(gff3Feature.getName());
          result.add(bedFeature);
        }
      }
      return result;
    }
    catch(IOException e){
      throw new RuntimeException(e);
    }
  }

  public static void setStrandToNone(Collection<BEDFeature> features){
    features.stream().forEach(f -> ((SimpleBEDFeature) f).setStrand(Strand.NONE));
  }

}
