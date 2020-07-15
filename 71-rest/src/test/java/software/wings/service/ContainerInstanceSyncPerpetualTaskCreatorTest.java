package software.wings.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static software.wings.service.InstanceSyncConstants.CONTAINER_SERVICE_NAME;
import static software.wings.service.InstanceSyncConstants.CONTAINER_TYPE;
import static software.wings.service.InstanceSyncConstants.NAMESPACE;
import static software.wings.service.InstanceSyncConstants.RELEASE_NAME;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import io.harness.category.element.UnitTests;
import io.harness.perpetualtask.PerpetualTaskClientContext;
import io.harness.perpetualtask.instancesync.ContainerInstanceSyncPerpetualTaskClient;
import io.harness.perpetualtask.instancesync.ContainerInstanceSyncPerpetualTaskClientParams;
import io.harness.perpetualtask.internal.PerpetualTaskRecord;
import io.harness.rule.Owner;
import io.harness.rule.OwnerRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import software.wings.WingsBaseTest;
import software.wings.api.ContainerDeploymentInfoWithNames;
import software.wings.api.DeploymentSummary;
import software.wings.api.K8sDeploymentInfo;
import software.wings.beans.ContainerInfrastructureMapping;
import software.wings.beans.DirectKubernetesInfrastructureMapping;
import software.wings.beans.EcsInfrastructureMapping;
import software.wings.beans.infrastructure.instance.Instance;
import software.wings.beans.infrastructure.instance.info.EcsContainerInfo;
import software.wings.beans.infrastructure.instance.info.K8sPodInfo;
import software.wings.beans.infrastructure.instance.info.KubernetesContainerInfo;
import software.wings.service.impl.instance.InstanceSyncTestConstants;
import software.wings.service.intfc.instance.InstanceService;

import java.util.List;

public class ContainerInstanceSyncPerpetualTaskCreatorTest extends WingsBaseTest {
  @Mock private InstanceService instanceService;
  @Mock private ContainerInstanceSyncPerpetualTaskClient perpetualTaskClient;

  @InjectMocks @Inject private ContainerInstanceSyncPerpetualTaskCreator perpetualTaskCreator;

