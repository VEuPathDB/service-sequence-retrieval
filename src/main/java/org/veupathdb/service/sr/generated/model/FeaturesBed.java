package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.File;

@JsonDeserialize(
    as = FeaturesBedImpl.class
)
public interface FeaturesBed {
  @JsonProperty("bed")
  File getBed();

  @JsonProperty("bed")
  void setBed(File bed);

  @JsonProperty("startOffset")
  StartOffset getStartOffset();

  @JsonProperty("startOffset")
  void setStartOffset(StartOffset startOffset);
}
