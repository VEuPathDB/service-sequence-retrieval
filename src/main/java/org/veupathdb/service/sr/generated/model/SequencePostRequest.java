package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = SequencePostRequestImpl.class
)
public interface SequencePostRequest {
  @JsonProperty("features")
  List<Feature> getFeatures();

  @JsonProperty("features")
  void setFeatures(List<Feature> features);

  @JsonProperty("deflineFormat")
  DeflineFormat getDeflineFormat();

  @JsonProperty("deflineFormat")
  void setDeflineFormat(DeflineFormat deflineFormat);

  @JsonProperty(
      value = "forceStrandedness",
      defaultValue = "false"
  )
  boolean getForceStrandedness();

  @JsonProperty(
      value = "forceStrandedness",
      defaultValue = "false"
  )
  void setForceStrandedness(boolean forceStrandedness);

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  int getBasesPerLine();

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  void setBasesPerLine(int basesPerLine);
}
