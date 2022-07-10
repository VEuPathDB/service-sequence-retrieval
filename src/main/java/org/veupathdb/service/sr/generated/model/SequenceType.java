package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SequenceType {
  @JsonProperty("genomic")
  GENOMIC("genomic"),

  @JsonProperty("protein")
  PROTEIN("protein");

  private String name;

  SequenceType(String name) {
    this.name = name;
  }
  public String getValue(){ return name; } 
}
