package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StartOffset {
  @JsonProperty("zero")
  ZERO("zero"),

  @JsonProperty("one")
  ONE("one");

  private String name;

  StartOffset(String name) {
    this.name = name;
  }
  public String getValue(){ return name; } 
}
