package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SequenceType {
  @JsonProperty("genomic")
  GENOMIC("genomic"),

  @JsonProperty("protein")
  PROTEIN("protein");

  public final String name;

  SequenceType(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
