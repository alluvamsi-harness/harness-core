package io.harness.impl;

import static io.harness.annotations.dev.HarnessTeam.DX;

import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.gitsync.GitFileDetails;
import io.harness.beans.gitsync.GitFilePathDetails;
import io.harness.beans.gitsync.GitPRCreateRequest;
import io.harness.beans.gitsync.GitWebhookDetails;
import io.harness.delegate.beans.connector.scm.ScmConnector;
import io.harness.impl.jgit.JgitGitServiceImpl;
import io.harness.impl.scm.SCMServiceGitClientImpl;
import io.harness.product.ci.scm.proto.CreateFileResponse;
import io.harness.product.ci.scm.proto.CreatePRResponse;
import io.harness.product.ci.scm.proto.CreateWebhookResponse;
import io.harness.product.ci.scm.proto.DeleteFileResponse;
import io.harness.product.ci.scm.proto.DeleteWebhookResponse;
import io.harness.product.ci.scm.proto.FileBatchContentResponse;
import io.harness.product.ci.scm.proto.FileContent;
import io.harness.product.ci.scm.proto.FindFilesInBranchResponse;
import io.harness.product.ci.scm.proto.FindFilesInCommitResponse;
import io.harness.product.ci.scm.proto.GetLatestCommitResponse;
import io.harness.product.ci.scm.proto.IsLatestFileResponse;
import io.harness.product.ci.scm.proto.ListBranchesResponse;
import io.harness.product.ci.scm.proto.ListCommitsResponse;
import io.harness.product.ci.scm.proto.ListWebhooksResponse;
import io.harness.product.ci.scm.proto.UpdateFileResponse;
import io.harness.service.ScmOrchestratorService;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({ @Inject }))
@Slf4j
@OwnedBy(DX)
public class ScmOrchestratorServiceImpl implements ScmOrchestratorService {
  private SCMServiceGitClientImpl scmServiceGitClient;
  private JgitGitServiceImpl jgitGitService;

  @Override
  public CreateFileResponse createFile(ScmConnector scmConnector, GitFileDetails gitFileDetails) {
    return scmServiceGitClient.createFile(scmConnector, gitFileDetails);
  }

  @Override
  public UpdateFileResponse updateFile(ScmConnector scmConnector, GitFileDetails gitFileDetails) {
    return scmServiceGitClient.updateFile(scmConnector, gitFileDetails);
  }

  @Override
  public DeleteFileResponse deleteFile(ScmConnector scmConnector, GitFilePathDetails gitFilePathDetails) {
    return scmServiceGitClient.deleteFile(scmConnector, gitFilePathDetails);
  }

  @Override
  public FileContent getFileContent(ScmConnector scmConnector, GitFilePathDetails gitFilePathDetails) {
    return scmServiceGitClient.getFileContent(scmConnector, gitFilePathDetails);
  }

  @Override
  public FileContent getLatestFile(ScmConnector scmConnector, GitFilePathDetails gitFilePathDetails) {
    return scmServiceGitClient.getLatestFile(scmConnector, gitFilePathDetails);
  }

  @Override
  public IsLatestFileResponse isLatestFile(
      ScmConnector scmConnector, GitFilePathDetails gitFilePathDetails, FileContent fileContent) {
    return scmServiceGitClient.isLatestFile(scmConnector, gitFilePathDetails, fileContent);
  }

  @Override
  public FileContent pushFile(ScmConnector scmConnector, GitFileDetails gitFileDetails) {
    return scmServiceGitClient.pushFile(scmConnector, gitFileDetails);
  }

  @Override
  public FindFilesInBranchResponse findFilesInBranch(ScmConnector scmConnector, String branchName) {
    return scmServiceGitClient.findFilesInBranch(scmConnector, branchName);
  }

  @Override
  public FindFilesInCommitResponse findFilesInCommit(ScmConnector scmConnector, GitFilePathDetails gitFilePathDetails) {
    return scmServiceGitClient.findFilesInCommit(scmConnector, gitFilePathDetails);
  }

  @Override
  public GetLatestCommitResponse getLatestCommit(ScmConnector scmConnector, String branchName) {
    return scmServiceGitClient.getLatestCommit(scmConnector, branchName);
  }

  @Override
  public ListBranchesResponse listBranches(ScmConnector scmConnector) {
    return scmServiceGitClient.listBranches(scmConnector);
  }

  @Override
  public ListCommitsResponse listCommits(ScmConnector scmConnector, String branchName) {
    return scmServiceGitClient.listCommits(scmConnector, branchName);
  }

  @Override
  public FileBatchContentResponse listFiles(ScmConnector connector, List<String> foldersList, String branchName) {
    return scmServiceGitClient.listFiles(connector, foldersList, branchName);
  }

  @Override
  public void createNewBranch(ScmConnector scmConnector, String branch, String defaultBranchName) {
    scmServiceGitClient.createNewBranch(scmConnector, branch, defaultBranchName);
  }

  @Override
  public CreateWebhookResponse createWebhook(ScmConnector scmConnector, GitWebhookDetails gitWebhookDetails) {
    return scmServiceGitClient.createWebhook(scmConnector, gitWebhookDetails);
  }

  @Override
  public DeleteWebhookResponse deleteWebhook(ScmConnector scmConnector, String id) {
    return scmServiceGitClient.deleteWebhook(scmConnector, id);
  }

  @Override
  public ListWebhooksResponse listWebhook(ScmConnector scmConnector) {
    return scmServiceGitClient.listWebhook(scmConnector);
  }

  @Override
  public CreateWebhookResponse upsertWebhook(ScmConnector scmConnector, GitWebhookDetails gitWebhookDetails) {
    return scmServiceGitClient.upsertWebhook(scmConnector, gitWebhookDetails);
  }

  @Override
  public CreatePRResponse createPullRequest(ScmConnector scmConnector, GitPRCreateRequest gitPRCreateRequest) {
    return scmServiceGitClient.createPullRequest(scmConnector, gitPRCreateRequest);
  }
}
