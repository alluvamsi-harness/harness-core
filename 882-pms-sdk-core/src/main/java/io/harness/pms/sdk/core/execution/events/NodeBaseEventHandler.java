package io.harness.pms.sdk.core.execution.events;

import io.harness.data.structure.CollectionUtils;
import io.harness.logging.AutoLogContext;
import io.harness.logging.AutoLogContext.OverrideBehavior;
import io.harness.metrics.ThreadAutoLogContext;
import io.harness.monitoring.EventMonitoringService;
import io.harness.monitoring.MonitoringInfo;
import io.harness.pms.contracts.ambiance.Ambiance;
import io.harness.pms.execution.utils.AmbianceUtils;
import io.harness.pms.gitsync.PmsGitSyncBranchContextGuard;
import io.harness.pms.gitsync.PmsGitSyncHelper;

import com.google.inject.Inject;
import com.google.protobuf.Message;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;

public abstract class NodeBaseEventHandler<T extends Message> {
  public static String LISTENER_END_METRIC = "%s_queue_time";
  public static String LISTENER_START_METRIC = "%s_time_in_queue";

  @Inject private PmsGitSyncHelper pmsGitSyncHelper;
  @Inject private EventMonitoringService eventMonitoringService;

  protected PmsGitSyncBranchContextGuard gitSyncContext(T event) {
    return pmsGitSyncHelper.createGitSyncBranchContextGuard(extractAmbiance(event), true);
  };

  @NonNull protected abstract Map<String, String> extraLogProperties(T event);

  protected abstract Ambiance extractAmbiance(T event);

  protected abstract Map<String, String> extractMetricContext(T message);

  protected abstract String getMetricPrefix(T message);

  public boolean handleEvent(T event, Map<String, String> metadataMap, long createdAt) {
    try (PmsGitSyncBranchContextGuard ignore1 = gitSyncContext(event); AutoLogContext ignore2 = autoLogContext(event)) {
      ThreadAutoLogContext metricContext =
          new ThreadAutoLogContext(extractMetricContext(event), OverrideBehavior.OVERRIDE_NESTS);
      MonitoringInfo monitoringInfo = MonitoringInfo.builder()
                                          .createdAt(createdAt)
                                          .metricPrefix(getMetricPrefix(event))
                                          .metricContext(metricContext)
                                          .build();
      eventMonitoringService.sendMetric(LISTENER_START_METRIC, monitoringInfo, metadataMap);
      boolean isSuccess = handleEventWithContext(event);
      eventMonitoringService.sendMetric(LISTENER_END_METRIC, monitoringInfo, metadataMap);
      return isSuccess;
    }
  }

  protected abstract boolean handleEventWithContext(T event);

  protected AutoLogContext autoLogContext(T event) {
    Map<String, String> logContext = new HashMap<>();
    logContext.putAll(AmbianceUtils.logContextMap(extractAmbiance(event)));
    logContext.putAll(CollectionUtils.emptyIfNull(extraLogProperties(event)));
    return new AutoLogContext(logContext, OverrideBehavior.OVERRIDE_NESTS);
  }
}
