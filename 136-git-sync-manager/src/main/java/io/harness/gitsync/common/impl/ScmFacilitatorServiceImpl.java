/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.gitsync.common.impl;

import static io.harness.data.structure.CollectionUtils.emptyIfNull;
import static io.harness.data.structure.EmptyPredicate.isEmpty;
import static io.harness.data.structure.EmptyPredicate.isNotEmpty;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.PageRequestDTO;
import io.harness.beans.Scope;
import io.harness.delegate.beans.connector.scm.ScmConnector;
import io.harness.gitsync.beans.GitRepositoryDTO;
import io.harness.gitsync.common.beans.InfoForGitPush;
import io.harness.gitsync.common.beans.ScmApis;
import io.harness.gitsync.common.dtos.GitBranchDetailsDTO;
import io.harness.gitsync.common.dtos.GitBranchesResponseDTO;
import io.harness.gitsync.common.dtos.GitRepositoryResponseDTO;
import io.harness.gitsync.common.dtos.ScmCommitFileResponseDTO;
import io.harness.gitsync.common.dtos.ScmCreateBranchRequestDTO;
import io.harness.gitsync.common.dtos.ScmCreateFileRequestDTO;
import io.harness.gitsync.common.dtos.ScmCreatePRRequestDTO;
import io.harness.gitsync.common.dtos.ScmCreatePRResponseDTO;
import io.harness.gitsync.common.dtos.ScmGetFileRequestDTO;
import io.harness.gitsync.common.dtos.ScmGetFileResponseDTO;
import io.harness.gitsync.common.dtos.ScmUpdateFileRequestDTO;
import io.harness.gitsync.common.helper.GitSyncConnectorHelper;
import io.harness.gitsync.common.scmerrorhandling.ScmApiErrorHandlingHelper;
import io.harness.gitsync.common.service.ScmFacilitatorService;
import io.harness.gitsync.common.service.ScmOrchestratorService;
import io.harness.ng.beans.PageRequest;
import io.harness.product.ci.scm.proto.CreateBranchResponse;
import io.harness.product.ci.scm.proto.CreateFileResponse;
import io.harness.product.ci.scm.proto.CreatePRResponse;
import io.harness.product.ci.scm.proto.FileContent;
import io.harness.product.ci.scm.proto.GetUserRepoResponse;
import io.harness.product.ci.scm.proto.GetUserReposResponse;
import io.harness.product.ci.scm.proto.ListBranchesWithDefaultResponse;
import io.harness.product.ci.scm.proto.Repository;
import io.harness.product.ci.scm.proto.UpdateFileResponse;

import com.google.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor = @__({ @Inject }))
@OwnedBy(HarnessTeam.PL)
public class ScmFacilitatorServiceImpl implements ScmFacilitatorService {
  GitSyncConnectorHelper gitSyncConnectorHelper;
  ScmOrchestratorService scmOrchestratorService;

  @Override
  public List<String> listBranchesUsingConnector(String accountIdentifier, String orgIdentifier,
      String projectIdentifier, String connectorIdentifierRef, String repoURL, PageRequest pageRequest,
      String searchTerm) {
    return scmOrchestratorService.processScmRequestUsingConnectorSettings(scmClientFacilitatorService
        -> scmClientFacilitatorService.listBranchesForRepoByConnector(accountIdentifier, orgIdentifier,
            projectIdentifier, connectorIdentifierRef, repoURL, pageRequest, searchTerm),
        projectIdentifier, orgIdentifier, accountIdentifier, connectorIdentifierRef, null, null);
  }

  @Override
  public List<GitRepositoryResponseDTO> listReposByRefConnector(String accountIdentifier, String orgIdentifier,
      String projectIdentifier, String connectorRef, PageRequest pageRequest, String searchTerm) {
    ScmConnector scmConnector =
        gitSyncConnectorHelper.getScmConnector(accountIdentifier, orgIdentifier, projectIdentifier, connectorRef);
    GetUserReposResponse response = scmOrchestratorService.processScmRequestUsingConnectorSettings(
        scmClientFacilitatorService
        -> scmClientFacilitatorService.listUserRepos(accountIdentifier, orgIdentifier, projectIdentifier, scmConnector,
            PageRequestDTO.builder().pageIndex(pageRequest.getPageIndex()).pageSize(pageRequest.getPageSize()).build()),
        scmConnector);
    if (isFailureResponse(response.getStatus())) {
      ScmApiErrorHandlingHelper.processAndThrowError(
          ScmApis.LIST_REPOSITORIES, scmConnector.getConnectorType(), response.getStatus(), response.getError());
    }

    return prepareListRepoResponse(scmConnector, response);
  }

