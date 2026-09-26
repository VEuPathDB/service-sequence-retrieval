package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;

@JsonDeserialize(
    as = JobResponseImpl.class
)
public interface JobResponse {
  @JsonProperty("jobID")
  String getJobID();

  @JsonProperty("jobID")
  void setJobID(String jobID);

  @JsonProperty("status")
  JobStatus getStatus();

  @JsonProperty("status")
  void setStatus(JobStatus status);

  @JsonProperty("queuePosition")
  Integer getQueuePosition();

  @JsonProperty("queuePosition")
  void setQueuePosition(Integer queuePosition);

  @JsonProperty("created")
  Date getCreated();

  @JsonProperty("created")
  void setCreated(Date created);

  @JsonProperty("started")
  Date getStarted();

  @JsonProperty("started")
  void setStarted(Date started);

  @JsonProperty("finished")
  Date getFinished();

  @JsonProperty("finished")
  void setFinished(Date finished);
}
