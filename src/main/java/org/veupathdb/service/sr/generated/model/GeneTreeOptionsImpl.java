package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("format")
public class GeneTreeOptionsImpl implements GeneTreeOptions {
  @JsonProperty(
      value = "format",
      defaultValue = "tbd"
  )
  private GeneTreeFormat format;

  @JsonProperty(
      value = "format",
      defaultValue = "tbd"
  )
  public GeneTreeFormat getFormat() {
    return this.format;
  }

  @JsonProperty(
      value = "format",
      defaultValue = "tbd"
  )
  public void setFormat(GeneTreeFormat format) {
    this.format = format;
  }
}
