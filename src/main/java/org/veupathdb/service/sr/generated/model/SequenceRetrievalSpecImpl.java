package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "features",
    "featuresStr",
    "featuresUrl",
    "fileFormat",
    "uploadMethod",
    "startOffset",
    "deflineFormat",
    "forceStrandedness",
    "basesPerLine",
    "sequenceType"
})
public class SequenceRetrievalSpecImpl implements SequenceRetrievalSpec {
  @JsonProperty("features")
  private List<Feature> features;

  @JsonProperty("featuresStr")
  private String featuresStr;

  @JsonProperty("featuresUrl")
  private String featuresUrl;

  @JsonProperty("fileFormat")
  private SupportedFileFormat fileFormat;

  @JsonProperty("uploadMethod")
  private UploadMethod uploadMethod;

  @JsonProperty("startOffset")
  private StartOffset startOffset;

  @JsonProperty("deflineFormat")
  private DeflineFormat deflineFormat;

  @JsonProperty("forceStrandedness")
  private Boolean forceStrandedness;

  @JsonProperty("basesPerLine")
  private Integer basesPerLine;

  @JsonProperty("sequenceType")
  private SequenceType sequenceType;

  @JsonProperty("features")
  public List<Feature> getFeatures() {
    return this.features;
  }

  @JsonProperty("features")
  public void setFeatures(List<Feature> features) {
    this.features = features;
  }

  @JsonProperty("featuresStr")
  public String getFeaturesStr() {
    return this.featuresStr;
  }

  @JsonProperty("featuresStr")
  public void setFeaturesStr(String featuresStr) {
    this.featuresStr = featuresStr;
  }

  @JsonProperty("featuresUrl")
  public String getFeaturesUrl() {
    return this.featuresUrl;
  }

  @JsonProperty("featuresUrl")
  public void setFeaturesUrl(String featuresUrl) {
    this.featuresUrl = featuresUrl;
  }

  @JsonProperty("fileFormat")
  public SupportedFileFormat getFileFormat() {
    return this.fileFormat;
  }

  @JsonProperty("fileFormat")
  public void setFileFormat(SupportedFileFormat fileFormat) {
    this.fileFormat = fileFormat;
  }

  @JsonProperty("uploadMethod")
  public UploadMethod getUploadMethod() {
    return this.uploadMethod;
  }

  @JsonProperty("uploadMethod")
  public void setUploadMethod(UploadMethod uploadMethod) {
    this.uploadMethod = uploadMethod;
  }

  @JsonProperty("startOffset")
  public StartOffset getStartOffset() {
    return this.startOffset;
  }

  @JsonProperty("startOffset")
  public void setStartOffset(StartOffset startOffset) {
    this.startOffset = startOffset;
  }

  @JsonProperty("deflineFormat")
  public DeflineFormat getDeflineFormat() {
    return this.deflineFormat;
  }

  @JsonProperty("deflineFormat")
  public void setDeflineFormat(DeflineFormat deflineFormat) {
    this.deflineFormat = deflineFormat;
  }

  @JsonProperty("forceStrandedness")
  public Boolean getForceStrandedness() {
    return this.forceStrandedness;
  }

  @JsonProperty("forceStrandedness")
  public void setForceStrandedness(Boolean forceStrandedness) {
    this.forceStrandedness = forceStrandedness;
  }

  @JsonProperty("basesPerLine")
  public Integer getBasesPerLine() {
    return this.basesPerLine;
  }

  @JsonProperty("basesPerLine")
  public void setBasesPerLine(Integer basesPerLine) {
    this.basesPerLine = basesPerLine;
  }

  @JsonProperty("sequenceType")
  public SequenceType getSequenceType() {
    return this.sequenceType;
  }

  @JsonProperty("sequenceType")
  public void setSequenceType(SequenceType sequenceType) {
    this.sequenceType = sequenceType;
  }
}
