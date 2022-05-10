package org.veupathdb.service.sr.model;


public class ReferenceSequenceSpec {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  private String fasta;

  public String getFasta() {
    return fasta;
  }

  public void setFasta(String fasta) {
    this.fasta = fasta;
  }

  private String index;

  public String getIndex() {
    return index;
  }

  public void setIndex(String index) {
    this.index = index;
  }

  private int maxSequencesPerRequest;

  public int getMaxSequencesPerRequest() {
    return maxSequencesPerRequest;
  }

  public void setMaxSequencesPerRequest(int maxSequencesPerRequest) {
    this.maxSequencesPerRequest = maxSequencesPerRequest;
  }

  private int maxTotalBasesPerRequest;

  public int getMaxTotalBasesPerRequest() {
    return maxTotalBasesPerRequest;
  }

  public void setMaxTotalBasesPerRequest(int maxTotalBasesPerRequest) {
    this.maxTotalBasesPerRequest = maxTotalBasesPerRequest;
  }

  private boolean disallowReverseComplement;

  public boolean getDisallowReverseComplement() {
    return disallowReverseComplement;
  }

  public void setDisallowReverseComplement(boolean disallowReverseComplement) {
    this.disallowReverseComplement = disallowReverseComplement;
  }
}
