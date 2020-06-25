package software.wings.sm.states;

import static io.harness.data.structure.UUIDGenerator.generateUuid;
import static io.harness.waiter.OrchestrationNotifyEventListener.ORCHESTRATION;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import com.github.reinert.jjschema.Attributes;
import com.github.reinert.jjschema.SchemaIgnore;
import io.harness.beans.DelegateTask;
import io.harness.delegate.beans.TaskData;
import io.harness.tasks.Cd1SetupFields;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.mongodb.morphia.annotations.Transient;
import org.slf4j.Logger;
import software.wings.beans.PrometheusConfig;
import software.wings.beans.SettingAttribute;
import software.wings.beans.TaskType;
import software.wings.metrics.MetricType;
import software.wings.metrics.TimeSeriesMetricDefinition;
import software.wings.resources.PrometheusResource;
import software.wings.service.impl.analysis.AnalysisContext;
import software.wings.service.impl.analysis.DataCollectionCallback;
import software.wings.service.impl.analysis.TimeSeries;
import software.wings.service.impl.apm.APMDataCollectionInfo;
import software.wings.service.impl.apm.APMMetricInfo;
import software.wings.service.intfc.prometheus.PrometheusAnalysisService;
import software.wings.sm.ExecutionContext;
import software.wings.sm.StateType;
import software.wings.verification.VerificationStateAnalysisExecutionData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by rsingh on 2/6/18.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@FieldNameConstants(innerTypeName = "PrometheusStateKeys")
public class PrometheusState extends AbstractMetricAnalysisState {
  @Transient @SchemaIgnore public static final String TEST_HOST_NAME = "testNode";
  @Transient @SchemaIgnore public static final String CONTROL_HOST_NAME = "controlNode";
  @Inject private PrometheusAnalysisService prometheusAnalysisService;

  @Attributes(required = true, title = "Prometheus Server") private String analysisServerConfigId;

  private List<TimeSeries> timeSeriesToAnalyze;

  public PrometheusState(String name) {
    super(name, StateType.PROMETHEUS);
  }

  @Override
  @Attributes(required = true, title = "Include nodes from previous phases")
  public boolean getIncludePreviousPhaseNodes() {
    return includePreviousPhaseNodes;
  }

  @Override
  protected String triggerAnalysisDataCollection(ExecutionContext context, AnalysisContext analysisContext,
      VerificationStateAnalysisExecutionData executionData, Map<String, String> hosts) {
    String envId = getEnvId(context);
    String resolvedAnalysisServerConfigId =
        getResolvedConnectorId(context, PrometheusStateKeys.analysisServerConfigId, analysisServerConfigId);
    final SettingAttribute settingAttribute = settingsService.get(resolvedAnalysisServerConfigId);
    Preconditions.checkNotNull(
        settingAttribute, "No prometheus setting with id: " + resolvedAnalysisServerConfigId + " found");

    final PrometheusConfig prometheusConfig = (PrometheusConfig) settingAttribute.getValue();

    metricAnalysisService.saveMetricTemplates(context.getAppId(), StateType.PROMETHEUS,
        context.getStateExecutionInstanceId(), null, createMetricTemplates(timeSeriesToAnalyze));

    renderURLExpressions(context, timeSeriesToAnalyze);
    final Map<String, List<APMMetricInfo>> metricEndpoints =
        prometheusAnalysisService.apmMetricEndPointsFetchInfo(timeSeriesToAnalyze);
    final long dataCollectionStartTimeStamp = dataCollectionStartTimestampMillis();
    final APMDataCollectionInfo dataCollectionInfo =
        APMDataCollectionInfo.builder()
            .baseUrl(prometheusConfig.getUrl())
            .validationUrl(PrometheusConfig.VALIDATION_URL)
            .encryptedDataDetails(secretManager.getEncryptionDetails(
                prometheusConfig, context.getAppId(), context.getWorkflowExecutionId()))
            .hosts(hosts)
            .stateType(StateType.PROMETHEUS)
            .applicationId(context.getAppId())
            .stateExecutionId(context.getStateExecutionInstanceId())
            .workflowId(context.getWorkflowId())
            .workflowExecutionId(context.getWorkflowExecutionId())
            .serviceId(getPhaseServiceId(context))
            .startTime(dataCollectionStartTimeStamp)
            .dataCollectionMinute(0)
            .metricEndpoints(metricEndpoints)
            .accountId(appService.getAccountIdByAppId(context.getAppId()))
            .strategy(getComparisonStrategy())
            .dataCollectionTotalTime(Integer.parseInt(getTimeDuration()))
            .initialDelaySeconds(getDelaySeconds(initialAnalysisDelay))
            .build();

    String waitId = generateUuid();
    String infrastructureMappingId = context.fetchInfraMappingId();
    DelegateTask delegateTask =
        DelegateTask.builder()
            .accountId(appService.getAccountIdByAppId(context.getAppId()))
            .setupAbstraction(Cd1SetupFields.APP_ID_FIELD, context.getAppId())
            .waitId(waitId)
            .data(TaskData.builder()
                      .async(true)
                      .taskType(TaskType.APM_METRIC_DATA_COLLECTION_TASK.name())
                      .parameters(new Object[] {dataCollectionInfo})
                      .timeout(TimeUnit.MINUTES.toMillis(Integer.parseInt(getTimeDuration()) + 5))
                      .build())
            .setupAbstraction(Cd1SetupFields.ENV_ID_FIELD, envId)
            .setupAbstraction(Cd1SetupFields.INFRASTRUCTURE_MAPPING_ID_FIELD, infrastructureMappingId)
            .build();
    waitNotifyEngine.waitForAllOn(ORCHESTRATION,
        DataCollectionCallback.builder()
            .appId(context.getAppId())
            .stateExecutionId(context.getStateExecutionInstanceId())
            .executionData(executionData)
            .dataCollectionStartTime(dataCollectionStartTimeStamp)
            .dataCollectionEndTime(
                dataCollectionStartTimeStamp + TimeUnit.MINUTES.toMillis(Integer.parseInt(getTimeDuration())))
            .build(),
        waitId);
    return delegateService.queueTask(delegateTask);
  }

  @Override
  public Map<String, String> validateFields() {
    return PrometheusResource.validateTransactions(timeSeriesToAnalyze, false);
  }

  private void renderURLExpressions(ExecutionContext executionContext, List<TimeSeries> timeSeriesToAnalyze) {
    timeSeriesToAnalyze.forEach(
        timeSeries -> timeSeries.setUrl(executionContext.renderExpression(timeSeries.getUrl())));
  }

  public static Map<String, TimeSeriesMetricDefinition> createMetricTemplates(List<TimeSeries> timeSeriesToAnalyze) {
    Map<String, TimeSeriesMetricDefinition> rv = new HashMap<>();
    timeSeriesToAnalyze.forEach(timeSeries
        -> rv.put(timeSeries.getMetricName(),
            TimeSeriesMetricDefinition.builder()
                .metricName(timeSeries.getMetricName())
                .metricType(MetricType.valueOf(timeSeries.getMetricType()))
                .build()));
    return rv;
  }

  @Override
  public Logger getLogger() {
    return logger;
  }
}
