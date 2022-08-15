package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.File;
import java.util.Map;

@JsonDeserialize(
    as = SequencesAsyncSequenceTypeFileFormatPostMultipartFormDataImpl.class
)
public interface SequencesAsyncSequenceTypeFileFormatPostMultipartFormData {
  @JsonProperty("uploadMethod")
  UploadMethod getUploadMethod();

  @JsonProperty("uploadMethod")
  void setUploadMethod(UploadMethod uploadMethod);

  @JsonProperty("url")
  String getUrl();

  @JsonProperty("url")
  void setUrl(String url);

  @JsonProperty("file")
  File getFile();

  @JsonProperty("file")
  void setFile(File file);

  @JsonAnyGetter
  Map<String, Object> getAdditionalProperties();

  @JsonAnySetter
  void setAdditionalProperties(String key, Object value);
}
