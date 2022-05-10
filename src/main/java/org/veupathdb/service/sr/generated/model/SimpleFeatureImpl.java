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
public class SimpleFeatureImpl implements SimpleFeature {
  @JsonProperty("contig")
  private String contig;

  @JsonProperty("start")
  private int start;

  @JsonProperty("end")
  private int end;

  @JsonProperty("query")
  private String query;

  @JsonProperty("strand")
  private String strand;

  @JsonProperty("contig")
  public String getContig() {
    return this.contig;
  }

  @JsonProperty("contig")
  public void setContig(String contig) {
    this.contig = contig;
  }

  @JsonProperty("start")
  public int getStart() {
    return this.start;
  }

  @JsonProperty("start")
  public void setStart(int start) {
    this.start = start;
  }

  @JsonProperty("end")
  public int getEnd() {
    return this.end;
  }

  @JsonProperty("end")
  public void setEnd(int end) {
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

  @JsonProperty("strand")
  public String getStrand() {
    return this.strand;
  }

  @JsonProperty("strand")
  public void setStrand(String strand) {
    this.strand = strand;
  }
}
