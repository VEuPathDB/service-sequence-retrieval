package org.veupathdb.service.sr.model;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

import org.veupathdb.service.sr.model.ReferenceSequenceSpec;


public class ReferenceSequenceConfig {
  private List < ReferenceSequenceSpec > referenceSequenceSpecs;

  public List < ReferenceSequenceSpec > getReferenceSequenceSpecs() {
    return Collections.unmodifiableList(this.referenceSequenceSpecs);
  }

  public void setReferenceSequenceSpecs(List<ReferenceSequenceSpec> referenceSequenceSpecs) {
    this.referenceSequenceSpecs = referenceSequenceSpecs;
  }
}
