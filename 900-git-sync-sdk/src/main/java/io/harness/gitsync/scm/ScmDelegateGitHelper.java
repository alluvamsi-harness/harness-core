package io.harness.gitsync.scm;

import static io.harness.annotations.dev.HarnessTeam.DX;
import static io.harness.beans.gitsync.GitFileDetails.GitFileDetailsBuilder;
import static io.harness.utils.DelegateOwner.getNGTaskSetupAbstractionsWithOwner;

import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.DelegateTaskRequest;
import io.harness.delegate.beans.DelegateResponseData;
import io.harness.delegate.task.scm.ScmPushTaskParams;
import io.harness.delegate.task.scm.ScmPushTaskResponseData;
import io.harness.eraro.ErrorCode;
import io.harness.exception.ExceptionUtils;
import io.harness.exception.InvalidRequestException;
import io.harness.exception.UnexpectedException;
import io.harness.exception.UnknownEnumTypeException;
import io.harness.exception.WingsException;
import io.harness.git.model.ChangeType;
import io.harness.gitsync.common.beans.InfoForGitPush;
import io.harness.gitsync.interceptor.GitEntityInfo;
import io.harness.gitsync.scm.beans.ScmPushResponse;
import io.harness.impl.ScmResponseStatusUtils;
import io.harness.product.ci.scm.proto.CreateFileResponse;
import io.harness.product.ci.scm.proto.DeleteFileResponse;
import io.harness.product.ci.scm.proto.UpdateFileResponse;
import io.harness.service.DelegateGrpcClientWrapper;

import software.wings.beans.TaskType;

import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

@Slf4j
@OwnedBy(DX)
public class ScmDelegateGitHelper implements ScmGitHelper {
  @Inject private DelegateGrpcClientWrapper delegateGrpcClientWrapper;

  @Override
  public ScmPushResponse pushToGitBasedOnChangeType(
      String yaml, ChangeType changeType, GitEntityInfo gitBranchInfo, InfoForGitPush infoForPush) {
    GitFileDetailsBuilder gitFileDetails = ScmGitUtils.getGitFileDetails(gitBranchInfo, yaml);
    if (changeType.equals(ChangeType.MODIFY) || changeType.equals(ChangeType.DELETE)) {
      gitFileDetails.oldFileSha(gitBranchInfo.getLastObjectId());
    }
    ScmPushTaskParams scmPushTaskParams = ScmPushTaskParams.builder()
                                              .changeType(changeType)
                                              .scmConnector(infoForPush.getScmConnector())
                                              .gitFileDetails(gitFileDetails.build())
                                              .encryptedDataDetails(infoForPush.getEncryptedDataDetailList())
                                              .isNewBranch(infoForPush.isNewBranch())
                                              .baseBranch(gitBranchInfo.getBaseBranch())
                                              .build();
    final Map<String, String> ngTaskSetupAbstractionsWithOwner = getNGTaskSetupAbstractionsWithOwner(
        infoForPush.getAccountId(), infoForPush.getOrgIdentifier(), infoForPush.getProjectIdentifier());
    DelegateTaskRequest delegateTaskRequest = DelegateTaskRequest.builder()
                                                  .accountId(infoForPush.getAccountId())
                                                  .taskSetupAbstractions(ngTaskSetupAbstractionsWithOwner)
                                                  .taskType(TaskType.SCM_PUSH_TASK.name())
                                                  .taskParameters(scmPushTaskParams)
                                                  .executionTimeout(Duration.ofMinutes(2))
                                                  .build();
    DelegateResponseData responseData = delegateGrpcClientWrapper.executeSyncTask(delegateTaskRequest);
    ScmPushTaskResponseData scmPushTaskResponseData = (ScmPushTaskResponseData) responseData;
    ScmPushResponse scmPushResponse = null;
    try {
      switch (changeType) {
        case ADD:
          try {
            final CreateFileResponse createFileResponse =
                CreateFileResponse.parseFrom(scmPushTaskResponseData.getCreateFileResponse());
            ScmResponseStatusUtils.checkScmResponseStatusAndThrowException(
                createFileResponse.getStatus(), createFileResponse.getError());
            return ScmGitUtils.createScmCreateFileResponse(yaml, infoForPush, createFileResponse);
          } catch (Exception e) {
            // If in create file we get same file we have to throw new exception.
            final WingsException cause = ExceptionUtils.cause(ErrorCode.SCM_CONFLICT_ERROR, e);
            if (cause != null) {
              throw new InvalidRequestException(String.format(
                  "A file with name %s already exists in the remote Git repository", gitBranchInfo.getFilePath()));
            }
            throw e;
          }
        case MODIFY:
          final UpdateFileResponse updateFileResponse =
              UpdateFileResponse.parseFrom(scmPushTaskResponseData.getUpdateFileResponse());
          ScmResponseStatusUtils.checkScmResponseStatusAndThrowException(
              updateFileResponse.getStatus(), updateFileResponse.getError());
          return ScmGitUtils.createScmUpdateFileResponse(yaml, infoForPush, updateFileResponse);
        case DELETE:
          final DeleteFileResponse deleteFileResponse =
              DeleteFileResponse.parseFrom(scmPushTaskResponseData.getDeleteFileResponse());
          ScmResponseStatusUtils.checkScmResponseStatusAndThrowException(
              deleteFileResponse.getStatus(), deleteFileResponse.getError());
          return ScmGitUtils.createScmDeleteFileResponse(infoForPush, deleteFileResponse);
        case NONE:
        case RENAME:
          throw new NotImplementedException(changeType + " is not Implemented");
        default:
          throw new UnknownEnumTypeException("Change Type", changeType.toString());
      }
    } catch (InvalidProtocolBufferException e) {
      throw new UnexpectedException("Unexpected error occurred while doing scm operation");
    }
  }
}
