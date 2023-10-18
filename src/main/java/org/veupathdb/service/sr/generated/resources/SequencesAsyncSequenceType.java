package org.veupathdb.service.sr.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.generated.model.JobResponse;
import org.veupathdb.service.sr.generated.model.SequencePostRequest;
import org.veupathdb.service.sr.generated.model.SequencesAsyncSequenceTypeFileFormatPostMultipartFormData;
import org.veupathdb.service.sr.generated.model.ServerError;
import org.veupathdb.service.sr.generated.model.StartOffset;
import org.veupathdb.service.sr.generated.support.ResponseDelegate;

@Path("/sequences-async/{sequenceType}")
public interface SequencesAsyncSequenceType {
  @POST
  @Produces("application/json")
  @Consumes("application/json")
  PostSequencesAsyncBySequenceTypeResponse postSequencesAsyncBySequenceType(
      @PathParam("sequenceType") String sequenceType, SequencePostRequest entity);

  @POST
  @Path("/{fileFormat}")
  @Produces("application/json")
  @Consumes("multipart/form-data")
  PostSequencesAsyncBySequenceTypeAndFileFormatResponse postSequencesAsyncBySequenceTypeAndFileFormat(
      @PathParam("sequenceType") String sequenceType, @PathParam("fileFormat") String fileFormat,
      @QueryParam("deflineFormat") @DefaultValue("REGIONONLY") DeflineFormat deflineFormat,
      @QueryParam("basesPerLine") @DefaultValue("60") Integer basesPerLine,
      @QueryParam("startOffset") @DefaultValue("ONE") StartOffset startOffset,
      SequencesAsyncSequenceTypeFileFormatPostMultipartFormData entity);

  class PostSequencesAsyncBySequenceTypeResponse extends ResponseDelegate {
    private PostSequencesAsyncBySequenceTypeResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostSequencesAsyncBySequenceTypeResponse(Response response) {
      super(response);
    }

    public static PostSequencesAsyncBySequenceTypeResponse respond200WithApplicationJson(
        JobResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostSequencesAsyncBySequenceTypeResponse(responseBuilder.build(), entity);
    }

    public static PostSequencesAsyncBySequenceTypeResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostSequencesAsyncBySequenceTypeResponse(responseBuilder.build(), entity);
    }
  }

  class PostSequencesAsyncBySequenceTypeAndFileFormatResponse extends ResponseDelegate {
    private PostSequencesAsyncBySequenceTypeAndFileFormatResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    private PostSequencesAsyncBySequenceTypeAndFileFormatResponse(Response response) {
      super(response);
    }

    public static PostSequencesAsyncBySequenceTypeAndFileFormatResponse respond200WithApplicationJson(
        JobResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostSequencesAsyncBySequenceTypeAndFileFormatResponse(responseBuilder.build(), entity);
    }

    public static PostSequencesAsyncBySequenceTypeAndFileFormatResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostSequencesAsyncBySequenceTypeAndFileFormatResponse(responseBuilder.build(), entity);
    }
  }
}
