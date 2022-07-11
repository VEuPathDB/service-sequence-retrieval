package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "contig",
    "start",
    "end",
    "query",
    "strand"
})
public class FeatureImpl implements Feature {
  @JsonProperty("contig")
  private String contig;

  @JsonProperty("start")
  private Integer start;

  @JsonProperty("end")
  private Integer end;

  @JsonProperty("query")
  private String query;

  @JsonProperty(
      value = "strand",
      defaultValue = "either"
  )
  private Strand strand;

  @JsonProperty("contig")
  public String getContig() {
    return this.contig;
  }

  @JsonProperty("contig")
  public void setContig(String contig) {
    this.contig = contig;
  }

  @JsonProperty("start")
  public Integer getStart() {
    return this.start;
  }

  @JsonProperty("start")
  public void setStart(Integer start) {
    this.start = start;
  }

  @JsonProperty("end")
  public Integer getEnd() {
    return this.end;
  }

  @JsonProperty("end")
  public void setEnd(Integer end) {
    this.end = end;
  }

  @JsonProperty("query")
  public String getQuery() {
    return this.query;
  }

  @JsonProperty("query")
  public void setQuery(String query) {
    this.query = query;
  }

  @JsonProperty(
      value = "strand",
      defaultValue = "either"
  )
  public Strand getStrand() {
    return this.strand;
  }

  @JsonProperty(
      value = "strand",
      defaultValue = "either"
  )
  public void setStrand(Strand strand) {
    this.strand = strand;
  }
}