  @Override
  public GitBranchesResponseDTO listBranchesV2(String accountIdentifier, String orgIdentifier, String projectIdentifier,
      String connectorRef, String repoName, PageRequest pageRequest, String searchTerm) {
    final ScmConnector scmConnector = gitSyncConnectorHelper.getScmConnectorForGivenRepo(
        accountIdentifier, orgIdentifier, projectIdentifier, connectorRef, repoName);
    ListBranchesWithDefaultResponse listBranchesWithDefaultResponse =
        scmOrchestratorService.processScmRequestUsingConnectorSettings(scmClientFacilitatorService
            -> scmClientFacilitatorService.listBranches(accountIdentifier, orgIdentifier, projectIdentifier,
                scmConnector,
                PageRequestDTO.builder()
                    .pageIndex(pageRequest.getPageIndex())
                    .pageSize(pageRequest.getPageSize())
                    .build()),
            scmConnector);

    if (isFailureResponse(listBranchesWithDefaultResponse.getStatus())) {
      ScmApiErrorHandlingHelper.processAndThrowError(ScmApis.LIST_BRANCHES, scmConnector.getConnectorType(),
          listBranchesWithDefaultResponse.getStatus(), listBranchesWithDefaultResponse.getError());
    }

    List<GitBranchDetailsDTO> gitBranches =
        emptyIfNull(listBranchesWithDefaultResponse.getBranchesList())
            .stream()
            .map(branchName -> GitBranchDetailsDTO.builder().name(branchName).build())
            .collect(Collectors.toList());
    return GitBranchesResponseDTO.builder()
        .branches(gitBranches)
        .defaultBranch(GitBranchDetailsDTO.builder().name(listBranchesWithDefaultResponse.getDefaultBranch()).build())
        .build();
  }

  @Override
  public ScmCommitFileResponseDTO createFile(ScmCreateFileRequestDTO scmCreateFileRequestDTO) {
    Scope scope = scmCreateFileRequestDTO.getScope();
    // TODO Put validations over request here
    ScmConnector scmConnector = gitSyncConnectorHelper.getScmConnectorForGivenRepo(scope.getAccountIdentifier(),
        scope.getOrgIdentifier(), scope.getProjectIdentifier(), scmCreateFileRequestDTO.getConnectorRef(),
        scmCreateFileRequestDTO.getRepoName());
    CreateFileResponse createFileResponse =
        scmOrchestratorService.processScmRequestUsingConnectorSettings(scmClientFacilitatorService
            -> scmClientFacilitatorService.createFile(InfoForGitPush.builder()
                                                          .accountId(scope.getAccountIdentifier())
                                                          .orgIdentifier(scope.getOrgIdentifier())
                                                          .projectIdentifier(scope.getProjectIdentifier())
                                                          .filePath(scmCreateFileRequestDTO.getFilePath())
                                                          .baseBranch(scmCreateFileRequestDTO.getBaseBranch())
                                                          .branch(scmCreateFileRequestDTO.getBranchName())
                                                          .commitMsg(scmCreateFileRequestDTO.getCommitMessage())
                                                          .completeFilePath(scmCreateFileRequestDTO.getFilePath())
                                                          .isNewBranch(scmCreateFileRequestDTO.isCommitToNewBranch())
                                                          .scmConnector(scmConnector)
                                                          .yaml(scmCreateFileRequestDTO.getFileContent())
                                                          .build()),
            scmConnector);

    if (isFailureResponse(createFileResponse.getStatus())) {
      ScmApiErrorHandlingHelper.processAndThrowError(ScmApis.CREATE_FILE, scmConnector.getConnectorType(),
          createFileResponse.getStatus(), createFileResponse.getError());
    }

    return ScmCommitFileResponseDTO.builder()
        .commitId(createFileResponse.getCommitId())
        .blobId(createFileResponse.getBlobId())
        .build();
  }

