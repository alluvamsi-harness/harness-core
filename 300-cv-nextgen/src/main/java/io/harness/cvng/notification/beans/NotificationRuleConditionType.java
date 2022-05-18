/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cvng.notification.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum NotificationRuleConditionType {
  @JsonProperty("ErrorBudgetRemainingPercentage") ERROR_BUDGET_REMAINING_PERCENTAGE(NotificationRuleType.SLO),
  @JsonProperty("ErrorBudgetRemainingMinutes") ERROR_BUDGET_REMAINING_MINUTES(NotificationRuleType.SLO),
  @JsonProperty("ErrorBudgetBurnRate") ERROR_BUDGET_BURN_RATE(NotificationRuleType.SLO),
  @JsonProperty("ChangeImpact") CHANGE_IMPACT(NotificationRuleType.MONITORED_SERVICE),
  @JsonProperty("HealthScore") HEALTH_SCORE(NotificationRuleType.MONITORED_SERVICE),
  @JsonProperty("ChangeObserved") CHANGE_OBSERVED(NotificationRuleType.MONITORED_SERVICE);

  private final NotificationRuleType notificationRuleType;

  NotificationRuleConditionType(NotificationRuleType notificationRuleType) {
    this.notificationRuleType = notificationRuleType;
  }

  public NotificationRuleType getNotificationRuleType() {
    return this.notificationRuleType;
  }
}
