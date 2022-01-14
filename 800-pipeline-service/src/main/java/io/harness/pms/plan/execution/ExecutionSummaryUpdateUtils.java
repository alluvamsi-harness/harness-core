package io.harness.pms.plan.execution;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.ExecutionErrorInfo;
import io.harness.dto.converter.FailureInfoDTOConverter;
import io.harness.engine.utils.OrchestrationUtils;
import io.harness.execution.NodeExecution;
import io.harness.plan.NodeType;
import io.harness.pms.contracts.ambiance.Level;
import io.harness.pms.execution.ExecutionStatus;
import io.harness.pms.execution.utils.AmbianceUtils;
import io.harness.pms.plan.execution.beans.PipelineExecutionSummaryEntity;
import io.harness.steps.StepSpecTypeConstants;

import java.util.Objects;
import java.util.Optional;
import org.springframework.data.mongodb.core.query.Update;

/**
 * A utility to generate updates for the layout graph used in the list api for stage layout
 */
@OwnedBy(HarnessTeam.PIPELINE)
public class ExecutionSummaryUpdateUtils {
  public static void addStageUpdateCriteria(Update update, String planExecutionId, NodeExecution nodeExecution) {
    String stageUuid = nodeExecution.getNode().getUuid();
    ExecutionStatus status = ExecutionStatus.getExecutionStatus(nodeExecution.getStatus());
    if (Objects.equals(nodeExecution.getNode().getStepType().getType(), StepSpecTypeConstants.BARRIER)) {
      Optional<Level> stage = AmbianceUtils.getStageLevelFromAmbiance(nodeExecution.getAmbiance());
      stage.ifPresent(stageNode
          -> update.set(PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.layoutNodeMap + "."
                  + stageNode.getSetupId() + ".barrierFound",
              true));
    }
    if (!OrchestrationUtils.isStageNode(nodeExecution)) {
      return;
    }
    // If the nodes is of type Identity, there is no need to update the status. We want to update the status only when
    // there is a PlanNode
    if (!nodeExecution.getNode().getNodeType().equals(NodeType.IDENTITY_PLAN_NODE)) {
      update.set(
          PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.layoutNodeMap + "." + stageUuid + ".status", status);
    }
    update.set(PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.layoutNodeMap + "." + stageUuid + ".startTs",
        nodeExecution.getStartTs());
    update.set(PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.layoutNodeMap + "." + stageUuid + ".nodeRunInfo",
        nodeExecution.getNodeRunInfo());
    if (nodeExecution.getEndTs() != null) {
      update.set(PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.layoutNodeMap + "." + stageUuid + ".endTs",
          nodeExecution.getEndTs());
    }
    if (nodeExecution.getFailureInfo() != null) {
      update.set(
          PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.layoutNodeMap + "." + stageUuid + ".failureInfo",
          ExecutionErrorInfo.builder().message(nodeExecution.getFailureInfo().getErrorMessage()).build());
      update.set(
          PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.layoutNodeMap + "." + stageUuid + ".failureInfoDTO",
          FailureInfoDTOConverter.toFailureInfoDTO(nodeExecution.getFailureInfo()));
    }
    if (nodeExecution.getSkipInfo() != null) {
      update.set(PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.layoutNodeMap + "." + stageUuid + ".skipInfo",
          nodeExecution.getSkipInfo());
    }
  }

  public static void addPipelineUpdateCriteria(Update update, String planExecutionId, NodeExecution nodeExecution) {
    if (OrchestrationUtils.isPipelineNode(nodeExecution)) {
      ExecutionStatus status = ExecutionStatus.getExecutionStatus(nodeExecution.getStatus());
      update.set(PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.internalStatus, nodeExecution.getStatus());
      update.set(PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.status, status);
      if (nodeExecution.getEndTs() != null) {
        update.set(PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.endTs, nodeExecution.getEndTs());
      }
      if (status == ExecutionStatus.FAILED) {
        update.set(PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.executionErrorInfo,
            ExecutionErrorInfo.builder().message(nodeExecution.getFailureInfo().getErrorMessage()).build());
        update.set(PipelineExecutionSummaryEntity.PlanExecutionSummaryKeys.failureInfo,
            FailureInfoDTOConverter.toFailureInfoDTO(nodeExecution.getFailureInfo()));
      }
    }
  }
}