package org.veupathdb.service.demo.generated.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.demo.generated.support.ResponseDelegate;

@Path("/api")
public interface Api {
  @GET
  @Produces("text/html")
  GetApiResponse getApi();

  class GetApiResponse extends ResponseDelegate {
    private GetApiResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetApiResponse(Response response) {
      super(response);
    }

    public static GetApiResponse respond200WithTextHtml(Object entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "text/html");
      responseBuilder.entity(entity);
      return new GetApiResponse(responseBuilder.build(), entity);
    }
  }
}
