/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cvng.dashboard.services.impl;

import static io.harness.cvng.beans.DataSourceType.ERROR_TRACKING;

import io.harness.cvng.analysis.beans.LiveMonitoringLogAnalysisClusterDTO;
import io.harness.cvng.analysis.beans.LiveMonitoringLogAnalysisRadarChartClusterDTO;
import io.harness.cvng.analysis.entities.LogAnalysisCluster;
import io.harness.cvng.analysis.entities.LogAnalysisCluster.Frequency;
import io.harness.cvng.analysis.entities.LogAnalysisResult;
import io.harness.cvng.analysis.entities.LogAnalysisResult.AnalysisResult;
import io.harness.cvng.analysis.entities.LogAnalysisResult.LogAnalysisTag;
import io.harness.cvng.analysis.entities.LogAnalysisResult.RadarChartTag;
import io.harness.cvng.analysis.services.api.LogAnalysisService;
import io.harness.cvng.core.beans.params.MonitoredServiceParams;
import io.harness.cvng.core.beans.params.PageParams;
import io.harness.cvng.core.beans.params.TimeRangeParams;
import io.harness.cvng.core.beans.params.filterParams.LiveMonitoringLogAnalysisFilter;
import io.harness.cvng.core.beans.params.filterParams.MonitoredServiceLogAnalysisFilter;
import io.harness.cvng.core.entities.CVConfig;
import io.harness.cvng.core.services.api.CVConfigService;
import io.harness.cvng.core.services.api.VerificationTaskService;
import io.harness.cvng.dashboard.beans.AnalyzedLogDataDTO;
import io.harness.cvng.dashboard.beans.AnalyzedLogDataDTO.FrequencyDTO;
import io.harness.cvng.dashboard.beans.AnalyzedLogDataDTO.LogData;
import io.harness.cvng.dashboard.beans.AnalyzedRadarChartLogDataDTO;
import io.harness.cvng.dashboard.services.api.LogDashboardService;
import io.harness.cvng.utils.CVNGParallelExecutor;
import io.harness.ng.beans.PageResponse;
import io.harness.utils.PageUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogDashboardServiceImpl implements LogDashboardService {
  @Inject private LogAnalysisService logAnalysisService;
  @Inject private CVConfigService cvConfigService;
  @Inject private CVNGParallelExecutor cvngParallelExecutor;
  @Inject private VerificationTaskService verificationTaskService;

  @Override
  @Deprecated
  public PageResponse<AnalyzedLogDataDTO> getAllLogsData(MonitoredServiceParams monitoredServiceParams,
      TimeRangeParams timeRangeParams, LiveMonitoringLogAnalysisFilter liveMonitoringLogAnalysisFilter,
      PageParams pageParams) {
    List<CVConfig> configs = getCVConfigs(monitoredServiceParams, liveMonitoringLogAnalysisFilter);
    List<String> cvConfigIds = configs.stream().map(CVConfig::getUuid).collect(Collectors.toList());
    List<LogAnalysisTag> tags = liveMonitoringLogAnalysisFilter.filterByClusterTypes()
        ? liveMonitoringLogAnalysisFilter.getClusterTypes()
        : Arrays.asList(LogAnalysisTag.values());
    return getLogs(monitoredServiceParams.getAccountIdentifier(), monitoredServiceParams.getProjectIdentifier(),
        monitoredServiceParams.getOrgIdentifier(), monitoredServiceParams.getServiceIdentifier(),
        monitoredServiceParams.getEnvironmentIdentifier(), tags, timeRangeParams.getStartTime(),
        timeRangeParams.getEndTime(), cvConfigIds, pageParams.getPage(), pageParams.getSize());
  }

  @Override
  public PageResponse<AnalyzedRadarChartLogDataDTO> getAllRadarChartLogsData(
      MonitoredServiceParams monitoredServiceParams,
      MonitoredServiceLogAnalysisFilter monitoredServiceLogAnalysisFilter, PageParams pageParams) {
    TimeRangeParams timeRangeParams =
        TimeRangeParams.builder()
            .startTime(Instant.ofEpochMilli(monitoredServiceLogAnalysisFilter.getStartTimeMillis()))
            .endTime(Instant.ofEpochMilli(monitoredServiceLogAnalysisFilter.getEndTimeMillis()))
            .build();
    List<LogAnalysisTag> logAnalysisTagList = new ArrayList<>();
    if (!Objects.isNull(monitoredServiceLogAnalysisFilter.getClusterTypes())) {
      for (RadarChartTag radarChartTag : monitoredServiceLogAnalysisFilter.getClusterTypes()) {
        logAnalysisTagList.add(LogAnalysisResult.RadarChartTagToLogAnalysisTag(radarChartTag));
      }
    }
    LiveMonitoringLogAnalysisFilter liveMonitoringLogAnalysisFilter =
        LiveMonitoringLogAnalysisFilter.builder()
            .healthSourceIdentifiers(monitoredServiceLogAnalysisFilter.getHealthSourceIdentifiers())
            .clusterTypes(logAnalysisTagList)
            .build();

    List<CVConfig> configs = getCVConfigs(monitoredServiceParams, liveMonitoringLogAnalysisFilter);
    List<String> cvConfigIds = configs.stream().map(CVConfig::getUuid).collect(Collectors.toList());
    List<LogAnalysisTag> tags = liveMonitoringLogAnalysisFilter.filterByClusterTypes()
        ? liveMonitoringLogAnalysisFilter.getClusterTypes()
        : Arrays.asList(LogAnalysisTag.values());
    return getRadarChartLogs(monitoredServiceParams.getAccountIdentifier(),
        monitoredServiceParams.getProjectIdentifier(), monitoredServiceParams.getOrgIdentifier(),
        monitoredServiceParams.getServiceIdentifier(), monitoredServiceParams.getEnvironmentIdentifier(), tags,
        timeRangeParams.getStartTime(), timeRangeParams.getEndTime(), cvConfigIds, pageParams.getPage(),
        pageParams.getSize(), monitoredServiceLogAnalysisFilter);
  }

  @Override
  @Deprecated
  public List<LiveMonitoringLogAnalysisClusterDTO> getLogAnalysisClusters(MonitoredServiceParams monitoredServiceParams,
      TimeRangeParams timeRangeParams, LiveMonitoringLogAnalysisFilter liveMonitoringLogAnalysisFilter) {
    List<LiveMonitoringLogAnalysisClusterDTO> liveMonitoringLogAnalysisClusterDTOS = new ArrayList<>();
    List<String> cvConfigIds = getCVConfigs(monitoredServiceParams, liveMonitoringLogAnalysisFilter)
                                   .stream()
                                   .map(CVConfig::getUuid)
                                   .collect(Collectors.toList());
    List<LogAnalysisTag> tags = liveMonitoringLogAnalysisFilter.filterByClusterTypes()
        ? liveMonitoringLogAnalysisFilter.getClusterTypes()
        : Arrays.asList(LogAnalysisTag.values());

    cvConfigIds.forEach(cvConfigId -> {
      List<AnalysisResult> logAnalysisResults =
          getAnalysisResultForCvConfigId(cvConfigId, timeRangeParams.getStartTime(), timeRangeParams.getEndTime());

      Map<Long, LogAnalysisTag> labelTagMap = new HashMap<>();
      logAnalysisResults.forEach(result -> {
        Long label = result.getLabel();
        if (!labelTagMap.containsKey(label) || result.getTag().isMoreSevereThan(labelTagMap.get(label))) {
          labelTagMap.put(label, result.getTag());
        }
      });

      String verificationTaskId = verificationTaskService.getServiceGuardVerificationTaskId(
          monitoredServiceParams.getAccountIdentifier(), cvConfigId);
      List<LogAnalysisCluster> clusters =
          logAnalysisService.getAnalysisClusters(verificationTaskId, labelTagMap.keySet());
      clusters.forEach(logAnalysisCluster -> {
        liveMonitoringLogAnalysisClusterDTOS.add(LiveMonitoringLogAnalysisClusterDTO.builder()
                                                     .x(logAnalysisCluster.getX())
                                                     .y(logAnalysisCluster.getY())
                                                     .tag(labelTagMap.get(logAnalysisCluster.getLabel()))
                                                     .text(logAnalysisCluster.getText())
                                                     .build());
      });
    });
    return liveMonitoringLogAnalysisClusterDTOS.stream()
        .filter(cluster -> tags.contains(cluster.getTag()))
        .collect(Collectors.toList());
  }

  @Override
  public List<LiveMonitoringLogAnalysisRadarChartClusterDTO> getLogAnalysisRadarChartClusters(
      MonitoredServiceParams monitoredServiceParams,
      MonitoredServiceLogAnalysisFilter monitoredServiceLogAnalysisFilter) {
    List<LiveMonitoringLogAnalysisRadarChartClusterDTO> liveMonitoringLogAnalysisRadarChartClusterDTOS =
        new ArrayList<>();
    TimeRangeParams timeRangeParams =
        TimeRangeParams.builder()
            .startTime(Instant.ofEpochMilli(monitoredServiceLogAnalysisFilter.getStartTimeMillis()))
            .endTime(Instant.ofEpochMilli(monitoredServiceLogAnalysisFilter.getEndTimeMillis()))
            .build();
    List<LogAnalysisTag> logAnalysisTagList = new ArrayList<>();
    if (!Objects.isNull(monitoredServiceLogAnalysisFilter.getClusterTypes())) {
      for (RadarChartTag radarChartTag : monitoredServiceLogAnalysisFilter.getClusterTypes()) {
        logAnalysisTagList.add(LogAnalysisResult.RadarChartTagToLogAnalysisTag(radarChartTag));
      }
    }
    LiveMonitoringLogAnalysisFilter liveMonitoringLogAnalysisFilter =
        LiveMonitoringLogAnalysisFilter.builder()
            .healthSourceIdentifiers(monitoredServiceLogAnalysisFilter.getHealthSourceIdentifiers())
            .clusterTypes(logAnalysisTagList)
            .build();

    List<String> cvConfigIds = getCVConfigs(monitoredServiceParams, liveMonitoringLogAnalysisFilter)
                                   .stream()
                                   .map(CVConfig::getUuid)
                                   .collect(Collectors.toList());
    List<LogAnalysisTag> tags = liveMonitoringLogAnalysisFilter.filterByClusterTypes()
        ? liveMonitoringLogAnalysisFilter.getClusterTypes()
        : Arrays.asList(LogAnalysisTag.values());

    cvConfigIds.forEach(cvConfigId -> {
      List<AnalysisResult> logAnalysisResults =
          getAnalysisResultForCvConfigId(cvConfigId, timeRangeParams.getStartTime(), timeRangeParams.getEndTime());

      Map<Long, LogAnalysisTag> labelTagMap = new HashMap<>();
      logAnalysisResults.forEach(result -> {
        Long label = result.getLabel();
        if (!labelTagMap.containsKey(label) || result.getTag().isMoreSevereThan(labelTagMap.get(label))) {
          labelTagMap.put(label, result.getTag());
        }
      });

      String verificationTaskId = verificationTaskService.getServiceGuardVerificationTaskId(
          monitoredServiceParams.getAccountIdentifier(), cvConfigId);
      List<LogAnalysisCluster> clusters =
          logAnalysisService.getAnalysisClusters(verificationTaskId, labelTagMap.keySet());
      clusters.forEach(logAnalysisCluster -> {
        liveMonitoringLogAnalysisRadarChartClusterDTOS.add(
            LiveMonitoringLogAnalysisRadarChartClusterDTO.builder()
                .clusterId(UUID.nameUUIDFromBytes(
                                   (verificationTaskId + ":" + logAnalysisCluster.getLabel()).getBytes(Charsets.UTF_8))
                               .toString())
                .clusterType(
                    LogAnalysisResult.LogAnalysisTagToRadarChartTag(labelTagMap.get(logAnalysisCluster.getLabel())))
                .text(logAnalysisCluster.getText())
                .build());
      });
    });

    List<LiveMonitoringLogAnalysisRadarChartClusterDTO> sortedList =
        liveMonitoringLogAnalysisRadarChartClusterDTOS.stream()
            .filter(cluster -> tags.contains(LogAnalysisResult.RadarChartTagToLogAnalysisTag(cluster.getClusterType())))
            .sorted(Comparator.comparing(LiveMonitoringLogAnalysisRadarChartClusterDTO::getClusterId))
            .collect(Collectors.toList());

    if (monitoredServiceLogAnalysisFilter.hasClusterIdFilter()) {
      sortedList = sortedList.stream()
                       .filter(liveMonitoringLogAnalysisRadarChartClusterDTO
                           -> liveMonitoringLogAnalysisRadarChartClusterDTO.getClusterId().equals(
                               monitoredServiceLogAnalysisFilter.getClusterId()))
                       .collect(Collectors.toList());
      Preconditions.checkState(sortedList.size() <= 1, "clusterId filter should result in one or zero cluster");
    }

    if (sortedList.size() > 0) {
      setAngleAndRadiusForRadarChartCluster(
          sortedList.stream()
              .filter(liveMonitoringLogAnalysisRadarChartClusterDTO
                  -> tags.contains(LogAnalysisResult.RadarChartTagToLogAnalysisTag(
                      liveMonitoringLogAnalysisRadarChartClusterDTO.getClusterType())))
              .collect(Collectors.toList()));
    }

    return filterClusterByAngle(sortedList, monitoredServiceLogAnalysisFilter);
  }

  private void setAngleAndRadiusForRadarChartCluster(
      List<LiveMonitoringLogAnalysisRadarChartClusterDTO> liveMonitoringLogAnalysisRadarChartClusterDTOS) {
    int totalSize = liveMonitoringLogAnalysisRadarChartClusterDTOS.size();
    Preconditions.checkArgument(totalSize != 0, "Radar CHart List size cannot be 0 for the angle calculation");
    double angleDifference = (double) 360 / totalSize;
    double angle = 0;
    Random random = new Random(123456789);

    for (int i = 0; i < liveMonitoringLogAnalysisRadarChartClusterDTOS.size(); i++) {
      LiveMonitoringLogAnalysisRadarChartClusterDTO liveMonitoringLogAnalysisRadarChartClusterDTO =
          liveMonitoringLogAnalysisRadarChartClusterDTOS.get(i);
      liveMonitoringLogAnalysisRadarChartClusterDTO.setAngle(angle);
      liveMonitoringLogAnalysisRadarChartClusterDTO.setRadius(
          getRandomRadiusInExpectedRange(liveMonitoringLogAnalysisRadarChartClusterDTO.getClusterType(), random));
      angle += angleDifference;
      angle = Math.min(angle, 360);
    }
  }

  private double getRandomRadiusInExpectedRange(RadarChartTag tag, Random random) {
    if (tag.equals(LogAnalysisTag.KNOWN)) {
      return random.nextDouble() * 0.5 + 0.5;
    } else {
      return random.nextDouble() + 1;
    }
  }

  private List<LiveMonitoringLogAnalysisRadarChartClusterDTO> filterClusterByAngle(
      List<LiveMonitoringLogAnalysisRadarChartClusterDTO> liveMonitoringLogAnalysisRadarChartClusterDTOList,
      MonitoredServiceLogAnalysisFilter monitoredServiceLogAnalysisFilter) {
    List<LiveMonitoringLogAnalysisRadarChartClusterDTO> filteredLogs =
        liveMonitoringLogAnalysisRadarChartClusterDTOList;
    if (monitoredServiceLogAnalysisFilter.filterByAngle()) {
      Preconditions.checkArgument(monitoredServiceLogAnalysisFilter.getMinAngle() >= 0
              && monitoredServiceLogAnalysisFilter.getMaxAngle() <= 360,
          "Angle filter range should be between 0 and 360");
      filteredLogs = liveMonitoringLogAnalysisRadarChartClusterDTOList.stream()
                         .filter(result
                             -> result.getAngle() >= monitoredServiceLogAnalysisFilter.getMinAngle()
                                 && result.getAngle() <= monitoredServiceLogAnalysisFilter.getMaxAngle())
                         .collect(Collectors.toList());
    }
    return filteredLogs;
  }

  private List<CVConfig> getCVConfigs(
      MonitoredServiceParams monitoredServiceParams, LiveMonitoringLogAnalysisFilter liveMonitoringLogAnalysisFilter) {
    List<CVConfig> configs;
    if (liveMonitoringLogAnalysisFilter.filterByHealthSourceIdentifiers()) {
      configs =
          cvConfigService.list(monitoredServiceParams, liveMonitoringLogAnalysisFilter.getHealthSourceIdentifiers());
    } else {
      configs = cvConfigService.list(monitoredServiceParams);
    }

    // Limit to NOT include Error Tracking configs
    configs = configs.stream().filter(config -> !ERROR_TRACKING.equals(config.getType())).collect(Collectors.toList());

    return configs;
  }

  private PageResponse<AnalyzedLogDataDTO> getLogs(String accountId, String projectIdentifier, String orgIdentifier,
      String serviceIdentifier, String environmentIdentifer, List<LogAnalysisTag> tags, Instant startTime,
      Instant endTime, List<String> cvConfigIds, int page, int size) {
    // for each cvConfigId, get the list of unknown and unexpected analysis results.
    // Total number of calls to DB = total number of cvConfigs that are part of this category for this service+env with
    // type as LOG
    Map<String, List<AnalysisResult>> cvConfigAnalysisResultMap = new ConcurrentHashMap<>();

    List<LogData> logDataToBeReturned = Collections.synchronizedList(new ArrayList<>());
    List<Callable<Map<String, List<AnalysisResult>>>> callables = new ArrayList<>();
    cvConfigIds.forEach(cvConfigId -> {
      callables.add(() -> {
        Map<String, List<AnalysisResult>> configResult = new HashMap<>();
        configResult.put(cvConfigId, getAnalysisResultForCvConfigId(cvConfigId, startTime, endTime));
        return configResult;
      });
    });

    List<Map<String, List<AnalysisResult>>> allResults = cvngParallelExecutor.executeParallel(callables);
    allResults.forEach(result -> cvConfigAnalysisResultMap.putAll(result));

    // for each cvConfigId, make a call to get the labels/texts
    List<Callable<List<LogData>>> logDataCallables = new ArrayList<>();
    cvConfigAnalysisResultMap.keySet().forEach(cvConfigId -> {
      logDataCallables.add(() -> {
        List<AnalysisResult> analysisResults = cvConfigAnalysisResultMap.get(cvConfigId);
        Set<Long> labels = analysisResults.stream().map(AnalysisResult::getLabel).collect(Collectors.toSet());
        String verificationTaskId = verificationTaskService.getServiceGuardVerificationTaskId(accountId, cvConfigId);
        List<LogAnalysisCluster> clusters = logAnalysisService.getAnalysisClusters(verificationTaskId, labels);
        return mergeClusterWithResults(analysisResults, clusters, startTime, endTime);
      });
    });

    List<List<LogData>> logDataResults = cvngParallelExecutor.executeParallel(logDataCallables);
    logDataResults.forEach(result -> logDataToBeReturned.addAll(result));

    List<AnalyzedLogDataDTO> sortedList = new ArrayList<>();
    // create the sorted set first. Then form the page response.
    logDataToBeReturned.stream()
        .filter(logData -> tags.contains(logData.getTag()))
        .forEach(logData
            -> sortedList.add(AnalyzedLogDataDTO.builder()
                                  .projectIdentifier(projectIdentifier)
                                  .orgIdentifier(orgIdentifier)
                                  .serviceIdentifier(serviceIdentifier)
                                  .environmentIdentifier(environmentIdentifer)
                                  .logData(logData)
                                  .build()));
    Collections.sort(sortedList);
    return PageUtils.offsetAndLimit(sortedList, page, size);
  }

  private PageResponse<AnalyzedRadarChartLogDataDTO> getRadarChartLogs(String accountId, String projectIdentifier,
      String orgIdentifier, String serviceIdentifier, String environmentIdentifer, List<LogAnalysisTag> tags,
      Instant startTime, Instant endTime, List<String> cvConfigIds, int page, int size,
      MonitoredServiceLogAnalysisFilter monitoredServiceLogAnalysisFilter) {
    // for each cvConfigId, get the list of unknown and unexpected analysis results.
    // Total number of calls to DB = total number of cvConfigs that are part of this category for this service+env with
    // type as LOG
    Map<String, List<AnalysisResult>> cvConfigAnalysisResultMap = new ConcurrentHashMap<>();

    List<AnalyzedRadarChartLogDataDTO.RadarChartLogData> logDataToBeReturned =
        Collections.synchronizedList(new ArrayList<>());
    List<Callable<Map<String, List<AnalysisResult>>>> callables = new ArrayList<>();
    cvConfigIds.forEach(cvConfigId -> {
      callables.add(() -> {
        Map<String, List<AnalysisResult>> configResult = new HashMap<>();
        configResult.put(cvConfigId, getAnalysisResultForCvConfigId(cvConfigId, startTime, endTime));
        return configResult;
      });
    });

    List<Map<String, List<AnalysisResult>>> allResults = cvngParallelExecutor.executeParallel(callables);
    allResults.forEach(result -> cvConfigAnalysisResultMap.putAll(result));

    // for each cvConfigId, make a call to get the labels/texts
    List<Callable<List<AnalyzedRadarChartLogDataDTO.RadarChartLogData>>> logDataCallables = new ArrayList<>();
    cvConfigAnalysisResultMap.keySet().forEach(cvConfigId -> {
      logDataCallables.add(() -> {
        List<AnalysisResult> analysisResults = cvConfigAnalysisResultMap.get(cvConfigId);
        Set<Long> labels = analysisResults.stream().map(AnalysisResult::getLabel).collect(Collectors.toSet());
        String verificationTaskId = verificationTaskService.getServiceGuardVerificationTaskId(accountId, cvConfigId);
        List<LogAnalysisCluster> clusters = logAnalysisService.getAnalysisClusters(verificationTaskId, labels);
        return mergeClusterWithRadarChartResults(analysisResults, clusters, startTime, endTime);
      });
    });

    List<List<AnalyzedRadarChartLogDataDTO.RadarChartLogData>> logDataResults =
        cvngParallelExecutor.executeParallel(logDataCallables);
    logDataResults.forEach(result -> logDataToBeReturned.addAll(result));

    List<AnalyzedRadarChartLogDataDTO> sortedList = new ArrayList<>();
    // create the sorted set first. Then form the page response.
    logDataToBeReturned.stream()
        .filter(logData -> tags.contains(LogAnalysisResult.RadarChartTagToLogAnalysisTag(logData.getClusterType())))
        .forEach(logData
            -> sortedList.add(AnalyzedRadarChartLogDataDTO.builder()
                                  .projectIdentifier(projectIdentifier)
                                  .orgIdentifier(orgIdentifier)
                                  .serviceIdentifier(serviceIdentifier)
                                  .environmentIdentifier(environmentIdentifer)
                                  .logData(logData)
                                  .build()));
    List<AnalyzedRadarChartLogDataDTO> sortedAnalyzedRadarChartLogDataDTOS =
        sortedList.stream()
            .sorted(Comparator.comparing(x -> x.getLogData().getClusterId()))
            .collect(Collectors.toList());

    if (monitoredServiceLogAnalysisFilter.hasClusterIdFilter()) {
      sortedAnalyzedRadarChartLogDataDTOS = sortedAnalyzedRadarChartLogDataDTOS.stream()
                                                .filter(analyzedRadarChartLogDataDTO
                                                    -> analyzedRadarChartLogDataDTO.getLogData().getClusterId().equals(
                                                        monitoredServiceLogAnalysisFilter.getClusterId()))
                                                .collect(Collectors.toList());
      Preconditions.checkState(
          sortedAnalyzedRadarChartLogDataDTOS.size() <= 1, "clusterId filter should result in one or zero cluster");
    }

    if (sortedAnalyzedRadarChartLogDataDTOS.size() > 0) {
      setAngleAndRadiusForRadarChartData(sortedAnalyzedRadarChartLogDataDTOS.stream()
                                             .filter(analyzedRadarChartLogDataDTO
                                                 -> tags.contains(LogAnalysisResult.RadarChartTagToLogAnalysisTag(
                                                     analyzedRadarChartLogDataDTO.getLogData().getClusterType())))
                                             .collect(Collectors.toList()));
    }

    sortedAnalyzedRadarChartLogDataDTOS =
        filterDataByAngle(sortedAnalyzedRadarChartLogDataDTOS, monitoredServiceLogAnalysisFilter);
    return PageUtils.offsetAndLimit(sortedAnalyzedRadarChartLogDataDTOS, page, size);
  }

  private void setAngleAndRadiusForRadarChartData(List<AnalyzedRadarChartLogDataDTO> analyzedRadarChartLogDataDTOList) {
    int totalSize = analyzedRadarChartLogDataDTOList.size();
    Preconditions.checkArgument(totalSize != 0, "Radar CHart List size cannot be 0 for the angle calculation");
    double angleDifference = (double) 360 / totalSize;
    double angle = 0;
    Random random = new Random(123456789);

    for (int i = 0; i < analyzedRadarChartLogDataDTOList.size(); i++) {
      AnalyzedRadarChartLogDataDTO analyzedRadarChartLogDataDTO = analyzedRadarChartLogDataDTOList.get(i);
      analyzedRadarChartLogDataDTO.getLogData().setAngle(angle);
      analyzedRadarChartLogDataDTO.getLogData().setRadius(
          getRandomRadiusInExpectedRange(analyzedRadarChartLogDataDTO.getLogData().getClusterType(), random));
      angle += angleDifference;
      angle = Math.min(angle, 360);
    }
  }

  private List<AnalyzedRadarChartLogDataDTO> filterDataByAngle(
      List<AnalyzedRadarChartLogDataDTO> analyzedRadarChartLogDataDTOList,
      MonitoredServiceLogAnalysisFilter monitoredServiceLogAnalysisFilter) {
    List<AnalyzedRadarChartLogDataDTO> filteredLogs = analyzedRadarChartLogDataDTOList;
    if (monitoredServiceLogAnalysisFilter.filterByAngle()) {
      Preconditions.checkArgument(monitoredServiceLogAnalysisFilter.getMinAngle() >= 0
              && monitoredServiceLogAnalysisFilter.getMaxAngle() <= 360,
          "Angle filter range should be between 0 and 360");
      filteredLogs = analyzedRadarChartLogDataDTOList.stream()
                         .filter(result
                             -> result.getLogData().getAngle() >= monitoredServiceLogAnalysisFilter.getMinAngle()
                                 && result.getLogData().getAngle() <= monitoredServiceLogAnalysisFilter.getMaxAngle())
                         .collect(Collectors.toList());
    }
    return filteredLogs;
  }

  private List<AnalyzedRadarChartLogDataDTO.RadarChartLogData> mergeClusterWithRadarChartResults(
      List<AnalysisResult> analysisResults, List<LogAnalysisCluster> analysisClusters, Instant start, Instant end) {
    Map<Long, AnalysisResult> labelTagMap = new HashMap<>();

    analysisResults.forEach(result -> {
      Long label = result.getLabel();
      if (!labelTagMap.containsKey(label) || result.getTag().isMoreSevereThan(labelTagMap.get(label).getTag())) {
        labelTagMap.put(label, result);
      }
    });

    List<AnalyzedRadarChartLogDataDTO.RadarChartLogData> logDataList = new ArrayList<>();

    analysisClusters.forEach(cluster -> {
      Map<Long, Integer> trendMap =
          cluster.getFrequencyTrend().stream().collect(Collectors.toMap(Frequency::getTimestamp, Frequency::getCount));

      // filter and keep only those within the timerange we want.
      trendMap = trendMap.entrySet()
                     .stream()
                     .filter(e
                         -> e.getKey() >= TimeUnit.MILLISECONDS.toMinutes(start.toEpochMilli())
                             && e.getKey() <= TimeUnit.MILLISECONDS.toMinutes(end.toEpochMilli()))
                     .collect(Collectors.toMap(x -> TimeUnit.MINUTES.toMillis(x.getKey()), x -> x.getValue()));

      List<FrequencyDTO> frequencies = new ArrayList<>();

      trendMap.forEach(
          (timestamp, count) -> frequencies.add(FrequencyDTO.builder().timestamp(timestamp).count(count).build()));

      AnalysisResult analysisResult = labelTagMap.get(cluster.getLabel());

      // TODO:going forward this if condition needs to be removed as we are saving high riskScore for unknown and
      // unexpected tags in the db itself.
      if (LogAnalysisTag.getAnomalousTags().contains(analysisResult.getTag())) {
        analysisResult.setRiskScore(1.0);
      }

      AnalyzedRadarChartLogDataDTO.RadarChartLogData data =
          AnalyzedRadarChartLogDataDTO.RadarChartLogData.builder()
              .text(cluster.getText())
              .label(cluster.getLabel())
              .clusterId(UUID.nameUUIDFromBytes(
                                 (cluster.getVerificationTaskId() + ":" + cluster.getLabel()).getBytes(Charsets.UTF_8))
                             .toString())
              .count(trendMap.values().stream().collect(Collectors.summingInt(Integer::intValue)))
              .trend(frequencies)
              .clusterType(LogAnalysisResult.LogAnalysisTagToRadarChartTag(analysisResult.getTag()))
              .riskScore(analysisResult.getRiskScore())
              .riskStatus(analysisResult.getRisk())
              .build();
      logDataList.add(data);
    });
    return logDataList;
  }

  private List<AnalysisResult> getAnalysisResultForCvConfigId(String cvConfigId, Instant startTime, Instant endTime) {
    List<LogAnalysisResult> analysisResults = logAnalysisService.getAnalysisResults(cvConfigId, startTime, endTime);
    return analysisResults.stream()
        .map(LogAnalysisResult::getLogAnalysisResults)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private List<LogData> mergeClusterWithResults(
      List<AnalysisResult> analysisResults, List<LogAnalysisCluster> analysisClusters, Instant start, Instant end) {
    Map<Long, AnalysisResult> labelTagMap = new HashMap<>();

    analysisResults.forEach(result -> {
      Long label = result.getLabel();
      if (!labelTagMap.containsKey(label) || result.getTag().isMoreSevereThan(labelTagMap.get(label).getTag())) {
        labelTagMap.put(label, result);
      }
    });

    List<LogData> logDataList = new ArrayList<>();

    analysisClusters.forEach(cluster -> {
      Map<Long, Integer> trendMap =
          cluster.getFrequencyTrend().stream().collect(Collectors.toMap(Frequency::getTimestamp, Frequency::getCount));

      // filter and keep only those within the timerange we want.
      trendMap = trendMap.entrySet()
                     .stream()
                     .filter(e
                         -> e.getKey() >= TimeUnit.MILLISECONDS.toMinutes(start.toEpochMilli())
                             && e.getKey() <= TimeUnit.MILLISECONDS.toMinutes(end.toEpochMilli()))
                     .collect(Collectors.toMap(x -> TimeUnit.MINUTES.toMillis(x.getKey()), x -> x.getValue()));

      List<FrequencyDTO> frequencies = new ArrayList<>();

      trendMap.forEach(
          (timestamp, count) -> frequencies.add(FrequencyDTO.builder().timestamp(timestamp).count(count).build()));

      AnalysisResult analysisResult = labelTagMap.get(cluster.getLabel());

      // TODO:going forward this if condition needs to be removed as we are saving high riskScore for unknown and
      // unexpected tags in the db itself.
      if (LogAnalysisTag.getAnomalousTags().contains(analysisResult.getTag())) {
        analysisResult.setRiskScore(1.0);
      }
      LogData data = LogData.builder()
                         .text(cluster.getText())
                         .label(cluster.getLabel())
                         .count(trendMap.values().stream().collect(Collectors.summingInt(Integer::intValue)))
                         .trend(frequencies)
                         .tag(analysisResult.getTag())
                         .riskScore(analysisResult.getRiskScore())
                         .riskStatus(analysisResult.getRisk())
                         .build();
      logDataList.add(data);
    });
    return logDataList;
  }
}
