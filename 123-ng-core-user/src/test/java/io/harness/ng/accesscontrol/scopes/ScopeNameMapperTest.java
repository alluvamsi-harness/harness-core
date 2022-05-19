/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.ng.accesscontrol.scopes;

import static io.harness.annotations.dev.HarnessTeam.PL;
import static io.harness.rule.OwnerRule.NAMANG;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import io.harness.CategoryTest;
import io.harness.accesscontrol.scopes.ScopeDTO;
import io.harness.annotations.dev.OwnedBy;
import io.harness.category.element.UnitTests;
import io.harness.exception.InvalidRequestException;
import io.harness.ng.core.dto.OrganizationDTO;
import io.harness.ng.core.dto.OrganizationResponse;
import io.harness.ng.core.dto.ProjectDTO;
import io.harness.ng.core.dto.ProjectResponse;
import io.harness.ng.core.dto.ResponseDTO;
import io.harness.organization.remote.OrganizationClient;
import io.harness.project.remote.ProjectClient;
import io.harness.rule.Owner;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import retrofit2.Call;
import retrofit2.Response;

@OwnedBy(PL)
public class ScopeNameMapperTest extends CategoryTest {
  @Mock private OrganizationClient organizationClient;
  @Mock private ProjectClient projectClient;
  @Spy @Inject @InjectMocks private ScopeNameMapper scopeNameMapper;

  private static final String ACCOUNT_IDENTIFIER = "A1";
  private static final String ORG_IDENTIFIER = "O1";
  private static final String PROJECT_IDENTIFIER = "P1";
  private static final String ORG_NAME = "O1_Name";
  private static final String PROJECT_NAME = "P1_Name";
  private static final OrganizationResponse organizationResponse =
      OrganizationResponse.builder()
          .organization(OrganizationDTO.builder().name(ORG_NAME).identifier(ORG_IDENTIFIER).build())
          .build();
  private static final ProjectResponse projectResponse =
      ProjectResponse.builder()
          .project(ProjectDTO.builder().name(PROJECT_NAME).identifier(PROJECT_IDENTIFIER).build())
          .build();

  @Before
  public void setup() {
    initMocks(this);
  }

  @Test
  @Owner(developers = NAMANG)
  @Category(UnitTests.class)
  public void testToScopeNameDTOWhenOrgWhenExists() throws IOException {
    ScopeDTO scopeDTO = ScopeDTO.builder().accountIdentifier(ACCOUNT_IDENTIFIER).orgIdentifier(ORG_IDENTIFIER).build();
    Call<ResponseDTO<Optional<OrganizationResponse>>> request = mock(Call.class);
    doReturn(request).when(organizationClient).getOrganization(ORG_IDENTIFIER, ACCOUNT_IDENTIFIER);

    when(request.clone()).thenReturn(request);
    doReturn(Response.success(ResponseDTO.newResponse(Optional.of(organizationResponse)))).when(request).execute();
    ScopeNameDTO result = scopeNameMapper.toScopeNameDTO(scopeDTO);
    verify(organizationClient, times(1)).getOrganization(ORG_IDENTIFIER, ACCOUNT_IDENTIFIER);
    verifyNoMoreInteractions(organizationClient);
    verifyNoMoreInteractions(projectClient);
    assertThat(result.getAccountIdentifier()).isEqualTo(ACCOUNT_IDENTIFIER);
    assertThat(result.getOrgIdentifier()).isEqualTo(ORG_IDENTIFIER);
    assertThat(result.getProjectIdentifier()).isEqualTo(null);
    assertThat(result.getOrgName()).isEqualTo(ORG_NAME);
    assertThat(result.getProjectName()).isEqualTo(null);
  }

  @Test
  @Owner(developers = NAMANG)
  @Category(UnitTests.class)
  public void testToScopeNameDTOWhenOrgWhenNotExists() throws IOException {
    ScopeDTO scopeDTO = ScopeDTO.builder().accountIdentifier(ACCOUNT_IDENTIFIER).orgIdentifier(ORG_IDENTIFIER).build();
    Call<ResponseDTO<Optional<OrganizationResponse>>> request = mock(Call.class);
    doReturn(request).when(organizationClient).getOrganization(ORG_IDENTIFIER, ACCOUNT_IDENTIFIER);

    when(request.clone()).thenReturn(request);
    doReturn(Response.success(ResponseDTO.newResponse(Optional.empty()))).when(request).execute();
    try {
      scopeNameMapper.toScopeNameDTO(scopeDTO);
      fail("Expected failure as org does not exists");
    } catch (InvalidRequestException ex) {
      assertThat(ex.getParams().get("message")).isEqualTo("Organization details not found");
    }
  }

  @Test
  @Owner(developers = NAMANG)
  @Category(UnitTests.class)
  public void testToScopeNameDTOWhenProjectWhenOrgNotExists() throws IOException {
    ScopeDTO scopeDTO = ScopeDTO.builder()
                            .accountIdentifier(ACCOUNT_IDENTIFIER)
                            .orgIdentifier(ORG_IDENTIFIER)
                            .projectIdentifier(PROJECT_IDENTIFIER)
                            .build();
    Call<ResponseDTO<Optional<OrganizationResponse>>> request = mock(Call.class);
    doReturn(request).when(organizationClient).getOrganization(ORG_IDENTIFIER, ACCOUNT_IDENTIFIER);

    when(request.clone()).thenReturn(request);
    doReturn(Response.success(ResponseDTO.newResponse(Optional.empty()))).when(request).execute();
    try {
      scopeNameMapper.toScopeNameDTO(scopeDTO);
      fail("Expected failure as org does not exists");
    } catch (InvalidRequestException ex) {
      assertThat(ex.getParams().get("message")).isEqualTo("Organization details not found");
    }
  }

