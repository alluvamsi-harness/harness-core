package software.wings.service.impl.yaml.handler.workflow;

import static io.harness.annotations.dev.HarnessTeam.CDC;
import static io.harness.data.structure.HasPredicate.hasSome;

import io.harness.annotations.dev.OwnedBy;

import software.wings.beans.BasicOrchestrationWorkflow.BasicOrchestrationWorkflowBuilder;
import software.wings.beans.Workflow;
import software.wings.beans.Workflow.WorkflowBuilder;
import software.wings.beans.WorkflowPhase;
import software.wings.beans.concurrency.ConcurrencyStrategy;
import software.wings.yaml.workflow.BasicWorkflowYaml;

import com.google.inject.Singleton;
import java.util.List;
/**
 * @author rktummala on 11/1/17
 */
@OwnedBy(CDC)
@Singleton
public class BasicWorkflowYamlHandler extends WorkflowYamlHandler<BasicWorkflowYaml> {
  @Override
  protected void setOrchestrationWorkflow(WorkflowInfo workflowInfo, WorkflowBuilder workflow) {
    BasicOrchestrationWorkflowBuilder basicOrchestrationWorkflowBuilder =
        BasicOrchestrationWorkflowBuilder.aBasicOrchestrationWorkflow();

    List<WorkflowPhase> phaseList = workflowInfo.getPhaseList();
    if (hasSome(phaseList)) {
      WorkflowPhase workflowPhase = phaseList.get(0);
      workflow.infraMappingId(workflowPhase.getInfraMappingId()).serviceId(workflowPhase.getServiceId());
      workflow.infraDefinitionId(workflowPhase.getInfraDefinitionId());
    }

    basicOrchestrationWorkflowBuilder.withFailureStrategies(workflowInfo.getFailureStrategies())
        .withNotificationRules(workflowInfo.getNotificationRules())
        .withPostDeploymentSteps(workflowInfo.getPostDeploymentSteps())
        .withPreDeploymentSteps(workflowInfo.getPreDeploymentSteps())
        .withRollbackWorkflowPhaseIdMap(workflowInfo.getRollbackPhaseMap())
        .withUserVariables(workflowInfo.getUserVariables())
        .withWorkflowPhases(phaseList);
    if (workflowInfo.getConcurrencyStrategy() != null) {
      basicOrchestrationWorkflowBuilder.withConcurrencyStrategy(
          ConcurrencyStrategy.buildFromUnit(workflowInfo.getConcurrencyStrategy()));
    }
    workflow.orchestrationWorkflow(basicOrchestrationWorkflowBuilder.build());
  }

  @Override
  public BasicWorkflowYaml toYaml(Workflow bean, String appId) {
    BasicWorkflowYaml basicWorkflowYaml = BasicWorkflowYaml.builder().build();
    toYaml(basicWorkflowYaml, bean, appId);
    return basicWorkflowYaml;
  }
}
