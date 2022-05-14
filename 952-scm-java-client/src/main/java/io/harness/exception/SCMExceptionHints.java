/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SCMExceptionHints {
  public static final String INVALID_CREDENTIALS = "Please check your credentials.";
  public static final String BITBUCKET_INVALID_CREDENTIALS = "Please check your Bitbucket connector credentials.";
  public static final String GITHUB_INVALID_CREDENTIALS = "Please check your Github connector credentials.";
  public static final String FILE_NOT_FOUND =
      "Please check the requested file path / branch / repo name if they exist or not.";
  public static final String CREATE_PULL_REQUEST_VALIDATION_FAILED = "Please check the following:\n"
      + "1. If already a pull request exists for request source to target branch.\n"
      + "2. If source branch and target branch both exists in git repository.\n"
      + "3. If title of the pull request is empty.";
  public static final String REPOSITORY_NOT_FOUND_ERROR = "Please check if the repository exists in git or not";
  public static final String CREATE_FILE_NOT_FOUND_ERROR = "Please check the following:\n"
      + "1. If requested git repository exists or not.\n"
      + "2. If requested branch exists or not.";
  public static final String UPDATE_FILE_NOT_FOUND_ERROR = "Please check the following:\n"
      + "1. If requested git repository exists or not.\n"
      + "2. If requested branch exists or not.";
  public static final String CREATE_FILE_CONFLICT_ERROR =
      "Please check if there's already a file in git for the given filepath";
  public static final String UPDATE_FILE_CONFLICT_ERROR =
      "Please check the input blob id of the requested file. It should match with current blob id of the file at head of the branch in git";
  public static final String CREATE_FILE_UNPROCESSABLE_ENTITY_ERROR =
      "Please check if requested filepath is a valid one.";
  public static final String UPDATE_FILE_UNPROCESSABLE_ENTITY_ERROR =
      "Please check if requested filepath is a valid one.";
}
