package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ErrorType {
  @JsonProperty("bad-request")
  BADREQUEST("bad-request"),

  @JsonProperty("unauthorized")
  UNAUTHORIZED("unauthorized"),

  @JsonProperty("forbidden")
  FORBIDDEN("forbidden"),

  @JsonProperty("not-found")
  NOTFOUND("not-found"),

  @JsonProperty("bad-method")
  BADMETHOD("bad-method"),

  @JsonProperty("invalid-input")
  INVALIDINPUT("invalid-input"),

  @JsonProperty("server-error")
  SERVERERROR("server-error");

  public final String value;

  public String getValue() {
    return this.value;
  }

  ErrorType(String name) {
    this.value = name;
  }
}
