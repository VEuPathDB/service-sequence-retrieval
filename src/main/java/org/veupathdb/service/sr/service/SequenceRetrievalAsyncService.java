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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.lang.StringBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.net.URL;

import org.veupathdb.service.sr.generated.model.JobResponse;
import org.veupathdb.lib.compute.platform.AsyncPlatform;
import org.veupathdb.lib.compute.platform.job.JobSubmission;
import org.veupathdb.lib.hash_id.HashID;
import org.veupathdb.lib.jackson.Json;
import org.veupathdb.service.sr.generated.model.JobResponse;

public class SequenceRetrievalAsyncService extends Controller implements SequencesAsyncSequenceType {

  private JobResponse asyncResponse(SequenceRetrievalSpec spec){
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
  public PostSequencesAsyncBySequenceTypeResponse postSequencesAsyncBySequenceType(String sequenceType, SequencePostRequest entity) {
    var deflineFormat = entity.getDeflineFormat();
    var basesPerLine = entity.getBasesPerLine();
    var features = entity.getFeatures();
    var spec = new SequenceRetrievalSpecImpl();
    spec.setFeatures(features);
    spec.setDeflineFormat(deflineFormat);
    spec.setBasesPerLine(basesPerLine);
    spec.setSequenceType(sequenceType);
    return PostSequencesAsyncBySequenceTypeResponse.respond200WithApplicationJson(asyncResponse(spec));
  }

  @Override
  public PostSequencesAsyncBySequenceTypeAndFileFormatResponse postSequencesAsyncBySequenceTypeAndFileFormat(
      String sequenceType,
      String fileFormatStr,
      DeflineFormat deflineFormat,
      Integer basesPerLine,
      StartOffset startOffset,
      SequencesAsyncSequenceTypeFileFormatPostMultipartFormData entity){

    var file = entity.getFile();
    var url = entity.getUrl();

    var spec = new SequenceRetrievalSpecImpl();

    spec.setDeflineFormat(deflineFormat);
    spec.setBasesPerLine(basesPerLine);
    var fileFormat = EnumUtil.validate(fileFormatStr, SupportedFileFormat.values(), NotFoundException::new);
    spec.setFileFormat(fileFormat);
    spec.setSequenceType(sequenceType);

    var uploadMethod = entity.getUploadMethod();
    switch(uploadMethod){
      case FILE:
        spec.setFeaturesStr(slurp(file));
        break;
      case URL:
        spec.setFeaturesUrl(url);
        break;
    }
    return PostSequencesAsyncBySequenceTypeAndFileFormatResponse.respond200WithApplicationJson(asyncResponse(spec));
  }

  private static String slurp(File file){
    try {
      return getFileContent(new FileInputStream(file), "UTF-8");
    } catch (IOException e){
      throw new RuntimeException(e);
    }
  }

  //https://stackoverflow.com/a/15161590
  private static String getFileContent(FileInputStream fis, String encoding ) throws IOException
 {
   try( BufferedReader br =
           new BufferedReader( new InputStreamReader(fis, encoding )))
   {
      StringBuilder sb = new StringBuilder();
      String line;
      while(( line = br.readLine()) != null ) {
         sb.append( line );
         sb.append( '\n' );
      }
      return sb.toString();
   }
}
}
