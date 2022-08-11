package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StartOffset {
  @JsonProperty("ZERO")
  ZERO("ZERO"),

  @JsonProperty("ONE")
  ONE("ONE");

  public final String name;

  StartOffset(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
