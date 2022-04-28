package org.veupathdb.service.sequence.controller;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.StreamingOutput;

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import org.veupathdb.service.sequence.generated.model.SequenceGenomicPostRequest;
import org.veupathdb.service.sequence.generated.resources.SequenceGenomic;

import org.veupathdb.service.sequence.util.StreamSequences;
import org.veupathdb.service.sequence.service.Sequences;

public class SequenceGenomicController implements SequenceGenomic {

  @Context
  private Request req;

  @Override
  @Authenticated
  public PostSequenceGenomicResponse postSequenceGenomic(SequenceGenomicPostRequest entity) {

    var features = entity.getFeatures();

    var sequences = Sequences.getSequenceFile("genomic");

    var spec = Sequences.getSequenceSpec("genomic");
    var index = Sequences.getSequenceIndex("genomic");

    //todo validate features using index and spec, produce new format

    var stream = StreamSequences.responseStream(sequences, features, entity.getDeflineFormat(), entity.getForceStrandedness(), entity.getBasesPerLine());

    return PostSequenceGenomicResponse.respond200WithApplicationOctetStream(stream);

  }
}
