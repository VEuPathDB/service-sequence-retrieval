package org.veupathdb.service.sr.generated.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@JsonDeserialize(
    using = Features.FeaturesDeserializer.class
)
@JsonSerialize(
    using = Features.Serializer.class
)
public interface Features {
  FeaturesList getFeaturesList();

  boolean isFeaturesList();

  FeaturesBed getFeaturesBed();

  boolean isFeaturesBed();

  FeaturesGFF3 getFeaturesGFF3();

  boolean isFeaturesGFF3();

  class Serializer extends StdSerializer<Features> {
    public Serializer() {
      super(Features.class);}

    public void serialize(Features object, JsonGenerator jsonGenerator,
        SerializerProvider jsonSerializerProvider) throws IOException, JsonProcessingException {
      if ( object.isFeaturesList()) {
        jsonGenerator.writeObject(object.getFeaturesList());
        return;
      }
      if ( object.isFeaturesBed()) {
        jsonGenerator.writeObject(object.getFeaturesBed());
        return;
      }
      if ( object.isFeaturesGFF3()) {
        jsonGenerator.writeObject(object.getFeaturesGFF3());
        return;
      }
      throw new IOException("Can't figure out type of object" + object);
    }
  }

  class FeaturesDeserializer extends StdDeserializer<Features> {
    public FeaturesDeserializer() {
      super(Features.class);}

    private boolean looksLikeFeaturesList(Map<String, Object> map) {
      return map.keySet().containsAll(Arrays.asList("features"));
    }

    private boolean looksLikeFeaturesBed(Map<String, Object> map) {
      return map.keySet().containsAll(Arrays.asList("bed","startOffset"));
    }

    private boolean looksLikeFeaturesGFF3(Map<String, Object> map) {
      return map.keySet().containsAll(Arrays.asList("gff3"));
    }

    public Features deserialize(JsonParser jsonParser, DeserializationContext jsonContext) throws
        IOException, JsonProcessingException {
      ObjectMapper mapper  = new ObjectMapper();
      Map<String, Object> map = mapper.readValue(jsonParser, Map.class);
      if ( looksLikeFeaturesList(map) ) return new FeaturesImpl(mapper.convertValue(map, FeaturesListImpl.class));
      if ( looksLikeFeaturesBed(map) ) return new FeaturesImpl(mapper.convertValue(map, FeaturesBedImpl.class));
      if ( looksLikeFeaturesGFF3(map) ) return new FeaturesImpl(mapper.convertValue(map, FeaturesGFF3Impl.class));
      throw new IOException("Can't figure out type of object" + map);
    }
  }
}
