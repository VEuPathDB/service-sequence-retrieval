package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Strand {
  @JsonProperty("POSITIVE")
  POSITIVE("POSITIVE"),

  @JsonProperty("NEGATIVE")
  NEGATIVE("NEGATIVE"),

  @JsonProperty("NONE")
  NONE("NONE");

  private String name;

  Strand(String name) {
    this.name = name;
  }
  public String getValue(){ return name; } 
}
