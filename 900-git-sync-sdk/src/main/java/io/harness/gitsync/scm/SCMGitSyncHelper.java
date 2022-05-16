/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.gitsync.scm;

import static io.harness.annotations.dev.HarnessTeam.DX;
import static io.harness.data.structure.EmptyPredicate.isNotEmpty;
import static io.harness.data.structure.HarnessStringUtils.emptyIfNull;
import static io.harness.gitsync.interceptor.GitSyncConstants.DEFAULT;

import io.harness.ScopeIdentifiers;
import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.Scope;
import io.harness.eraro.ErrorCode;
import io.harness.exception.ExceptionUtils;
import io.harness.exception.InvalidRequestException;
import io.harness.exception.WingsException;
import io.harness.exception.ngexception.beans.ScmErrorMetadataDTO;
import io.harness.git.model.ChangeType;
import io.harness.gitsync.FileInfo;
import io.harness.gitsync.GetFileRequest;
import io.harness.gitsync.GetFileResponse;
import io.harness.gitsync.GitMetaData;
import io.harness.gitsync.HarnessToGitPushInfoServiceGrpc.HarnessToGitPushInfoServiceBlockingStub;
import io.harness.gitsync.PushFileResponse;
import io.harness.gitsync.common.helper.ChangeTypeMapper;
import io.harness.gitsync.common.helper.GitSyncGrpcClientUtils;
import io.harness.gitsync.common.helper.UserPrincipalMapper;
import io.harness.gitsync.exceptions.GitSyncException;
import io.harness.gitsync.interceptor.GitEntityInfo;
import io.harness.gitsync.persistance.GitSyncSdkService;
import io.harness.gitsync.scm.beans.SCMNoOpResponse;
import io.harness.gitsync.scm.beans.ScmGetFileResponse;
import io.harness.gitsync.scm.beans.ScmGitMetaData;
import io.harness.gitsync.scm.beans.ScmPushResponse;
import io.harness.impl.ScmResponseStatusUtils;
import io.harness.ng.core.EntityDetail;
import io.harness.ng.core.entitydetail.EntityDetailRestToProtoMapper;
import io.harness.security.Principal;
import io.harness.security.SourcePrincipalContextBuilder;
import io.harness.security.dto.ServiceAccountPrincipal;
import io.harness.security.dto.ServicePrincipal;
import io.harness.security.dto.UserPrincipal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.StringValue;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Singleton
@Slf4j
@OwnedBy(DX)
public class SCMGitSyncHelper {
  @Inject private HarnessToGitPushInfoServiceBlockingStub harnessToGitPushInfoServiceBlockingStub;
  @Inject private EntityDetailRestToProtoMapper entityDetailRestToProtoMapper;
  @Inject GitSyncSdkService gitSyncSdkService;

  public ScmPushResponse pushToGit(
      GitEntityInfo gitBranchInfo, String yaml, ChangeType changeType, EntityDetail entityDetail) {
    if (gitBranchInfo.isSyncFromGit()) {
      return getResponseInG2H(gitBranchInfo, entityDetail);
    }

    final FileInfo fileInfo = getFileInfo(gitBranchInfo, yaml, changeType, entityDetail);
    final PushFileResponse pushFileResponse =
        GitSyncGrpcClientUtils.retryAndProcessException(harnessToGitPushInfoServiceBlockingStub::pushFile, fileInfo);
    try {
      checkForError(pushFileResponse);
    } catch (WingsException e) {
      throwDifferentExceptionInCaseOfChangeTypeAdd(gitBranchInfo, changeType, e);
    }
    return ScmGitUtils.createScmPushResponse(yaml, gitBranchInfo, pushFileResponse, entityDetail, changeType);
  }

  public ScmGetFileResponse getFile(Scope scope, String repoName, String branchName, String filePath, String commitId,
      String connectorRef, Map<String, String> contextMap) {
    final GetFileRequest getFileRequest =
        GetFileRequest.newBuilder()
            .setRepoName(repoName)
            .setConnectorRef(connectorRef)
            .setBranchName(Strings.nullToEmpty(branchName))
            .setFilePath(filePath)
            .putAllContextMap(contextMap)
            .setScopeIdentifiers(ScopeIdentifiers.newBuilder()
                                     .setAccountIdentifier(scope.getAccountIdentifier())
                                     .setOrgIdentifier(Strings.nullToEmpty(scope.getOrgIdentifier()))
                                     .setProjectIdentifier(Strings.nullToEmpty(scope.getProjectIdentifier()))
                                     .build())
            .build();
    final GetFileResponse getFileResponse = GitSyncGrpcClientUtils.retryAndProcessException(
        harnessToGitPushInfoServiceBlockingStub::getFile, getFileRequest);

    // Add Error Handling
    return ScmGetFileResponse.builder()
        .fileContent(getFileResponse.getFileContent())
        .gitMetaData(getGitMetaData(getFileResponse.getGitMetaData()))
        .build();
  }

  @VisibleForTesting
  protected void throwDifferentExceptionInCaseOfChangeTypeAdd(
      GitEntityInfo gitBranchInfo, ChangeType changeType, WingsException e) {
    if (changeType.equals(ChangeType.ADD)) {
      final WingsException cause = ExceptionUtils.cause(ErrorCode.SCM_CONFLICT_ERROR, e);
      if (cause != null) {
        throw new InvalidRequestException(String.format(
            "A file with name %s already exists in the remote Git repository", gitBranchInfo.getFilePath()));
      }
    }
    throw e;
  }

