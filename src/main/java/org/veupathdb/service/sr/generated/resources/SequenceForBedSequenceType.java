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
import org.veupathdb.service.sr.generated.model.SequenceForBedSequenceTypePostMultipartFormData;
import org.veupathdb.service.sr.generated.model.StartOffset;
import org.veupathdb.service.sr.generated.support.ResponseDelegate;

@Path("/sequenceForBed/{sequenceType}")
public interface SequenceForBedSequenceType {
  @POST
  @Produces("application/octet-stream")
  @Consumes("multipart/form-data")
  PostSequenceForBedBySequenceTypeResponse postSequenceForBedBySequenceType(
      @PathParam("sequenceType") String sequenceType,
      @QueryParam("deflineFormat") DeflineFormat deflineFormat,
      @QueryParam("forceStrandedness") @DefaultValue("false") boolean forceStrandedness,
      @QueryParam("startOffset") StartOffset startOffset,
      @QueryParam("basesPerLine") @DefaultValue("60") int basesPerLine,
      SequenceForBedSequenceTypePostMultipartFormData entity);

  class PostSequenceForBedBySequenceTypeResponse extends ResponseDelegate {
    private PostSequenceForBedBySequenceTypeResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostSequenceForBedBySequenceTypeResponse(Response response) {
      super(response);
    }

    public static PostSequenceForBedBySequenceTypeResponse respond200WithApplicationOctetStream(
        StreamingOutput entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      return new PostSequenceForBedBySequenceTypeResponse(responseBuilder.build(), entity);
    }
  }
}
