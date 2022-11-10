package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "features",
    "deflineFormat",
    "basesPerLine"
})
public class SequencePostRequestImpl implements SequencePostRequest {
  @JsonProperty("features")
  private List<Feature> features;

  @JsonProperty(
      value = "deflineFormat",
      defaultValue = "REGIONONLY"
  )
  private DeflineFormat deflineFormat;

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  private Integer basesPerLine;

  @JsonProperty("features")
  public List<Feature> getFeatures() {
    return this.features;
  }

  @JsonProperty("features")
  public void setFeatures(List<Feature> features) {
    this.features = features;
  }

  @JsonProperty(
      value = "deflineFormat",
      defaultValue = "REGIONONLY"
  )
  public DeflineFormat getDeflineFormat() {
    return this.deflineFormat;
  }

  @JsonProperty(
      value = "deflineFormat",
      defaultValue = "REGIONONLY"
  )
  public void setDeflineFormat(DeflineFormat deflineFormat) {
    this.deflineFormat = deflineFormat;
  }

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  public Integer getBasesPerLine() {
    return this.basesPerLine;
  }

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  public void setBasesPerLine(Integer basesPerLine) {
    this.basesPerLine = basesPerLine;
  }
}
