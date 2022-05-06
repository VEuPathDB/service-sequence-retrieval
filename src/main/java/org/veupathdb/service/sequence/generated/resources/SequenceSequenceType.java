package org.veupathdb.service.sequence.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.veupathdb.service.sequence.generated.model.SequencePostRequest;
import org.veupathdb.service.sequence.generated.model.ServerError;
import org.veupathdb.service.sequence.generated.model.UnauthorizedError;
import org.veupathdb.service.sequence.generated.support.ResponseDelegate;

@Path("/sequence/{sequenceType}")
public interface SequenceSequenceType {
  @POST
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  @Consumes("application/json")
  PostSequenceBySequenceTypeResponse postSequenceBySequenceType(
      @PathParam("sequenceType") String sequenceType, SequencePostRequest entity);

  class PostSequenceBySequenceTypeResponse extends ResponseDelegate {
    private PostSequenceBySequenceTypeResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostSequenceBySequenceTypeResponse(Response response) {
      super(response);
    }

    public static PostSequenceBySequenceTypeResponse respond200WithApplicationOctetStream(
        StreamingOutput entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      return new PostSequenceBySequenceTypeResponse(responseBuilder.build(), entity);
    }

    public static PostSequenceBySequenceTypeResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostSequenceBySequenceTypeResponse(responseBuilder.build(), entity);
    }

    public static PostSequenceBySequenceTypeResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostSequenceBySequenceTypeResponse(responseBuilder.build(), entity);
    }
  }
}
