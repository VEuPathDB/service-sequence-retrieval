package org.veupathdb.service.demo.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("input")
public class ReverseRequestImpl implements ReverseRequest {
  @JsonProperty("input")
  private String input;

  @JsonProperty("input")
  public String getInput() {
    return this.input;
  }

  @JsonProperty("input")
  public void setInput(String input) {
    this.input = input;
  }
}
