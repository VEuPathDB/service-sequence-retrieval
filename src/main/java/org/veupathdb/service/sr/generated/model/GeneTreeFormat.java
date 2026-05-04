package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GeneTreeFormat {
  @JsonProperty("newick")
  NEWICK("newick");

  public final String value;

  public String getValue() {
    return this.value;
  }

  GeneTreeFormat(String name) {
    this.value = name;
  }
}
