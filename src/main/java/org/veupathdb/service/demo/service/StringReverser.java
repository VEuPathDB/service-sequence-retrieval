package org.veupathdb.service.demo.service;

import org.veupathdb.lib.hash_id.HashID;
import org.veupathdb.lib.jackson.Json;
import org.veupathdb.service.demo.generated.model.ReverseRequest;
import org.veupathdb.service.demo.generated.resources.Reverse;

public class StringReverser implements Reverse {

  @Override
  public PostReverseResponse postReverse(ReverseRequest entity) {
    var jobID = HashID.ofMD5(Json.convert(entity).toString());

    return null;
  }

}
