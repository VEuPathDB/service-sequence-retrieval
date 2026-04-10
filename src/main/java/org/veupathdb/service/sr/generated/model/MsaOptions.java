package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = MsaOptionsImpl.class
)
public interface MsaOptions {
  @JsonProperty(
      value = "format",
      defaultValue = "clustal"
  )
  MsaFormat getFormat();

  @JsonProperty(
      value = "format",
      defaultValue = "clustal"
  )
  void setFormat(MsaFormat format);

  @JsonProperty("metadataUrl")
  String getMetadataUrl();

  @JsonProperty("metadataUrl")
  void setMetadataUrl(String metadataUrl);
}
