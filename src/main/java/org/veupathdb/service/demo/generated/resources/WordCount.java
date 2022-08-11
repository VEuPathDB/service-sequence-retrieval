package org.veupathdb.service.demo.generated.resources;

import java.io.File;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.demo.generated.model.JobResponse;
import org.veupathdb.service.demo.generated.model.ServerError;
import org.veupathdb.service.demo.generated.support.ResponseDelegate;

@Path("/word-count")
public interface WordCount {
  @POST
  @Produces("application/json")
  @Consumes("multipart/form-data")
  PostWordCountResponse postWordCount(File entity);

  class PostWordCountResponse extends ResponseDelegate {
    private PostWordCountResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostWordCountResponse(Response response) {
      super(response);
    }

    public static PostWordCountResponse respond200WithApplicationJson(JobResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostWordCountResponse(responseBuilder.build(), entity);
    }

    public static PostWordCountResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostWordCountResponse(responseBuilder.build(), entity);
    }
  }
}
