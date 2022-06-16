package org.veupathdb.service.demo.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum JobStatus {
  @JsonProperty("queued")
  QUEUED("queued"),

  @JsonProperty("in-progress")
  INPROGRESS("in-progress"),

  @JsonProperty("complete")
  COMPLETE("complete"),

  @JsonProperty("failed")
  FAILED("failed"),

  @JsonProperty("expired")
  EXPIRED("expired");

  public final String name;

  JobStatus(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
