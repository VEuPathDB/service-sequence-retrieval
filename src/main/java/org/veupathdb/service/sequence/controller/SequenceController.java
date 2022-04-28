package org.veupathdb.service.sequence.controller;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.StreamingOutput;

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import org.veupathdb.service.sequence.generated.model.SequencePostRequest;
import org.veupathdb.service.sequence.generated.resources.SequenceSequenceType;

import org.veupathdb.service.sequence.util.StreamSequences;
import org.veupathdb.service.sequence.util.Validate;
import org.veupathdb.service.sequence.service.Sequences;

public class SequenceController implements SequenceSequenceType {

  @Context
  private Request req;

  @Override
  @Authenticated
  public PostSequenceBySequenceTypeResponse postSequenceBySequenceType(String sequenceType, SequencePostRequest entity) {


    var spec = Sequences.getSequenceSpec(sequenceType);
    var index = Sequences.getSequenceIndex(sequenceType);
    var sequences = Sequences.getSequenceFile(sequenceType);

    var features = Validate.getValidatedFeatures(sequenceType, index, entity, spec);

    var stream = StreamSequences.responseStream(sequences, features, entity.getDeflineFormat(), entity.getForceStrandedness(), entity.getBasesPerLine());

    return PostSequenceBySequenceTypeResponse.respond200WithApplicationOctetStream(stream);

  }
}
