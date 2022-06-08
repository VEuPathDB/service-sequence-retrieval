package org.veupathdb.service.demo.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("invalid-input")
@JsonPropertyOrder({
    "status",
    "message",
    "errors"
})
public class UnprocessableEntityErrorImpl implements UnprocessableEntityError {
  @JsonProperty("status")
  private final ErrorType status = _DISCRIMINATOR_TYPE_NAME;

  @JsonProperty("message")
  private String message;

  @JsonProperty("errors")
  private UnprocessableEntityError.ErrorsType errors;

  @JsonProperty("status")
  public ErrorType getStatus() {
    return this.status;
  }

  @JsonProperty("message")
  public String getMessage() {
    return this.message;
  }

  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }

  @JsonProperty("errors")
  public UnprocessableEntityError.ErrorsType getErrors() {
    return this.errors;
  }

  @JsonProperty("errors")
  public void setErrors(UnprocessableEntityError.ErrorsType errors) {
    this.errors = errors;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "general",
      "byKey"
  })
  public static class ErrorsTypeImpl implements UnprocessableEntityError.ErrorsType {
    @JsonProperty("general")
    private List<String> general;

    @JsonProperty("byKey")
    private UnprocessableEntityError.ErrorsType.ByKeyType byKey;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new ExcludingMap();

    @JsonProperty("general")
    public List<String> getGeneral() {
      return this.general;
    }

    @JsonProperty("general")
    public void setGeneral(List<String> general) {
      this.general = general;
    }

    @JsonProperty("byKey")
    public UnprocessableEntityError.ErrorsType.ByKeyType getByKey() {
      return this.byKey;
    }

    @JsonProperty("byKey")
    public void setByKey(UnprocessableEntityError.ErrorsType.ByKeyType byKey) {
      this.byKey = byKey;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
      return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String key, Object value) {
      this.additionalProperties.put(key, value);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder
    public static class ByKeyTypeImpl implements UnprocessableEntityError.ErrorsType.ByKeyType {
      @JsonIgnore
      private Map<String, Object> additionalProperties = new ExcludingMap();

      @JsonAnyGetter
      public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
      }

      @JsonAnySetter
      public void setAdditionalProperties(String key, Object value) {
        this.additionalProperties.put(key, value);
      }
    }
  }
}
