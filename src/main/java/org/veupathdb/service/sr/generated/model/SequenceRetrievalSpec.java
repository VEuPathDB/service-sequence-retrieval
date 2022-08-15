package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = SequenceRetrievalSpecImpl.class
)
public interface SequenceRetrievalSpec {
  @JsonProperty("features")
  List<Feature> getFeatures();

  @JsonProperty("features")
  void setFeatures(List<Feature> features);

  @JsonProperty("deflineFormat")
  DeflineFormat getDeflineFormat();

  @JsonProperty("deflineFormat")
  void setDeflineFormat(DeflineFormat deflineFormat);

  @JsonProperty("forceStrandedness")
  Boolean getForceStrandedness();

  @JsonProperty("forceStrandedness")
  void setForceStrandedness(Boolean forceStrandedness);

  @JsonProperty("basesPerLine")
  Integer getBasesPerLine();

  @JsonProperty("basesPerLine")
  void setBasesPerLine(Integer basesPerLine);

  @JsonProperty("sequenceType")
  SequenceType getSequenceType();

  @JsonProperty("sequenceType")
  void setSequenceType(SequenceType sequenceType);
}
