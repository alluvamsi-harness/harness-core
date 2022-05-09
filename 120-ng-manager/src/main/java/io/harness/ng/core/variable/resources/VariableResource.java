/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ng.core.variable.resources;

import static io.harness.NGCommonEntityConstants.ACCOUNT_KEY;
import static io.harness.NGCommonEntityConstants.IDENTIFIER_KEY;
import static io.harness.NGCommonEntityConstants.ORG_KEY;
import static io.harness.NGCommonEntityConstants.PROJECT_KEY;
import static io.harness.NGResourceFilterConstants.PAGE_KEY;
import static io.harness.NGResourceFilterConstants.SEARCH_TERM_KEY;
import static io.harness.NGResourceFilterConstants.SIZE_KEY;
import static io.harness.ng.core.variable.VariablePermissions.VARIABLE_DELETE_PERMISSION;
import static io.harness.ng.core.variable.VariablePermissions.VARIABLE_EDIT_PERMISSION;
import static io.harness.ng.core.variable.VariablePermissions.VARIABLE_RESOURCE_TYPE;
import static io.harness.ng.core.variable.VariablePermissions.VARIABLE_VIEW_PERMISSION;

import io.harness.accesscontrol.AccountIdentifier;
import io.harness.accesscontrol.NGAccessControlCheck;
import io.harness.accesscontrol.OrgIdentifier;
import io.harness.accesscontrol.ProjectIdentifier;
import io.harness.accesscontrol.acl.api.Resource;
import io.harness.accesscontrol.acl.api.ResourceScope;
import io.harness.accesscontrol.clients.AccessControlClient;
import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.ng.beans.PageResponse;
import io.harness.ng.core.dto.ResponseDTO;
import io.harness.ng.core.variable.dto.VariableRequestDTO;
import io.harness.ng.core.variable.dto.VariableResponseDTO;
import io.harness.ng.core.variable.entity.Variable;
import io.harness.ng.core.variable.mappers.VariableMapper;
import io.harness.ng.core.variable.services.VariableService;

import com.google.inject.Inject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@OwnedBy(HarnessTeam.PL)
@Api("/variables")
@Path("/variables")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@AllArgsConstructor(onConstructor = @__({ @Inject }))
public class VariableResource {
  private static final String INCLUDE_VARIABLES_FROM_EVERY_SUB_SCOPE = "includeVariablesFromEverySubScope";
  private final VariableService variableService;
  private final VariableMapper variableMapper;
  private final AccessControlClient accessControlClient;

  @GET
  @Path("{identifier}")
  @ApiOperation(value = "Get a Variable", nickname = "getVariable")
  @Hidden
  @NGAccessControlCheck(resourceType = VARIABLE_RESOURCE_TYPE, permission = VARIABLE_VIEW_PERMISSION)
  public ResponseDTO<VariableResponseDTO> get(@PathParam(IDENTIFIER_KEY) @NotNull String identifier,
      @QueryParam(ACCOUNT_KEY) @NotNull @AccountIdentifier String accountIdentifier,
      @QueryParam(ORG_KEY) @OrgIdentifier String orgIdentifier,
      @QueryParam(PROJECT_KEY) @ProjectIdentifier String projectIdentifier) {
    Optional<VariableResponseDTO> variable =
        variableService.get(accountIdentifier, orgIdentifier, projectIdentifier, identifier);
    if (!variable.isPresent()) {
      throw new NotFoundException(String.format("Variable with identifier [%s] in project [%s] and org [%s] not found",
          identifier, projectIdentifier, orgIdentifier));
    }
    return ResponseDTO.newResponse(variable.get());
  }

  @POST
  @ApiOperation(value = "Create a Variable", nickname = "createVariable")
  @Hidden
  public ResponseDTO<VariableResponseDTO> create(@QueryParam(ACCOUNT_KEY) @NotNull String accountIdentifier,
      @NotNull @Valid VariableRequestDTO variableRequestDTO) {
    accessControlClient.checkForAccessOrThrow(
        ResourceScope.of(accountIdentifier, variableRequestDTO.getVariable().getOrgIdentifier(),
            variableRequestDTO.getVariable().getProjectIdentifier()),
        Resource.of(VARIABLE_RESOURCE_TYPE, null), VARIABLE_EDIT_PERMISSION);
    Variable createdVariable = variableService.create(accountIdentifier, variableRequestDTO.getVariable());
    return ResponseDTO.newResponse(variableMapper.toResponseWrapper(createdVariable));
  }

  @GET
  @ApiOperation(value = "Gets Variable list", nickname = "getVariablesList")
  @Hidden
  @NGAccessControlCheck(resourceType = VARIABLE_RESOURCE_TYPE, permission = VARIABLE_VIEW_PERMISSION)
  public ResponseDTO<PageResponse<VariableResponseDTO>> list(
      @NotNull @QueryParam(ACCOUNT_KEY) @AccountIdentifier String accountIdentifier,
      @QueryParam(ORG_KEY) @OrgIdentifier String orgIdentifier,
      @QueryParam(PROJECT_KEY) @ProjectIdentifier String projectIdentifier,
      @QueryParam(PAGE_KEY) @DefaultValue("0") int page, @QueryParam(SIZE_KEY) @DefaultValue("100") int size,
      @QueryParam(SEARCH_TERM_KEY) String searchTerm,
      @QueryParam(INCLUDE_VARIABLES_FROM_EVERY_SUB_SCOPE) @DefaultValue(
          "false") boolean includeVariablesFromEverySubScope) {
    return ResponseDTO.newResponse(variableService.list(accountIdentifier, orgIdentifier, projectIdentifier, page, size,
        searchTerm, includeVariablesFromEverySubScope));
  }

  @PUT
  @ApiOperation(value = "Update a Variable", nickname = "updateVariable")
  @Hidden
  public ResponseDTO<VariableResponseDTO> update(@QueryParam(ACCOUNT_KEY) @NotNull String accountIdentifier,
      @NotNull @Valid VariableRequestDTO variableRequestDTO) {
    accessControlClient.checkForAccessOrThrow(
        ResourceScope.of(accountIdentifier, variableRequestDTO.getVariable().getOrgIdentifier(),
            variableRequestDTO.getVariable().getProjectIdentifier()),
        Resource.of(VARIABLE_RESOURCE_TYPE, null), VARIABLE_EDIT_PERMISSION);
    Variable updatedVariable = variableService.update(accountIdentifier, variableRequestDTO.getVariable());
    return ResponseDTO.newResponse(variableMapper.toResponseWrapper(updatedVariable));
  }

  @DELETE
  @Path("{identifier}")
  @ApiOperation(value = "Delete a Variable", nickname = "deleteVariable")
  @Hidden
  @NGAccessControlCheck(resourceType = VARIABLE_RESOURCE_TYPE, permission = VARIABLE_DELETE_PERMISSION)
  public ResponseDTO<Boolean> delete(@QueryParam(ACCOUNT_KEY) @NotNull @AccountIdentifier String accountIdentifier,
      @QueryParam(ORG_KEY) @OrgIdentifier String orgIdentifier,
      @QueryParam(PROJECT_KEY) @ProjectIdentifier String projectIdentifier,
      @PathParam(IDENTIFIER_KEY) @NotBlank String variableIdentifier) {
    boolean deleted = variableService.delete(accountIdentifier, orgIdentifier, projectIdentifier, variableIdentifier);
    return ResponseDTO.newResponse(deleted);
  }
}
