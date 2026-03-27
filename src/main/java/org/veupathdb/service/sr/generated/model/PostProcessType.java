package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PostProcessType {
  @JsonProperty("orthomclMSA")
  ORTHOMCLMSA("orthomclMSA"),

  @JsonProperty("isolatesMSA")
  ISOLATESMSA("isolatesMSA"),

  @JsonProperty("geneTree")
  GENETREE("geneTree");

  public final String value;

  public String getValue() {
    return this.value;
  }

  PostProcessType(String name) {
    this.value = name;
  }
}
