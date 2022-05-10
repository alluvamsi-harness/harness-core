/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SCMExceptionExplanations {
  public static final String UNABLE_TO_PUSH_TO_REPO_WITH_USER_CREDENTIALS =
      "We couldn't perform required git operation. Possible reasons can be:\n"
      + "1. Missing permissions to fetch selected branch of the repo\n"
      + "2. Repo does not exist or has been deleted\n"
      + "3. Credentials are invalid or have expired";
  public static final String LIST_REPO_WITH_INVALID_CREDS =
      "We couldn't list repositories as the credentials provided in connector are invalid or have expired.";
  public static final String GET_FILE_WITH_INVALID_CREDS =
      "We couldn't fetch requested file from git as the credentials provided in connector are invalid or have expired.";
  public static final String CREATE_PULL_REQUEST_WITH_INVALID_CREDS =
      "We couldn't create pull request in git as the credentials provided in connector are invalid or have expired.";
  public static final String FILE_NOT_FOUND = "The requested file path doesn't exist in git. Possible reasons can be:\n"
      + "1. The requested file path doesn't exist for given branch and repo\n"
      + "2. The given branch or repo is invalid";
  public static final String CREATE_FILE_WITH_INVALID_CREDS =
      "We couldn't create file in git as the credentials provided in connector are invalid or have expired.";
  public static final String UPDATE_FILE_WITH_INVALID_CREDS =
      "We couldn't update file in git as the credentials provided in connector are invalid or have expired.";
  public static final String CREATE_PULL_REQUEST_VALIDATION_FAILED =
      "There was issue while creating pull request. Possible reasons can be:\n"
      + "1. There is already an open pull request from source to target branch for given git repository.\n"
      + "2. The source branch or target branch doesn't exist for given git repository.\n"
      + "3. The title of the pull request is empty";
  public static final String REPOSITORY_NOT_FOUND_ERROR = "The requested repository doesn't exist in git.";
  public static final String CREATE_FILE_NOT_FOUND_ERROR =
      "There was issue while creating file in git. Possible reasons can be:\n"
      + "1. The requested git repository doesn't exist\n"
      + "2. The requested branch doesn't exist in given git repository.";
  public static final String UPDATE_FILE_NOT_FOUND_ERROR =
      "There was issue while updating file in git. Possible reasons can be:\n"
      + "1. The requested git repository doesn't exist\n"
      + "2. The requested branch doesn't exist in given git repository.";
  public static final String CREATE_FILE_CONFLICT_ERROR =
      "File with given filepath already exists in git, thus couldn't create a new file";
  public static final String UPDATE_FILE_CONFLICT_ERROR =
      "The input blob id of the requested file doesn't match with current blob id of the file at head of the branch in git, which results in update operation failure.";
  public static final String CREATE_FILE_UNPROCESSABLE_ENTITY_ERROR =
      "Requested filepath doesn't match with expected valid format.";
  public static final String UPDATE_FILE_UNPROCESSABLE_ENTITY_ERROR =
      "Requested filepath doesn't match with expected valid format.";
}
