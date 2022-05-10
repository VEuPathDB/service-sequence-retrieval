package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "features",
    "deflineFormat",
    "forceStrandedness",
    "basesPerLine"
})
public class SequencePostRequestImpl implements SequencePostRequest {
  @JsonProperty("features")
  private List<Feature> features;

  @JsonProperty("deflineFormat")
  private DeflineFormat deflineFormat;

  @JsonProperty(
      value = "forceStrandedness",
      defaultValue = "false"
  )
  private boolean forceStrandedness;

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  private int basesPerLine;

  @JsonProperty("features")
  public List<Feature> getFeatures() {
    return this.features;
  }

  @JsonProperty("features")
  public void setFeatures(List<Feature> features) {
    this.features = features;
  }

  @JsonProperty("deflineFormat")
  public DeflineFormat getDeflineFormat() {
    return this.deflineFormat;
  }

  @JsonProperty("deflineFormat")
  public void setDeflineFormat(DeflineFormat deflineFormat) {
    this.deflineFormat = deflineFormat;
  }

  @JsonProperty(
      value = "forceStrandedness",
      defaultValue = "false"
  )
  public boolean getForceStrandedness() {
    return this.forceStrandedness;
  }

  @JsonProperty(
      value = "forceStrandedness",
      defaultValue = "false"
  )
  public void setForceStrandedness(boolean forceStrandedness) {
    this.forceStrandedness = forceStrandedness;
  }

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  public int getBasesPerLine() {
    return this.basesPerLine;
  }

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  public void setBasesPerLine(int basesPerLine) {
    this.basesPerLine = basesPerLine;
  }
}
