/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.gitsync.common.scmerrorhandling.handlers.github;

import static io.harness.annotations.dev.HarnessTeam.PL;
import static io.harness.eraro.ErrorCode.UNEXPECTED;

import io.harness.annotations.dev.OwnedBy;
import io.harness.exception.NestedExceptionUtils;
import io.harness.exception.ScmException;
import io.harness.exception.ScmResourceNotFoundException;
import io.harness.exception.ScmUnauthorizedException;
import io.harness.exception.WingsException;
import io.harness.gitsync.common.scmerrorhandling.exceptions.github.GithubScmExceptionExplanations;
import io.harness.gitsync.common.scmerrorhandling.exceptions.github.GithubScmExceptionHints;
import io.harness.gitsync.common.scmerrorhandling.handlers.ScmApiErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@OwnedBy(PL)
public class GithubListBranchesScmApiErrorHandler implements ScmApiErrorHandler {
  @Override
  public void handleError(int statusCode, String errorMessage) throws WingsException {
    switch (statusCode) {
      case 401:
      case 403:
        throw NestedExceptionUtils.hintWithExplanationException(GithubScmExceptionHints.INVALID_CREDENTIALS,
            GithubScmExceptionExplanations.LIST_BRANCH_WITH_INVALID_CRED, new ScmUnauthorizedException(errorMessage));
      case 404:
        throw NestedExceptionUtils.hintWithExplanationException(GithubScmExceptionHints.REPO_NOT_FOUND,
            GithubScmExceptionExplanations.LIST_BRANCH_WHEN_REPO_NOT_EXIST,
            new ScmResourceNotFoundException(errorMessage));
      default:
        log.error(String.format("Error while listing github branches: [%s: %s]", statusCode, errorMessage));
        throw new ScmException(UNEXPECTED);
    }
  }
}
