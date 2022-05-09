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
import io.harness.gitsync.common.scmerrorhandling.handlers.bitbucket.BitbucketListBranchesScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.bitbucket.BitbucketListRepoScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.github.GithubListBranchesScmApiErrorHandler;
import io.harness.gitsync.common.scmerrorhandling.handlers.github.GithubListRepoScmApiErrorHandler;

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
          .build();

  public ScmApiErrorHandler getHandler(ScmApis scmApi, RepoProviders repoProvider) {
    try {
      return scmApiErrorHandlerMap.get(Pair.of(scmApi, repoProvider)).newInstance();
    } catch (Exception ex) {
      log.error(String.format("Error while getting handler for scmApi [%s] and repoProvider [%s]"), ex);
    }
    return defaultScmApiErrorHandler;
  }
}