  @Override
  public ScmCommitFileResponseDTO updateFile(ScmUpdateFileRequestDTO scmUpdateFileRequestDTO) {
    Scope scope = scmUpdateFileRequestDTO.getScope();
    // TODO Put validations over request here
    ScmConnector scmConnector = gitSyncConnectorHelper.getScmConnectorForGivenRepo(scope.getAccountIdentifier(),
        scope.getOrgIdentifier(), scope.getProjectIdentifier(), scmUpdateFileRequestDTO.getConnectorRef(),
        scmUpdateFileRequestDTO.getRepoName());
    UpdateFileResponse updateFileResponse =
        scmOrchestratorService.processScmRequestUsingConnectorSettings(scmClientFacilitatorService
            -> scmClientFacilitatorService.updateFile(InfoForGitPush.builder()
                                                          .accountId(scope.getAccountIdentifier())
                                                          .orgIdentifier(scope.getOrgIdentifier())
                                                          .projectIdentifier(scope.getProjectIdentifier())
                                                          .filePath(scmUpdateFileRequestDTO.getFilePath())
                                                          .baseBranch(scmUpdateFileRequestDTO.getBaseBranch())
                                                          .branch(scmUpdateFileRequestDTO.getBranchName())
                                                          .commitMsg(scmUpdateFileRequestDTO.getCommitMessage())
                                                          .completeFilePath(scmUpdateFileRequestDTO.getFilePath())
                                                          .isNewBranch(scmUpdateFileRequestDTO.isCommitToNewBranch())
                                                          .scmConnector(scmConnector)
                                                          .oldFileSha(scmUpdateFileRequestDTO.getOldFileSha())
                                                          .yaml(scmUpdateFileRequestDTO.getFileContent())
                                                          .build()),
            scmConnector);

    if (isFailureResponse(updateFileResponse.getStatus())) {
      ScmApiErrorHandlingHelper.processAndThrowError(ScmApis.UPDATE_FILE, scmConnector.getConnectorType(),
          updateFileResponse.getStatus(), updateFileResponse.getError());
    }

    return ScmCommitFileResponseDTO.builder()
        .commitId(updateFileResponse.getCommitId())
        .blobId(updateFileResponse.getBlobId())
        .build();
  }

  @Override
  public ScmCreatePRResponseDTO createPR(ScmCreatePRRequestDTO scmCreatePRRequestDTO) {
    scmCreatePRRequestDTO.validate();

    Scope scope = scmCreatePRRequestDTO.getScope();
    ScmConnector scmConnector =
        gitSyncConnectorHelper.getScmConnectorForGivenRepo(scope.getAccountIdentifier(), scope.getOrgIdentifier(),
            scope.getProjectIdentifier(), scmCreatePRRequestDTO.getConnectorRef(), scmCreatePRRequestDTO.getRepoName());

    CreatePRResponse createPRResponse =
        scmOrchestratorService.processScmRequestUsingConnectorSettings(scmClientFacilitatorService
            -> scmClientFacilitatorService.createPullRequest(scope, scmCreatePRRequestDTO.getConnectorRef(),
                scmCreatePRRequestDTO.getRepoName(), scmCreatePRRequestDTO.getSourceBranch(),
                scmCreatePRRequestDTO.getTargetBranch(), scmCreatePRRequestDTO.getTitle()),
            scmConnector);

    if (isFailureResponse(createPRResponse.getStatus())) {
      ScmApiErrorHandlingHelper.processAndThrowError(ScmApis.CREATE_PULL_REQUEST, scmConnector.getConnectorType(),
          createPRResponse.getStatus(), createPRResponse.getError());
    }

    return ScmCreatePRResponseDTO.builder().prNumber(createPRResponse.getNumber()).build();
  }

  @Override
  public ScmGetFileResponseDTO getFileByBranch(ScmGetFileRequestDTO scmGetFileRequestDTO) {
    Scope scope = scmGetFileRequestDTO.getScope();
    String branchName = isEmpty(scmGetFileRequestDTO.getBranchName())
        ? getDefaultBranch(scope.getAccountIdentifier(), scope.getOrgIdentifier(), scope.getProjectIdentifier(),
            scmGetFileRequestDTO.getConnectorRef(), scmGetFileRequestDTO.getRepoName())
        : scmGetFileRequestDTO.getBranchName();

    ScmConnector scmConnector =
        gitSyncConnectorHelper.getScmConnectorForGivenRepo(scope.getAccountIdentifier(), scope.getOrgIdentifier(),
            scope.getProjectIdentifier(), scmGetFileRequestDTO.getConnectorRef(), scmGetFileRequestDTO.getRepoName());

    FileContent fileContent = scmOrchestratorService.processScmRequestUsingConnectorSettings(scmClientFacilitatorService
        -> scmClientFacilitatorService.getFile(scope.getAccountIdentifier(), scope.getOrgIdentifier(),
            scope.getProjectIdentifier(), scmGetFileRequestDTO.getConnectorRef(), scmGetFileRequestDTO.getRepoName(),
            branchName, scmGetFileRequestDTO.getFilePath(), null),
        scmConnector);

    if (isFailureResponse(fileContent.getStatus())) {
      ScmApiErrorHandlingHelper.processAndThrowError(
          ScmApis.GET_FILE, scmConnector.getConnectorType(), fileContent.getStatus(), fileContent.getError());
    }

    return ScmGetFileResponseDTO.builder()
        .fileContent(fileContent.getContent())
        .blobId(fileContent.getBlobId())
        .commitId(fileContent.getCommitId())
        .build();
  }

