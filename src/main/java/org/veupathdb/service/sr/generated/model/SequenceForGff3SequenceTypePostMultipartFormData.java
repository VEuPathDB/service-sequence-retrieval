package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.File;
import java.util.Map;

@JsonDeserialize(
    as = SequenceForGff3SequenceTypePostMultipartFormDataImpl.class
)
public interface SequenceForGff3SequenceTypePostMultipartFormData {
  @JsonProperty("file")
  File getFile();

  @JsonProperty("file")
  void setFile(File file);

  @JsonAnyGetter
  Map<String, Object> getAdditionalProperties();

  @JsonAnySetter
  void setAdditionalProperties(String key, Object value);
}
