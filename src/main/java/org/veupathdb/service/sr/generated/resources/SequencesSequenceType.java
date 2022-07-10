package org.veupathdb.service.sr.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.sr.generated.model.PlainTextFastaResponse;
import org.veupathdb.service.sr.generated.model.SequencePostRequest;
import org.veupathdb.service.sr.generated.support.ResponseDelegate;

@Path("/sequences/{sequenceType}")
public interface SequencesSequenceType {
  @POST
  @Produces("text/x-fasta")
  @Consumes("application/json")
  PostSequencesBySequenceTypeResponse postSequencesBySequenceType(
      @PathParam("sequenceType") String sequenceType, SequencePostRequest entity);

  class PostSequencesBySequenceTypeResponse extends ResponseDelegate {
    private PostSequencesBySequenceTypeResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostSequencesBySequenceTypeResponse(Response response) {
      super(response);
    }

    public static PostSequencesBySequenceTypeResponse respond200WithTextXFasta(
        PlainTextFastaResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "text/x-fasta");
      responseBuilder.entity(entity);
      return new PostSequencesBySequenceTypeResponse(responseBuilder.build(), entity);
    }
  }
}
