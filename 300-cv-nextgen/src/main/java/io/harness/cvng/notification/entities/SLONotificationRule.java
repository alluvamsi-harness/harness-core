/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cvng.notification.entities;

import io.harness.cvng.notification.beans.NotificationRuleType;
import io.harness.cvng.notification.beans.SLONotificationRuleCondition.SLONotificationRuleConditionType;
import io.harness.cvng.servicelevelobjective.entities.SLOHealthIndicator;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.mongodb.morphia.query.UpdateOperations;

@JsonTypeName("SLO")
@Data
@SuperBuilder
@NoArgsConstructor
@FieldNameConstants(innerTypeName = "SLONotificationRuleKeys")
@EqualsAndHashCode(callSuper = true)
public class SLONotificationRule extends NotificationRule {
  @NonNull List<SLONotificationRuleEntityCondition> conditions;

  @Override
  public NotificationRuleType getType() {
    return NotificationRuleType.SLO;
  }

  @Data
  @SuperBuilder
  public static class SLONotificationRuleEntityCondition {
    @NonNull SLONotificationRuleConditionType conditionType;
    @NonNull Double threshold;

    public boolean shouldSendNotification(SLOHealthIndicator sloHealthIndicator) {
      return this.getConditionType().equals(SLONotificationRuleConditionType.ERROR_BUDGET_REMAINING_PERCENTAGE)
          && sloHealthIndicator.getErrorBudgetRemainingPercentage() <= this.getThreshold();
    }
  }

  public static class SLONotificationRuleUpdatableEntity
      extends NotificationRuleUpdatableEntity<SLONotificationRule, SLONotificationRule> {
    @Override
    public void setUpdateOperations(
        UpdateOperations<SLONotificationRule> updateOperations, SLONotificationRule sloNotificationRule) {
      setCommonOperations(updateOperations, sloNotificationRule);
      updateOperations.set(SLONotificationRuleKeys.conditions, sloNotificationRule.getConditions());
    }
  }
}
