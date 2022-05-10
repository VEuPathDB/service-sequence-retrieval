package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.File;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("gff3")
public class FeaturesGFF3Impl implements FeaturesGFF3 {
  @JsonProperty("gff3")
  private File gff3;

  @JsonProperty("gff3")
  public File getGff3() {
    return this.gff3;
  }

  @JsonProperty("gff3")
  public void setGff3(File gff3) {
    this.gff3 = gff3;
  }
}
