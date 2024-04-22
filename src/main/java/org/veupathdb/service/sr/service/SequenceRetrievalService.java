package org.veupathdb.service.sr.service;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gusdb.fgputil.Timer;
import org.veupathdb.service.sr.generated.model.*;
import org.veupathdb.service.sr.generated.resources.SequencesSequenceType;
import org.veupathdb.service.sr.reference.ReferenceDAOFactory;
import org.veupathdb.service.sr.util.EnumUtil;
import org.veupathdb.service.sr.util.FeatureAdapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.function.Consumer;

public class SequenceRetrievalService implements SequencesSequenceType {

  private static final Logger LOG = LogManager.getLogger(SequenceRetrievalService.class);

  /**
   * Extend generated streamer class so we can log processing duration
   */
  private static class StreamerWithLogging extends PlainTextFastaResponseStream {
    public StreamerWithLogging(Consumer<OutputStream> streamer) {
      super(streamer);
    }
    @Override
    public void write(OutputStream output) throws IOException, WebApplicationException {
      Timer timer = new Timer();
      super.write(output);
      LOG.info("Took " + timer.getElapsedStringAndRestart() + " to retrieve and stream sequences.");
    }
  }

  @Override
  public PostSequencesBySequenceTypeResponse postSequencesBySequenceType(String sequenceType, SequencePostRequest entity) {

    var deflineFormat = entity.getDeflineFormat();
    var basesPerLine = entity.getBasesPerLine();

    var features = FeatureAdapter.toBEDFeatures(entity.getFeatures());

    var stream = ReferenceDAOFactory.get(sequenceType).validateAndPrepareResponse(features, deflineFormat, basesPerLine);

    return PostSequencesBySequenceTypeResponse.respond200WithTextXFasta(new StreamerWithLogging(stream));

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

    Timer timer = new Timer();
    LOG.info("Beginning input data fetch of submission type " + uploadMethod +
        ", data type " + fileFormat + " to input data located at " +
        (uploadMethod == UploadMethod.FILE ? file.getAbsoluteFile().toString() : url));
    try (InputStream fileStream = switch (uploadMethod) {
      case FILE -> new FileInputStream(file);
      case URL -> new URL(url).openStream();
    }) {
      LOG.info("Took " + timer.getElapsedStringAndRestart() + " to open stream to input data.");
      var features = switch (fileFormat) {
        case BED -> FeatureAdapter.readBed(fileStream, startOffset);
        case GFF3 -> FeatureAdapter.readGff3AndConvertToBed(fileStream);
      };

      LOG.info("Took " + timer.getElapsedStringAndRestart() + " to read features from input data.");
      var stream = ReferenceDAOFactory.get(sequenceType).validateAndPrepareResponse(features, deflineFormat, basesPerLine);

      LOG.info("Took " + timer.getElapsedStringAndRestart() + " to prepare to stream response.");
      return PostSequencesBySequenceTypeAndFileFormatResponse.respond200WithTextXFasta(new StreamerWithLogging(stream));

    } catch (IOException e) {
      throw new RuntimeException("Unable to complete file processing", e);
    }
  }
}
