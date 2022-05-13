package io.harness.exception.ngexception;

public class CILiteEngineException extends RuntimeException {
  private static final String MESSAGE_KEY = "message";

  public CILiteEngineException(Exception e) {
    super(e);
  }

  public CILiteEngineException(String message) {
    super(message);
  }

  public CILiteEngineException(String message, Exception e) {
    super(message, e);
  }
}
