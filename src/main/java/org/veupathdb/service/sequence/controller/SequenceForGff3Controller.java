package org.veupathdb.service.sequence.controller;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.StreamingOutput;

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import org.veupathdb.service.sequence.generated.model.DeflineFormat;
import org.veupathdb.service.sequence.generated.model.StartOffset;
import org.veupathdb.service.sequence.generated.model.SequenceForGff3SequenceTypePostMultipartFormData;
import org.veupathdb.service.sequence.generated.resources.SequenceForGff3SequenceType;

import org.veupathdb.service.sequence.util.StreamSequences;
import org.veupathdb.service.sequence.util.Validate;
import org.veupathdb.service.sequence.util.ConvertFeatures;
import org.veupathdb.service.sequence.service.Sequences;

import java.io.File;

public class SequenceForGff3Controller implements SequenceForGff3SequenceType {

  @Context
  private Request req;

  @Override
  @Authenticated
  public PostSequenceForGff3BySequenceTypeResponse postSequenceForGff3BySequenceType(
       String sequenceType,
       DeflineFormat deflineFormat,
       boolean forceStrandedness,
       int basesPerLine, SequenceForGff3SequenceTypePostMultipartFormData entity){
    var requestedFeatures = ConvertFeatures.featuresFromGff3(entity.getFile());

    var spec = Sequences.getSequenceSpec(sequenceType);
    var index = Sequences.getSequenceIndex(sequenceType);
    var sequences = Sequences.getSequenceFile(sequenceType);

    var features = Validate.getValidatedFeatures(sequenceType, index, requestedFeatures, forceStrandedness, spec);

    var stream = StreamSequences.responseStream(sequences, features, deflineFormat, forceStrandedness, basesPerLine);

    return PostSequenceForGff3BySequenceTypeResponse.respond200WithApplicationOctetStream(stream);

  }
}
