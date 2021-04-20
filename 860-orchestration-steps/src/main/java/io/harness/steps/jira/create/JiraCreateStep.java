package io.harness.steps.jira.create;

import static io.harness.annotations.dev.HarnessTeam.CDC;

import io.harness.annotations.dev.OwnedBy;
import io.harness.delegate.task.jira.JiraTaskNGParameters;
import io.harness.delegate.task.jira.JiraTaskNGParameters.JiraTaskNGParametersBuilder;
import io.harness.delegate.task.jira.JiraTaskNGResponse;
import io.harness.jira.JiraActionNG;
import io.harness.plancreator.steps.common.StepElementParameters;
import io.harness.pms.contracts.ambiance.Ambiance;
import io.harness.pms.contracts.execution.tasks.TaskRequest;
import io.harness.pms.contracts.steps.StepType;
import io.harness.pms.sdk.core.steps.executables.TaskExecutable;
import io.harness.pms.sdk.core.steps.io.StepInputPackage;
import io.harness.pms.sdk.core.steps.io.StepResponse;
import io.harness.steps.StepSpecTypeConstants;
import io.harness.steps.jira.JiraStepHelperService;
import io.harness.supplier.ThrowingSupplier;

import com.google.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;

@OwnedBy(CDC)
public class JiraCreateStep implements TaskExecutable<StepElementParameters, JiraTaskNGResponse> {
  public static final StepType STEP_TYPE = StepType.newBuilder().setType(StepSpecTypeConstants.JIRA_CREATE).build();

  @Inject private JiraStepHelperService jiraStepHelperService;

  @Override
  public TaskRequest obtainTask(
      Ambiance ambiance, StepElementParameters stepParameters, StepInputPackage inputPackage) {
    JiraCreateSpecParameters specParameters = (JiraCreateSpecParameters) stepParameters.getSpec();
    JiraTaskNGParametersBuilder paramsBuilder =
        JiraTaskNGParameters.builder()
            .action(JiraActionNG.CREATE_ISSUE)
            .projectKey(specParameters.getProjectKey().getValue())
            .issueType(specParameters.getIssueType().getValue())
            .fields(specParameters.getFields() == null
                    ? null
                    : specParameters.getFields().entrySet().stream().collect(
                        Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue())));
    return jiraStepHelperService.prepareTaskRequest(paramsBuilder, ambiance,
        specParameters.getConnectorRef().getValue(), stepParameters.getTimeout().getValue(), "Jira Task: Create Issue");
  }

  @Override
  public StepResponse handleTaskResult(Ambiance ambiance, StepElementParameters stepParameters,
      ThrowingSupplier<JiraTaskNGResponse> responseSupplier) throws Exception {
    return jiraStepHelperService.prepareStepResponse(responseSupplier);
  }

  @Override
  public Class<StepElementParameters> getStepParametersClass() {
    return StepElementParameters.class;
  }
}
