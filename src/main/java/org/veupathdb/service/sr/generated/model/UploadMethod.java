package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UploadMethod {
  @JsonProperty("file")
  FILE("file"),

  @JsonProperty("url")
  URL("url");

  private String name;

  UploadMethod(String name) {
    this.name = name;
  }
  public String getValue(){ return name; } 
}
