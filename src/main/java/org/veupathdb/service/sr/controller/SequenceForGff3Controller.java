package org.veupathdb.service.sr.controller;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.StreamingOutput;
import org.glassfish.jersey.server.ContainerRequest;

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.generated.model.StartOffset;
import org.veupathdb.service.sr.generated.model.SequenceForGff3SequenceTypePostMultipartFormData;
import org.veupathdb.service.sr.generated.resources.SequenceForGff3SequenceType;

import org.veupathdb.service.sr.util.StreamSequences;
import org.veupathdb.service.sr.util.Validate;
import org.veupathdb.service.sr.util.ConvertFeatures;
import org.veupathdb.service.sr.service.Sequences;

import java.io.File;

public class SequenceForGff3Controller implements SequenceForGff3SequenceType {

  @Context
  private ContainerRequest req;

  @Override
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

    StreamingOutput stream = StreamSequences.responseStream(sequences, features, deflineFormat, forceStrandedness, basesPerLine);

    return PostSequenceForGff3BySequenceTypeResponse.respond200WithApplicationOctetStream(stream);

  }
}
