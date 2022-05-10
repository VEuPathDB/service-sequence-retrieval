package org.veupathdb.service.sr.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.veupathdb.service.sr.generated.model.SequenceGenomicPostRequest;
import org.veupathdb.service.sr.generated.model.ServerError;
import org.veupathdb.service.sr.generated.model.UnauthorizedError;
import org.veupathdb.service.sr.generated.support.ResponseDelegate;

@Path("/sequence/genomic")
public interface SequenceGenomic {
  @POST
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  @Consumes("application/json")
  PostSequenceGenomicResponse postSequenceGenomic(SequenceGenomicPostRequest entity);

  class PostSequenceGenomicResponse extends ResponseDelegate {
    private PostSequenceGenomicResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostSequenceGenomicResponse(Response response) {
      super(response);
    }

    public static PostSequenceGenomicResponse respond200WithApplicationOctetStream(
        StreamingOutput entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      return new PostSequenceGenomicResponse(responseBuilder.build(), entity);
    }

    public static PostSequenceGenomicResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostSequenceGenomicResponse(responseBuilder.build(), entity);
    }

    public static PostSequenceGenomicResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostSequenceGenomicResponse(responseBuilder.build(), entity);
    }
  }
}
