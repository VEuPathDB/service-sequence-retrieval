package org.veupathdb.service.demo.service;

import jakarta.ws.rs.NotFoundException;
import org.veupathdb.lib.compute.platform.AsyncJob;
import org.veupathdb.lib.compute.platform.AsyncPlatform;
import org.veupathdb.lib.hash_id.HashID;
import org.veupathdb.lib.jackson.Json;
import org.veupathdb.service.demo.generated.model.*;
import org.veupathdb.service.demo.generated.resources.JobsJobId;
import org.veupathdb.service.demo.generated.resources.Reverse;

public class Controller implements Reverse, JobsJobId {

  @Override
  public PostReverseResponse postReverse(ReverseRequest entity) {
    var json  = Json.convert(entity);
    var jobID = HashID.ofMD5(json.toString());

    AsyncPlatform.submitJob("string-reverse-queue", jobID, json);

    return PostReverseResponse.respond200WithApplicationJson(convert(AsyncPlatform.getJob(jobID)));
  }

  @Override
  public GetJobsByJobIdResponse getJobsByJobId(String rawJobID) {
    return GetJobsByJobIdResponse.respond200WithApplicationJson(
      convert(AsyncPlatform.getJob(parseIDOrNotFound(rawJobID))));
  }

  private HashID parseIDOrNotFound(String rawID) {
    try {
      return new HashID(rawID);
    } catch (IllegalArgumentException e) {
      throw new NotFoundException();
    }
  }

  private ReverseResponse convert(AsyncJob job) {
    var out = new ReverseResponseImpl();

    out.setJobID(job.getJobID().toString());
    out.setQueuePosition(job.getQueuePosition());
    out.setStatus(convertEnum(job));

    return out;
  }

  private JobStatus convertEnum(AsyncJob job) {
    return switch (job.getStatus()) {
      case Queued -> JobStatus.QUEUED;
      case InProgress -> JobStatus.INPROGRESS;
      case Complete -> JobStatus.COMPLETE;
      case Failed -> JobStatus.FAILED;
      case Expired -> JobStatus.EXPIRED;
    };
  }
}
