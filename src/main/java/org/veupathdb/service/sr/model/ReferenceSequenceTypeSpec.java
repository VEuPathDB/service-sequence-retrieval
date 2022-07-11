package org.veupathdb.service.sr.model;

public class ReferenceSequenceTypeSpec {

  private String fastaFile;
  private String indexFile;
  private long maxSequencesPerRequest;
  private long maxTotalBasesPerRequest;
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

  public long getMaxSequencesPerRequest() {
    return maxSequencesPerRequest;
  }

  public void setMaxSequencesPerRequest(long maxSequencesPerRequest) {
    this.maxSequencesPerRequest = maxSequencesPerRequest;
  }

  public long getMaxTotalBasesPerRequest() {
    return maxTotalBasesPerRequest;
  }

  public void setMaxTotalBasesPerRequest(long maxTotalBasesPerRequest) {
    this.maxTotalBasesPerRequest = maxTotalBasesPerRequest;
  }

  public boolean getDisallowReverseComplement() {
    return disallowReverseComplement;
  }

  public void setDisallowReverseComplement(boolean disallowReverseComplement) {
    this.disallowReverseComplement = disallowReverseComplement;
  }
}
