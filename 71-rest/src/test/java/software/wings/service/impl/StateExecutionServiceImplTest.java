package software.wings.service.impl;

import static io.harness.data.structure.UUIDGenerator.generateUuid;
import static io.harness.rule.OwnerRule.PRASHANT;
import static io.harness.rule.OwnerRule.VAIBHAV_SI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static software.wings.utils.WingsTestConstants.ACCOUNT_ID;
import static software.wings.utils.WingsTestConstants.APP_ID;
import static software.wings.utils.WingsTestConstants.INFRA_DEFINITION_ID;
import static software.wings.utils.WingsTestConstants.INFRA_MAPPING_ID;
import static software.wings.utils.WingsTestConstants.WORKFLOW_EXECUTION_ID;

import io.harness.category.element.UnitTests;
import io.harness.rule.OwnerRule.Owner;
import org.joor.Reflect;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import software.wings.WingsBaseTest;
import software.wings.api.PhaseElement;
import software.wings.api.PhaseExecutionData;
import software.wings.api.PhaseExecutionData.PhaseExecutionDataBuilder;
import software.wings.api.SelectNodeStepExecutionSummary;
import software.wings.beans.ServiceInstance;
import software.wings.service.intfc.AppService;
import software.wings.service.intfc.FeatureFlagService;
import software.wings.sm.PhaseExecutionSummary;
import software.wings.sm.PhaseStepExecutionSummary;
import software.wings.sm.StateExecutionInstance;
import software.wings.sm.StateExecutionInstance.Builder;
import software.wings.sm.StateType;

import java.util.Collections;
import java.util.List;

public class StateExecutionServiceImplTest extends WingsBaseTest {
  private static final String RANDOM = "RANDOM";

  @Mock private FeatureFlagService featureFlagService;
  @Mock private AppService appService;

  private StateExecutionServiceImpl stateExecutionService = spy(new StateExecutionServiceImpl());

  @Before
  public void setUp() throws Exception {
    Reflect.on(stateExecutionService).set("featureFlagService", featureFlagService);
    Reflect.on(stateExecutionService).set("appService", appService);
  }

  @Test
  @Owner(developers = VAIBHAV_SI)
  @Category(UnitTests.class)
  public void shouldReturnHostsForMultiplePhases() {
    PhaseElement phaseElement = PhaseElement.builder().infraDefinitionId(INFRA_DEFINITION_ID).build();
    List<ServiceInstance> serviceInstanceList =
        Collections.singletonList(ServiceInstance.Builder.aServiceInstance().build());
    SelectNodeStepExecutionSummary selectNodeStepExecutionSummary = new SelectNodeStepExecutionSummary();
    selectNodeStepExecutionSummary.setExcludeSelectedHostsFromFuturePhases(true);
    selectNodeStepExecutionSummary.setServiceInstanceList(serviceInstanceList);
    PhaseStepExecutionSummary phaseStepExecutionSummary = new PhaseStepExecutionSummary();
    phaseStepExecutionSummary.setStepExecutionSummaryList(Collections.singletonList(selectNodeStepExecutionSummary));
    PhaseExecutionSummary phaseExecutionSummary = new PhaseExecutionSummary();
    phaseExecutionSummary.setPhaseStepExecutionSummaryMap(Collections.singletonMap(RANDOM, phaseStepExecutionSummary));
    PhaseExecutionData phaseExecutionData = PhaseExecutionDataBuilder.aPhaseExecutionData()
                                                .withInfraDefinitionId(INFRA_DEFINITION_ID)
                                                .withPhaseExecutionSummary(phaseExecutionSummary)
                                                .build();
    StateExecutionInstance stateExecutionInstance = Builder.aStateExecutionInstance().appId(APP_ID).build();
    doReturn(Collections.singletonList(phaseExecutionData))
        .when(stateExecutionService)
        .fetchPhaseExecutionData(any(), any(), any(), any());
    doReturn(ACCOUNT_ID).when(appService).getAccountIdByAppId(any());
    doReturn(true).when(featureFlagService).isEnabled(any(), any());

    List<ServiceInstance> hostExclusionList =
        stateExecutionService.getHostExclusionList(stateExecutionInstance, phaseElement, null);

    assertThat(hostExclusionList).isEqualTo(serviceInstanceList);
  }

  @Test
  @Owner(developers = VAIBHAV_SI)
  @Category(UnitTests.class)
  public void shouldReturnEmptyWhenNoPreviousPhases() {
    PhaseElement phaseElement = PhaseElement.builder().infraDefinitionId(INFRA_DEFINITION_ID).build();
    StateExecutionInstance stateExecutionInstance = Builder.aStateExecutionInstance().appId(APP_ID).build();
    doReturn(Collections.emptyList()).when(stateExecutionService).fetchPhaseExecutionData(any(), any(), any(), any());
    doReturn(ACCOUNT_ID).when(appService).getAccountIdByAppId(any());
    doReturn(true).when(featureFlagService).isEnabled(any(), any());

    List<ServiceInstance> hostExclusionList =
        stateExecutionService.getHostExclusionList(stateExecutionInstance, phaseElement, null);

    assertThat(hostExclusionList).isEqualTo(Collections.emptyList());
  }

