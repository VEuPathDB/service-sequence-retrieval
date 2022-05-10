package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Strand {
  @JsonProperty("X")
  X("X"),

  @JsonProperty("Y")
  Y("Y");

  public final String name;

  Strand(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
