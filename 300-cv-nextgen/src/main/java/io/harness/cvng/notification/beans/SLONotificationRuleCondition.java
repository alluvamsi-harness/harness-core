/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cvng.notification.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SLONotificationRuleCondition extends NotificationRuleCondition {
  @NonNull SLONotificationRuleConditionType conditionType;
  @NonNull SLONotificationRuleConditionSpec spec;

  public enum SLONotificationRuleConditionType {
    @JsonProperty("ErrorBudgetRemainingPercentage") ERROR_BUDGET_REMAINING_PERCENTAGE,
    @JsonProperty("ErrorBudgetRemainingMinutes") ERROR_BUDGET_REMAINING_MINUTES,
    @JsonProperty("ErrorBudgetBurnRate") ERROR_BUDGET_BURN_RATE;
  }

  @Data
  @Builder
  public static class SLONotificationRuleConditionSpec {
    @NonNull Double threshold;
  }
}
