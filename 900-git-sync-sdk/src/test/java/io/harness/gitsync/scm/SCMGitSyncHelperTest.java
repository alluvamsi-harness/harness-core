/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.gitsync.scm;

import static io.harness.rule.OwnerRule.MEET;
import static io.harness.rule.OwnerRule.MOHIT_GARG;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import io.harness.EntityType;
import io.harness.GitSdkTestBase;
import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.IdentifierRef;
import io.harness.category.element.UnitTests;
import io.harness.common.EntityReference;
import io.harness.eraro.ErrorCode;
import io.harness.eventsframework.schemas.entity.EntityDetailProtoDTO;
import io.harness.exception.ExplanationException;
import io.harness.exception.HintException;
import io.harness.exception.InvalidRequestException;
import io.harness.exception.SCMExceptionHints;
import io.harness.exception.ScmException;
import io.harness.exception.WingsException;
import io.harness.exception.ngexception.beans.ScmErrorMetadataDTO;
import io.harness.git.model.ChangeType;
import io.harness.gitsync.FileInfo;
import io.harness.gitsync.HarnessToGitPushInfoServiceGrpc;
import io.harness.gitsync.PushFileResponse;
import io.harness.gitsync.common.helper.GitSyncGrpcClientUtils;
import io.harness.gitsync.exceptions.GitSyncException;
import io.harness.gitsync.interceptor.GitEntityInfo;
import io.harness.gitsync.persistance.GitSyncSdkService;
import io.harness.gitsync.scm.beans.ScmPushResponse;
import io.harness.ng.core.EntityDetail;
import io.harness.ng.core.entitydetail.EntityDetailRestToProtoMapper;
import io.harness.rule.Owner;
import io.harness.security.SourcePrincipalContextBuilder;
import io.harness.security.dto.Principal;
import io.harness.security.dto.UserPrincipal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

@OwnedBy(HarnessTeam.PL)
public class SCMGitSyncHelperTest extends GitSdkTestBase {
  private final String accountId = "accountId";
  private final String orgId = "orgId";
  private final String projectId = "projectId";
  private final String commitId = "commitId";
  private final String commitMessage = "message";
  private final String branch = "branch";
  private final String baseBranch = "baseBranch";
  private final String filePath = "filePath";
  private final String folderPath = "folderPath";
  private final String lastObjectId = "lastObjectId";
  private final String yamlGitConfigId = "yamlGitConfigId";
  private final String error = "Error";
  private final String name = "name";
  private final String yaml = "yaml";
  private final Map<String, String> contextMap = new HashMap<>();

  @InjectMocks SCMGitSyncHelper scmGitSyncHelper;
  @Mock EntityDetailRestToProtoMapper entityDetailRestToProtoMapper;
  @Mock GitSyncSdkService gitSyncSdkService;
  @Mock EntityDetailProtoDTO entityDetailProtoDTO;
  @Mock HarnessToGitPushInfoServiceGrpc.HarnessToGitPushInfoServiceBlockingStub harnessToGitPushInfoServiceBlockingStub;

  EntityReference entityReference;
  GitEntityInfo gitEntityInfo1;
  GitEntityInfo gitEntityInfo2;
  PushFileResponse pushFileResponse1;
  PushFileResponse pushFileResponse2;
  PushFileResponse pushFileResponse3;
  PushFileResponse pushFileResponse4;
  EntityDetail entityDetail;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    gitEntityInfo1 = buildGitEntityInfo(branch, baseBranch, commitId, commitMessage, filePath, folderPath, false, false,
        true, true, lastObjectId, yamlGitConfigId);
    gitEntityInfo2 = buildGitEntityInfo(branch, baseBranch, commitId, commitMessage, filePath, folderPath, false, false,
        true, false, lastObjectId, yamlGitConfigId);
    entityReference =
        IdentifierRef.builder().accountIdentifier(accountId).orgIdentifier(orgId).projectIdentifier(projectId).build();
    entityDetail = EntityDetail.builder().entityRef(entityReference).name(name).type(EntityType.CONNECTORS).build();

