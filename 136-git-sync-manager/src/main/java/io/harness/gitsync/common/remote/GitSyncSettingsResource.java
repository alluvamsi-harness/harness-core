package io.harness.gitsync.common.remote;

import static io.harness.annotations.dev.HarnessTeam.DX;

import io.harness.NGCommonEntityConstants;
import io.harness.annotations.dev.OwnedBy;
import io.harness.gitsync.common.dtos.GitSyncSettingsDTO;
import io.harness.gitsync.common.service.GitSyncSettingsService;

import com.google.inject.Inject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import retrofit2.http.Body;

@Api("/git-sync-settings")
@Path("/git-sync-settings")
@Produces({"application/json", "text/yaml", "text/html"})
@Consumes({"application/json", "text/yaml", "text/html"})
@AllArgsConstructor(onConstructor = @__({ @Inject }))
@OwnedBy(DX)
public class GitSyncSettingsResource {
  private final GitSyncSettingsService gitSyncSettingsService;

  @POST
  @ApiOperation(value = "Create a Git Sync Setting", nickname = "postGitSyncSetting")
  public GitSyncSettingsDTO create(@Body @NotNull GitSyncSettingsDTO request) {
    return gitSyncSettingsService.save(request);
  }

  @GET
  @ApiOperation(value = "Get git sync settings", nickname = "getGitSyncSettings")
  public GitSyncSettingsDTO get(@QueryParam(NGCommonEntityConstants.PROJECT_KEY) String projectIdentifier,
      @QueryParam(NGCommonEntityConstants.ORG_KEY) String organizationIdentifier,
      @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) @NotEmpty String accountIdentifier) {
    return gitSyncSettingsService.get(accountIdentifier, organizationIdentifier, projectIdentifier);
  }
}
