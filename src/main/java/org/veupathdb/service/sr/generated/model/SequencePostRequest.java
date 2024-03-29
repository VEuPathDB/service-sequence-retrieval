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

  @JsonProperty(
      value = "deflineFormat",
      defaultValue = "REGIONONLY"
  )
  DeflineFormat getDeflineFormat();

  @JsonProperty(
      value = "deflineFormat",
      defaultValue = "REGIONONLY"
  )
  void setDeflineFormat(DeflineFormat deflineFormat);

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  Integer getBasesPerLine();

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  void setBasesPerLine(Integer basesPerLine);
}
