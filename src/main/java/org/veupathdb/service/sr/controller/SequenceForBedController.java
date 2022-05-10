package org.veupathdb.service.sr.controller;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.StreamingOutput;

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.generated.model.StartOffset;
import org.veupathdb.service.sr.generated.model.SequenceForBedSequenceTypePostMultipartFormData;
import org.veupathdb.service.sr.generated.resources.SequenceForBedSequenceType;

import org.veupathdb.service.sr.util.StreamSequences;
import org.veupathdb.service.sr.util.Validate;
import org.veupathdb.service.sr.util.ConvertFeatures;
import org.veupathdb.service.sr.service.Sequences;

import java.io.File;

public class SequenceForBedController implements SequenceForBedSequenceType {

  @Context
  private Request req;

  @Override
  @Authenticated
  public PostSequenceForBedBySequenceTypeResponse postSequenceForBedBySequenceType(
       String sequenceType,
       DeflineFormat deflineFormat,
       boolean forceStrandedness,
       StartOffset startOffset, int basesPerLine, SequenceForBedSequenceTypePostMultipartFormData entity){
    var requestedFeatures = ConvertFeatures.featuresFromBed(entity.getFile(), startOffset);

    var spec = Sequences.getSequenceSpec(sequenceType);
    var index = Sequences.getSequenceIndex(sequenceType);
    var sequences = Sequences.getSequenceFile(sequenceType);

    var features = Validate.getValidatedFeatures(sequenceType, index, requestedFeatures, forceStrandedness, spec);

    var stream = StreamSequences.responseStream(sequences, features, deflineFormat, forceStrandedness, basesPerLine);

    return PostSequenceForBedBySequenceTypeResponse.respond200WithApplicationOctetStream(stream);

  }
}
