package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("format")
public class OrthomclMsaOptionsImpl implements OrthomclMsaOptions {
  @JsonProperty(
      value = "format",
      defaultValue = "clustal"
  )
  private OrthomclMsaFormat format;

  @JsonProperty(
      value = "format",
      defaultValue = "clustal"
  )
  public OrthomclMsaFormat getFormat() {
    return this.format;
  }

  @JsonProperty(
      value = "format",
      defaultValue = "clustal"
  )
  public void setFormat(OrthomclMsaFormat format) {
    this.format = format;
  }
}
