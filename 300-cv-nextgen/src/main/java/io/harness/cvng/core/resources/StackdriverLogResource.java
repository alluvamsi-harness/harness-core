package io.harness.cvng.core.resources;

import io.harness.annotations.ExposeInternalException;
import io.harness.cvng.core.services.api.StackdriverService;
import io.harness.ng.core.dto.ErrorDTO;
import io.harness.ng.core.dto.FailureDTO;
import io.harness.ng.core.dto.ResponseDTO;
import io.harness.security.annotations.NextGenManagerAuth;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.LinkedHashMap;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Api("stackdriver-log")
@Path("/stackdriver-log")
@Produces("application/json")
@NextGenManagerAuth
@ExposeInternalException
@ApiResponses(value =
    {
      @ApiResponse(code = 400, response = FailureDTO.class, message = "Bad Request")
      , @ApiResponse(code = 500, response = ErrorDTO.class, message = "Internal server error")
    })
public class StackdriverLogResource {
  @Inject private StackdriverService stackdriverService;

  @POST
  @Path("/sample-data")
  @Timed
  @ExceptionMetered
  @ApiOperation(value = "get sample data for a query", nickname = "getStackdriverLogSampleData")
  public ResponseDTO<List<LinkedHashMap>> getStackdriverSampleData(@NotNull @QueryParam("accountId") String accountId,
      @NotNull @QueryParam("connectorIdentifier") final String connectorIdentifier,
      @QueryParam("orgIdentifier") @NotNull String orgIdentifier,
      @QueryParam("projectIdentifier") @NotNull String projectIdentifier, @QueryParam("tracingId") String tracingId,
      @NotNull String query) {
    return ResponseDTO.newResponse(stackdriverService.getSampleLogData(
        accountId, connectorIdentifier, orgIdentifier, projectIdentifier, query, tracingId));
  }
}
