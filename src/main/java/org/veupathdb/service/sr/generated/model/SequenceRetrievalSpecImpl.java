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
    "basesPerLine",
    "sequenceType"
})
public class SequenceRetrievalSpecImpl implements SequenceRetrievalSpec {
  @JsonProperty("features")
  private List<Feature> features;

  @JsonProperty("deflineFormat")
  private DeflineFormat deflineFormat;

  @JsonProperty("forceStrandedness")
  private Boolean forceStrandedness;

  @JsonProperty("basesPerLine")
  private Integer basesPerLine;

  @JsonProperty("sequenceType")
  private SequenceType sequenceType;

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

  @JsonProperty("forceStrandedness")
  public Boolean getForceStrandedness() {
    return this.forceStrandedness;
  }

  @JsonProperty("forceStrandedness")
  public void setForceStrandedness(Boolean forceStrandedness) {
    this.forceStrandedness = forceStrandedness;
  }

  @JsonProperty("basesPerLine")
  public Integer getBasesPerLine() {
    return this.basesPerLine;
  }

  @JsonProperty("basesPerLine")
  public void setBasesPerLine(Integer basesPerLine) {
    this.basesPerLine = basesPerLine;
  }

  @JsonProperty("sequenceType")
  public SequenceType getSequenceType() {
    return this.sequenceType;
  }

  @JsonProperty("sequenceType")
  public void setSequenceType(SequenceType sequenceType) {
    this.sequenceType = sequenceType;
  }
}
