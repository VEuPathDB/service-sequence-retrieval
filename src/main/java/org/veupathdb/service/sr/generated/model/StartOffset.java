package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StartOffset {
  @JsonProperty("ZERO")
  ZERO("ZERO"),

  @JsonProperty("ONE")
  ONE("ONE");

  private String name;

  StartOffset(String name) {
    this.name = name;
  }
  public String getValue(){ return name; } 
}
