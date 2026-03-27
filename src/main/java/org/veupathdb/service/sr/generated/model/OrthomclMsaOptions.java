package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = OrthomclMsaOptionsImpl.class
)
public interface OrthomclMsaOptions {
  @JsonProperty(
      value = "format",
      defaultValue = "clustal"
  )
  OrthomclMsaFormat getFormat();

  @JsonProperty(
      value = "format",
      defaultValue = "clustal"
  )
  void setFormat(OrthomclMsaFormat format);
}
