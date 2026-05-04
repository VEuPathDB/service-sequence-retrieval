package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = GeneTreeOptionsImpl.class
)
public interface GeneTreeOptions {
  @JsonProperty(
      value = "format",
      defaultValue = "newick"
  )
  GeneTreeFormat getFormat();

  @JsonProperty(
      value = "format",
      defaultValue = "newick"
  )
  void setFormat(GeneTreeFormat format);
}
