/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.ng.accesscontrol.scopes;

import static io.harness.annotations.dev.HarnessTeam.PL;

import static org.apache.commons.lang3.StringUtils.isBlank;

import io.harness.accesscontrol.scopes.ScopeDTO;
import io.harness.annotations.dev.OwnedBy;
import io.harness.exception.InvalidRequestException;
import io.harness.organization.remote.OrganizationClient;
import io.harness.project.remote.ProjectClient;
import io.harness.remote.client.NGRestUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javax.validation.constraints.NotNull;

@OwnedBy(PL)
@Singleton
public class ScopeNameMapper {
  private final OrganizationClient organizationClient;
  private final ProjectClient projectClient;

  @Inject
  public ScopeNameMapper(
      @Named("PRIVILEGED") OrganizationClient organizationClient, @Named("PRIVILEGED") ProjectClient projectClient) {
    this.organizationClient = organizationClient;
    this.projectClient = projectClient;
  }

  public ScopeNameDTO toScopeNameDTO(@NotNull ScopeDTO scopeDTO) {
    String orgName = null;
    String projectName = null;
    if (!isBlank(scopeDTO.getOrgIdentifier())) {
      orgName = NGRestUtils
                    .getResponseWithRetry(organizationClient.getOrganization(
                                              scopeDTO.getOrgIdentifier(), scopeDTO.getAccountIdentifier()),
                        String.format("Error while fetching organization details for org identifier: %s",
                            scopeDTO.getOrgIdentifier()))
                    .<InvalidRequestException>orElseThrow(
                        () -> { throw new InvalidRequestException("Organization details not found"); })
                    .getOrganization()
                    .getName();
    }
    if (!isBlank(scopeDTO.getProjectIdentifier())) {
      projectName = NGRestUtils
                        .getResponse(projectClient.getProject(scopeDTO.getProjectIdentifier(),
                            scopeDTO.getAccountIdentifier(), scopeDTO.getOrgIdentifier()))
                        .<InvalidRequestException>orElseThrow(
                            () -> { throw new InvalidRequestException("Project details not found"); })
                        .getProject()
                        .getName();
    }
    return ScopeNameDTO.builder()
        .accountIdentifier(scopeDTO.getAccountIdentifier())
        .orgIdentifier(scopeDTO.getOrgIdentifier())
        .projectIdentifier(scopeDTO.getProjectIdentifier())
        .orgName(orgName)
        .projectName(projectName)
        .build();
  }

  public static ScopeDTO fromScopeNameDTO(ScopeNameDTO scopeNameDTO) {
    return ScopeDTO.builder()
        .accountIdentifier(scopeNameDTO.getAccountIdentifier())
        .orgIdentifier(scopeNameDTO.getOrgIdentifier())
        .projectIdentifier(scopeNameDTO.getProjectIdentifier())
        .build();
  }
}
