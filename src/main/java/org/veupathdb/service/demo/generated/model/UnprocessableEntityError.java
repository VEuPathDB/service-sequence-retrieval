package org.veupathdb.service.demo.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;

@JsonTypeName("invalid-input")
@JsonDeserialize(
    as = UnprocessableEntityErrorImpl.class
)
public interface UnprocessableEntityError extends Error {
  ErrorType _DISCRIMINATOR_TYPE_NAME = ErrorType.INVALIDINPUT;

  @JsonProperty("status")
  ErrorType getStatus();

  @JsonProperty("message")
  String getMessage();

  @JsonProperty("message")
  void setMessage(String message);

  @JsonProperty("errors")
  ErrorsType getErrors();

  @JsonProperty("errors")
  void setErrors(ErrorsType errors);

  @JsonDeserialize(
      as = UnprocessableEntityErrorImpl.ErrorsTypeImpl.class
  )
  interface ErrorsType {
    @JsonProperty("general")
    List<String> getGeneral();

    @JsonProperty("general")
    void setGeneral(List<String> general);

    @JsonProperty("byKey")
    ByKeyType getByKey();

    @JsonProperty("byKey")
    void setByKey(ByKeyType byKey);

    @JsonAnyGetter
    Map<String, Object> getAdditionalProperties();

    @JsonAnySetter
    void setAdditionalProperties(String key, Object value);

    @JsonDeserialize(
        as = UnprocessableEntityErrorImpl.ErrorsTypeImpl.ByKeyTypeImpl.class
    )
    interface ByKeyType {
      @JsonAnyGetter
      Map<String, Object> getAdditionalProperties();

      @JsonAnySetter
      void setAdditionalProperties(String key, Object value);
    }
  }
}
