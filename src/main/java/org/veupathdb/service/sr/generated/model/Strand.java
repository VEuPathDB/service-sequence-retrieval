package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Strand {
  @JsonProperty("positive")
  POSITIVE("positive"),

  @JsonProperty("negative")
  NEGATIVE("negative"),

  @JsonProperty("either")
  EITHER("either");

  private String name;

  Strand(String name) {
    this.name = name;
  }
  public String getValue(){ return name; } 
}
