package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Strand {
  @JsonProperty("POSITIVE")
  POSITIVE("POSITIVE"),

  @JsonProperty("NEGATIVE")
  NEGATIVE("NEGATIVE"),

  @JsonProperty("NONE")
  NONE("NONE");

  public final String value;

  public String getValue() {
    return this.value;
  }

  Strand(String name) {
    this.value = name;
  }
}
