package org.veupathdb.service.sr.service;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.veupathdb.service.sr.generated.model.*;
import org.veupathdb.service.sr.generated.resources.SequencesSequenceType;
import org.veupathdb.service.sr.util.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URL;

public class SequenceRetrievalService implements SequencesSequenceType {

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

  @Override
  public PostSequencesBySequenceTypeAndFileFormatResponse postSequencesBySequenceTypeAndFileFormat(
      String sequenceTypeStr,
      String fileFormatStr,
      DeflineFormat deflineFormat,
      Boolean forceStrandedness,
      Integer basesPerLine,
      StartOffset startOffset,
      SequencesSequenceTypeFileFormatPostMultipartFormData entity){

      var uploadMethod = entity.getUploadMethod();
      var file = entity.getFile();
      var url = entity.getUrl();

    // throw not found since these are path params
    var fileFormat = EnumUtil.validate(fileFormatStr, SupportedFileFormat.values(), NotFoundException::new);
    var sequenceType = EnumUtil.validate(sequenceTypeStr, SequenceType.values(), NotFoundException::new);

    try (InputStream fileStream = switch (uploadMethod) {
      case FILE -> new FileInputStream(file);
      case URL -> new URL(url).openStream();
    }) {

      var requestedFeatures = switch (fileFormat) {
        case BED -> ConvertFeatures.featuresFromBed(fileStream, startOffset);
        case GFF3 -> ConvertFeatures.featuresFromGff3(fileStream);
      };

      var spec = Sequences.getSequenceSpec(sequenceType);
      var index = Sequences.getSequenceIndex(sequenceType);
      var sequences = Sequences.getSequenceFile(sequenceType);

      var features = Validate.getValidatedFeatures(sequenceType, index, requestedFeatures, forceStrandedness, spec);

      return PostSequencesBySequenceTypeAndFileFormatResponse.respond200WithTextXFasta(new PlainTextFastaResponseStream(
          StreamSequences.responseStream(sequences, features, deflineFormat, forceStrandedness, basesPerLine)
      ));

    } catch (IOException e) {
      throw new RuntimeException("Unable to complete file processing", e);
    }
  }
}
