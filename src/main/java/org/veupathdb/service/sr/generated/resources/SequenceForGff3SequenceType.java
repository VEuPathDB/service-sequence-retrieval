package org.veupathdb.service.sr.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.generated.model.SequenceForGff3SequenceTypePostMultipartFormData;
import org.veupathdb.service.sr.generated.model.ServerError;
import org.veupathdb.service.sr.generated.model.UnauthorizedError;
import org.veupathdb.service.sr.generated.support.ResponseDelegate;

@Path("/sequenceForGff3/{sequenceType}")
public interface SequenceForGff3SequenceType {
  @POST
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  @Consumes("multipart/form-data")
  PostSequenceForGff3BySequenceTypeResponse postSequenceForGff3BySequenceType(
      @PathParam("sequenceType") String sequenceType,
      @QueryParam("deflineFormat") DeflineFormat deflineFormat,
      @QueryParam("forceStrandedness") @DefaultValue("false") boolean forceStrandedness,
      @QueryParam("basesPerLine") @DefaultValue("60") int basesPerLine,
      SequenceForGff3SequenceTypePostMultipartFormData entity);

  class PostSequenceForGff3BySequenceTypeResponse extends ResponseDelegate {
    private PostSequenceForGff3BySequenceTypeResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostSequenceForGff3BySequenceTypeResponse(Response response) {
      super(response);
    }

    public static PostSequenceForGff3BySequenceTypeResponse respond200WithApplicationOctetStream(
        StreamingOutput entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      return new PostSequenceForGff3BySequenceTypeResponse(responseBuilder.build(), entity);
    }

    public static PostSequenceForGff3BySequenceTypeResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostSequenceForGff3BySequenceTypeResponse(responseBuilder.build(), entity);
    }

    public static PostSequenceForGff3BySequenceTypeResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostSequenceForGff3BySequenceTypeResponse(responseBuilder.build(), entity);
    }
  }
}
