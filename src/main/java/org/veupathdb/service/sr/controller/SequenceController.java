package org.veupathdb.service.sr.controller;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.StreamingOutput;
import org.glassfish.jersey.server.ContainerRequest;

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import org.veupathdb.service.sr.generated.model.SequencePostRequest;
import org.veupathdb.service.sr.generated.resources.SequenceSequenceType;

import org.veupathdb.service.sr.util.StreamSequences;
import org.veupathdb.service.sr.util.Validate;
import org.veupathdb.service.sr.service.Sequences;

public class SequenceController implements SequenceSequenceType {

  @Context
  private ContainerRequest req;

  @Override
  public PostSequenceBySequenceTypeResponse postSequenceBySequenceType(String sequenceType, SequencePostRequest entity) {
    var forceStrandedness = entity.getForceStrandedness();
    var deflineFormat = entity.getDeflineFormat();
    var basesPerLine = entity.getBasesPerLine();
    var requestedFeatures = entity.getFeatures();

    var spec = Sequences.getSequenceSpec(sequenceType);
    var index = Sequences.getSequenceIndex(sequenceType);
    var sequences = Sequences.getSequenceFile(sequenceType);

    var features = Validate.getValidatedFeatures(sequenceType, index, requestedFeatures, forceStrandedness, spec);

    StreamingOutput stream = StreamSequences.responseStream(sequences, features, deflineFormat, forceStrandedness, basesPerLine);

    return PostSequenceBySequenceTypeResponse.respond200WithApplicationOctetStream(stream);

  }
}
