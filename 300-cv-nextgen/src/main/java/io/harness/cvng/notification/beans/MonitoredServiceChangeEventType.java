/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cvng.notification.beans;

import io.harness.cvng.beans.activity.ActivityType;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

public enum MonitoredServiceChangeEventType {
  // TODO: use ChangeCategory.java instead, once Alert is changed to Incident.
  @JsonProperty("Deployment")
  DEPLOYMENT(Arrays.asList(ActivityType.DEPLOYMENT, ActivityType.HARNESS_CD_CURRENT_GEN, ActivityType.HARNESS_CD)),
  @JsonProperty("Infrastructure") INFRASTRUCTURE(Arrays.asList(ActivityType.KUBERNETES, ActivityType.CONFIG)),
  @JsonProperty("Incident") INCIDENT(Arrays.asList(ActivityType.PAGER_DUTY));

  @Getter private List<ActivityType> activityTypes;

  MonitoredServiceChangeEventType(List<ActivityType> activityTypes) {
    this.activityTypes = activityTypes;
  }
}
