package org.veupathdb.service.sr.reference;

import htsjdk.samtools.util.Locatable;
import htsjdk.tribble.bed.BEDFeature;
import jakarta.ws.rs.BadRequestException;

import java.util.Collection;
import java.util.Optional;

import htsjdk.tribble.annotation.Strand;

public class ReferenceSequenceSpec {

  private final String name;
  private final long maxSequencesPerRequest;
  private final long maxTotalBasesPerRequest;
  private final boolean isStranded;

  public ReferenceSequenceSpec(String name, long maxSequencesPerRequest, long maxTotalBasesPerRequest, boolean isStranded) {
    this.name = name;
    this.maxSequencesPerRequest = maxSequencesPerRequest;
    this.maxTotalBasesPerRequest = maxTotalBasesPerRequest;
    this.isStranded = isStranded;
  }

  public <T extends BEDFeature> void validateFeatures(Collection<T> features) {
    if (!isStranded && features.stream().anyMatch(f -> !f.getStrand().equals(Strand.NONE))) {
      throw new BadRequestException("Requested stranded feature on an unstranded reference sequence " + name);
    }
    if (features.size() > maxSequencesPerRequest) {
      throw new BadRequestException("Requested " + features.size() + " features but reference sequence " + name + " has a per-request limit of " + maxSequencesPerRequest);
    }
    var totalRequestedBases = features.stream().mapToInt(Locatable::getLengthOnReference).sum();
    if (totalRequestedBases > maxTotalBasesPerRequest) {
      throw new BadRequestException("You requested a sequence of length " + totalRequestedBases + ". The limit for " + name + " is " + maxTotalBasesPerRequest + " each.");
    }
  }
}
