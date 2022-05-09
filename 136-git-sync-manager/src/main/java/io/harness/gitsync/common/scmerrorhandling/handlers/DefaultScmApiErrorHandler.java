/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.gitsync.common.scmerrorhandling.handlers;

import static io.harness.annotations.dev.HarnessTeam.PL;
import static io.harness.data.structure.EmptyPredicate.isNotEmpty;

import io.harness.annotations.dev.OwnedBy;
import io.harness.eraro.ErrorCode;
import io.harness.exception.ExplanationException;
import io.harness.exception.NestedExceptionUtils;
import io.harness.exception.SCMExceptionExplanations;
import io.harness.exception.SCMExceptionHints;
import io.harness.exception.ScmException;
import io.harness.exception.UnexpectedException;
import io.harness.exception.WingsException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@OwnedBy(PL)
public class DefaultScmApiErrorHandler implements ScmApiErrorHandler {
  @Override
  public void handleError(int statusCode, String errorMessage) throws WingsException {
    if (statusCode >= 300) {
      ErrorCode errorCode = convertScmStatusCodeToErrorCode(statusCode);
      if (errorCode == ErrorCode.UNEXPECTED) {
        log.error("Encountered new status code: [{}] with message: [{}] from scm", statusCode, errorMessage);
        throw new UnexpectedException("Unexpected error occurred while doing scm operation");
      } else if (errorCode == ErrorCode.SCM_NOT_FOUND_ERROR) {
        throw NestedExceptionUtils.hintWithExplanationException(SCMExceptionHints.INVALID_CREDENTIALS,
            SCMExceptionExplanations.UNABLE_TO_PUSH_TO_REPO_WITH_USER_CREDENTIALS, new ScmException(errorCode));
      }
      if (isNotEmpty(errorMessage)) {
        throw new ExplanationException(errorMessage, new ScmException(errorCode));
      } else {
        throw new ScmException(errorCode);
      }
    }
  }

  private ErrorCode convertScmStatusCodeToErrorCode(int statusCode) {
    switch (statusCode) {
      case 304:
        return ErrorCode.SCM_NOT_MODIFIED;
      case 404:
        return ErrorCode.SCM_NOT_FOUND_ERROR;
      case 409:
        return ErrorCode.SCM_CONFLICT_ERROR;
      case 422:
        return ErrorCode.SCM_UNPROCESSABLE_ENTITY;
      case 401:
      case 403:
        return ErrorCode.SCM_UNAUTHORIZED;
      case 500:
        return ErrorCode.SCM_INTERNAL_SERVER_ERROR;
      default:
        return ErrorCode.UNEXPECTED;
    }
  }
}
