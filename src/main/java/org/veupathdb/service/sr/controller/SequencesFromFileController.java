package org.veupathdb.service.sr.controller;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.server.ContainerRequest;
import org.veupathdb.service.sr.generated.model.*;
import org.veupathdb.service.sr.generated.resources.SequencesForFileFormatSequenceType;
import org.veupathdb.service.sr.service.Sequences;
import org.veupathdb.service.sr.util.ConvertFeatures;
import org.veupathdb.service.sr.util.EnumUtil;
import org.veupathdb.service.sr.util.StreamSequences;
import org.veupathdb.service.sr.util.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SequencesFromFileController implements SequencesForFileFormatSequenceType {

  @Context
  private ContainerRequest req;

  @Override
  public PostSequencesForByFileFormatAndSequenceTypeResponse postSequencesForByFileFormatAndSequenceType(
      String fileFormatStr,
      String sequenceType,
      DeflineFormat deflineFormat,
      Boolean forceStrandedness,
      Integer basesPerLine,
      StartOffset startOffset,
      String uploadMethodStr,
      InputStream file,
      FormDataContentDisposition meta,
      String url) {

    // throw not found since this is a path param
    var fileFormat = EnumUtil.validate(fileFormatStr, SupportedFileFormat.values(), NotFoundException::new);
    // throw bad request since this is in the request body
    var uploadMethod = EnumUtil.validate(uploadMethodStr, UploadMethod.values(), BadRequestException::new);

    try (InputStream fileStream = switch (uploadMethod) {
      case FILE -> file;
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

      return PostSequencesForByFileFormatAndSequenceTypeResponse.respond200WithTextXFasta(new PlainTextFastaResponseStream(
          StreamSequences.responseStream(sequences, features, deflineFormat, forceStrandedness, basesPerLine)
      ));

    } catch (IOException e) {
      throw new RuntimeException("Unable to complete file processing", e);
    }
  }
}
