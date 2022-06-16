package org.veupathdb.service.demo.service;

import jakarta.ws.rs.NotFoundException;
import org.jetbrains.annotations.NotNull;
import org.veupathdb.lib.compute.platform.job.AsyncJob;
import org.veupathdb.lib.compute.platform.AsyncPlatform;
import org.veupathdb.lib.hash_id.HashID;
import org.veupathdb.service.demo.generated.model.JobResponse;
import org.veupathdb.service.demo.generated.model.JobResponseImpl;
import org.veupathdb.service.demo.generated.model.JobStatus;

public class Controller {

  /**
   * Looks up the target job, throwing a {@link NotFoundException} if the job
   * could not be found.
   * <p>
   * Additionally, if the given raw job ID string is not a valid job ID, that
   * will count as an unfindable job and cause a {@link NotFoundException} to be
   * thrown.
   *
   * @param rawID Raw string ID for the job to lookup.
   *
   * @return The target job.
   *
   * @throws NotFoundException If the given raw job ID is invalid or does not
   * point to an existing job.
   */
  @NotNull
  protected AsyncJob getJobOrNotFound(String rawID) {
    var out = AsyncPlatform.getJob(parseIDOrNotFound(rawID));
    if (out == null)
      throw new NotFoundException("target job not found");
    return out;
  }


  /**
   * Parses the given raw job ID string into a {@link HashID}.
   * <p>
   * If the raw ID cannot be parsed into a valid {@code HashID} then there is no
   * way the job could exist, so a {@link NotFoundException} will be thrown.
   *
   * @param rawID Raw string ID to parse.
   *
   * @return The parsed {@code HashID}.
   *
   * @throws NotFoundException If the given raw job ID is not a valid
   * {@code HashID} value.
   */
  @NotNull
  private HashID parseIDOrNotFound(String rawID) {
    try {
      return new HashID(rawID);
    } catch (IllegalArgumentException e) {
      throw new NotFoundException("invalid hash ID");
    }
  }

  protected JobResponse convert(AsyncJob job) {
    var out = new JobResponseImpl();

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
