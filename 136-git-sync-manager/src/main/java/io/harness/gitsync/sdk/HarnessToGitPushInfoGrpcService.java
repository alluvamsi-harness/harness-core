/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.gitsync.sdk;

import static io.harness.AuthorizationServiceHeader.GIT_SYNC_SERVICE;
import static io.harness.annotations.dev.HarnessTeam.DX;
import static io.harness.gitsync.common.scmerrorhandling.ScmErrorCodeToHttpStatusCodeMapping.HTTP_500;

import io.harness.annotations.dev.OwnedBy;
import io.harness.eventsframework.schemas.entity.EntityScopeInfo;
import io.harness.exception.ExceptionUtils;
import io.harness.exception.InvalidRequestException;
import io.harness.exception.exceptionmanager.ExceptionManager;
import io.harness.gitsync.BranchDetails;
import io.harness.gitsync.CreateFileRequest;
import io.harness.gitsync.CreateFileResponse;
import io.harness.gitsync.CreatePRRequest;
import io.harness.gitsync.CreatePRResponse;
import io.harness.gitsync.ErrorDetails;
import io.harness.gitsync.FileInfo;
import io.harness.gitsync.GetFileRequest;
import io.harness.gitsync.GetFileResponse;
import io.harness.gitsync.HarnessToGitPushInfoServiceGrpc.HarnessToGitPushInfoServiceImplBase;
import io.harness.gitsync.IsGitSyncEnabled;
import io.harness.gitsync.PushFileResponse;
import io.harness.gitsync.PushInfo;
import io.harness.gitsync.PushResponse;
import io.harness.gitsync.RepoDetails;
import io.harness.gitsync.UpdateFileRequest;
import io.harness.gitsync.UpdateFileResponse;
import io.harness.gitsync.common.service.HarnessToGitHelperService;
import io.harness.logging.MdcContextSetter;
import io.harness.manage.GlobalContextManager;
import io.harness.ng.core.entitydetail.EntityDetailProtoToRestMapper;
import io.harness.security.Principal;
import io.harness.security.PrincipalContextData;
import io.harness.security.PrincipalProtoMapper;
import io.harness.security.SourcePrincipalContextData;
import io.harness.security.dto.PrincipalType;
import io.harness.serializer.KryoSerializer;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
@OwnedBy(DX)
public class HarnessToGitPushInfoGrpcService extends HarnessToGitPushInfoServiceImplBase {
  @Inject HarnessToGitHelperService harnessToGitHelperService;
  @Inject KryoSerializer kryoSerializer;
  @Inject EntityDetailProtoToRestMapper entityDetailProtoToRestMapper;
  @Inject ExceptionManager exceptionManager;

  @Override
  public void pushFromHarness(PushInfo request, StreamObserver<PushResponse> responseObserver) {
    try (MdcContextSetter ignore1 = new MdcContextSetter(request.getContextMapMap())) {
      log.debug("Grpc request received for pushFromHarness");
      harnessToGitHelperService.postPushOperation(request);
      responseObserver.onNext(PushResponse.newBuilder().build());
      responseObserver.onCompleted();
      log.debug("Grpc request completed for pushFromHarness");
    }
  }

  @Override
  public void pushFile(FileInfo request, StreamObserver<PushFileResponse> responseObserver) {
    PushFileResponse pushFileResponse;
    try (GlobalContextManager.GlobalContextGuard guard = GlobalContextManager.ensureGlobalContextGuard();
         MdcContextSetter ignore1 = new MdcContextSetter(request.getContextMapMap())) {
      log.debug("Grpc request received for pushFile");
      setPrincipal(request);
      pushFileResponse = harnessToGitHelperService.pushFile(request);
      log.debug("Grpc request completed for pushFile");
    } catch (Exception e) {
      log.error("Push to git failed with exception", e);
      final String message = ExceptionUtils.getMessage(e);
      pushFileResponse = PushFileResponse.newBuilder()
                             .setDefaultBranchName("")
                             .setIsDefault(false)
                             .setScmResponseCode(-1)
                             .setCommitId("")
                             .setError(message)
                             .setStatus(0)
                             .setDefaultBranchName("")
                             .build();
    }
    responseObserver.onNext(pushFileResponse);
    responseObserver.onCompleted();
  }

  @Override
  public void getFile(GetFileRequest request, StreamObserver<GetFileResponse> responseObserver) {
    GetFileResponse getFileResponse;
    try (GlobalContextManager.GlobalContextGuard guard = GlobalContextManager.ensureGlobalContextGuard();
         MdcContextSetter ignore1 = new MdcContextSetter(request.getContextMapMap())) {
      getFileResponse = harnessToGitHelperService.getFile(request);
    } catch (Exception ex) {
      log.error("Faced exception during getFile GIT call", ex);
      final String errorMessage = ExceptionUtils.getMessage(ex);
      getFileResponse = GetFileResponse.newBuilder()
                            .setStatusCode(HTTP_500)
                            .setError(ErrorDetails.newBuilder().setErrorMessage(errorMessage).build())
                            .build();
    }
    responseObserver.onNext(getFileResponse);
    responseObserver.onCompleted();
  }

