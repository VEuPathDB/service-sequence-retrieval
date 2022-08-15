package org.veupathdb.service.sr.service;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.veupathdb.service.sr.generated.model.*;
import org.veupathdb.service.sr.generated.resources.SequencesAsyncSequenceType;
import org.veupathdb.service.sr.util.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.List;
import java.net.URL;

import org.veupathdb.service.sr.generated.model.JobResponse;
import org.veupathdb.lib.compute.platform.AsyncPlatform;
import org.veupathdb.lib.compute.platform.job.JobSubmission;
import org.veupathdb.lib.hash_id.HashID;
import org.veupathdb.lib.jackson.Json;
import org.veupathdb.service.sr.generated.model.JobResponse;

public class SequenceRetrievalAsyncService extends Controller implements SequencesAsyncSequenceType {

  private JobResponse asyncResponse(
      List<Feature> features,
      SequenceType sequenceType,
      DeflineFormat deflineFormat,
      Boolean forceStrandedness,
      Integer basesPerLine){
    var spec = new SequenceRetrievalSpecImpl();
    spec.setFeatures(features);
    spec.setDeflineFormat(deflineFormat);
    spec.setForceStrandedness(forceStrandedness);
    spec.setBasesPerLine(basesPerLine);
    spec.setSequenceType(sequenceType);
    var json  = Json.convert(spec);
    var jobID = HashID.ofMD5(json.toString());

    // Check if we have a job already with this hash ID
    var oldJob = AsyncPlatform.getJob(jobID);

    // If we do (if the AsyncPlatform call returned non-null)
    if (oldJob != null)
      // Return that job status.
      return convert(oldJob);

    AsyncPlatform.submitJob("sequence-retrieval-queue", JobSubmission.builder()
      .jobID(jobID)
      .config(json)
      .build());

    return convert(AsyncPlatform.getJob(jobID));

  }
  @Override
  public PostSequencesAsyncBySequenceTypeResponse postSequencesAsyncBySequenceType(String sequenceTypeStr, SequencePostRequest entity) {
    var forceStrandedness = entity.getForceStrandedness();
    var deflineFormat = entity.getDeflineFormat();
    var basesPerLine = entity.getBasesPerLine();
    var requestedFeatures = entity.getFeatures();
    var sequenceType = EnumUtil.validate(sequenceTypeStr, SequenceType.values(), NotFoundException::new);

    var spec = Sequences.getSequenceSpec(sequenceType);
    var index = Sequences.getSequenceIndex(sequenceType);
    var sequences = Sequences.getSequenceFile(sequenceType);

    var features = Validate.getValidatedFeatures(sequenceType, index, requestedFeatures, forceStrandedness, spec);
      return PostSequencesAsyncBySequenceTypeResponse.respond200WithApplicationJson(asyncResponse(features, sequenceType, deflineFormat, forceStrandedness, basesPerLine));

  }

  @Override
  public PostSequencesAsyncBySequenceTypeAndFileFormatResponse postSequencesAsyncBySequenceTypeAndFileFormat(
      String sequenceTypeStr,
      String fileFormatStr,
      DeflineFormat deflineFormat,
      Boolean forceStrandedness,
      Integer basesPerLine,
      StartOffset startOffset,
      SequencesAsyncSequenceTypeFileFormatPostMultipartFormData entity){

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

      return PostSequencesAsyncBySequenceTypeAndFileFormatResponse.respond200WithApplicationJson(asyncResponse(features, sequenceType, deflineFormat, forceStrandedness, basesPerLine));

    } catch (IOException e) {
      throw new RuntimeException("Unable to complete file processing", e);
    }
  }
}
