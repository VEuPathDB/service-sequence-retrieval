package org.veupathdb.service.demo.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = ReverseRequestImpl.class
)
public interface ReverseRequest {
  @JsonProperty("input")
  String getInput();

  @JsonProperty("input")
  void setInput(String input);
}
