package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = SimpleFeatureImpl.class
)
public interface SimpleFeature {
  @JsonProperty("contig")
  String getContig();

  @JsonProperty("contig")
  void setContig(String contig);

  @JsonProperty("start")
  int getStart();

  @JsonProperty("start")
  void setStart(int start);

  @JsonProperty("end")
  int getEnd();

  @JsonProperty("end")
  void setEnd(int end);

  @JsonProperty("query")
  String getQuery();

  @JsonProperty("query")
  void setQuery(String query);

  @JsonProperty("strand")
  String getStrand();

  @JsonProperty("strand")
  void setStrand(String strand);
}
