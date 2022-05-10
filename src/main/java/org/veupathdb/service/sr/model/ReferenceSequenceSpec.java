package org.veupathdb.service.sr.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class ReferenceSequenceSpec {
  private String name;

  @JsonGetter
  public String getName() {
    return name;
  }

  @JsonSetter
  public void setName(String name) {
    this.name = name;
  }

  private String fasta;

  @JsonGetter
  public String getFasta() {
    return fasta;
  }

  @JsonSetter
  public void setFasta(String fasta) {
    this.fasta = fasta;
  }

  private String index;

  @JsonGetter
  public String getIndex() {
    return index;
  }

  @JsonSetter
  public void setIndex(String index) {
    this.index = index;
  }

  private int maxSequencesPerRequest;

  @JsonGetter
  public int getMaxSequencesPerRequest() {
    return maxSequencesPerRequest;
  }

  @JsonSetter
  public void setMaxSequencesPerRequest(int maxSequencesPerRequest) {
    this.maxSequencesPerRequest = maxSequencesPerRequest;
  }

  private int maxTotalBasesPerRequest;

  @JsonGetter
  public int getMaxTotalBasesPerRequest() {
    return maxTotalBasesPerRequest;
  }

  @JsonSetter
  public void setMaxTotalBasesPerRequest(int maxTotalBasesPerRequest) {
    this.maxTotalBasesPerRequest = maxTotalBasesPerRequest;
  }

  private boolean disallowReverseComplement;

  @JsonGetter
  public boolean getDisallowReverseComplement() {
    return disallowReverseComplement;
  }

  @JsonSetter
  public void setDisallowReverseComplement(boolean disallowReverseComplement) {
    this.disallowReverseComplement = disallowReverseComplement;
  }
}
