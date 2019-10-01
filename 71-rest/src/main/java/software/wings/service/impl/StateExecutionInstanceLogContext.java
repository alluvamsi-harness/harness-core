package software.wings.service.impl;

import io.harness.logging.AutoLogContext;

public class StateExecutionInstanceLogContext extends AutoLogContext {
  public static final String ID = "StateExecutionInstanceId";

  public StateExecutionInstanceLogContext(String stateExecutionInstanceId, OverrideBehavior behavior) {
    super(ID, stateExecutionInstanceId, behavior);
  }
}
