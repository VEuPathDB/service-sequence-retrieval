package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PostProcessType {
  @JsonProperty("MSA")
  MSA("MSA"),

  @JsonProperty("GENETREE")
  GENETREE("GENETREE");

  public final String value;

  public String getValue() {
    return this.value;
  }

  PostProcessType(String name) {
    this.value = name;
  }
}
