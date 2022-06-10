package org.veupathdb.service.demo.service;

import org.veupathdb.lib.compute.platform.AsyncPlatform;
import org.veupathdb.lib.hash_id.HashID;
import org.veupathdb.lib.jackson.Json;
import org.veupathdb.service.demo.generated.model.ReverseRequest;
import org.veupathdb.service.demo.generated.resources.Reverse;

public class ReverseController extends Controller implements Reverse {

  @Override
  public PostReverseResponse postReverse(ReverseRequest entity) {
    var json  = Json.convert(entity);
    var jobID = HashID.ofMD5(json.toString());

    AsyncPlatform.submitJob("string-reverse-queue", jobID, json);

    return PostReverseResponse.respond200WithApplicationJson(convert(AsyncPlatform.getJob(jobID)));
  }

}