    pushFileResponse1 = buildPushFileResponse(1, 404, error);
    pushFileResponse2 = buildPushFileResponse(1, 304, error);
    pushFileResponse3 = buildPushFileResponse(1, 400, error);
    pushFileResponse4 = buildPushFileResponse(1, 409, error);
  }

  @Test
  @Owner(developers = MEET)
  @Category(UnitTests.class)
  public void testPushToGit() throws IOException {
    when(gitSyncSdkService.isDefaultBranch(entityDetail.getEntityRef().getAccountIdentifier(),
             entityDetail.getEntityRef().getOrgIdentifier(), entityDetail.getEntityRef().getProjectIdentifier()))
        .thenReturn(true);
    ScmPushResponse scmPushResponse1 = scmGitSyncHelper.pushToGit(gitEntityInfo1, yaml, ChangeType.ADD, entityDetail);
    assertThat(scmPushResponse1.isPushToDefaultBranch()).isEqualTo(true);

    Principal principal = new UserPrincipal(name, "DUMMY_USER_EMAIL", name, accountId);
    SourcePrincipalContextBuilder.setSourcePrincipal(principal);
    when(entityDetailRestToProtoMapper.createEntityDetailDTO(entityDetail)).thenReturn(entityDetailProtoDTO);
    MDC.setContextMap(contextMap);

    when(GitSyncGrpcClientUtils.retryAndProcessException(
             harnessToGitPushInfoServiceBlockingStub::pushFile, any(FileInfo.class)))
        .thenReturn(pushFileResponse1);
    assertThatThrownBy(() -> scmGitSyncHelper.pushToGit(gitEntityInfo2, yaml, ChangeType.ADD, entityDetail))
        .hasMessage("Please check your credentials.");
    when(GitSyncGrpcClientUtils.retryAndProcessException(
             harnessToGitPushInfoServiceBlockingStub::pushFile, any(FileInfo.class)))
        .thenReturn(pushFileResponse2);
    assertThatThrownBy(() -> scmGitSyncHelper.pushToGit(gitEntityInfo2, yaml, ChangeType.ADD, entityDetail))
        .hasMessage(error);
    when(GitSyncGrpcClientUtils.retryAndProcessException(
             harnessToGitPushInfoServiceBlockingStub::pushFile, any(FileInfo.class)))
        .thenReturn(pushFileResponse3);
    assertThatThrownBy(() -> scmGitSyncHelper.pushToGit(gitEntityInfo2, yaml, ChangeType.ADD, entityDetail))
        .hasMessage("Unexpected error occurred while doing scm operation");
    when(GitSyncGrpcClientUtils.retryAndProcessException(
             harnessToGitPushInfoServiceBlockingStub::pushFile, any(FileInfo.class)))
        .thenReturn(pushFileResponse4);
    assertThatThrownBy(() -> scmGitSyncHelper.pushToGit(gitEntityInfo2, yaml, ChangeType.ADD, entityDetail))
        .hasMessage("A file with name filePath already exists in the remote Git repository");

    MDC.clear();
  }

  @Test
  @Owner(developers = MOHIT_GARG)
  @Category(UnitTests.class)
  public void testCheckForError() {
    String errorMessage = "errorMessage";
    scmGitSyncHelper.checkForError(buildPushFileResponse(1, 200, errorMessage));

    assertThatThrownBy(() -> scmGitSyncHelper.checkForError(buildPushFileResponse(0, 0, errorMessage)))
        .hasMessage(errorMessage)
        .isInstanceOf(GitSyncException.class);

    assertThatThrownBy(() -> scmGitSyncHelper.checkForError(buildPushFileResponse(1, 304, errorMessage)))
        .isInstanceOf(ExplanationException.class)
        .hasMessage(errorMessage);

    assertThatThrownBy(() -> scmGitSyncHelper.checkForError(buildPushFileResponse(1, 304, "")))
        .isInstanceOf(ScmException.class)
        .hasMessage("");

    assertThatThrownBy(() -> scmGitSyncHelper.checkForError(buildPushFileResponse(1, 404, errorMessage)))
        .isInstanceOf(HintException.class)
        .hasMessage(SCMExceptionHints.INVALID_CREDENTIALS);

    try {
      scmGitSyncHelper.checkForError(buildPushFileResponse(1, 304, errorMessage));
    } catch (WingsException ex) {
      assertThat(ex.getMetadata()).isNotNull();
      assertThat(ex.getMetadata() instanceof ScmErrorMetadataDTO).isTrue();
      assertThat(((ScmErrorMetadataDTO) ex.getMetadata()).getConflictCommitId().equals(commitId)).isTrue();
    }
  }

  @Test
  @Owner(developers = MOHIT_GARG)
  @Category(UnitTests.class)
  public void testThrowDifferentExceptionInCaseOfChangeTypeAddMethod() {
    WingsException exception = new WingsException("dummy");
    assertThatThrownBy(()
                           -> scmGitSyncHelper.throwDifferentExceptionInCaseOfChangeTypeAdd(
                               GitEntityInfo.builder().build(), ChangeType.DELETE, exception))
        .isEqualTo(exception);

    assertThatThrownBy(()
                           -> scmGitSyncHelper.throwDifferentExceptionInCaseOfChangeTypeAdd(
                               GitEntityInfo.builder().build(), ChangeType.ADD, exception))
        .isEqualTo(exception);

    WingsException exception2 = new ScmException(ErrorCode.SCM_CONFLICT_ERROR);
    assertThatThrownBy(()
                           -> scmGitSyncHelper.throwDifferentExceptionInCaseOfChangeTypeAdd(
                               GitEntityInfo.builder().build(), ChangeType.ADD, exception2))
        .isInstanceOf(InvalidRequestException.class);
  }

  private GitEntityInfo buildGitEntityInfo(String branch, String baseBranch, String commitId, String commitMsg,
      String filePath, String folderPath, Boolean isFullSyncFlow, boolean findDefaultFromOtherRepos,
      Boolean isNewBranch, Boolean isSyncFromGit, String lastObjectId, String yamlGitConfigId) {
    return GitEntityInfo.builder()
        .branch(branch)
        .baseBranch(baseBranch)
        .commitId(commitId)
        .commitMsg(commitMsg)
        .filePath(filePath)
        .findDefaultFromOtherRepos(findDefaultFromOtherRepos)
        .folderPath(folderPath)
        .isFullSyncFlow(isFullSyncFlow)
        .isNewBranch(isNewBranch)
        .isSyncFromGit(isSyncFromGit)
        .lastObjectId(lastObjectId)
        .yamlGitConfigId(yamlGitConfigId)
        .build();
  }

  private PushFileResponse buildPushFileResponse(int status, int scmResponseCode, String errorMsg) {
    return PushFileResponse.newBuilder()
        .setStatus(status)
        .setScmResponseCode(scmResponseCode)
        .setError(errorMsg)
        .setCommitId(commitId)
        .build();
  }
}
