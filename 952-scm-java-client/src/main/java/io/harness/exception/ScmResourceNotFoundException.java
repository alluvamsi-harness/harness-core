package io.harness.exception;

import static io.harness.annotations.dev.HarnessTeam.PL;

import io.harness.annotations.dev.OwnedBy;
import io.harness.eraro.ErrorCode;

@OwnedBy(PL)
public class ScmResourceNotFoundException extends ScmException {
  public ScmResourceNotFoundException(String errorMessage) {
    super(errorMessage, null, ErrorCode.SCM_NOT_FOUND_ERROR);
  }
}
