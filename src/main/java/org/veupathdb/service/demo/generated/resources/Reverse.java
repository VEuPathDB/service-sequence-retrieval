package org.veupathdb.service.demo.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.demo.generated.model.JobResponse;
import org.veupathdb.service.demo.generated.model.ReverseRequest;
import org.veupathdb.service.demo.generated.model.ServerError;
import org.veupathdb.service.demo.generated.support.ResponseDelegate;

@Path("/reverse")
public interface Reverse {
  @POST
  @Produces("application/json")
  @Consumes("application/json")
  PostReverseResponse postReverse(ReverseRequest entity);

  class PostReverseResponse extends ResponseDelegate {
    private PostReverseResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostReverseResponse(Response response) {
      super(response);
    }

    public static PostReverseResponse respond200WithApplicationJson(JobResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostReverseResponse(responseBuilder.build(), entity);
    }

    public static PostReverseResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostReverseResponse(responseBuilder.build(), entity);
    }
  }
}
