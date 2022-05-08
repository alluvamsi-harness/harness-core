/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cvng.notification.entities;

import io.harness.cvng.notification.beans.MonitoredServiceChangeEventType;
import io.harness.cvng.notification.beans.MonitoredServiceNotificationRuleConditionType;
import io.harness.cvng.notification.beans.NotificationRuleType;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.mongodb.morphia.query.UpdateOperations;

@JsonTypeName("MONITORED_SERVICE")
@Data
@SuperBuilder
@NoArgsConstructor
@FieldNameConstants(innerTypeName = "MonitoredServiceNotificationRuleKeys")
@EqualsAndHashCode(callSuper = true)
public class MonitoredServiceNotificationRule extends NotificationRule {
  @NonNull List<MonitoredServiceNotificationRuleEntityCondition> conditions;

  @Override
  public NotificationRuleType getType() {
    return NotificationRuleType.MONITORED_SERVICE;
  }

  @Data
  @SuperBuilder
  public static class MonitoredServiceNotificationRuleEntityCondition {
    @NonNull MonitoredServiceNotificationRuleConditionType conditionType;
    @NotNull MonitoredServiceNotificationRuleEntityConditionSpec spec;
  }

  @Data
  @SuperBuilder
  public abstract static class MonitoredServiceNotificationRuleEntityConditionSpec {
    public abstract MonitoredServiceNotificationRuleConditionType getConditionType();
  }

  @SuperBuilder
  @Data
  public static class MonitoredServiceChangeImpactEntityConditionSpec
      extends MonitoredServiceNotificationRuleEntityConditionSpec {
    @NonNull List<MonitoredServiceChangeEventType> changeEventTypes;
    @NonNull Double threshold;
    @NonNull String period;

    @Override
    public MonitoredServiceNotificationRuleConditionType getConditionType() {
      return MonitoredServiceNotificationRuleConditionType.CHANGE_IMPACT;
    }
  }

  @SuperBuilder
  @Data
  public static class MonitoredServiceHealthScoreEntityConditionSpec
      extends MonitoredServiceNotificationRuleEntityConditionSpec {
    @NonNull Double threshold;
    @NonNull String period;

    @Override
    public MonitoredServiceNotificationRuleConditionType getConditionType() {
      return MonitoredServiceNotificationRuleConditionType.HEALTH_SCORE;
    }
  }

  @SuperBuilder
  @Data
  public static class MonitoredServiceChangeObservedEntityConditionSpec
      extends MonitoredServiceNotificationRuleEntityConditionSpec {
    @NonNull List<MonitoredServiceChangeEventType> changeEventTypes;

    @Override
    public MonitoredServiceNotificationRuleConditionType getConditionType() {
      return MonitoredServiceNotificationRuleConditionType.CHANGE_OBSERVED;
    }
  }

  public static class MonitoredServiceNotificationRuleUpdatableEntity
      extends NotificationRuleUpdatableEntity<MonitoredServiceNotificationRule, MonitoredServiceNotificationRule> {
    @Override
    public void setUpdateOperations(UpdateOperations<MonitoredServiceNotificationRule> updateOperations,
        MonitoredServiceNotificationRule monitoredServiceNotificationRule) {
      setCommonOperations(updateOperations, monitoredServiceNotificationRule);
      updateOperations.set(
          MonitoredServiceNotificationRuleKeys.conditions, monitoredServiceNotificationRule.getConditions());
    }
  }
}
