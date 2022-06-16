package org.veupathdb.service.demo.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
}
