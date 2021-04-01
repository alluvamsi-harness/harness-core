package io.harness.batch.processing.tasklet;

import static io.harness.ccm.cluster.entities.K8sWorkload.encodeDotsInKey;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.batch.processing.ccm.CCMJobConstants;
import io.harness.batch.processing.ccm.ClusterType;
import io.harness.batch.processing.ccm.InstanceCategory;
import io.harness.batch.processing.ccm.InstanceInfo;
import io.harness.batch.processing.config.BatchMainConfig;
import io.harness.batch.processing.dao.intfc.PublishedMessageDao;
import io.harness.batch.processing.pricing.data.CloudProvider;
import io.harness.batch.processing.service.intfc.CloudProviderService;
import io.harness.batch.processing.service.intfc.InstanceDataBulkWriteService;
import io.harness.batch.processing.service.intfc.InstanceDataService;
import io.harness.batch.processing.service.intfc.InstanceResourceService;
import io.harness.batch.processing.tasklet.reader.PublishedMessageReader;
import io.harness.batch.processing.tasklet.util.InstanceMetaDataUtils;
import io.harness.batch.processing.tasklet.util.K8sResourceUtils;
import io.harness.batch.processing.writer.constants.EventTypeConstants;
import io.harness.batch.processing.writer.constants.InstanceMetaDataConstants;
import io.harness.batch.processing.writer.constants.K8sCCMConstants;
import io.harness.ccm.commons.beans.InstanceState;
import io.harness.ccm.commons.beans.InstanceType;
import io.harness.ccm.commons.beans.Resource;
import io.harness.event.grpc.PublishedMessage;
import io.harness.grpc.utils.HTimestamps;
import io.harness.perpetualtask.k8s.watch.NodeInfo;

import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

@OwnedBy(HarnessTeam.CE)
@Slf4j
public class K8sNodeInfoTasklet implements Tasklet {
  private JobParameters parameters;
  @Autowired private BatchMainConfig config;
  @Autowired private InstanceDataService instanceDataService;
  @Autowired private PublishedMessageDao publishedMessageDao;
  @Autowired private CloudProviderService cloudProviderService;
  @Autowired private InstanceResourceService instanceResourceService;
  @Autowired private InstanceDataBulkWriteService instanceDataBulkWriteService;

  private static final String AWS_SPOT_INSTANCE = "spot";
  private static final String AZURE_SPOT_INSTANCE = "spot";
  private static final boolean UPDATE_OLD_NODE_DATA = false;

  private static final int VM_INDEX = 2;
  private static final String VM_INDEX_VALUE = "virtualMachines";
  private static final int VMSS_INDEX = 4;
  private static final String VMSS_INDEX_VALUE = "virtualMachineScaleSets";
  private static final int SUBSCRIPTION_INDEX = 3;
  private static final String SUBSCRIPTION_INDEX_VALUE = "subscriptions";
  private static final int RESOURCE_GROUP_INDEX = 5;
  private static final String RESOURCE_GROUP_INDEX_VALUE = "resourceGroups";

  @Override
  public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
    parameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
    Long startTime = CCMJobConstants.getFieldLongValueFromJobParams(parameters, CCMJobConstants.JOB_START_DATE);
    Long endTime = CCMJobConstants.getFieldLongValueFromJobParams(parameters, CCMJobConstants.JOB_END_DATE);
    String accountId = parameters.getString(CCMJobConstants.ACCOUNT_ID);
    int batchSize = config.getBatchQueryConfig().getQueryBatchSize();

