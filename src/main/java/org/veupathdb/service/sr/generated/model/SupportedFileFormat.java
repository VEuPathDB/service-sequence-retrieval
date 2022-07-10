package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SupportedFileFormat {
  @JsonProperty("bed")
  BED("bed"),

  @JsonProperty("gff3")
  GFF3("gff3");

  private String name;

  SupportedFileFormat(String name) {
    this.name = name;
  }
  public String getValue(){ return name; } 
}
