package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;

@JsonDeserialize(
    as = HealthResponseImpl.class
)
public interface HealthResponse {
  @JsonProperty("status")
  StatusType getStatus();

  @JsonProperty("status")
  void setStatus(StatusType status);

  @JsonProperty("dependencies")
  List<DependencyStatus> getDependencies();

  @JsonProperty("dependencies")
  void setDependencies(List<DependencyStatus> dependencies);

  @JsonProperty("info")
  InfoType getInfo();

  @JsonProperty("info")
  void setInfo(InfoType info);

  enum StatusType {
    @JsonProperty("healthy")
    HEALTHY("healthy"),

    @JsonProperty("unhealthy")
    UNHEALTHY("unhealthy");

    public final String value;

    public String getValue() {
      return this.value;
    }

    StatusType(String name) {
      this.value = name;
    }
  }

  @JsonDeserialize(
      as = HealthResponseImpl.InfoTypeImpl.class
  )
  interface InfoType {
    @JsonProperty("threads")
    Integer getThreads();

    @JsonProperty("threads")
    void setThreads(Integer threads);

    @JsonProperty("uptime")
    String getUptime();

    @JsonProperty("uptime")
    void setUptime(String uptime);

    @JsonProperty("uptimeMillis")
    Long getUptimeMillis();

    @JsonProperty("uptimeMillis")
    void setUptimeMillis(Long uptimeMillis);

    @JsonAnyGetter
    Map<String, Object> getAdditionalProperties();

    @JsonAnySetter
    void setAdditionalProperties(String key, Object value);
  }
}
