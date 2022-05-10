package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.File;

@JsonDeserialize(
    as = FeaturesGFF3Impl.class
)
public interface FeaturesGFF3 {
  @JsonProperty("gff3")
  File getGff3();

  @JsonProperty("gff3")
  void setGff3(File gff3);
}
