package io.harness.exception.exceptionmanager.exceptionhandler;

import static io.harness.exception.WingsException.USER;

import io.harness.exception.ExplanationException;
import io.harness.exception.HintException;
import io.harness.exception.InvalidRequestException;
import io.harness.exception.NestedExceptionUtils;
import io.harness.exception.WingsException;
import io.harness.exception.ngexception.CILiteEngineException;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class CILiteEngineExceptionHandler implements ExceptionHandler {
  public static Set<Class<? extends Exception>> exceptions() {
    return ImmutableSet.<Class<? extends Exception>>builder().add(CILiteEngineException.class).build();
  }

  @Override
  public WingsException handleException(Exception exception) {
    CILiteEngineException ex = (CILiteEngineException) exception;
    return NestedExceptionUtils.hintWithExplanationException(HintException.HINT_CI_LITE_ENGINE_CONNECTIVITY,
        ExplanationException.EXPLANATION_LITE_ENGINE_CHECK, new InvalidRequestException(exception.getMessage(), USER));
  }
}
