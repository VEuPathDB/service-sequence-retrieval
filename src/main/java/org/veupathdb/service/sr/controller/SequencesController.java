package org.veupathdb.service.sr.controller;

import jakarta.ws.rs.NotFoundException;
import org.veupathdb.service.sr.generated.model.PlainTextFastaResponseStream;
import org.veupathdb.service.sr.generated.model.SequencePostRequest;
import org.veupathdb.service.sr.generated.model.SequenceType;
import org.veupathdb.service.sr.generated.resources.SequencesSequenceType;
import org.veupathdb.service.sr.service.Sequences;
import org.veupathdb.service.sr.util.EnumUtil;
import org.veupathdb.service.sr.util.StreamSequences;
import org.veupathdb.service.sr.util.Validate;

public class SequencesController implements SequencesSequenceType {

  @Override
  public PostSequencesBySequenceTypeResponse postSequencesBySequenceType(String sequenceTypeStr, SequencePostRequest entity) {
    var forceStrandedness = entity.getForceStrandedness();
    var deflineFormat = entity.getDeflineFormat();
    var basesPerLine = entity.getBasesPerLine();
    var requestedFeatures = entity.getFeatures();
    var sequenceType = EnumUtil.validate(sequenceTypeStr, SequenceType.values(), NotFoundException::new);

    var spec = Sequences.getSequenceSpec(sequenceType);
    var index = Sequences.getSequenceIndex(sequenceType);
    var sequences = Sequences.getSequenceFile(sequenceType);

    var features = Validate.getValidatedFeatures(sequenceType, index, requestedFeatures, forceStrandedness, spec);

    return PostSequencesBySequenceTypeResponse.respond200WithTextXFasta(new PlainTextFastaResponseStream(
        StreamSequences.responseStream(sequences, features, deflineFormat, forceStrandedness, basesPerLine)
    ));

  }

}