  private FileInfo getFileInfo(
      GitEntityInfo gitBranchInfo, String yaml, ChangeType changeType, EntityDetail entityDetail) {
    FileInfo.Builder builder = FileInfo.newBuilder()
                                   .setPrincipal(getPrincipal())
                                   .setAccountId(entityDetail.getEntityRef().getAccountIdentifier())
                                   .setBranch(gitBranchInfo.getBranch())
                                   .setEntityDetail(entityDetailRestToProtoMapper.createEntityDetailDTO(entityDetail))
                                   .setChangeType(ChangeTypeMapper.toProto(changeType))
                                   .setFilePath(gitBranchInfo.getFilePath())
                                   .setFolderPath(gitBranchInfo.getFolderPath())
                                   .setIsNewBranch(gitBranchInfo.isNewBranch())
                                   .setCommitMsg(StringValue.of(gitBranchInfo.getCommitMsg()))
                                   .setYamlGitConfigId(gitBranchInfo.getYamlGitConfigId())
                                   .putAllContextMap(MDC.getCopyOfContextMap())
                                   .setYaml(emptyIfNull(yaml));

    if (gitBranchInfo.getIsFullSyncFlow() != null) {
      builder.setIsFullSyncFlow(gitBranchInfo.getIsFullSyncFlow());
    } else {
      builder.setIsFullSyncFlow(false);
    }

    if (gitBranchInfo.getBaseBranch() != null) {
      builder.setBaseBranch(StringValue.of(gitBranchInfo.getBaseBranch()));
    }

    if (gitBranchInfo.getLastObjectId() != null) {
      builder.setOldFileSha(StringValue.of(gitBranchInfo.getLastObjectId()));
    }

    if (gitBranchInfo.getResolvedConflictCommitId() != null
        && !gitBranchInfo.getResolvedConflictCommitId().equals(DEFAULT)) {
      builder.setCommitId(gitBranchInfo.getResolvedConflictCommitId());
    }

    return builder.build();
  }

  private SCMNoOpResponse getResponseInG2H(GitEntityInfo gitBranchInfo, EntityDetail entityDetail) {
    final boolean defaultBranch = gitSyncSdkService.isDefaultBranch(entityDetail.getEntityRef().getAccountIdentifier(),
        entityDetail.getEntityRef().getOrgIdentifier(), entityDetail.getEntityRef().getProjectIdentifier());
    return SCMNoOpResponse.builder()
        .filePath(gitBranchInfo.getFilePath())
        .pushToDefaultBranch(defaultBranch)
        .objectId(gitBranchInfo.getLastObjectId())
        .yamlGitConfigId(gitBranchInfo.getYamlGitConfigId())
        .branch(gitBranchInfo.getBranch())
        .folderPath(gitBranchInfo.getFolderPath())
        .commitId(gitBranchInfo.getCommitId())
        .build();
  }

  @VisibleForTesting
  protected void checkForError(PushFileResponse pushFileResponse) {
    if (pushFileResponse.getStatus() != 1) {
      final String errorMessage =
          isNotEmpty(pushFileResponse.getError()) ? pushFileResponse.getError() : "Error in doing git push";
      throw new GitSyncException(errorMessage);
    }
    try {
      ScmResponseStatusUtils.checkScmResponseStatusAndThrowException(
          pushFileResponse.getScmResponseCode(), pushFileResponse.getError());
    } catch (WingsException ex) {
      ex.setMetadata(ScmErrorMetadataDTO.builder().conflictCommitId(pushFileResponse.getCommitId()).build());
      throw ex;
    }
  }

  private Principal getPrincipal() {
    final io.harness.security.dto.Principal sourcePrincipal = SourcePrincipalContextBuilder.getSourcePrincipal();
    if (sourcePrincipal == null) {
      throw new InvalidRequestException("Principal cannot be null");
    }
    final Principal.Builder principalBuilder = Principal.newBuilder();
    switch (sourcePrincipal.getType()) {
      case USER:
        UserPrincipal userPrincipalFromContext = (UserPrincipal) sourcePrincipal;
        return principalBuilder.setUserPrincipal(UserPrincipalMapper.toProto(userPrincipalFromContext)).build();
      case SERVICE:
        final ServicePrincipal servicePrincipalFromContext = (ServicePrincipal) sourcePrincipal;
        final io.harness.security.ServicePrincipal servicePrincipal =
            io.harness.security.ServicePrincipal.newBuilder().setName(servicePrincipalFromContext.getName()).build();
        return principalBuilder.setServicePrincipal(servicePrincipal).build();
      case SERVICE_ACCOUNT:
        final ServiceAccountPrincipal serviceAccountPrincipalFromContext = (ServiceAccountPrincipal) sourcePrincipal;
        final io.harness.security.ServiceAccountPrincipal serviceAccountPrincipal =
            io.harness.security.ServiceAccountPrincipal.newBuilder()
                .setName(StringValue.of(serviceAccountPrincipalFromContext.getName()))
                .setEmail(StringValue.of(serviceAccountPrincipalFromContext.getEmail()))
                .setUserName(StringValue.of(serviceAccountPrincipalFromContext.getUsername()))
                .build();
        return principalBuilder.setServiceAccountPrincipal(serviceAccountPrincipal).build();
      default:
        throw new InvalidRequestException("Principal type not set.");
    }
  }

  private ScmGitMetaData getGitMetaData(GitMetaData gitMetaData) {
    return ScmGitMetaData.builder()
        .blobId(gitMetaData.getBlobId())
        .branchName(gitMetaData.getBranchName())
        .repoName(gitMetaData.getRepoName())
        .filePath(gitMetaData.getFilePath())
        .commitId(gitMetaData.getCommitId())
        .build();
  }
}
