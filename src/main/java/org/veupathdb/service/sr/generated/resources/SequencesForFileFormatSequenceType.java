package org.veupathdb.service.sr.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.veupathdb.service.sr.generated.model.DeflineFormat;
import org.veupathdb.service.sr.generated.model.PlainTextFastaResponse;
import org.veupathdb.service.sr.generated.model.StartOffset;
import org.veupathdb.service.sr.generated.support.ResponseDelegate;

import java.io.InputStream;

@Path("/sequences-for/{fileFormat}/{sequenceType}")
public interface SequencesForFileFormatSequenceType {
  @POST
  @Produces("text/x-fasta")
  @Consumes("multipart/form-data")
  PostSequencesForByFileFormatAndSequenceTypeResponse postSequencesForByFileFormatAndSequenceType(
      @PathParam("fileFormat") String fileFormat,
      @PathParam("sequenceType") String sequenceType,
      @QueryParam("deflineFormat") DeflineFormat deflineFormat,
      @QueryParam("forceStrandedness") @DefaultValue("false") Boolean forceStrandedness,
      @QueryParam("basesPerLine") @DefaultValue("60") Integer basesPerLine,
      @QueryParam("startOffset") StartOffset startOffset,
      @FormDataParam("uploadMethod") String uploadMethod,
      @FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataContentDisposition meta,
      @FormDataParam("url") String url);

  class PostSequencesForByFileFormatAndSequenceTypeResponse extends ResponseDelegate {
    private PostSequencesForByFileFormatAndSequenceTypeResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostSequencesForByFileFormatAndSequenceTypeResponse(Response response) {
      super(response);
    }

    public static PostSequencesForByFileFormatAndSequenceTypeResponse respond200WithTextXFasta(
        PlainTextFastaResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "text/x-fasta");
      responseBuilder.entity(entity);
      return new PostSequencesForByFileFormatAndSequenceTypeResponse(responseBuilder.build(), entity);
    }
  }
}