  @Test
  @Owner(developers = NAMANG)
  @Category(UnitTests.class)
  public void testToScopeNameDTOWhenProjectWhenOrgExistsProjectNotExists() throws IOException {
    ScopeDTO scopeDTO = ScopeDTO.builder()
                            .accountIdentifier(ACCOUNT_IDENTIFIER)
                            .orgIdentifier(ORG_IDENTIFIER)
                            .projectIdentifier(PROJECT_IDENTIFIER)
                            .build();
    Call<ResponseDTO<Optional<OrganizationResponse>>> request = mock(Call.class);
    doReturn(request).when(organizationClient).getOrganization(ORG_IDENTIFIER, ACCOUNT_IDENTIFIER);

    when(request.clone()).thenReturn(request);
    doReturn(Response.success(ResponseDTO.newResponse(Optional.of(organizationResponse)))).when(request).execute();

    Call<ResponseDTO<Optional<ProjectResponse>>> request_1 = mock(Call.class);
    doReturn(request_1).when(projectClient).getProject(PROJECT_IDENTIFIER, ACCOUNT_IDENTIFIER, ORG_IDENTIFIER);

    when(request_1.clone()).thenReturn(request_1);
    doReturn(Response.success(ResponseDTO.newResponse(Optional.empty()))).when(request_1).execute();
    try {
      scopeNameMapper.toScopeNameDTO(scopeDTO);
      fail("Expected failure as project does not exists");
    } catch (InvalidRequestException ex) {
      assertThat(ex.getParams().get("message")).isEqualTo("Project details not found");
    }
  }

  @Test
  @Owner(developers = NAMANG)
  @Category(UnitTests.class)
  public void testToScopeNameDTOWhenProjectWhenOrgExistsProjectExists() throws IOException {
    ScopeDTO scopeDTO = ScopeDTO.builder()
                            .accountIdentifier(ACCOUNT_IDENTIFIER)
                            .orgIdentifier(ORG_IDENTIFIER)
                            .projectIdentifier(PROJECT_IDENTIFIER)
                            .build();
    Call<ResponseDTO<Optional<OrganizationResponse>>> request = mock(Call.class);
    doReturn(request).when(organizationClient).getOrganization(ORG_IDENTIFIER, ACCOUNT_IDENTIFIER);

    when(request.clone()).thenReturn(request);
    doReturn(Response.success(ResponseDTO.newResponse(Optional.of(organizationResponse)))).when(request).execute();

    Call<ResponseDTO<Optional<ProjectResponse>>> request_1 = mock(Call.class);
    doReturn(request_1).when(projectClient).getProject(PROJECT_IDENTIFIER, ACCOUNT_IDENTIFIER, ORG_IDENTIFIER);

    when(request_1.clone()).thenReturn(request_1);
    doReturn(Response.success(ResponseDTO.newResponse(Optional.of(projectResponse)))).when(request_1).execute();
    ScopeNameDTO result = scopeNameMapper.toScopeNameDTO(scopeDTO);
    verify(organizationClient, times(1)).getOrganization(ORG_IDENTIFIER, ACCOUNT_IDENTIFIER);
    verify(projectClient, times(1)).getProject(PROJECT_IDENTIFIER, ACCOUNT_IDENTIFIER, ORG_IDENTIFIER);
    verifyNoMoreInteractions(organizationClient);
    verifyNoMoreInteractions(projectClient);
    assertThat(result.getAccountIdentifier()).isEqualTo(ACCOUNT_IDENTIFIER);
    assertThat(result.getOrgIdentifier()).isEqualTo(ORG_IDENTIFIER);
    assertThat(result.getProjectIdentifier()).isEqualTo(PROJECT_IDENTIFIER);
    assertThat(result.getOrgName()).isEqualTo(ORG_NAME);
    assertThat(result.getProjectName()).isEqualTo(PROJECT_NAME);
  }

  @Test
  @Owner(developers = NAMANG)
  @Category(UnitTests.class)
  public void testToScopeNameDTOWhenAccount() {
    ScopeDTO scopeDTO = ScopeDTO.builder().accountIdentifier(ACCOUNT_IDENTIFIER).build();

    ScopeNameDTO result = scopeNameMapper.toScopeNameDTO(scopeDTO);
    verifyNoMoreInteractions(organizationClient);
    verifyNoMoreInteractions(projectClient);
    assertThat(result.getAccountIdentifier()).isEqualTo(ACCOUNT_IDENTIFIER);
    assertThat(result.getOrgIdentifier()).isEqualTo(null);
    assertThat(result.getProjectIdentifier()).isEqualTo(null);
    assertThat(result.getOrgName()).isEqualTo(null);
    assertThat(result.getProjectName()).isEqualTo(null);
  }
}