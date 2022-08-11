package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UploadMethod {
  @JsonProperty("file")
  FILE("file"),

  @JsonProperty("url")
  URL("url");

  public final String name;

  UploadMethod(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
