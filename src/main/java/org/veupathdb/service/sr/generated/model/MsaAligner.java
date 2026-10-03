package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MsaAligner {
  @JsonProperty("clustalo")
  CLUSTALO("clustalo"),

  @JsonProperty("mafft")
  MAFFT("mafft");

  public final String value;

  public String getValue() {
    return this.value;
  }

  MsaAligner(String name) {
    this.value = name;
  }
}
