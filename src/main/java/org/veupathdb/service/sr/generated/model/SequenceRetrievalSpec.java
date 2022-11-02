package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = SequenceRetrievalSpecImpl.class
)
public interface SequenceRetrievalSpec {
  @JsonProperty("features")
  List<Feature> getFeatures();

  @JsonProperty("features")
  void setFeatures(List<Feature> features);

  @JsonProperty("featuresStr")
  String getFeaturesStr();

  @JsonProperty("featuresStr")
  void setFeaturesStr(String featuresStr);

  @JsonProperty("featuresUrl")
  String getFeaturesUrl();

  @JsonProperty("featuresUrl")
  void setFeaturesUrl(String featuresUrl);

  @JsonProperty("fileFormat")
  SupportedFileFormat getFileFormat();

  @JsonProperty("fileFormat")
  void setFileFormat(SupportedFileFormat fileFormat);

  @JsonProperty("uploadMethod")
  UploadMethod getUploadMethod();

  @JsonProperty("uploadMethod")
  void setUploadMethod(UploadMethod uploadMethod);

  @JsonProperty("startOffset")
  StartOffset getStartOffset();

  @JsonProperty("startOffset")
  void setStartOffset(StartOffset startOffset);

  @JsonProperty("deflineFormat")
  DeflineFormat getDeflineFormat();

  @JsonProperty("deflineFormat")
  void setDeflineFormat(DeflineFormat deflineFormat);

  @JsonProperty("forceStrandedness")
  Boolean getForceStrandedness();

  @JsonProperty("forceStrandedness")
  void setForceStrandedness(Boolean forceStrandedness);

  @JsonProperty("basesPerLine")
  Integer getBasesPerLine();

  @JsonProperty("basesPerLine")
  void setBasesPerLine(Integer basesPerLine);

  @JsonProperty("sequenceType")
  SequenceType getSequenceType();

  @JsonProperty("sequenceType")
  void setSequenceType(SequenceType sequenceType);
}
