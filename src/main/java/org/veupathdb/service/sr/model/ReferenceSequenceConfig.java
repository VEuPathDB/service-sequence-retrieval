package org.veupathdb.service.sr.model;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import org.veupathdb.service.sr.model.ReferenceSequenceSpec;


public class ReferenceSequenceConfig {
  private final List < ReferenceSequenceSpec > referenceSequenceSpecs;

  public ReferenceSequenceConfig() {
    referenceSequenceSpecs = new ArrayList <>();
  }

  @JsonGetter
  public List < ReferenceSequenceSpec > getReferenceSequenceSpecs() {
    return Collections.unmodifiableList(this.referenceSequenceSpecs);
  }


  @JsonSetter
  public void setReferenceSequenceSpecs(ReferenceSequenceSpec[] referenceSequenceSpecs) {
    this.referenceSequenceSpecs.addAll(Arrays.asList(referenceSequenceSpecs));
  }
}