  @Test
  @Owner(developers = VAIBHAV_SI)
  @Category(UnitTests.class)
  public void shouldReturnHostsForMultiplePhasesForFeatureFlagOff() {
    PhaseElement phaseElement = PhaseElement.builder().infraMappingId(INFRA_MAPPING_ID).build();
    List<ServiceInstance> serviceInstanceList =
        Collections.singletonList(ServiceInstance.Builder.aServiceInstance().build());
    SelectNodeStepExecutionSummary selectNodeStepExecutionSummary = new SelectNodeStepExecutionSummary();
    selectNodeStepExecutionSummary.setExcludeSelectedHostsFromFuturePhases(true);
    selectNodeStepExecutionSummary.setServiceInstanceList(serviceInstanceList);
    PhaseStepExecutionSummary phaseStepExecutionSummary = new PhaseStepExecutionSummary();
    phaseStepExecutionSummary.setStepExecutionSummaryList(Collections.singletonList(selectNodeStepExecutionSummary));
    PhaseExecutionSummary phaseExecutionSummary = new PhaseExecutionSummary();
    phaseExecutionSummary.setPhaseStepExecutionSummaryMap(Collections.singletonMap(RANDOM, phaseStepExecutionSummary));
    PhaseExecutionData phaseExecutionData = PhaseExecutionDataBuilder.aPhaseExecutionData()
                                                .withInfraMappingId(INFRA_MAPPING_ID)
                                                .withPhaseExecutionSummary(phaseExecutionSummary)
                                                .build();
    StateExecutionInstance stateExecutionInstance = Builder.aStateExecutionInstance().appId(APP_ID).build();
    doReturn(Collections.singletonList(phaseExecutionData))
        .when(stateExecutionService)
        .fetchPhaseExecutionData(any(), any(), any(), any());
    doReturn(ACCOUNT_ID).when(appService).getAccountIdByAppId(any());
    doReturn(false).when(featureFlagService).isEnabled(any(), any());

    List<ServiceInstance> hostExclusionList =
        stateExecutionService.getHostExclusionList(stateExecutionInstance, phaseElement, null);

    assertThat(hostExclusionList).isEqualTo(serviceInstanceList);
  }

  @Test
  @Owner(developers = PRASHANT)
  @Category(UnitTests.class)
  public void fetchPreviousPhaseStateExecutionInstance() {
    String uuid1 = generateUuid();
    String uuid2 = generateUuid();
    String uuid3 = generateUuid();
    String uuid4 = generateUuid();
    StateExecutionInstance.Builder instance =
        Builder.aStateExecutionInstance().appId(APP_ID).executionUuid(WORKFLOW_EXECUTION_ID);

    StateExecutionInstance executionInstance1 = instance.uuid(uuid1)
                                                    .displayName("App Resize")
                                                    .stateName("App Resize")
                                                    .stateType(StateType.PCF_RESIZE.name())
                                                    .parentInstanceId(uuid2)
                                                    .build();
    StateExecutionInstance executionInstance2 = instance.uuid(uuid2)
                                                    .displayName("Deploy")
                                                    .stateName("Deploy")
                                                    .stateType(StateType.PHASE_STEP.name())
                                                    .parentInstanceId(uuid3)
                                                    .build();

    StateExecutionInstance executionInstance3 = instance.uuid(uuid3)
                                                    .displayName("Phase 2")
                                                    .stateName("Phase 2")
                                                    .stateType(StateType.PHASE.name())
                                                    .prevInstanceId(uuid4)
                                                    .build();

    StateExecutionInstance executionInstance4 =
        instance.uuid(uuid4).displayName("Phase 1").stateName("Phase 1").stateType(StateType.PHASE.name()).build();

    doReturn(executionInstance1)
        .when(stateExecutionService)
        .getStateExecutionInstance(APP_ID, WORKFLOW_EXECUTION_ID, uuid1);
    doReturn(executionInstance2)
        .when(stateExecutionService)
        .getStateExecutionInstance(APP_ID, WORKFLOW_EXECUTION_ID, uuid2);
    doReturn(executionInstance3)
        .when(stateExecutionService)
        .getStateExecutionInstance(APP_ID, WORKFLOW_EXECUTION_ID, uuid3);
    doReturn(executionInstance4)
        .when(stateExecutionService)
        .getStateExecutionInstance(APP_ID, WORKFLOW_EXECUTION_ID, uuid4);
    StateExecutionInstance previousInstance =
        stateExecutionService.fetchPreviousPhaseStateExecutionInstance(APP_ID, WORKFLOW_EXECUTION_ID, uuid1);
    assertThat(previousInstance).isNotNull();
    assertThat(previousInstance.getUuid()).isEqualTo(uuid4);
    assertThat(previousInstance.getStateName()).isEqualTo("Phase 1");
  }
}