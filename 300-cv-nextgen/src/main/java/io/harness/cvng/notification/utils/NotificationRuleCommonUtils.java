/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cvng.notification.utils;

import com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;

public class NotificationRuleCommonUtils {
  public static long getDurationInMillis(String duration) {
    long lookBackDurationInMillis = 0;
    if (duration != null) {
      Preconditions.checkState(!duration.isEmpty(), "duration can not be empty");
      if (duration.charAt(duration.length() - 1) != 'm') {
        throw new IllegalArgumentException("duration should end with m, ex: 5m, 10m etc.");
      }
      duration = duration.substring(0, duration.length() - 1);
      try {
        long lookBackDurationInLong = Long.parseLong(duration);
        lookBackDurationInMillis = TimeUnit.MINUTES.toMillis(lookBackDurationInLong);
      } catch (NumberFormatException numberFormatException) {
        throw new IllegalArgumentException(
            "can not parse duration please check format for duration., ex: 5m, 10m etc.", numberFormatException);
      }
    }
    return lookBackDurationInMillis;
  }

  public static String getDurationAsString(long duration) {
    return TimeUnit.MILLISECONDS.toMinutes(duration) + "m";
  }
}
