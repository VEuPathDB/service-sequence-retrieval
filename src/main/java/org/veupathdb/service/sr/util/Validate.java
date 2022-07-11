package org.veupathdb.service.sr.util;


import jakarta.ws.rs.BadRequestException;

import htsjdk.samtools.reference.FastaSequenceIndex;
import org.veupathdb.service.sr.generated.model.Feature;
import org.veupathdb.service.sr.generated.model.SequenceType;
import org.veupathdb.service.sr.model.ReferenceSequenceTypeSpec;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

public class Validate {

  public static List<Feature> getValidatedFeatures(SequenceType sequenceType, FastaSequenceIndex index, List<Feature> features, boolean forceStrandedness, ReferenceSequenceTypeSpec spec){
    if(forceStrandedness && spec.getDisallowReverseComplement()){
      throw new BadRequestException("Reverse complement option not available for sequence type" + sequenceType);
    }

    if(features.size() > spec.getMaxSequencesPerRequest()){
      throw new BadRequestException("Requested more features than per-request limit of " + spec.getMaxSequencesPerRequest());
    }
    if(features.size() == 0){
      throw new BadRequestException("No features requested");
    }
    var numBasesRequested = 0;
    var errors = new LinkedHashMap<String, List<String>>();
    var totalErrors = 0;
    for (var feature: features){
      var contig = feature.getContig();
      if(!index.hasIndexEntry(contig)){
        totalErrors += addError(errors, "Contig(s) not in index", contig);
        continue;
      }
      var indexEntry = index.getIndexEntry(contig);
      if(feature.getStart() < 1){
        totalErrors += addError(errors, "Requested start before contig start", feature.getQuery());
        continue;
      }
      if(feature.getStart() >= feature.getEnd()){
        totalErrors += addError(errors, "Requested start not before requested end", feature.getQuery());
        continue;
      }
      if(feature.getEnd() > indexEntry.getSize()){
        totalErrors += addError(errors, "Requested end after contig end", feature.getQuery());
        continue;
      }
      numBasesRequested += (feature.getEnd() - feature.getStart() + 1);
      if(numBasesRequested > spec.getMaxTotalBasesPerRequest()){
        throw new BadRequestException("Requested more total bases than per-request limit of " + spec.getMaxTotalBasesPerRequest());
      }
      if(totalErrors > 100 ){
        throw new BadRequestException(">100 errors: " + errors.toString());
      } 


    }

    if(totalErrors > 0){
      throw new BadRequestException(totalErrors + " errors: " + errors.toString());
    } 
    return features;
  }

  private static int addError(Map<String, List<String>> m, String reason, String value){
    m.putIfAbsent(reason, new ArrayList<>());
    m.get(reason).add(value);
    return 1;
  }

}
