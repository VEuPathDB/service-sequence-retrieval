package org.veupathdb.service.demo.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "jobID",
    "status",
    "queuePosition"
})
public class JobResponseImpl implements JobResponse {
  @JsonProperty("jobID")
  private String jobID;

  @JsonProperty("status")
  private JobStatus status;

  @JsonProperty("queuePosition")
  private Integer queuePosition;

  @JsonProperty("jobID")
  public String getJobID() {
    return this.jobID;
  }

  @JsonProperty("jobID")
  public void setJobID(String jobID) {
    this.jobID = jobID;
  }

  @JsonProperty("status")
  public JobStatus getStatus() {
    return this.status;
  }

  @JsonProperty("status")
  public void setStatus(JobStatus status) {
    this.status = status;
  }

  @JsonProperty("queuePosition")
  public Integer getQueuePosition() {
    return this.queuePosition;
  }

  @JsonProperty("queuePosition")
  public void setQueuePosition(Integer queuePosition) {
    this.queuePosition = queuePosition;
  }
}