  @Override
  public void createFile(CreateFileRequest request, StreamObserver<CreateFileResponse> responseObserver) {
    CreateFileResponse createFileResponse;
    try (GlobalContextManager.GlobalContextGuard guard = GlobalContextManager.ensureGlobalContextGuard();
         MdcContextSetter ignore1 = new MdcContextSetter(request.getContextMapMap())) {
      createFileResponse = harnessToGitHelperService.createFile(request);
    } catch (Exception ex) {
      log.error("Faced exception during createFile GIT call", ex);
      final String errorMessage = ExceptionUtils.getMessage(ex);
      createFileResponse = CreateFileResponse.newBuilder()
                               .setStatusCode(HTTP_500)
                               .setError(ErrorDetails.newBuilder().setErrorMessage(errorMessage).build())
                               .build();
    }
    responseObserver.onNext(createFileResponse);
    responseObserver.onCompleted();
  }

  @Override
  public void updateFile(UpdateFileRequest request, StreamObserver<UpdateFileResponse> responseObserver) {
    UpdateFileResponse updateFileResponse;
    try (GlobalContextManager.GlobalContextGuard guard = GlobalContextManager.ensureGlobalContextGuard();
         MdcContextSetter ignore1 = new MdcContextSetter(request.getContextMapMap())) {
      updateFileResponse = harnessToGitHelperService.updateFile(request);
    } catch (Exception ex) {
      log.error("Faced exception during updateFile GIT call", ex);
      final String errorMessage = ExceptionUtils.getMessage(ex);
      updateFileResponse = UpdateFileResponse.newBuilder()
                               .setStatusCode(HTTP_500)
                               .setError(ErrorDetails.newBuilder().setErrorMessage(errorMessage).build())
                               .build();
    }
    responseObserver.onNext(updateFileResponse);
    responseObserver.onCompleted();
  }

  @Override
  public void createPullRequest(CreatePRRequest request, StreamObserver<CreatePRResponse> responseObserver) {
    CreatePRResponse createPRResponse;
    try (GlobalContextManager.GlobalContextGuard guard = GlobalContextManager.ensureGlobalContextGuard();
         MdcContextSetter ignore1 = new MdcContextSetter(request.getContextMapMap())) {
      createPRResponse = harnessToGitHelperService.createPullRequest(request);
    } catch (Exception ex) {
      log.error("Faced exception during createPullRequest GIT call", ex);
      final String errorMessage = ExceptionUtils.getMessage(ex);
      createPRResponse = CreatePRResponse.newBuilder()
                             .setError(ErrorDetails.newBuilder().setErrorMessage(errorMessage).build())
                             .build();
    }
    responseObserver.onNext(createPRResponse);
    responseObserver.onCompleted();
  }

  @VisibleForTesting
  void setPrincipal(FileInfo request) {
    final Principal principalFromProto = request.getPrincipal();
    final io.harness.security.dto.Principal principal;
    if (request.getIsFullSyncFlow()) {
      principal = harnessToGitHelperService.getFullSyncUser(request);
    } else {
      principal = PrincipalProtoMapper.toPrincipalDTO(request.getAccountId(), principalFromProto);
    }
    validateThePrincipal(principal);
    GlobalContextManager.upsertGlobalContextRecord(PrincipalContextData.builder().principal(principal).build());
    GlobalContextManager.upsertGlobalContextRecord(SourcePrincipalContextData.builder().principal(principal).build());
  }

  private void validateThePrincipal(io.harness.security.dto.Principal principal) {
    if (principal.getType() == PrincipalType.SERVICE) {
      checkIfServicePrincipalIsValid((io.harness.security.dto.ServicePrincipal) principal);
    }
  }

  private void checkIfServicePrincipalIsValid(io.harness.security.dto.ServicePrincipal servicePrincipal) {
    if (!GIT_SYNC_SERVICE.getServiceId().equals(servicePrincipal.getName())) {
      throw new InvalidRequestException("Only git sync service principal is allowed for push");
    }
  }

  @Override
  public void isGitSyncEnabledForScope(EntityScopeInfo request, StreamObserver<IsGitSyncEnabled> responseObserver) {
    log.debug("Grpc request received for isGitSyncEnabledForScope");
    final Boolean gitSyncEnabled = harnessToGitHelperService.isGitSyncEnabled(request);
    responseObserver.onNext(IsGitSyncEnabled.newBuilder().setEnabled(gitSyncEnabled).build());
    responseObserver.onCompleted();
    log.debug("Grpc request completed for isGitSyncEnabledForScope");
  }

  @Override
  public void getDefaultBranch(RepoDetails request, StreamObserver<BranchDetails> responseObserver) {
    try (MdcContextSetter ignore1 = new MdcContextSetter(request.getContextMapMap())) {
      log.info("Grpc request received for getDefaultBranch");
      final BranchDetails branchDetails = harnessToGitHelperService.getBranchDetails(request);
      log.info("Grpc request completed for getDefaultBranch");
      responseObserver.onNext(branchDetails);
    } catch (Exception ex) {
      final String errorMessage = ExceptionUtils.getMessage(ex);
      responseObserver.onError(Status.fromThrowable(ex).withDescription(errorMessage).asRuntimeException());
    }
    responseObserver.onCompleted();
  }
}