  @Test
  @Owner(developers = OwnerRule.ACASIAN)
  @Category(UnitTests.class)
  public void createK8sPerpetualTasks() {
    doReturn(getK8sContainerInstances())
        .when(instanceService)
        .getInstancesForAppAndInframapping(anyString(), anyString());
    doReturn("perpetual-task-id")
        .when(perpetualTaskClient)
        .create(eq(InstanceSyncTestConstants.ACCOUNT_ID), any(ContainerInstanceSyncPerpetualTaskClientParams.class));

    final List<String> perpetualTaskIds =
        perpetualTaskCreator.createPerpetualTasks(getContainerInfrastructureMapping());

    ArgumentCaptor<ContainerInstanceSyncPerpetualTaskClientParams> captor =
        ArgumentCaptor.forClass(ContainerInstanceSyncPerpetualTaskClientParams.class);
    verify(perpetualTaskClient, times(3)).create(eq(InstanceSyncTestConstants.ACCOUNT_ID), captor.capture());

    assertThat(perpetualTaskIds).isNotEmpty();
    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getContainerType))
        .containsOnly("K8S");
    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getNamespace))
        .containsExactlyInAnyOrder("namespace-1", "namespace-2", "namespace-3");
    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getReleaseName))
        .containsExactlyInAnyOrder("release-1", "release-2", "release-3");
  }

  @Test
  @Owner(developers = OwnerRule.ACASIAN)
  @Category(UnitTests.class)
  public void createAzurePerpetualTasks() {
    doReturn(getAzureContainerInstances())
        .when(instanceService)
        .getInstancesForAppAndInframapping(anyString(), anyString());
    doReturn("perpetual-task-id")
        .when(perpetualTaskClient)
        .create(eq(InstanceSyncTestConstants.ACCOUNT_ID), any(ContainerInstanceSyncPerpetualTaskClientParams.class));

    final List<String> perpetualTaskIds =
        perpetualTaskCreator.createPerpetualTasks(getContainerInfrastructureMapping());

    ArgumentCaptor<ContainerInstanceSyncPerpetualTaskClientParams> captor =
        ArgumentCaptor.forClass(ContainerInstanceSyncPerpetualTaskClientParams.class);
    verify(perpetualTaskClient, times(3)).create(eq(InstanceSyncTestConstants.ACCOUNT_ID), captor.capture());

    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getContainerType))
        .containsOnlyNulls();
    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getNamespace))
        .containsExactlyInAnyOrder("namespace-1", "namespace-2", "namespace-3");
    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getContainerSvcName))
        .containsExactlyInAnyOrder("service-1", "service-2", "service-3");
  }

  @Test
  @Owner(developers = OwnerRule.ACASIAN)
  @Category(UnitTests.class)
  public void createAwsPerpetualTasks() {
    doReturn(getAwsContainerInstances())
        .when(instanceService)
        .getInstancesForAppAndInframapping(anyString(), anyString());
    doReturn("perpetual-task-id")
        .when(perpetualTaskClient)
        .create(eq(InstanceSyncTestConstants.ACCOUNT_ID), any(ContainerInstanceSyncPerpetualTaskClientParams.class));

    final List<String> perpetualTaskIds =
        perpetualTaskCreator.createPerpetualTasks(getContainerInfrastructureMapping());

    ArgumentCaptor<ContainerInstanceSyncPerpetualTaskClientParams> captor =
        ArgumentCaptor.forClass(ContainerInstanceSyncPerpetualTaskClientParams.class);
    verify(perpetualTaskClient, times(3)).create(eq(InstanceSyncTestConstants.ACCOUNT_ID), captor.capture());

    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getContainerType))
        .containsOnlyNulls();
    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getContainerSvcName))
        .containsExactlyInAnyOrder("service-1", "service-2", "service-3");
  }

  @Test
  @Owner(developers = OwnerRule.ACASIAN)
  @Category(UnitTests.class)
  public void createK8sPerpetualTasksForNewDeployment() {
    List<PerpetualTaskRecord> existingRecords =
        asList(PerpetualTaskRecord.builder()
                   .clientContext(PerpetualTaskClientContext.builder()
                                      .clientParams(ImmutableMap.of(
                                          CONTAINER_TYPE, "K8S", NAMESPACE, "namespace-1", RELEASE_NAME, "release-1"))
                                      .build())
                   .build());

    perpetualTaskCreator.createPerpetualTasksForNewDeployment(
        asList(
            DeploymentSummary.builder()
                .appId(InstanceSyncTestConstants.APP_ID)
                .accountId(InstanceSyncTestConstants.ACCOUNT_ID)
                .infraMappingId(InstanceSyncTestConstants.INFRA_MAPPING_ID)
                .deploymentInfo(K8sDeploymentInfo.builder().namespace("namespace-1").releaseName("release-1").build())
                .build(),
            DeploymentSummary.builder()
                .appId(InstanceSyncTestConstants.APP_ID)
                .accountId(InstanceSyncTestConstants.ACCOUNT_ID)
                .infraMappingId(InstanceSyncTestConstants.INFRA_MAPPING_ID)
                .deploymentInfo(K8sDeploymentInfo.builder().namespace("namespace-2").releaseName("release-2").build())
                .build()),
        existingRecords, new DirectKubernetesInfrastructureMapping());

    ArgumentCaptor<ContainerInstanceSyncPerpetualTaskClientParams> captor =
        ArgumentCaptor.forClass(ContainerInstanceSyncPerpetualTaskClientParams.class);
    verify(perpetualTaskClient, times(1)).create(eq(InstanceSyncTestConstants.ACCOUNT_ID), captor.capture());

    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getContainerType))
        .containsExactlyInAnyOrder("K8S");
    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getNamespace))
        .containsExactlyInAnyOrder("namespace-2");
    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getReleaseName))
        .containsExactlyInAnyOrder("release-2");
  }

  @Test
  @Owner(developers = OwnerRule.ACASIAN)
  @Category(UnitTests.class)
  public void createContainerServicePerpetualTasksForNewDeployment() {
    List<PerpetualTaskRecord> existingRecords =
        asList(PerpetualTaskRecord.builder()
                   .clientContext(
                       PerpetualTaskClientContext.builder()
                           .clientParams(ImmutableMap.of(NAMESPACE, "namespace-1", CONTAINER_SERVICE_NAME, "service-1"))
                           .build())
                   .build());

    perpetualTaskCreator.createPerpetualTasksForNewDeployment(
        asList(DeploymentSummary.builder()
                   .appId(InstanceSyncTestConstants.APP_ID)
                   .accountId(InstanceSyncTestConstants.ACCOUNT_ID)
                   .infraMappingId(InstanceSyncTestConstants.INFRA_MAPPING_ID)
                   .deploymentInfo(ContainerDeploymentInfoWithNames.builder()
                                       .namespace("namespace-1")
                                       .containerSvcName("service-1")
                                       .build())
                   .build(),
            DeploymentSummary.builder()
                .appId(InstanceSyncTestConstants.APP_ID)
                .accountId(InstanceSyncTestConstants.ACCOUNT_ID)
                .infraMappingId(InstanceSyncTestConstants.INFRA_MAPPING_ID)
                .deploymentInfo(ContainerDeploymentInfoWithNames.builder()
                                    .namespace("namespace-2")
                                    .containerSvcName("service-2")
                                    .build())
                .build()),
        existingRecords, new EcsInfrastructureMapping());

    ArgumentCaptor<ContainerInstanceSyncPerpetualTaskClientParams> captor =
        ArgumentCaptor.forClass(ContainerInstanceSyncPerpetualTaskClientParams.class);
    verify(perpetualTaskClient, times(1)).create(eq(InstanceSyncTestConstants.ACCOUNT_ID), captor.capture());

    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getContainerType))
        .containsOnlyNulls();
    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getNamespace))
        .containsExactlyInAnyOrder("namespace-2");
    assertThat(captor.getAllValues().stream().map(ContainerInstanceSyncPerpetualTaskClientParams::getContainerSvcName))
        .containsExactlyInAnyOrder("service-2");
  }

  private ContainerInfrastructureMapping getContainerInfrastructureMapping() {
    ContainerInfrastructureMapping infraMapping = new DirectKubernetesInfrastructureMapping();
    infraMapping.setAccountId(InstanceSyncTestConstants.ACCOUNT_ID);
    infraMapping.setAppId(InstanceSyncTestConstants.APP_ID);
    infraMapping.setUuid(InstanceSyncTestConstants.INFRA_MAPPING_ID);
    return infraMapping;
  }

  private List<Instance> getK8sContainerInstances() {
    return asList(Instance.builder()
                      .uuid("id-1")
                      .instanceInfo(K8sPodInfo.builder().namespace("namespace-1").releaseName("release-1").build())
                      .build(),
        Instance.builder()
            .uuid("id-2")
            .instanceInfo(K8sPodInfo.builder().namespace("namespace-2").releaseName("release-2").build())
            .build(),
        Instance.builder()
            .uuid("id-3")
            .instanceInfo(K8sPodInfo.builder().namespace("namespace-3").releaseName("release-3").build())
            .build(),
        Instance.builder()
            .uuid("id-5")
            .instanceInfo(K8sPodInfo.builder().namespace("namespace-1").releaseName("release-1").build())
            .build());
  }

  private List<Instance> getAzureContainerInstances() {
    return asList(
        Instance.builder()
            .uuid("id-1")
            .instanceInfo(
                KubernetesContainerInfo.builder().namespace("namespace-1").controllerName("service-1").build())
            .build(),
        Instance.builder()
            .uuid("id-2")
            .instanceInfo(
                KubernetesContainerInfo.builder().namespace("namespace-2").controllerName("service-2").build())
            .build(),
        Instance.builder()
            .uuid("id-3")
            .instanceInfo(
                KubernetesContainerInfo.builder().namespace("namespace-3").controllerName("service-3").build())
            .build(),
        Instance.builder()
            .uuid("id-5")
            .instanceInfo(
                KubernetesContainerInfo.builder().namespace("namespace-1").controllerName("service-1").build())
            .build());
  }

  private List<Instance> getAwsContainerInstances() {
    return asList(Instance.builder()
                      .uuid("id-1")
                      .instanceInfo(EcsContainerInfo.Builder.anEcsContainerInfo().withServiceName("service-1").build())
                      .build(),
        Instance.builder()
            .uuid("id-2")
            .instanceInfo(EcsContainerInfo.Builder.anEcsContainerInfo().withServiceName("service-2").build())
            .build(),
        Instance.builder()
            .uuid("id-3")
            .instanceInfo(EcsContainerInfo.Builder.anEcsContainerInfo().withServiceName("service-3").build())
            .build(),
        Instance.builder()
            .uuid("id-5")
            .instanceInfo(EcsContainerInfo.Builder.anEcsContainerInfo().withServiceName("service-1").build())
            .build());
  }
}
