package org.veupathdb.service.sequence.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DeflineFormat {
  @JsonProperty("query_only")
  QUERYONLY("query_only"),

  @JsonProperty("region_only")
  REGIONONLY("region_only"),

  @JsonProperty("query_and_region")
  QUERYANDREGION("query_and_region");

  public final String name;

  DeflineFormat(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
