package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DeflineFormat {
  @JsonProperty("QUERYONLY")
  QUERYONLY("QUERYONLY"),

  @JsonProperty("REGIONONLY")
  REGIONONLY("REGIONONLY"),

  @JsonProperty("QUERYANDREGION")
  QUERYANDREGION("QUERYANDREGION");

  public final String name;

  DeflineFormat(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
