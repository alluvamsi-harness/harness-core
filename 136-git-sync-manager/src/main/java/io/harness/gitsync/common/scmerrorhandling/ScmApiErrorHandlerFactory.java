/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.gitsync.common.scmerrorhandling;

import static io.harness.annotations.dev.HarnessTeam.PL;

import io.harness.annotations.dev.OwnedBy;
import io.harness.gitsync.common.beans.ScmApis;
import io.harness.gitsync.common.dtos.RepoProviders;
import io.harness.gitsync.common.scmerrorhandling.handlers.DefaultScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.ScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.bitbucket.BitbucketCreateBranchScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.bitbucket.BitbucketCreateFileScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.bitbucket.BitbucketCreatePullRequestScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.bitbucket.BitbucketGetFileScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.bitbucket.BitbucketListBranchesScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.bitbucket.BitbucketListRepoScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.bitbucket.BitbucketUpdateFileScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.github.GithubCreateBranchScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.github.GithubCreateFileScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.github.GithubCreatePullRequestScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.github.GithubGetFileScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.github.GithubListBranchesScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.github.GithubListRepoScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.github.GithubUpdateFileScmApiErrorHandler;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

@UtilityClass
@Slf4j
@OwnedBy(PL)
class ScmApiErrorHandlerFactory {
  @Inject DefaultScmApiErrorHandler defaultScmApiErrorHandler;
  private final Map<Pair<ScmApis, RepoProviders>, Class<? extends ScmApiErrorHandler>> scmApiErrorHandlerMap =
      ImmutableMap.<Pair<ScmApis, RepoProviders>, Class<? extends ScmApiErrorHandler>>builder()
          .put(Pair.of(ScmApis.LIST_REPOSITORIES, RepoProviders.BITBUCKET), BitbucketListRepoScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.LIST_REPOSITORIES, RepoProviders.GITHUB), GithubListRepoScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.LIST_BRANCHES, RepoProviders.BITBUCKET), BitbucketListBranchesScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.LIST_BRANCHES, RepoProviders.GITHUB), GithubListBranchesScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.GET_FILE, RepoProviders.BITBUCKET), BitbucketGetFileScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.GET_FILE, RepoProviders.GITHUB), GithubGetFileScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.CREATE_PULL_REQUEST, RepoProviders.BITBUCKET),
              BitbucketCreatePullRequestScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.CREATE_PULL_REQUEST, RepoProviders.GITHUB),
              GithubCreatePullRequestScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.CREATE_FILE, RepoProviders.BITBUCKET), BitbucketCreateFileScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.CREATE_FILE, RepoProviders.GITHUB), GithubCreateFileScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.UPDATE_FILE, RepoProviders.BITBUCKET), BitbucketUpdateFileScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.UPDATE_FILE, RepoProviders.GITHUB), GithubUpdateFileScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.CREATE_BRANCH, RepoProviders.BITBUCKET), BitbucketCreateBranchScmApiErrorHandler.class)
          .put(Pair.of(ScmApis.CREATE_BRANCH, RepoProviders.GITHUB), GithubCreateBranchScmApiErrorHandler.class)
          .build();

  public ScmApiErrorHandler getHandler(ScmApis scmApi, RepoProviders repoProvider) {
    try {
      return scmApiErrorHandlerMap.get(Pair.of(scmApi, repoProvider)).newInstance();
    } catch (Exception ex) {
      log.error(
          String.format("Error while getting handler for scmApi [%s] and repoProvider [%s]", scmApi, repoProvider), ex);
    }
    return defaultScmApiErrorHandler;
  }
}
