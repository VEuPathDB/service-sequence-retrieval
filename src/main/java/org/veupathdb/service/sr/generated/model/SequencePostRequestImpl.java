package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "features",
    "deflineFormat",
    "basesPerLine",
    "postProcess",
    "orthomclMsaOptions",
    "isolatesMsaOptions",
    "geneTreeOptions"
})
public class SequencePostRequestImpl implements SequencePostRequest {
  @JsonProperty("features")
  private List<Feature> features;

  @JsonProperty(
      value = "deflineFormat",
      defaultValue = "REGIONONLY"
  )
  private DeflineFormat deflineFormat;

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  private Integer basesPerLine;

  @JsonProperty("postProcess")
  private PostProcessType postProcess;

  @JsonProperty("orthomclMsaOptions")
  private OrthomclMsaOptions orthomclMsaOptions;

  @JsonProperty("isolatesMsaOptions")
  private IsolatesMsaOptions isolatesMsaOptions;

  @JsonProperty("geneTreeOptions")
  private GeneTreeOptions geneTreeOptions;

  @JsonProperty("features")
  public List<Feature> getFeatures() {
    return this.features;
  }

  @JsonProperty("features")
  public void setFeatures(List<Feature> features) {
    this.features = features;
  }

  @JsonProperty(
      value = "deflineFormat",
      defaultValue = "REGIONONLY"
  )
  public DeflineFormat getDeflineFormat() {
    return this.deflineFormat;
  }

  @JsonProperty(
      value = "deflineFormat",
      defaultValue = "REGIONONLY"
  )
  public void setDeflineFormat(DeflineFormat deflineFormat) {
    this.deflineFormat = deflineFormat;
  }

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  public Integer getBasesPerLine() {
    return this.basesPerLine;
  }

  @JsonProperty(
      value = "basesPerLine",
      defaultValue = "60"
  )
  public void setBasesPerLine(Integer basesPerLine) {
    this.basesPerLine = basesPerLine;
  }

  @JsonProperty("postProcess")
  public PostProcessType getPostProcess() {
    return this.postProcess;
  }

  @JsonProperty("postProcess")
  public void setPostProcess(PostProcessType postProcess) {
    this.postProcess = postProcess;
  }

  @JsonProperty("orthomclMsaOptions")
  public OrthomclMsaOptions getOrthomclMsaOptions() {
    return this.orthomclMsaOptions;
  }

  @JsonProperty("orthomclMsaOptions")
  public void setOrthomclMsaOptions(OrthomclMsaOptions orthomclMsaOptions) {
    this.orthomclMsaOptions = orthomclMsaOptions;
  }

  @JsonProperty("isolatesMsaOptions")
  public IsolatesMsaOptions getIsolatesMsaOptions() {
    return this.isolatesMsaOptions;
  }

  @JsonProperty("isolatesMsaOptions")
  public void setIsolatesMsaOptions(IsolatesMsaOptions isolatesMsaOptions) {
    this.isolatesMsaOptions = isolatesMsaOptions;
  }

  @JsonProperty("geneTreeOptions")
  public GeneTreeOptions getGeneTreeOptions() {
    return this.geneTreeOptions;
  }

  @JsonProperty("geneTreeOptions")
  public void setGeneTreeOptions(GeneTreeOptions geneTreeOptions) {
    this.geneTreeOptions = geneTreeOptions;
  }
}
