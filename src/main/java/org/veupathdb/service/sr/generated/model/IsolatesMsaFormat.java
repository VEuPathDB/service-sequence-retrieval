package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IsolatesMsaFormat {
  @JsonProperty("tbd")
  TBD("tbd");

  public final String value;

  public String getValue() {
    return this.value;
  }

  IsolatesMsaFormat(String name) {
    this.value = name;
  }
}
