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
import org.veupathdb.service.sr.generated.model.PlainTextFastaResponse;
import org.veupathdb.service.sr.generated.model.SequencePostRequest;
import org.veupathdb.service.sr.generated.model.SequencesSequenceTypeFileFormatPostMultipartFormData;
import org.veupathdb.service.sr.generated.model.StartOffset;
import org.veupathdb.service.sr.generated.support.ResponseDelegate;

@Path("/sequences/{sequenceType}")
public interface SequencesSequenceType {
  @POST
  @Produces("text/x-fasta")
  @Consumes("application/json")
  PostSequencesBySequenceTypeResponse postSequencesBySequenceType(
      @PathParam("sequenceType") String sequenceType, SequencePostRequest entity);

  @POST
  @Path("/{fileFormat}")
  @Produces("text/x-fasta")
  @Consumes("multipart/form-data")
  PostSequencesBySequenceTypeAndFileFormatResponse postSequencesBySequenceTypeAndFileFormat(
      @PathParam("sequenceType") String sequenceType, @PathParam("fileFormat") String fileFormat,
      @QueryParam("deflineFormat") @DefaultValue("REGIONONLY") DeflineFormat deflineFormat,
      @QueryParam("basesPerLine") @DefaultValue("60") Integer basesPerLine,
      @QueryParam("startOffset") @DefaultValue("ONE") StartOffset startOffset,
      SequencesSequenceTypeFileFormatPostMultipartFormData entity);

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

  class PostSequencesBySequenceTypeAndFileFormatResponse extends ResponseDelegate {
    private PostSequencesBySequenceTypeAndFileFormatResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostSequencesBySequenceTypeAndFileFormatResponse(Response response) {
      super(response);
    }

    public static PostSequencesBySequenceTypeAndFileFormatResponse respond200WithTextXFasta(
        PlainTextFastaResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "text/x-fasta");
      responseBuilder.entity(entity);
      return new PostSequencesBySequenceTypeAndFileFormatResponse(responseBuilder.build(), entity);
    }
  }
}
