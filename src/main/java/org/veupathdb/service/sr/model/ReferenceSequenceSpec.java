package org.veupathdb.service.sr.model;

public class ReferenceSequenceSpec {

  private String fastaFile;
  private String indexFile;
  private int maxSequencesPerRequest;
  private int maxTotalBasesPerRequest;
  private boolean disallowReverseComplement = true;

  public String getFastaFile() {
    return fastaFile;
  }

  public void setFastaFile(String fastaFile) {
    this.fastaFile = fastaFile;
  }

  public String getIndexFile() {
    return indexFile;
  }

  public void setIndexFile(String indexFile) {
    this.indexFile = indexFile;
  }

  public int getMaxSequencesPerRequest() {
    return maxSequencesPerRequest;
  }

  public void setMaxSequencesPerRequest(int maxSequencesPerRequest) {
    this.maxSequencesPerRequest = maxSequencesPerRequest;
  }

  public int getMaxTotalBasesPerRequest() {
    return maxTotalBasesPerRequest;
  }

  public void setMaxTotalBasesPerRequest(int maxTotalBasesPerRequest) {
    this.maxTotalBasesPerRequest = maxTotalBasesPerRequest;
  }

  public boolean getDisallowReverseComplement() {
    return disallowReverseComplement;
  }

  public void setDisallowReverseComplement(boolean disallowReverseComplement) {
    this.disallowReverseComplement = disallowReverseComplement;
  }
}
