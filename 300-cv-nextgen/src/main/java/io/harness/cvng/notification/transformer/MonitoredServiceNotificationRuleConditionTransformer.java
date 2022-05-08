/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cvng.notification.transformer;

import io.harness.cvng.core.beans.params.ProjectParams;
import io.harness.cvng.notification.beans.MonitoredServiceChangeImpactConditionSpec;
import io.harness.cvng.notification.beans.MonitoredServiceChangeObservedConditionSpec;
import io.harness.cvng.notification.beans.MonitoredServiceHealthScoreConditionSpec;
import io.harness.cvng.notification.beans.MonitoredServiceNotificationRuleCondition;
import io.harness.cvng.notification.beans.MonitoredServiceNotificationRuleConditionSpec;
import io.harness.cvng.notification.beans.MonitoredServiceNotificationRuleConditionType;
import io.harness.cvng.notification.beans.NotificationRuleDTO;
import io.harness.cvng.notification.beans.NotificationRuleType;
import io.harness.cvng.notification.entities.MonitoredServiceNotificationRule;
import io.harness.cvng.notification.entities.MonitoredServiceNotificationRule.MonitoredServiceChangeImpactEntityConditionSpec;
import io.harness.cvng.notification.entities.MonitoredServiceNotificationRule.MonitoredServiceChangeObservedEntityConditionSpec;
import io.harness.cvng.notification.entities.MonitoredServiceNotificationRule.MonitoredServiceHealthScoreEntityConditionSpec;
import io.harness.cvng.notification.entities.MonitoredServiceNotificationRule.MonitoredServiceNotificationRuleEntityCondition;
import io.harness.cvng.notification.entities.MonitoredServiceNotificationRule.MonitoredServiceNotificationRuleEntityConditionSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MonitoredServiceNotificationRuleConditionTransformer
    extends NotificationRuleConditionTransformer<MonitoredServiceNotificationRule,
        MonitoredServiceNotificationRuleCondition> {
  @Override
  public MonitoredServiceNotificationRule getEntity(
      ProjectParams projectParams, NotificationRuleDTO notificationRuleDTO) {
    List<MonitoredServiceNotificationRuleCondition> monitoredServiceNotificationRuleConditions = new ArrayList<>();
    notificationRuleDTO.getConditions().forEach(condition
        -> monitoredServiceNotificationRuleConditions.add((MonitoredServiceNotificationRuleCondition) condition));
    return MonitoredServiceNotificationRule.builder()
        .accountId(projectParams.getAccountIdentifier())
        .orgIdentifier(projectParams.getOrgIdentifier())
        .projectIdentifier(projectParams.getProjectIdentifier())
        .identifier(notificationRuleDTO.getIdentifier())
        .name(notificationRuleDTO.getName())
        .enabled(notificationRuleDTO.isEnabled())
        .type(NotificationRuleType.MONITORED_SERVICE)
        .notificationMethod(notificationRuleDTO.getNotificationMethod())
        .conditions(monitoredServiceNotificationRuleConditions.stream()
                        .map(condition
                            -> MonitoredServiceNotificationRuleEntityCondition.builder()
                                   .conditionType(condition.getConditionType())
                                   .spec(getEntitySpec(condition.getSpec()))
                                   .build())
                        .collect(Collectors.toList()))
        .build();
  }

  @Override
  protected List<MonitoredServiceNotificationRuleCondition> getSpec(MonitoredServiceNotificationRule notificationRule) {
    return notificationRule.getConditions()
        .stream()
        .map(condition
            -> MonitoredServiceNotificationRuleCondition.builder()
                   .conditionType(condition.getConditionType())
                   .spec(getDTOSpec(condition.getSpec()))
                   .build())
        .collect(Collectors.toList());
  }

  private MonitoredServiceNotificationRuleEntityConditionSpec getEntitySpec(
      MonitoredServiceNotificationRuleConditionSpec conditionSpec) {
    if (conditionSpec.getConditionType().equals(MonitoredServiceNotificationRuleConditionType.CHANGE_IMPACT)) {
      MonitoredServiceChangeImpactConditionSpec spec = (MonitoredServiceChangeImpactConditionSpec) conditionSpec;
      return MonitoredServiceChangeImpactEntityConditionSpec.builder()
          .changeEventTypes(spec.getChangeEventTypes())
          .threshold(spec.getThreshold())
          .period(spec.getPeriod())
          .build();
    } else if (conditionSpec.getConditionType().equals(MonitoredServiceNotificationRuleConditionType.HEALTH_SCORE)) {
      MonitoredServiceHealthScoreConditionSpec spec = (MonitoredServiceHealthScoreConditionSpec) conditionSpec;
      return MonitoredServiceHealthScoreEntityConditionSpec.builder()
          .threshold(spec.getThreshold())
          .period(spec.getPeriod())
          .build();
    } else if (conditionSpec.getConditionType().equals(MonitoredServiceNotificationRuleConditionType.CHANGE_OBSERVED)) {
      MonitoredServiceChangeObservedConditionSpec spec = (MonitoredServiceChangeObservedConditionSpec) conditionSpec;
      return MonitoredServiceChangeObservedEntityConditionSpec.builder()
          .changeEventTypes(spec.getChangeEventTypes())
          .build();
    } else {
      throw new RuntimeException(
          "Invalid Monitored Service Notification Rule Condition Type: " + conditionSpec.getConditionType());
    }
  }

  private MonitoredServiceNotificationRuleConditionSpec getDTOSpec(
      MonitoredServiceNotificationRuleEntityConditionSpec entityConditionSpec) {
    if (entityConditionSpec.getConditionType().equals(MonitoredServiceNotificationRuleConditionType.CHANGE_IMPACT)) {
      MonitoredServiceChangeImpactEntityConditionSpec conditionSpec =
          (MonitoredServiceChangeImpactEntityConditionSpec) entityConditionSpec;
      return MonitoredServiceChangeImpactConditionSpec.builder()
          .changeEventTypes(conditionSpec.getChangeEventTypes())
          .threshold(conditionSpec.getThreshold())
          .period(conditionSpec.getPeriod())
          .build();
    } else if (entityConditionSpec.getConditionType().equals(
                   MonitoredServiceNotificationRuleConditionType.HEALTH_SCORE)) {
      MonitoredServiceHealthScoreEntityConditionSpec conditionSpec =
          (MonitoredServiceHealthScoreEntityConditionSpec) entityConditionSpec;
      return MonitoredServiceHealthScoreConditionSpec.builder()
          .threshold(conditionSpec.getThreshold())
          .period(conditionSpec.getPeriod())
          .build();
    } else if (entityConditionSpec.getConditionType().equals(
                   MonitoredServiceNotificationRuleConditionType.CHANGE_OBSERVED)) {
      MonitoredServiceChangeObservedEntityConditionSpec conditionSpec =
          (MonitoredServiceChangeObservedEntityConditionSpec) entityConditionSpec;
      return MonitoredServiceChangeObservedConditionSpec.builder()
          .changeEventTypes(conditionSpec.getChangeEventTypes())
          .build();
    } else {
      throw new RuntimeException(
          "Invalid Monitored Service Notification Rule Condition Type: " + entityConditionSpec.getConditionType());
    }
  }
}
