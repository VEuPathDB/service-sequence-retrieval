package org.veupathdb.service.sequence.model;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import org.veupathdb.service.sequence.model.ReferenceSequenceSpec;


public class Config {
  private final List < ReferenceSequenceSpec > referenceSequenceSpecs;

  public Config() {
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
