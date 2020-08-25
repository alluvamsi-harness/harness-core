package io.harness.cvng.verificationjob.services.impl;

import static io.harness.cvng.core.services.CVNextGenConstants.DATA_COLLECTION_DELAY;
import static io.harness.data.structure.UUIDGenerator.generateUuid;
import static io.harness.rule.OwnerRule.KAMAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import io.harness.CvNextGenTest;
import io.harness.category.element.UnitTests;
import io.harness.cvng.beans.CVMonitoringCategory;
import io.harness.cvng.beans.DataSourceType;
import io.harness.cvng.client.VerificationManagerService;
import io.harness.cvng.core.entities.CVConfig;
import io.harness.cvng.core.entities.DataCollectionTask;
import io.harness.cvng.core.entities.SplunkCVConfig;
import io.harness.cvng.core.services.api.CVConfigService;
import io.harness.cvng.core.services.api.DataCollectionTaskService;
import io.harness.cvng.models.VerificationType;
import io.harness.cvng.verificationjob.beans.DeploymentVerificationTaskDTO;
import io.harness.cvng.verificationjob.beans.Sensitivity;
import io.harness.cvng.verificationjob.beans.TestVerificationJobDTO;
import io.harness.cvng.verificationjob.beans.VerificationJobDTO;
import io.harness.cvng.verificationjob.entities.DeploymentVerificationTask;
import io.harness.cvng.verificationjob.services.api.DeploymentVerificationTaskService;
import io.harness.cvng.verificationjob.services.api.VerificationJobService;
import io.harness.rule.Owner;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class DeploymentVerificationTaskServiceImplTest extends CvNextGenTest {
  @Inject private VerificationJobService verificationJobService;
  @Inject private DeploymentVerificationTaskService deploymentVerificationTaskService;
  @Inject private CVConfigService cvConfigService;
  @Mock private VerificationManagerService verificationManagerService;
  @Inject private DataCollectionTaskService dataCollectionTaskService;
  private String accountId;
  private String verificationJobIdentifier;
  private long deploymentStartTimeMs;
  private String connectorId;
  private String dataCollectionTaskId;
  private String projectIdetifier;
  private String orgIdentifier;
  @Before
  public void setup() throws IllegalAccessException {
    MockitoAnnotations.initMocks(this);
    verificationJobIdentifier = generateUuid();
    accountId = generateUuid();
    accountId = generateUuid();
    projectIdetifier = generateUuid();
    orgIdentifier = generateUuid();
    deploymentStartTimeMs = Instant.parse("2020-07-27T10:44:06.390Z").toEpochMilli();
    connectorId = generateUuid();
    dataCollectionTaskId = generateUuid();
    FieldUtils.writeField(
        deploymentVerificationTaskService, "verificationManagerService", verificationManagerService, true);
    when(verificationManagerService.createDeploymentVerificationDataCollectionTask(any(), any(), any(), any(), any()))
        .thenReturn(dataCollectionTaskId);
  }

  @Test
  @Owner(developers = KAMAL)
  @Category(UnitTests.class)
  public void testCreate_withInvalidJobIdentifier() {
    assertThatThrownBy(() -> deploymentVerificationTaskService.create(accountId, newVerificationTask()))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("No Job exists for verificationJobIdentifier: '"
            + "" + verificationJobIdentifier + "'");
  }

  @Test
  @Owner(developers = KAMAL)
  @Category(UnitTests.class)
  public void testCreate_withValidJobIdentifier() {
    verificationJobService.upsert(accountId, newVerificationJob());
    DeploymentVerificationTaskDTO deploymentVerificationTaskDTO = newVerificationTask();
    String verificationTaskId = deploymentVerificationTaskService.create(accountId, deploymentVerificationTaskDTO);
    DeploymentVerificationTaskDTO saved = deploymentVerificationTaskService.get(verificationTaskId);
    assertThat(saved.getVerificationJobIdentifier())
        .isEqualTo(deploymentVerificationTaskDTO.getVerificationJobIdentifier());
    assertThat(saved.getDeploymentStartTime()).isEqualTo(deploymentVerificationTaskDTO.getDeploymentStartTime());
  }

  @Test
  @Owner(developers = KAMAL)
  @Category(UnitTests.class)
  public void testCreate_nullOptionalParams() {
    verificationJobService.upsert(accountId, newVerificationJob());
    DeploymentVerificationTaskDTO deploymentVerificationTaskDTO =
        DeploymentVerificationTaskDTO.builder()
            .verificationJobIdentifier(verificationJobIdentifier)
            .deploymentStartTimeMs(deploymentStartTimeMs)
            .verificationTaskStartTimeMs(deploymentStartTimeMs + Duration.ofMinutes(2).toMillis())
            .dataCollectionDelayMs(Duration.ofMinutes(5).toMillis())
            .build();
    String verificationTaskId = deploymentVerificationTaskService.create(accountId, deploymentVerificationTaskDTO);
    DeploymentVerificationTaskDTO saved = deploymentVerificationTaskService.get(verificationTaskId);
    assertThat(saved.getVerificationJobIdentifier())
        .isEqualTo(deploymentVerificationTaskDTO.getVerificationJobIdentifier());
    assertThat(saved.getDeploymentStartTime()).isEqualTo(deploymentVerificationTaskDTO.getDeploymentStartTime());
    assertThat(saved.getNewVersionHosts()).isNull();
    assertThat(saved.getOldVersionHosts()).isNull();
    assertThat(saved.getNewHostsTrafficSplitPercentage()).isNull();
  }

  @Test
  @Owner(developers = KAMAL)
  @Category(UnitTests.class)
  public void testCreate_validOptionalParams() {
    verificationJobService.upsert(accountId, newVerificationJob());
    DeploymentVerificationTaskDTO deploymentVerificationTaskDTO =
        DeploymentVerificationTaskDTO.builder()
            .verificationJobIdentifier(verificationJobIdentifier)
            .deploymentStartTimeMs(deploymentStartTimeMs)
            .verificationTaskStartTimeMs(deploymentStartTimeMs + Duration.ofMinutes(2).toMillis())
            .dataCollectionDelayMs(Duration.ofMinutes(5).toMillis())
            .newVersionHosts(Sets.newHashSet("newHost1", "newHost2"))
            .oldVersionHosts(Sets.newHashSet("oldHost1", "oldHost2"))
            .newHostsTrafficSplitPercentage(30)
            .build();
    String verificationTaskId = deploymentVerificationTaskService.create(accountId, deploymentVerificationTaskDTO);
    DeploymentVerificationTaskDTO saved = deploymentVerificationTaskService.get(verificationTaskId);
    assertThat(saved.getVerificationJobIdentifier())
        .isEqualTo(deploymentVerificationTaskDTO.getVerificationJobIdentifier());
    assertThat(saved.getDeploymentStartTime()).isEqualTo(deploymentVerificationTaskDTO.getDeploymentStartTime());
    assertThat(saved.getNewVersionHosts()).isEqualTo(Sets.newHashSet("newHost1", "newHost2"));
    assertThat(saved.getOldVersionHosts()).isEqualTo(Sets.newHashSet("oldHost1", "oldHost2"));
    assertThat(saved.getNewHostsTrafficSplitPercentage()).isEqualTo(30);
  }

  @Test
  @Owner(developers = KAMAL)
  @Category(UnitTests.class)
  public void createDataCollectionTasks_validatePerpetualTaskCreationWithCorrectParams() {
    verificationJobService.upsert(accountId, newVerificationJob());
    cvConfigService.save(newCVConfig());
    String verificationTaskId = deploymentVerificationTaskService.create(accountId, newVerificationTask());
    DeploymentVerificationTask deploymentVerificationTask =
        deploymentVerificationTaskService.getVerificationTask(verificationTaskId);
    deploymentVerificationTaskService.createDataCollectionTasks(deploymentVerificationTask);
    verify(verificationManagerService)
        .createDeploymentVerificationDataCollectionTask(eq(accountId), eq(connectorId), eq(orgIdentifier),
            eq(projectIdetifier), eq(getDataCollectionWorkerId(verificationTaskId, connectorId)));
    DeploymentVerificationTask saved = deploymentVerificationTaskService.getVerificationTask(verificationTaskId);
    assertThat(saved.getDataCollectionTaskIds()).isEqualTo(Lists.newArrayList(dataCollectionTaskId));
  }

  @Test
  @Owner(developers = KAMAL)
  @Category(UnitTests.class)
  public void createDataCollectionTasks_validateDataCollectionTasksCreation() {
    verificationJobService.upsert(accountId, newVerificationJob());
    cvConfigService.save(newCVConfig());
    String verificationTaskId = deploymentVerificationTaskService.create(accountId, newVerificationTask());
    DeploymentVerificationTask deploymentVerificationTask =
        deploymentVerificationTaskService.getVerificationTask(verificationTaskId);
    deploymentVerificationTaskService.createDataCollectionTasks(deploymentVerificationTask);
    String workerId = getDataCollectionWorkerId(verificationTaskId, connectorId);
    DataCollectionTask firstTask = dataCollectionTaskService.getNextTask(accountId, workerId).get();
    assertThat(firstTask).isNotNull();
    assertThat(firstTask.getStartTime()).isEqualTo(Instant.parse("2020-07-27T10:46:00Z"));
    assertThat(firstTask.getEndTime()).isEqualTo(Instant.parse("2020-07-27T10:47:00Z"));
    assertThat(firstTask.getValidAfter())
        .isEqualTo(Instant.parse("2020-07-27T10:47:00Z").plus(Duration.ofMinutes(5)).toEpochMilli());
  }

  @Test
  @Owner(developers = KAMAL)
  @Category(UnitTests.class)
  public void testCreateDataCollectionTasks_validateDataCollectionTasksCreationWithDefaultDataCollectionDelay() {
    verificationJobService.upsert(accountId, newVerificationJob());
    cvConfigService.save(newCVConfig());
    DeploymentVerificationTaskDTO dto = DeploymentVerificationTaskDTO.builder()
                                            .verificationJobIdentifier(verificationJobIdentifier)
                                            .deploymentStartTimeMs(deploymentStartTimeMs)
                                            .verificationTaskStartTimeMs(deploymentStartTimeMs)
                                            .build();
    String verificationTaskId = deploymentVerificationTaskService.create(accountId, dto);
    DeploymentVerificationTask deploymentVerificationTask =
        deploymentVerificationTaskService.getVerificationTask(verificationTaskId);
    deploymentVerificationTaskService.createDataCollectionTasks(deploymentVerificationTask);
    DeploymentVerificationTask updated = deploymentVerificationTaskService.getVerificationTask(verificationTaskId);
    String workerId = getDataCollectionWorkerId(verificationTaskId, connectorId);
    DataCollectionTask firstTask = dataCollectionTaskService.getNextTask(accountId, workerId).get();
    assertThat(firstTask).isNotNull();
    assertThat(firstTask.getEndTime()).isEqualTo(Instant.parse("2020-07-27T10:45:00Z"));
    assertThat(firstTask.getValidAfter())
        .isEqualTo(Instant.parse("2020-07-27T10:45:00Z").plus(DATA_COLLECTION_DELAY).toEpochMilli());
    assertThat(updated.getExecutionStatus()).isEqualTo(io.harness.cvng.analysis.beans.ExecutionStatus.RUNNING);
  }

  private String getDataCollectionWorkerId(String verificationTaskId, String connectorId) {
    return UUID.nameUUIDFromBytes((verificationTaskId + ":" + connectorId).getBytes(Charsets.UTF_8)).toString();
  }
  private VerificationJobDTO newVerificationJob() {
    TestVerificationJobDTO testVerificationJobDTO = new TestVerificationJobDTO();
    testVerificationJobDTO.setIdentifier(verificationJobIdentifier);
    testVerificationJobDTO.setJobName(generateUuid());
    testVerificationJobDTO.setDataSources(Lists.newArrayList(DataSourceType.SPLUNK));
    testVerificationJobDTO.setBaselineVerificationTaskIdentifier(null);
    testVerificationJobDTO.setSensitivity(Sensitivity.MEDIUM);
    testVerificationJobDTO.setServiceIdentifier(generateUuid());
    testVerificationJobDTO.setOrgIdentifier(orgIdentifier);
    testVerificationJobDTO.setProjectIdentifier(projectIdetifier);
    testVerificationJobDTO.setEnvIdentifier(generateUuid());
    testVerificationJobDTO.setBaselineVerificationTaskIdentifier(generateUuid());
    testVerificationJobDTO.setDuration("15m");
    return testVerificationJobDTO;
  }

  private CVConfig newCVConfig() {
    SplunkCVConfig cvConfig = new SplunkCVConfig();
    cvConfig.setQuery("exception");
    cvConfig.setServiceInstanceIdentifier("serviceInstanceIdentifier");
    cvConfig.setVerificationType(VerificationType.LOG);
    cvConfig.setAccountId(accountId);
    cvConfig.setConnectorId(connectorId);
    cvConfig.setServiceIdentifier(generateUuid());
    cvConfig.setEnvIdentifier(generateUuid());
    cvConfig.setProjectIdentifier(generateUuid());
    cvConfig.setGroupId("groupId");
    cvConfig.setCategory(CVMonitoringCategory.PERFORMANCE);
    cvConfig.setProductName("productName");
    return cvConfig;
  }

  private DeploymentVerificationTaskDTO newVerificationTask() {
    return DeploymentVerificationTaskDTO.builder()
        .verificationJobIdentifier(verificationJobIdentifier)
        .deploymentStartTimeMs(deploymentStartTimeMs)
        .verificationTaskStartTimeMs(deploymentStartTimeMs + Duration.ofMinutes(2).toMillis())
        .dataCollectionDelayMs(Duration.ofMinutes(5).toMillis())
        .build();
  }
}
