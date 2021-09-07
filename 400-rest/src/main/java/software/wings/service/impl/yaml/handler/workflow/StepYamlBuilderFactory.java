package software.wings.service.impl.yaml.handler.workflow;

import io.harness.annotations.dev.HarnessModule;
import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.annotations.dev.TargetModule;

import software.wings.sm.StepType;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;

@TargetModule(HarnessModule._870_CG_ORCHESTRATION)
@OwnedBy(HarnessTeam.CDC)
@Slf4j
public class StepYamlBuilderFactory {
  @Inject private Injector injector;

  public StepYamlBuilder getStepYamlBuilderForStepType(StepType stepType) {
    StepYamlBuilder validator = null;
    try {
      validator = injector.getInstance(stepType.getYamlValidatorClass());
    } catch (Exception e) {
      log.warn("Can not get step yaml builder for step type" + stepType.getType(), e);
    }
    return validator;
  }
}
