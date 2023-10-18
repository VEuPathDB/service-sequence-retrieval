package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StartOffset {
  @JsonProperty("ZERO")
  ZERO("ZERO"),

  @JsonProperty("ONE")
  ONE("ONE");

  public final String value;

  public String getValue() {
    return this.value;
  }

  StartOffset(String name) {
    this.value = name;
  }
}
