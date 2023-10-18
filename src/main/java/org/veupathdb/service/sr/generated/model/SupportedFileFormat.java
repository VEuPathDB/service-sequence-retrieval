package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SupportedFileFormat {
  @JsonProperty("bed")
  BED("bed"),

  @JsonProperty("gff3")
  GFF3("gff3");

  public final String value;

  public String getValue() {
    return this.value;
  }

  SupportedFileFormat(String name) {
    this.value = name;
  }
}