  public void createBranch(ScmCreateBranchRequestDTO scmCreateBranchRequestDTO) {
    Scope scope = scmCreateBranchRequestDTO.getScope();
    ScmConnector scmConnector = gitSyncConnectorHelper.getScmConnectorForGivenRepo(scope.getAccountIdentifier(),
        scope.getOrgIdentifier(), scope.getProjectIdentifier(), scmCreateBranchRequestDTO.getConnectorRef(),
        scmCreateBranchRequestDTO.getRepoName());
    createBranch(
        scmCreateBranchRequestDTO.getBranchName(), scmCreateBranchRequestDTO.getBaseBranchName(), scmConnector);
  }

  private void createBranch(String branch, String baseBranch, ScmConnector scmConnector) {
    CreateBranchResponse createBranchResponse =
        scmOrchestratorService.processScmRequestUsingConnectorSettings(scmClientFacilitatorService
            -> scmClientFacilitatorService.createBranch(branch, baseBranch, scmConnector),
            scmConnector);

    if (isFailureResponse(createBranchResponse.getStatus())) {
      ScmApiErrorHandlingHelper.processAndThrowError(ScmApis.CREATE_BRANCH, scmConnector.getConnectorType(),
          createBranchResponse.getStatus(), createBranchResponse.getError());
    }

    return;
  }

  @Override
  public String getDefaultBranch(
      String accountIdentifier, String orgIdentifier, String projectIdentifier, String connectorRef, String repoName) {
    final ScmConnector scmConnector = gitSyncConnectorHelper.getScmConnectorForGivenRepo(
        accountIdentifier, orgIdentifier, projectIdentifier, connectorRef, repoName);
    GetUserRepoResponse getUserRepoResponse =
        scmOrchestratorService.processScmRequestUsingConnectorSettings(scmClientFacilitatorService
            -> scmClientFacilitatorService.getRepoDetails(
                accountIdentifier, orgIdentifier, projectIdentifier, scmConnector),
            scmConnector);

    if (isFailureResponse(getUserRepoResponse.getStatus())) {
      ScmApiErrorHandlingHelper.processAndThrowError(ScmApis.GET_DEFAULT_BRANCH, scmConnector.getConnectorType(),
          getUserRepoResponse.getStatus(), getUserRepoResponse.getError());
    }
    return getUserRepoResponse.getRepo().getBranch();
  }

  @Override
  public String getDefaultBranch(
      String accountIdentifier, String orgIdentifier, String projectIdentifier, String connectorRef, String repoName) {
    final ScmConnector scmConnector = gitSyncConnectorHelper.getScmConnectorForGivenRepo(
        accountIdentifier, orgIdentifier, projectIdentifier, connectorRef, repoName);
    GetUserRepoResponse getUserRepoResponse =
        scmOrchestratorService.processScmRequestUsingConnectorSettings(scmClientFacilitatorService
            -> scmClientFacilitatorService.getRepoDetails(
                accountIdentifier, orgIdentifier, projectIdentifier, scmConnector),
            scmConnector);

    if (isFailureResponse(getUserRepoResponse.getStatus())) {
      ScmApiErrorHandlingHelper.processAndThrowError(ScmApis.GET_DEFAULT_BRANCH, scmConnector.getConnectorType(),
          getUserRepoResponse.getStatus(), getUserRepoResponse.getError());
    }
    return getUserRepoResponse.getRepo().getBranch();
  }

  private List<GitRepositoryResponseDTO> prepareListRepoResponse(
      ScmConnector scmConnector, GetUserReposResponse response) {
    GitRepositoryDTO gitRepository = scmConnector.getGitRepositoryDetails();
    if (isNotEmpty(gitRepository.getName())) {
      return Collections.singletonList(GitRepositoryResponseDTO.builder().name(gitRepository.getName()).build());
    } else if (isNotEmpty(gitRepository.getOrg())) {
      return emptyIfNull(response.getReposList())
          .stream()
          .filter(repository -> repository.getNamespace().equals(gitRepository.getOrg()))
          .map(repository -> GitRepositoryResponseDTO.builder().name(repository.getName()).build())
          .collect(Collectors.toList());
    } else {
      return emptyIfNull(response.getReposList())
          .stream()
          .map(repository -> GitRepositoryResponseDTO.builder().name(repository.getName()).build())
          .collect(Collectors.toList());
    }
  }

  private boolean isFailureResponse(int statusCode) {
    return statusCode >= 300;
  }
}
