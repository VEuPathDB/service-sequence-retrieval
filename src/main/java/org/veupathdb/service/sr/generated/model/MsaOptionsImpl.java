package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "format",
    "metadataUrl"
})
public class MsaOptionsImpl implements MsaOptions {
  @JsonProperty(
      value = "format",
      defaultValue = "clustal"
  )
  private MsaFormat format;

  @JsonProperty("metadataUrl")
  private String metadataUrl;

  @JsonProperty(
      value = "format",
      defaultValue = "clustal"
  )
  public MsaFormat getFormat() {
    return this.format;
  }

  @JsonProperty(
      value = "format",
      defaultValue = "clustal"
  )
  public void setFormat(MsaFormat format) {
    this.format = format;
  }

  @JsonProperty("metadataUrl")
  public String getMetadataUrl() {
    return this.metadataUrl;
  }

  @JsonProperty("metadataUrl")
  public void setMetadataUrl(String metadataUrl) {
    this.metadataUrl = metadataUrl;
  }
}
