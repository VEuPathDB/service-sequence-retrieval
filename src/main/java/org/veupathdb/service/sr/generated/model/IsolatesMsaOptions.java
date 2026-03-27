package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = IsolatesMsaOptionsImpl.class
)
public interface IsolatesMsaOptions {
  @JsonProperty(
      value = "format",
      defaultValue = "tbd"
  )
  IsolatesMsaFormat getFormat();

  @JsonProperty(
      value = "format",
      defaultValue = "tbd"
  )
  void setFormat(IsolatesMsaFormat format);
}
