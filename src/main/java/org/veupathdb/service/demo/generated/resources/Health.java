package org.veupathdb.service.demo.generated.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.demo.generated.model.HealthResponse;
import org.veupathdb.service.demo.generated.model.ServerError;
import org.veupathdb.service.demo.generated.support.ResponseDelegate;

@Path("/health")
public interface Health {
  @GET
  @Produces("application/json")
  GetHealthResponse getHealth();

  class GetHealthResponse extends ResponseDelegate {
    private GetHealthResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetHealthResponse(Response response) {
      super(response);
    }

    public static GetHealthResponse respond200WithApplicationJson(HealthResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetHealthResponse(responseBuilder.build(), entity);
    }

    public static GetHealthResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetHealthResponse(responseBuilder.build(), entity);
    }
  }
}
