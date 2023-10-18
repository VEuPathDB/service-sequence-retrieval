package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UploadMethod {
  @JsonProperty("file")
  FILE("file"),

  @JsonProperty("url")
  URL("url");

  public final String value;

  public String getValue() {
    return this.value;
  }

  UploadMethod(String name) {
    this.value = name;
  }
}
