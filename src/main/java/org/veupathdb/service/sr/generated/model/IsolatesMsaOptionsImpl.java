package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("format")
public class IsolatesMsaOptionsImpl implements IsolatesMsaOptions {
  @JsonProperty(
      value = "format",
      defaultValue = "tbd"
  )
  private IsolatesMsaFormat format;

  @JsonProperty(
      value = "format",
      defaultValue = "tbd"
  )
  public IsolatesMsaFormat getFormat() {
    return this.format;
  }

  @JsonProperty(
      value = "format",
      defaultValue = "tbd"
  )
  public void setFormat(IsolatesMsaFormat format) {
    this.format = format;
  }
}
