package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = SequencePostRequestImpl.class
)
public interface SequencePostRequest {
  @JsonProperty("features")
  List<Feature> getFeatures();

  @JsonProperty("features")
  void setFeatures(List<Feature> features);

  @JsonProperty(
      value = "deflineFormat",
      defaultValue = "REGIONONLY"
  )
  DeflineFormat getDeflineFormat();

  @JsonProperty(
      value = "deflineFormat",
      defaultValue = "REGIONONLY"
  )
  void setDeflineFormat(DeflineFormat deflineFormat);

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  Integer getBasesPerLine();

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  void setBasesPerLine(Integer basesPerLine);

  @JsonProperty("postProcess")
  PostProcessType getPostProcess();

  @JsonProperty("postProcess")
  void setPostProcess(PostProcessType postProcess);

  @JsonProperty("orthomclMsaOptions")
  OrthomclMsaOptions getOrthomclMsaOptions();

  @JsonProperty("orthomclMsaOptions")
  void setOrthomclMsaOptions(OrthomclMsaOptions orthomclMsaOptions);

  @JsonProperty("isolatesMsaOptions")
  IsolatesMsaOptions getIsolatesMsaOptions();

  @JsonProperty("isolatesMsaOptions")
  void setIsolatesMsaOptions(IsolatesMsaOptions isolatesMsaOptions);

  @JsonProperty("geneTreeOptions")
  GeneTreeOptions getGeneTreeOptions();

  @JsonProperty("geneTreeOptions")
  void setGeneTreeOptions(GeneTreeOptions geneTreeOptions);
}
