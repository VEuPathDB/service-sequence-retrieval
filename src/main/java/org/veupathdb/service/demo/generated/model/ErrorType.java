package org.veupathdb.service.demo.generated.model;

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

  public final String name;

  ErrorType(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
