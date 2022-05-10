/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.gitsync.common.scmerrorhandling.handlers.github;

import static io.harness.annotations.dev.HarnessTeam.PL;
import static io.harness.gitsync.common.scmerrorhandling.handlers.github.ScmErrorHints.INVALID_CREDENTIALS;

import io.harness.annotations.dev.OwnedBy;
import io.harness.exception.NestedExceptionUtils;
import io.harness.exception.ScmConflictException;
import io.harness.exception.ScmResourceNotFoundException;
import io.harness.exception.ScmUnauthorizedException;
import io.harness.exception.ScmUnexpectedException;
import io.harness.exception.ScmUnprocessableEntityException;
import io.harness.exception.WingsException;
import io.harness.gitsync.common.scmerrorhandling.handlers.ScmApiErrorHandler;

@OwnedBy(PL)
public class GithubUpdateFileScmApiErrorHandler implements ScmApiErrorHandler {
  public static final String UPDATE_FILE_WITH_INVALID_CREDS =
      "The requested file couldn't be updated. " + ScmErrorExplanations.INVALID_CONNECTOR_CREDS;
  public static final String UPDATE_FILE_NOT_FOUND_ERROR_HINT = "Please check the following:\n"
      + "1. If requested Github repository exists or not.\n"
      + "2. If requested branch exists or not.";
  public static final String UPDATE_FILE_NOT_FOUND_ERROR_EXPLANATION =
      "There was issue while updating file in git. Possible reasons can be:\n"
      + "1. The requested Github repository doesn't exist\n"
      + "2. The requested branch doesn't exist in given Github repository.";
  public static final String UPDATE_FILE_CONFLICT_ERROR_HINT =
      "Please check the input blob id of the requested file. It should match with current blob id of the file at head of the branch in Github repository";
  public static final String UPDATE_FILE_CONFLICT_ERROR_EXPLANATION =
      "The input blob id of the requested file doesn't match with current blob id of the file at head of the branch in Github repository, which results in update operation failure.";
  public static final String UPDATE_FILE_UNPROCESSABLE_ENTITY_ERROR_HINT =
      "Please check if requested filepath is a valid one.";
  public static final String UPDATE_FILE_UNPROCESSABLE_ENTITY_ERROR_EXPLANATION =
      "Requested filepath doesn't match with expected valid format.";

  @Override
  public void handleError(int statusCode, String errorMessage) throws WingsException {
    switch (statusCode) {
      case 401:
      case 403:
        throw NestedExceptionUtils.hintWithExplanationException(
            INVALID_CREDENTIALS, UPDATE_FILE_WITH_INVALID_CREDS, new ScmUnauthorizedException(errorMessage));
      case 404:
        throw NestedExceptionUtils.hintWithExplanationException(UPDATE_FILE_NOT_FOUND_ERROR_HINT,
            UPDATE_FILE_NOT_FOUND_ERROR_EXPLANATION, new ScmResourceNotFoundException(errorMessage));
      case 409:
        throw NestedExceptionUtils.hintWithExplanationException(UPDATE_FILE_CONFLICT_ERROR_HINT,
            UPDATE_FILE_CONFLICT_ERROR_EXPLANATION, new ScmConflictException(errorMessage));
      case 422:
        throw NestedExceptionUtils.hintWithExplanationException(UPDATE_FILE_UNPROCESSABLE_ENTITY_ERROR_HINT,
            UPDATE_FILE_UNPROCESSABLE_ENTITY_ERROR_EXPLANATION, new ScmUnprocessableEntityException(errorMessage));
      default:
        throw new ScmUnexpectedException(errorMessage);
    }
  }
}
