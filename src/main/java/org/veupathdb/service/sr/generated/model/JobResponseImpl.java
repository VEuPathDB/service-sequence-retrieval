package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "jobID",
    "status",
    "queuePosition",
    "created",
    "started",
    "finished"
})
public class JobResponseImpl implements JobResponse {
  @JsonProperty("jobID")
  private String jobID;

  @JsonProperty("status")
  private JobStatus status;

  @JsonProperty("queuePosition")
  private Integer queuePosition;

  @JsonProperty("created")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
  )
  @JsonDeserialize(
      using = TimestampDeserializer.class
  )
  private Date created;

  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
  )
  @JsonDeserialize(
      using = TimestampDeserializer.class
  )
  @JsonProperty("started")
  private Date started;

  @JsonProperty("finished")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
  )
  @JsonDeserialize(
      using = TimestampDeserializer.class
  )
  private Date finished;

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

  @JsonProperty("created")
  public Date getCreated() {
    return this.created;
  }

  @JsonProperty("created")
  public void setCreated(Date created) {
    this.created = created;
  }

  @JsonProperty("started")
  public Date getStarted() {
    return this.started;
  }

  @JsonProperty("started")
  public void setStarted(Date started) {
    this.started = started;
  }

  @JsonProperty("finished")
  public Date getFinished() {
    return this.finished;
  }

  @JsonProperty("finished")
  public void setFinished(Date finished) {
    this.finished = finished;
  }
}
