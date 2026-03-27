package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrthomclMsaFormat {
  @JsonProperty("fasta")
  FASTA("fasta"),

  @JsonProperty("clustal")
  CLUSTAL("clustal"),

  @JsonProperty("msf")
  MSF("msf"),

  @JsonProperty("phylip")
  PHYLIP("phylip"),

  @JsonProperty("selex")
  SELEX("selex"),

  @JsonProperty("stockholm")
  STOCKHOLM("stockholm"),

  @JsonProperty("vienna")
  VIENNA("vienna");

  public final String value;

  public String getValue() {
    return this.value;
  }

  OrthomclMsaFormat(String name) {
    this.value = name;
  }
}
