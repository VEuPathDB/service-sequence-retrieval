package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(
    using = FeaturesData.FeaturesDataDeserializer.class
)
@JsonSerialize(
    using = FeaturesData.Serializer.class
)
public class FeaturesDataImpl implements FeaturesData {
  private Object anyType;

  private FeaturesDataImpl() {
    this.anyType = null;
  }

  public FeaturesDataImpl(FeaturesList featuresList) {
    this.anyType = featuresList;
  }

  public FeaturesDataImpl(FeaturesBed featuresBed) {
    this.anyType = featuresBed;
  }

  public FeaturesDataImpl(FeaturesGFF3 featuresGFF3) {
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
