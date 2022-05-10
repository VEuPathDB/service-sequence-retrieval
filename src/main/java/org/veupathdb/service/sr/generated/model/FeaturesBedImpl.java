package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.File;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "bed",
    "startOffset"
})
public class FeaturesBedImpl implements FeaturesBed {
  @JsonProperty("bed")
  private File bed;

  @JsonProperty("startOffset")
  private StartOffset startOffset;

  @JsonProperty("bed")
  public File getBed() {
    return this.bed;
  }

  @JsonProperty("bed")
  public void setBed(File bed) {
    this.bed = bed;
  }

  @JsonProperty("startOffset")
  public StartOffset getStartOffset() {
    return this.startOffset;
  }

  @JsonProperty("startOffset")
  public void setStartOffset(StartOffset startOffset) {
    this.startOffset = startOffset;
  }
}
