package io.harness.engine.executables.invokers;

import static io.harness.annotations.dev.HarnessTeam.CDC;

import io.harness.annotations.Redesign;
import io.harness.annotations.dev.OwnedBy;
import io.harness.engine.executables.ExecuteStrategy;
import io.harness.engine.executables.InvokerPackage;
import io.harness.pms.ambiance.Ambiance;
import io.harness.pms.execution.NodeExecutionProto;
import io.harness.pms.execution.utils.AmbianceUtils;
import io.harness.pms.plan.PlanNodeProto;
import io.harness.pms.sdk.core.execution.PmsNodeExecutionService;
import io.harness.pms.sdk.core.steps.executables.SyncExecutable;
import io.harness.pms.sdk.core.steps.io.StepResponse;
import io.harness.pms.sdk.registries.StepRegistry;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({"rawtypes", "unchecked"})
@OwnedBy(CDC)
@Redesign
@Slf4j
public class SyncStrategy implements ExecuteStrategy {
  @Inject private StepRegistry stepRegistry;
  @Inject private PmsNodeExecutionService pmsNodeExecutionService;

  @Override
  public void start(InvokerPackage invokerPackage) {
    NodeExecutionProto nodeExecution = invokerPackage.getNodeExecution();
    Ambiance ambiance = nodeExecution.getAmbiance();
    SyncExecutable syncExecutable = extractSyncExecutable(nodeExecution);
    StepResponse stepResponse =
        syncExecutable.executeSync(ambiance, pmsNodeExecutionService.extractResolvedStepParameters(nodeExecution),
            invokerPackage.getInputPackage(), invokerPackage.getPassThroughData());
    pmsNodeExecutionService.handleStepResponse(AmbianceUtils.obtainCurrentRuntimeId(ambiance), stepResponse);
  }

  SyncExecutable extractSyncExecutable(NodeExecutionProto nodeExecution) {
    PlanNodeProto node = nodeExecution.getNode();
    return (SyncExecutable) stepRegistry.obtain(node.getStepType());
  }
}
