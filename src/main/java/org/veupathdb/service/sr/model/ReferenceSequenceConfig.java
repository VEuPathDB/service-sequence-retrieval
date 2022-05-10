package org.veupathdb.service.sr.model;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

import org.veupathdb.service.sr.model.ReferenceSequenceSpec;


public class ReferenceSequenceConfig {
  private final List < ReferenceSequenceSpec > referenceSequenceSpecs;

  public ReferenceSequenceConfig() {
    referenceSequenceSpecs = new ArrayList <>();
  }

  public List < ReferenceSequenceSpec > getReferenceSequenceSpecs() {
    return Collections.unmodifiableList(this.referenceSequenceSpecs);
  }

  public void setReferenceSequenceSpecs(ReferenceSequenceSpec[] referenceSequenceSpecs) {
    this.referenceSequenceSpecs.addAll(Arrays.asList(referenceSequenceSpecs));
  }
}
