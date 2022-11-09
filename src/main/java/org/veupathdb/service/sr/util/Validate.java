package org.veupathdb.service.sr.util;


import jakarta.ws.rs.BadRequestException;

import htsjdk.samtools.reference.FastaSequenceIndex;
import org.veupathdb.service.sr.generated.model.Feature;
import org.veupathdb.service.sr.generated.model.SequenceType;
import org.veupathdb.service.sr.config.ReferenceSequenceTypeSpec;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import htsjdk.samtools.util.Locatable;

public class Validate {

  public static <T extends Locatable> List<T> getValidatedFeatures(SequenceType sequenceType, FastaSequenceIndex index, List<T> features, boolean forceStrandedness, ReferenceSequenceTypeSpec spec){
    if(forceStrandedness && spec.getDisallowReverseComplement()){
      throw new BadRequestException("Reverse complement option not available for sequence type " + sequenceType);
    }
    if(features.size() > spec.getMaxSequencesPerRequest()){
      throw new BadRequestException("Requested more features than per-request limit of " + spec.getMaxSequencesPerRequest());
    }
    if(features.size() == 0){
      throw new BadRequestException("No features requested");
    }
    if( features.stream().mapToInt(f -> f.getLengthOnReference()).sum() > spec.getMaxTotalBasesPerRequest()){
      throw new BadRequestException("Requested more total bases than per-request limit of " + spec.getMaxTotalBasesPerRequest());
    }
    return features;
  }

}
