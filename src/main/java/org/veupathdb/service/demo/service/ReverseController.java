package org.veupathdb.service.demo.service;

import org.veupathdb.lib.compute.platform.AsyncPlatform;
import org.veupathdb.lib.compute.platform.job.JobSubmission;
import org.veupathdb.lib.hash_id.HashID;
import org.veupathdb.lib.jackson.Json;
import org.veupathdb.service.demo.generated.model.ReverseRequest;
import org.veupathdb.service.demo.generated.resources.Reverse;

public class ReverseController extends Controller implements Reverse {

  @Override
  public PostReverseResponse postReverse(ReverseRequest entity) {
    var json  = Json.convert(entity);
    var jobID = HashID.ofMD5(json.toString());

    // Check if we have a job already with this hash ID
    var oldJob = AsyncPlatform.getJob(jobID);

    // If we do (if the AsyncPlatform call returned non-null)
    if (oldJob != null)
      // Return that job status.
      return PostReverseResponse.respond200WithApplicationJson(convert(oldJob));

    AsyncPlatform.submitJob("string-reverse-queue", JobSubmission.builder()
      .jobID(jobID)
      .config(json)
      .build());

    return PostReverseResponse.respond200WithApplicationJson(convert(AsyncPlatform.getJob(jobID)));
  }

}
