package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Strand {
  @JsonProperty("POSITIVE")
  POSITIVE("POSITIVE"),

  @JsonProperty("NEGATIVE")
  NEGATIVE("NEGATIVE"),

  @JsonProperty("NONE")
  NONE("NONE");

  public final String name;

  Strand(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
