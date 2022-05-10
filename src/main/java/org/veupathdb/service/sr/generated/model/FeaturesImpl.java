package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(
    using = Features.FeaturesDeserializer.class
)
@JsonSerialize(
    using = Features.Serializer.class
)
public class FeaturesImpl implements Features {
  private Object anyType;

  private FeaturesImpl() {
    this.anyType = null;
  }

  public FeaturesImpl(FeaturesList featuresList) {
    this.anyType = featuresList;
  }

  public FeaturesImpl(FeaturesBed featuresBed) {
    this.anyType = featuresBed;
  }

  public FeaturesImpl(FeaturesGFF3 featuresGFF3) {
    this.anyType = featuresGFF3;
  }

  public FeaturesList getFeaturesList() {
    if ( !(anyType instanceof  FeaturesList)) throw new IllegalStateException("fetching wrong type out of the union: org.veupathdb.service.sr.generated.model.FeaturesList");
    return (FeaturesList) anyType;
  }

  public boolean isFeaturesList() {
    return anyType instanceof FeaturesList;
  }

  public FeaturesBed getFeaturesBed() {
    if ( !(anyType instanceof  FeaturesBed)) throw new IllegalStateException("fetching wrong type out of the union: org.veupathdb.service.sr.generated.model.FeaturesBed");
    return (FeaturesBed) anyType;
  }

  public boolean isFeaturesBed() {
    return anyType instanceof FeaturesBed;
  }

  public FeaturesGFF3 getFeaturesGFF3() {
    if ( !(anyType instanceof  FeaturesGFF3)) throw new IllegalStateException("fetching wrong type out of the union: org.veupathdb.service.sr.generated.model.FeaturesGFF3");
    return (FeaturesGFF3) anyType;
  }

  public boolean isFeaturesGFF3() {
    return anyType instanceof FeaturesGFF3;
  }
}
