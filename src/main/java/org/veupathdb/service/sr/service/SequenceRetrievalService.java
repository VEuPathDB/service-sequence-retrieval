package org.veupathdb.service.sr.service;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.veupathdb.service.sr.generated.model.*;
import org.veupathdb.service.sr.generated.resources.SequencesSequenceType;

import org.veupathdb.service.sr.reference.ReferenceDAOFactory;
import org.veupathdb.service.sr.util.FeatureAdapter;
import org.veupathdb.service.sr.util.EnumUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URL;

public class SequenceRetrievalService implements SequencesSequenceType {

  @Override
  public PostSequencesBySequenceTypeResponse postSequencesBySequenceType(String sequenceType, SequencePostRequest entity) {

    var deflineFormat = entity.getDeflineFormat();
    var basesPerLine = entity.getBasesPerLine();

    var features = FeatureAdapter.toBEDFeatures(entity.getFeatures());

    var stream = ReferenceDAOFactory.get(sequenceType).validateAndPrepareResponse(features, deflineFormat, basesPerLine);

    return PostSequencesBySequenceTypeResponse.respond200WithTextXFasta(new PlainTextFastaResponseStream(stream));

  }

  @Override
  public PostSequencesBySequenceTypeAndFileFormatResponse postSequencesBySequenceTypeAndFileFormat(
      String sequenceType,
      String fileFormatStr,
      DeflineFormat deflineFormat,
      Integer basesPerLine,
      StartOffset startOffset,
      SequencesSequenceTypeFileFormatPostMultipartFormData entity){

      var uploadMethod = entity.getUploadMethod();
      var file = entity.getFile();
      var url = entity.getUrl();

    // throw not found since these are path params
    var fileFormat = EnumUtil.validate(fileFormatStr, SupportedFileFormat.values(), NotFoundException::new);

    try (InputStream fileStream = switch (uploadMethod) {
      case FILE -> new FileInputStream(file);
      case URL -> new URL(url).openStream();
    }) {

      var features = switch (fileFormat) {
        case BED -> FeatureAdapter.readBed(fileStream, startOffset);
        case GFF3 -> FeatureAdapter.readGff3AndConvertToBed(fileStream);
      };

      var stream = ReferenceDAOFactory.get(sequenceType).validateAndPrepareResponse(features, deflineFormat, basesPerLine);
      return PostSequencesBySequenceTypeAndFileFormatResponse.respond200WithTextXFasta(new PlainTextFastaResponseStream(stream));

    } catch (IOException e) {
      throw new RuntimeException("Unable to complete file processing", e);
    }
  }
}