    String messageType = EventTypeConstants.K8S_NODE_INFO;
    PublishedMessageReader publishedMessageReader =
        new PublishedMessageReader(publishedMessageDao, accountId, messageType, startTime, endTime, batchSize);
    List<PublishedMessage> publishedMessageList;
    do {
      publishedMessageList = publishedMessageReader.getNext();
      List<InstanceInfo> instanceInfoList =
          publishedMessageList.stream()
              .map(this::processNodeInfoMessage)
              .filter(instanceInfo -> null != instanceInfo.getAccountId())
              .filter(
                  instanceInfo -> instanceInfo.getMetaData().containsKey(InstanceMetaDataConstants.INSTANCE_CATEGORY))
              .collect(Collectors.toList());

      instanceDataBulkWriteService.updateList(instanceInfoList);
    } while (publishedMessageList.size() == batchSize);
    return null;
  }

  public InstanceInfo processNodeInfoMessage(PublishedMessage publishedMessage) {
    try {
      return process(publishedMessage);
    } catch (Exception ex) {
      log.error("K8sNodeInfoTasklet Exception ", ex);
    }
    return InstanceInfo.builder().metaData(Collections.emptyMap()).build();
  }

  public InstanceInfo process(PublishedMessage publishedMessage) {
    NodeInfo nodeInfo = (NodeInfo) publishedMessage.getMessage();
    String accountId = publishedMessage.getAccountId();
    String clusterId = nodeInfo.getClusterId();
    String nodeUid = nodeInfo.getNodeUid();

    Map<String, String> labelsMap = nodeInfo.getLabelsMap();
    Map<String, String> metaData = new HashMap<>();
    CloudProvider k8SCloudProvider =
        cloudProviderService.getK8SCloudProvider(nodeInfo.getCloudProviderId(), nodeInfo.getProviderId());
    String cloudProviderInstanceId = getCloudProviderInstanceId(nodeInfo.getProviderId(), k8SCloudProvider);
    if (CloudProvider.UNKNOWN == k8SCloudProvider) {
      return InstanceInfo.builder().metaData(metaData).build();
    }
    metaData.put(InstanceMetaDataConstants.CLOUD_PROVIDER, k8SCloudProvider.name());
    metaData.put(InstanceMetaDataConstants.REGION, labelsMap.get(K8sCCMConstants.REGION));
    metaData.put(InstanceMetaDataConstants.ZONE, labelsMap.get(K8sCCMConstants.ZONE));
    metaData.put(InstanceMetaDataConstants.CLUSTER_TYPE, ClusterType.K8S.name());
    metaData.put(InstanceMetaDataConstants.NODE_NAME, nodeInfo.getNodeName());
    metaData.put(InstanceMetaDataConstants.INSTANCE_FAMILY, labelsMap.get(K8sCCMConstants.INSTANCE_FAMILY));
    metaData.put(InstanceMetaDataConstants.OPERATING_SYSTEM, labelsMap.get(K8sCCMConstants.OPERATING_SYSTEM));
    metaData.put(InstanceMetaDataConstants.NODE_UID, nodeUid);
    metaData.put(InstanceMetaDataConstants.CLOUD_PROVIDER_INSTANCE_ID, cloudProviderInstanceId);
    metaData.put(InstanceMetaDataConstants.INSTANCE_CATEGORY, getInstanceCategory(k8SCloudProvider, labelsMap).name());
    metaData.put(InstanceMetaDataConstants.POD_CAPACITY,
        String.valueOf(K8sResourceUtils.getPodCapacity(nodeInfo.getAllocatableResourceMap())));
    if (null != labelsMap.get(K8sCCMConstants.COMPUTE_TYPE)) {
      metaData.put(InstanceMetaDataConstants.COMPUTE_TYPE, labelsMap.get(K8sCCMConstants.COMPUTE_TYPE));
    }
    if (k8SCloudProvider == CloudProvider.AZURE) {
      // Insert subscriptionid, resourcegroup name too in metadata
      String[] arrOfStr = nodeInfo.getProviderId().split("/");
      log.info("Azure providerId after split: {}", Arrays.toString(arrOfStr));
      try {
        if (arrOfStr[SUBSCRIPTION_INDEX].equals(SUBSCRIPTION_INDEX_VALUE)) {
          metaData.put(InstanceMetaDataConstants.AZURE_SUBSCRIPTION_ID, arrOfStr[SUBSCRIPTION_INDEX + 1]);
        }
      } catch (Exception e) {
        log.error("Error while getting subscription id", e);
        metaData.put(InstanceMetaDataConstants.AZURE_SUBSCRIPTION_ID, "");
      }

      try {
        if (arrOfStr[RESOURCE_GROUP_INDEX].equals(RESOURCE_GROUP_INDEX_VALUE)) {
          metaData.put(InstanceMetaDataConstants.AZURE_RESOURCEGROUP_NAME, arrOfStr[RESOURCE_GROUP_INDEX + 1]);
        }
      } catch (Exception e) {
        log.error("Error while getting resource group", e);
        metaData.put(InstanceMetaDataConstants.AZURE_RESOURCEGROUP_NAME, "");
      }
    }
    InstanceMetaDataUtils.populateNodePoolNameFromLabel(labelsMap, metaData);

    Resource allocatableResource = K8sResourceUtils.getResource(nodeInfo.getAllocatableResourceMap());
    Resource totalResource = allocatableResource;
    List<CloudProvider> cloudProviders = cloudProviderService.getFirstClassSupportedCloudProviders();
    String computeType =
        InstanceMetaDataUtils.getValueForKeyFromInstanceMetaData(InstanceMetaDataConstants.COMPUTE_TYPE, metaData);
    if (cloudProviders.contains(k8SCloudProvider) && !K8sCCMConstants.AWS_FARGATE_COMPUTE_TYPE.equals(computeType)) {
      Resource computeVMResource = instanceResourceService.getComputeVMResource(
          InstanceMetaDataUtils.getValueForKeyFromInstanceMetaData(InstanceMetaDataConstants.INSTANCE_FAMILY, metaData),
          InstanceMetaDataUtils.getValueForKeyFromInstanceMetaData(InstanceMetaDataConstants.REGION, metaData),
          k8SCloudProvider);
      if (null != computeVMResource) {
        totalResource = computeVMResource;
      }
    }

    return InstanceInfo.builder()
        .accountId(accountId)
        .settingId(nodeInfo.getCloudProviderId())
        .instanceId(nodeUid)
        .clusterId(clusterId)
        .cloudProviderInstanceId(cloudProviderInstanceId)
        .clusterName(nodeInfo.getClusterName())
        .instanceName(nodeInfo.getNodeName())
        .instanceType(InstanceType.K8S_NODE)
        .instanceState(InstanceState.RUNNING)
        .usageStartTime(HTimestamps.toInstant(nodeInfo.getCreationTime()))
        .resource(totalResource)
        .allocatableResource(allocatableResource)
        .labels(encodeDotsInKey(labelsMap))
        .metaData(metaData)
        .build();
  }

  @VisibleForTesting
  public InstanceCategory getInstanceCategory(CloudProvider k8SCloudProvider, Map<String, String> labelsMap) {
    InstanceCategory instanceCategory = InstanceCategory.ON_DEMAND;
    if (k8SCloudProvider == CloudProvider.GCP) {
      boolean preemptible = labelsMap.keySet().stream().anyMatch(key -> key.contains(K8sCCMConstants.PREEMPTIBLE_KEY));
      if (preemptible) {
        return InstanceCategory.SPOT;
      }
    } else if (k8SCloudProvider == CloudProvider.AWS) {
      List<String> lifecycleKeys = labelsMap.keySet()
                                       .stream()
                                       .filter(key
                                           -> key.contains(K8sCCMConstants.AWS_LIFECYCLE_KEY)
                                               || key.contains(K8sCCMConstants.AWS_CAPACITY_TYPE_KEY))
                                       .collect(Collectors.toList());
      for (String lifecycleKey : lifecycleKeys) {
        String lifecycle = labelsMap.get(lifecycleKey);
        if (lifecycle.toLowerCase().contains(AWS_SPOT_INSTANCE)) {
          return InstanceCategory.SPOT;
        }
      }
    } else if (k8SCloudProvider == CloudProvider.AZURE) {
      String lifecycle = labelsMap.get(K8sCCMConstants.AZURE_LIFECYCLE_KEY);
      if (null != lifecycle && lifecycle.toLowerCase().contains(AZURE_SPOT_INSTANCE)) {
        return InstanceCategory.SPOT;
      }
    }
    return instanceCategory;
  }

  @VisibleForTesting
  public String getCloudProviderInstanceId(String providerId, CloudProvider k8SCloudProvider) {
    if (null == providerId) {
      return "";
    }
    if (k8SCloudProvider == CloudProvider.AZURE) {
      // ProviderID:
      // azure:///subscriptions/20d6a917-99fa-4b1b-9b2e-a3d624e9dcf0/resourceGroups/mc_ce_dev-resourcegroup_cetest1_eastus/providers/Microsoft.Compute/virtualMachines/aks-agentpool-41737416-1
      // ProviderID:
      // azure:///subscriptions/20d6a917-99fa-4b1b-9b2e-a3d624e9dcf0/resourceGroups/mc_ce_dev-resourcegroup_ce-dev-cluster2_eastus/providers/Microsoft.Compute/virtualMachineScaleSets/aks-agentpool-14257926-vmss/virtualMachines/1
      String[] arrOfStr = providerId.split("/");
      try {
        if (arrOfStr[arrOfStr.length - VM_INDEX].equals(VM_INDEX_VALUE)
            && arrOfStr[arrOfStr.length - VMSS_INDEX].equals(VMSS_INDEX_VALUE)) {
          providerId = arrOfStr[arrOfStr.length - 3] + "_" + arrOfStr[arrOfStr.length - 1];
        } else if (arrOfStr[arrOfStr.length - VM_INDEX].equals(VM_INDEX_VALUE)) {
          providerId = arrOfStr[arrOfStr.length - 1];
        }
        return providerId;
      } catch (Exception e) {
        log.error("Error while getting cloud provider id", e);
        return "";
      }
    } else {
      return providerId.substring(providerId.lastIndexOf('/') + 1);
    }
  }
}
