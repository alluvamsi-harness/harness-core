package io.harness.gitsync.common.scmerrorhandling.handlers.bitbucket;

import static io.harness.annotations.dev.HarnessTeam.PL;
import static io.harness.eraro.ErrorCode.UNEXPECTED;

import io.harness.annotations.dev.OwnedBy;
import io.harness.exception.NestedExceptionUtils;
import io.harness.exception.ScmException;
import io.harness.exception.ScmResourceNotFoundException;
import io.harness.exception.ScmUnauthorizedException;
import io.harness.exception.WingsException;
import io.harness.gitsync.common.scmerrorhandling.exceptions.bitbucket.BitbucketScmExceptionExplanations;
import io.harness.gitsync.common.scmerrorhandling.exceptions.bitbucket.BitbucketScmExceptionHints;
import io.harness.gitsync.common.scmerrorhandling.handlers.ScmApiErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@OwnedBy(PL)
public class BitbucketListBranchesScmApiErrorHandler implements ScmApiErrorHandler {
  @Override
  public void handleError(int statusCode, String errorMessage) throws WingsException {
    switch (statusCode) {
      case 401:
      case 403:
        throw NestedExceptionUtils.hintWithExplanationException(BitbucketScmExceptionHints.INVALID_CREDENTIALS,
            BitbucketScmExceptionExplanations.LIST_BRANCH_WITH_INVALID_CRED,
            new ScmUnauthorizedException(errorMessage));
      case 404:
        throw NestedExceptionUtils.hintWithExplanationException(BitbucketScmExceptionHints.REPO_NOT_FOUND,
            BitbucketScmExceptionExplanations.LIST_BRANCH_WHEN_REPO_NOT_EXIST,
            new ScmResourceNotFoundException(errorMessage));
      default:
        log.error(String.format("Error while listing bitbucket branches: [%s: %s]", statusCode, errorMessage));
        throw new ScmException(UNEXPECTED);
    }
  }
}
