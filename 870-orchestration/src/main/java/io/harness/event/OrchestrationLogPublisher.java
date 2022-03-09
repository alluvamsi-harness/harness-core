/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.event;

import static io.harness.data.structure.HarnessStringUtils.emptyIfNull;

import io.harness.OrchestrationModuleConfig;
import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.OrchestrationEventLog;
import io.harness.engine.observers.NodeExecutionStartObserver;
import io.harness.engine.observers.NodeStartInfo;
import io.harness.engine.observers.NodeStatusUpdateObserver;
import io.harness.engine.observers.NodeUpdateInfo;
import io.harness.engine.observers.NodeUpdateObserver;
import io.harness.engine.observers.PlanStatusUpdateObserver;
import io.harness.engine.observers.StepDetailsUpdateInfo;
import io.harness.engine.observers.StepDetailsUpdateObserver;
import io.harness.eventsframework.EventsFrameworkConstants;
import io.harness.eventsframework.api.Producer;
import io.harness.eventsframework.producer.Message;
import io.harness.pms.PmsFeatureFlagService;
import io.harness.pms.contracts.ambiance.Ambiance;
import io.harness.pms.contracts.execution.events.OrchestrationEventType;
import io.harness.pms.contracts.visualisation.log.OrchestrationLogEvent;
import io.harness.pms.execution.utils.AmbianceUtils;
import io.harness.repositories.orchestrationEventLog.OrchestrationEventLogRepository;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.sql.Date;
import java.time.Duration;
import java.time.OffsetDateTime;

@OwnedBy(HarnessTeam.PIPELINE)
@Singleton
public class OrchestrationLogPublisher
    implements NodeUpdateObserver, NodeStatusUpdateObserver, PlanStatusUpdateObserver, StepDetailsUpdateObserver,
               NodeExecutionStartObserver {
  @Inject private OrchestrationEventLogRepository orchestrationEventLogRepository;
  @Inject @Named(EventsFrameworkConstants.ORCHESTRATION_LOG) private Producer producer;
  @Inject OrchestrationModuleConfig orchestrationModuleConfig;
  @Override
  public void onNodeStatusUpdate(NodeUpdateInfo nodeUpdateInfo) {
    createAndHandleEventLogV1(nodeUpdateInfo.getPlanExecutionId(), nodeUpdateInfo.getNodeExecutionId(),
        OrchestrationEventType.NODE_EXECUTION_STATUS_UPDATE);
  }

  @Override
  public void onPlanStatusUpdate(Ambiance ambiance) {
    createAndHandleEventLogV1(ambiance.getPlanExecutionId(), AmbianceUtils.obtainCurrentRuntimeId(ambiance),
        OrchestrationEventType.PLAN_EXECUTION_STATUS_UPDATE);
  }

  @Override
  public void onNodeUpdate(NodeUpdateInfo nodeUpdateInfo) {
    if (!orchestrationModuleConfig.isReduceOrchestrationLog()) {
      createAndHandleEventLogV1(nodeUpdateInfo.getNodeExecution().getPlanExecutionId(),
          AmbianceUtils.obtainCurrentRuntimeId(nodeUpdateInfo.getNodeExecution().getAmbiance()),
          OrchestrationEventType.NODE_EXECUTION_UPDATE);
    }
  }

  // Todo: Introduce batching over here
  public void createAndHandleEventLog(
      String planExecutionId, String nodeExecutionId, OrchestrationEventType eventType) {
    if (!orchestrationModuleConfig.isReduceOrchestrationLog()) {
      return;
    }
    createAndHandleEventLogV1(planExecutionId, nodeExecutionId, eventType);
  }

  // Todo: Introduce batching over here
  private void createAndHandleEventLogV1(
      String planExecutionId, String nodeExecutionId, OrchestrationEventType eventType) {
    orchestrationEventLogRepository.save(
        OrchestrationEventLog.builder()
            .createdAt(System.currentTimeMillis())
            .nodeExecutionId(nodeExecutionId)
            .orchestrationEventType(eventType)
            .planExecutionId(planExecutionId)
            .validUntil(Date.from(OffsetDateTime.now().plus(Duration.ofDays(14)).toInstant()))
            .build());
    OrchestrationLogEvent orchestrationLogEvent =
        OrchestrationLogEvent.newBuilder().setPlanExecutionId(planExecutionId).build();

    producer.send(Message.newBuilder()
                      .putAllMetadata(ImmutableMap.of("nodeExecutionId", emptyIfNull(nodeExecutionId),
                          "planExecutionId", planExecutionId, "eventType", eventType.name()))
                      .setData(orchestrationLogEvent.toByteString())
                      .build());
  }

  @Override
  public void onNodeStart(NodeStartInfo nodeStartInfo) {
    createAndHandleEventLogV1(nodeStartInfo.getNodeExecution().getPlanExecutionId(),
        nodeStartInfo.getNodeExecution().getUuid(), OrchestrationEventType.NODE_EXECUTION_START);
  }

  @Override
  public void onStepDetailsUpdate(StepDetailsUpdateInfo stepDetailsUpdateInfo) {
    createAndHandleEventLogV1(stepDetailsUpdateInfo.getPlanExecutionId(), stepDetailsUpdateInfo.getNodeExecutionId(),
        OrchestrationEventType.STEP_DETAILS_UPDATE);
  }

  @Override
  public void onStepInputsAdd(StepDetailsUpdateInfo stepDetailsUpdateInfo) {
    createAndHandleEventLogV1(stepDetailsUpdateInfo.getPlanExecutionId(), stepDetailsUpdateInfo.getNodeExecutionId(),
        OrchestrationEventType.STEP_INPUTS_UPDATE);
  }
}
