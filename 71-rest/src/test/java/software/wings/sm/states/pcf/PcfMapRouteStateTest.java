package software.wings.sm.states.pcf;

import static io.harness.data.structure.UUIDGenerator.generateUuid;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.joor.Reflect.on;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static software.wings.beans.Application.Builder.anApplication;
import static software.wings.beans.Environment.Builder.anEnvironment;
import static software.wings.beans.ServiceTemplate.Builder.aServiceTemplate;
import static software.wings.beans.SettingAttribute.Builder.aSettingAttribute;
import static software.wings.beans.artifact.Artifact.Builder.anArtifact;
import static software.wings.beans.command.Command.Builder.aCommand;
import static software.wings.beans.command.ServiceCommand.Builder.aServiceCommand;
import static software.wings.common.Constants.INFRA_ROUTE;
import static software.wings.common.Constants.PCF_APP_NAME;
import static software.wings.common.Constants.PCF_OLD_APP_NAME;
import static software.wings.service.impl.workflow.WorkflowServiceHelper.INFRA_ROUTE_PCF;
import static software.wings.service.intfc.ServiceTemplateService.EncryptedFieldComputeMode.MASKED;
import static software.wings.service.intfc.ServiceTemplateService.EncryptedFieldComputeMode.OBTAIN_VALUE;
import static software.wings.sm.states.pcf.MapRouteState.PCF_MAP_ROUTE_COMMAND;
import static software.wings.sm.states.pcf.PcfSwitchBlueGreenRoutes.PCF_BG_SWAP_ROUTE_COMMAND;
import static software.wings.utils.WingsTestConstants.ACCOUNT_ID;
import static software.wings.utils.WingsTestConstants.ACTIVITY_ID;
import static software.wings.utils.WingsTestConstants.APP_ID;
import static software.wings.utils.WingsTestConstants.APP_NAME;
import static software.wings.utils.WingsTestConstants.ARTIFACT_STREAM_ID;
import static software.wings.utils.WingsTestConstants.ENV_ID;
import static software.wings.utils.WingsTestConstants.ENV_NAME;
import static software.wings.utils.WingsTestConstants.INFRA_MAPPING_ID;
import static software.wings.utils.WingsTestConstants.INFRA_TEMP_ROUTE;
import static software.wings.utils.WingsTestConstants.PASSWORD;
import static software.wings.utils.WingsTestConstants.SERVICE_ID;
import static software.wings.utils.WingsTestConstants.SERVICE_NAME;
import static software.wings.utils.WingsTestConstants.TEMPLATE_ID;
import static software.wings.utils.WingsTestConstants.URL;
import static software.wings.utils.WingsTestConstants.USER_NAME;
import static software.wings.utils.WingsTestConstants.WORKFLOW_EXECUTION_ID;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import io.harness.beans.DelegateTask;
import io.harness.beans.EmbeddedUser;
import io.harness.beans.ExecutionStatus;
import io.harness.beans.SweepingOutputInstance;
import io.harness.category.element.UnitTests;
import io.harness.expression.VariableResolverTracker;
import io.harness.serializer.KryoUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mongodb.morphia.Key;
import software.wings.WingsBaseTest;
import software.wings.api.PhaseElement;
import software.wings.api.ServiceElement;
import software.wings.api.pcf.PcfRouteUpdateStateExecutionData;
import software.wings.app.MainConfiguration;
import software.wings.app.PortalConfig;
import software.wings.beans.Activity;
import software.wings.beans.Application;
import software.wings.beans.Environment;
import software.wings.beans.PcfConfig;
import software.wings.beans.Service;
import software.wings.beans.ServiceTemplate;
import software.wings.beans.ServiceVariable;
import software.wings.beans.ServiceVariable.Type;
import software.wings.beans.SettingAttribute;
import software.wings.beans.WorkflowExecution;
import software.wings.beans.artifact.Artifact;
import software.wings.beans.artifact.Artifact.ArtifactMetadataKeys;
import software.wings.beans.artifact.ArtifactStream;
import software.wings.beans.artifact.JenkinsArtifactStream;
import software.wings.beans.command.CommandType;
import software.wings.beans.command.ServiceCommand;
import software.wings.beans.container.PcfServiceSpecification;
import software.wings.common.InfrastructureConstants;
import software.wings.common.VariableProcessor;
import software.wings.expression.ManagerExpressionEvaluator;
import software.wings.helpers.ext.pcf.request.PcfCommandRouteUpdateRequest;
import software.wings.service.ServiceHelper;
import software.wings.service.intfc.ActivityService;
import software.wings.service.intfc.AppService;
import software.wings.service.intfc.ArtifactService;
import software.wings.service.intfc.ArtifactStreamService;
import software.wings.service.intfc.DelegateService;
import software.wings.service.intfc.EnvironmentService;
import software.wings.service.intfc.InfrastructureMappingService;
import software.wings.service.intfc.ServiceResourceService;
import software.wings.service.intfc.ServiceTemplateService;
import software.wings.service.intfc.SettingsService;
import software.wings.service.intfc.SweepingOutputService;
import software.wings.service.intfc.WorkflowExecutionService;
import software.wings.service.intfc.security.EncryptionService;
import software.wings.service.intfc.security.SecretManager;
import software.wings.sm.ExecutionContext;
import software.wings.sm.ExecutionContextImpl;
import software.wings.sm.ExecutionResponse;
import software.wings.sm.StateExecutionInstance;
import software.wings.sm.WorkflowStandardParams;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PcfMapRouteStateTest extends WingsBaseTest {
  private static final String BASE_URL = "https://env.harness.io/";
  public static final String ORG = "ORG";
  public static final String SPACE = "SPACE";
  private static final String PHASE_NAME = "phaseName";

  @Mock private SettingsService settingsService;
  @Mock private DelegateService delegateService;
  @Mock private ServiceResourceService serviceResourceService;
  @Mock private ActivityService activityService;
  @Mock private InfrastructureMappingService infrastructureMappingService;
  @Mock private AppService appService;
  @Mock private EnvironmentService environmentService;
  @Mock private ServiceTemplateService serviceTemplateService;
  @Mock private SecretManager secretManager;
  @Mock private WorkflowExecutionService workflowExecutionService;
  @Mock private ArtifactService artifactService;
  @Mock private ArtifactStreamService artifactStreamService;
  @Mock private EncryptionService encryptionService;
  @Mock private VariableProcessor variableProcessor;
  @Mock private ManagerExpressionEvaluator evaluator;
  @Mock private ServiceHelper serviceHelper;
  @Mock private SweepingOutputService sweepingOutputService;
  private PcfStateTestHelper pcfStateTestHelper = new PcfStateTestHelper();

  @InjectMocks private MapRouteState pcfRouteSwapState = new MapRouteState("name");
  @InjectMocks private PcfSwitchBlueGreenRoutes pcfSwitchBlueGreenRoutes = new PcfSwitchBlueGreenRoutes("name");
  @InjectMocks @Inject PcfStateHelper pcfStateHelper;

  @Mock private MainConfiguration configuration;

  private ExecutionContext context;

  private WorkflowStandardParams workflowStandardParams = pcfStateTestHelper.getWorkflowStandardParams();

  private ServiceElement serviceElement = pcfStateTestHelper.getServiceElement();

  @InjectMocks private PhaseElement phaseElement = pcfStateTestHelper.getPhaseElement(serviceElement);

  private StateExecutionInstance stateExecutionInstance =
      pcfStateTestHelper.getStateExecutionInstanceForRouteUpdateState(
          workflowStandardParams, phaseElement, serviceElement);

  private Application app = anApplication().uuid(APP_ID).name(APP_NAME).build();
  private Environment env = anEnvironment().appId(APP_ID).uuid(ENV_ID).name(ENV_NAME).build();
  private Service service = Service.builder()
                                .appId(APP_ID)
                                .uuid(SERVICE_ID)
                                .name(SERVICE_NAME)
                                .artifactStreamIds(singletonList(ARTIFACT_STREAM_ID))
                                .build();
  private Artifact artifact = anArtifact()
                                  .withArtifactSourceName("source")
                                  .withMetadata(ImmutableMap.of(ArtifactMetadataKeys.buildNo, "bn"))
                                  .withArtifactStreamId(ARTIFACT_STREAM_ID)
                                  .build();
  private ArtifactStream artifactStream =
      JenkinsArtifactStream.builder().appId(APP_ID).sourceName("").jobname("").artifactPaths(null).build();

  private SettingAttribute pcfConfig =
      aSettingAttribute()
          .withValue(
              PcfConfig.builder().endpointUrl(URL).password(PASSWORD).username(USER_NAME).accountId(ACCOUNT_ID).build())
          .build();

  private List<ServiceVariable> serviceVariableList =
      asList(ServiceVariable.builder().type(Type.TEXT).name("VAR_1").value("value1".toCharArray()).build(),
          ServiceVariable.builder().type(Type.ENCRYPTED_TEXT).name("VAR_2").value("value2".toCharArray()).build());

  private List<ServiceVariable> safeDisplayServiceVariableList =
      asList(ServiceVariable.builder().type(Type.TEXT).name("VAR_1").value("value1".toCharArray()).build(),
          ServiceVariable.builder().type(Type.ENCRYPTED_TEXT).name("VAR_2").value("*******".toCharArray()).build());

  private String outputName = InfrastructureConstants.PHASE_INFRA_MAPPING_KEY_NAME + phaseElement.getUuid();
  private SweepingOutputInstance sweepingOutputInstance = SweepingOutputInstance.builder()
                                                              .appId(APP_ID)
                                                              .name(outputName)
                                                              .uuid(generateUuid())
                                                              .workflowExecutionId(WORKFLOW_EXECUTION_ID)
                                                              .stateExecutionId(null)
                                                              .pipelineExecutionId(null)
                                                              .output(KryoUtils.asDeflatedBytes(INFRA_MAPPING_ID))
                                                              .build();

  @Before
  public void setup() throws IllegalAccessException {
    when(delegateService.queueTask(any())).thenReturn("ID");
    when(appService.get(APP_ID)).thenReturn(app);
    when(appService.getApplicationWithDefaults(APP_ID)).thenReturn(app);
    when(serviceResourceService.getWithDetails(APP_ID, SERVICE_ID)).thenReturn(service);
    when(environmentService.get(APP_ID, ENV_ID, false)).thenReturn(env);
    when(sweepingOutputService.find(any())).thenReturn(sweepingOutputInstance);
    ServiceCommand serviceCommand =
        aServiceCommand()
            .withCommand(aCommand().withCommandType(CommandType.SETUP).withName("Setup Service Cluster").build())
            .build();
    when(serviceResourceService.getCommandByName(APP_ID, SERVICE_ID, ENV_ID, "Setup Service Cluster"))
        .thenReturn(serviceCommand);

    EmbeddedUser currentUser = EmbeddedUser.builder().name("test").email("test@harness.io").build();
    workflowStandardParams.setCurrentUser(currentUser);

    on(workflowStandardParams).set("appService", appService);
    on(workflowStandardParams).set("environmentService", environmentService);
    on(workflowStandardParams).set("artifactService", artifactService);
    on(workflowStandardParams).set("serviceTemplateService", serviceTemplateService);
    on(workflowStandardParams).set("configuration", configuration);

    when(artifactService.get(any())).thenReturn(artifact);
    when(artifactStreamService.get(any())).thenReturn(artifactStream);
    when(infrastructureMappingService.get(APP_ID, INFRA_MAPPING_ID))
        .thenReturn(pcfStateTestHelper.getPcfInfrastructureMapping(Arrays.asList("R1", "R2"), Arrays.asList("R3")));

    Activity activity = Activity.builder().build();
    activity.setUuid(ACTIVITY_ID);
    when(activityService.save(any(Activity.class))).thenReturn(activity);

    when(settingsService.get(any())).thenReturn(pcfConfig);

    when(serviceTemplateService.getTemplateRefKeysByService(APP_ID, SERVICE_ID, ENV_ID))
        .thenReturn(singletonList(new Key<>(ServiceTemplate.class, "serviceTemplate", TEMPLATE_ID)));

    when(serviceTemplateService.get(APP_ID, TEMPLATE_ID)).thenReturn(aServiceTemplate().withUuid(TEMPLATE_ID).build());
    when(serviceTemplateService.computeServiceVariables(APP_ID, ENV_ID, TEMPLATE_ID, null, OBTAIN_VALUE))
        .thenReturn(serviceVariableList);
    when(serviceTemplateService.computeServiceVariables(APP_ID, ENV_ID, TEMPLATE_ID, null, MASKED))
        .thenReturn(safeDisplayServiceVariableList);
    when(secretManager.getEncryptionDetails(anyObject(), anyString(), anyString())).thenReturn(Collections.emptyList());
    FieldUtils.writeField(pcfRouteSwapState, "secretManager", secretManager, true);
    FieldUtils.writeField(pcfRouteSwapState, "pcfStateHelper", pcfStateHelper, true);
    FieldUtils.writeField(pcfSwitchBlueGreenRoutes, "pcfStateHelper", pcfStateHelper, true);
    when(workflowExecutionService.getExecutionDetails(anyString(), anyString(), anyBoolean(), anySet()))
        .thenReturn(WorkflowExecution.builder().build());
    context = new ExecutionContextImpl(stateExecutionInstance);
    on(context).set("variableProcessor", variableProcessor);
    on(context).set("evaluator", evaluator);
    when(variableProcessor.getVariables(any(), any())).thenReturn(emptyMap());
    when(evaluator.substitute(anyString(), anyMap(), any(VariableResolverTracker.class), anyString()))
        .thenAnswer(i -> i.getArguments()[0]);
    PortalConfig portalConfig = new PortalConfig();
    portalConfig.setUrl(BASE_URL);
    when(configuration.getPortal()).thenReturn(portalConfig);
    when(serviceResourceService.getPcfServiceSpecification(anyString(), anyString()))
        .thenReturn(PcfServiceSpecification.builder()
                        .manifestYaml("  applications:\n"
                            + "  - name : ${APPLICATION_NAME}\n"
                            + "    memory: 850M\n"
                            + "    instances : ${INSTANCE_COUNT}\n"
                            + "    buildpack: https://github.com/cloudfoundry/java-buildpack.git\n"
                            + "    path: ${FILE_LOCATION}\n"
                            + "    routes:\n"
                            + "      - route: ${ROUTE_MAP}\n"
                            + "serviceName: SERV\n")
                        .serviceId(service.getUuid())
                        .build());
    doNothing().when(serviceHelper).addPlaceholderTexts(any());
  }

  @Test
  @Category(UnitTests.class)
  public void testExecute_pcfAPP_Switch_Infra_routes() {
    pcfRouteSwapState.setPcfAppName("${" + PCF_APP_NAME + "}");
    pcfRouteSwapState.setRoute("${" + INFRA_ROUTE + "}");
    test_pcfAPP_Switch_Infra_routes();
  }

  @Test
  @Category(UnitTests.class)
  public void testExecute_pcfAPP_Switch_Infra_routes_New_Route_Name() {
    pcfRouteSwapState.setPcfAppName("${" + PCF_APP_NAME + "}");
    pcfRouteSwapState.setRoute("${" + INFRA_ROUTE_PCF + "}");
    test_pcfAPP_Switch_Infra_routes();
  }

  private void test_pcfAPP_Switch_Infra_routes() {
    on(context).set("serviceTemplateService", serviceTemplateService);
    on(context).set("sweepingOutputService", sweepingOutputService);
    ExecutionResponse executionResponse = pcfRouteSwapState.execute(context);

    assertThat(executionResponse.getExecutionStatus()).isEqualTo(ExecutionStatus.SUCCESS);
    PcfRouteUpdateStateExecutionData stateExecutionData =
        (PcfRouteUpdateStateExecutionData) executionResponse.getStateExecutionData();
    assertThat(stateExecutionData.getCommandName()).isEqualTo(PCF_MAP_ROUTE_COMMAND);
    PcfCommandRouteUpdateRequest pcfCommandRouteUpdateRequest =
        (PcfCommandRouteUpdateRequest) stateExecutionData.getPcfCommandRequest();

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData()).isNotNull();
    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames()).isNotNull();
    List<String> appNames = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames();
    assertThat(appNames).hasSize(1);
    assertThat(appNames.get(0)).isEqualTo("APP_NAME_SERVICE_NAME_ENV_NAME__2");

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes()).isNotNull();
    List<String> routes = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes();
    assertThat(routes).hasSize(2);
    assertThat(routes.contains("R1")).isTrue();
    assertThat(routes.contains("R2")).isTrue();

    ArgumentCaptor<DelegateTask> captor = ArgumentCaptor.forClass(DelegateTask.class);
    verify(delegateService).queueTask(captor.capture());
    DelegateTask delegateTask = captor.getValue();

    pcfCommandRouteUpdateRequest = (PcfCommandRouteUpdateRequest) delegateTask.getData().getParameters()[0];
    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData()).isNotNull();
    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames()).isNotNull();
    appNames = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames();
    assertThat(appNames).hasSize(1);
    assertThat(appNames.get(0)).isEqualTo("APP_NAME_SERVICE_NAME_ENV_NAME__2");

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes()).isNotNull();
    routes = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes();
    assertThat(routes).hasSize(2);
    assertThat(routes.contains("R1")).isTrue();
    assertThat(routes.contains("R2")).isTrue();
  }

  @Test
  @Category(UnitTests.class)
  public void testExecute_NPE_Validation() {
    ExecutionContextImpl mockContext = mock(ExecutionContextImpl.class);
    WorkflowStandardParams workflowStandardParams = mock(WorkflowStandardParams.class);
    doReturn(workflowStandardParams).when(mockContext).getContextElement(any());

    doReturn(anApplication().build()).when(workflowStandardParams).getApp();
    doReturn(null).when(workflowStandardParams).getEnv();
    try {
      pcfRouteSwapState.execute(mockContext);
      fail("Expecting Exception");
    } catch (Exception e) {
      // expected
    }
  }

  @Test
  @Category(UnitTests.class)
  public void testExecute_pcfAPP_infra_tempRoute() {
    pcfRouteSwapState.setPcfAppName("${" + PCF_APP_NAME + "}");
    pcfRouteSwapState.setRoute("${" + INFRA_TEMP_ROUTE + "}");

    on(context).set("serviceTemplateService", serviceTemplateService);
    on(context).set("sweepingOutputService", sweepingOutputService);
    ExecutionResponse executionResponse = pcfRouteSwapState.execute(context);

    assertThat(executionResponse.getExecutionStatus()).isEqualTo(ExecutionStatus.SUCCESS);
    PcfRouteUpdateStateExecutionData stateExecutionData =
        (PcfRouteUpdateStateExecutionData) executionResponse.getStateExecutionData();
    assertThat(stateExecutionData.getCommandName()).isEqualTo(PCF_MAP_ROUTE_COMMAND);
    PcfCommandRouteUpdateRequest pcfCommandRouteUpdateRequest =
        (PcfCommandRouteUpdateRequest) stateExecutionData.getPcfCommandRequest();

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData()).isNotNull();
    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames()).isNotNull();
    List<String> appNames = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames();
    assertThat(appNames).hasSize(1);
    assertThat(appNames.get(0)).isEqualTo("APP_NAME_SERVICE_NAME_ENV_NAME__2");

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes()).isNotNull();
    List<String> routes = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes();
    assertThat(routes).hasSize(1);
    assertThat(routes.contains("R3")).isTrue();

    ArgumentCaptor<DelegateTask> captor = ArgumentCaptor.forClass(DelegateTask.class);
    verify(delegateService).queueTask(captor.capture());
    DelegateTask delegateTask = captor.getValue();

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData()).isNotNull();
    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames()).isNotNull();
    appNames = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames();
    assertThat(appNames).hasSize(1);
    assertThat(appNames.get(0)).isEqualTo("APP_NAME_SERVICE_NAME_ENV_NAME__2");

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes()).isNotNull();
    routes = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes();
    assertThat(routes).hasSize(1);
    assertThat(routes.contains("R3")).isTrue();
  }

  @Test
  @Category(UnitTests.class)
  public void testExecute_pcfOldAPP_infra_route() {
    pcfRouteSwapState.setPcfAppName("${" + PCF_OLD_APP_NAME + "}");
    pcfRouteSwapState.setRoute("${" + INFRA_ROUTE + "}");

    on(context).set("serviceTemplateService", serviceTemplateService);
    on(context).set("sweepingOutputService", sweepingOutputService);
    ExecutionResponse executionResponse = pcfRouteSwapState.execute(context);

    assertThat(executionResponse.getExecutionStatus()).isEqualTo(ExecutionStatus.SUCCESS);
    PcfRouteUpdateStateExecutionData stateExecutionData =
        (PcfRouteUpdateStateExecutionData) executionResponse.getStateExecutionData();
    assertThat(stateExecutionData.getCommandName()).isEqualTo(PCF_MAP_ROUTE_COMMAND);
    PcfCommandRouteUpdateRequest pcfCommandRouteUpdateRequest =
        (PcfCommandRouteUpdateRequest) stateExecutionData.getPcfCommandRequest();

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData()).isNotNull();
    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames()).isNotNull();
    List<String> appNames = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames();
    assertThat(appNames).hasSize(1);
    assertThat(appNames.get(0)).isEqualTo("APP_NAME_SERVICE_NAME_ENV_NAME__1");

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes()).isNotNull();
    List<String> routes = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes();
    assertThat(routes).hasSize(2);
    assertThat(routes.contains("R1")).isTrue();
    assertThat(routes.contains("R2")).isTrue();

    ArgumentCaptor<DelegateTask> captor = ArgumentCaptor.forClass(DelegateTask.class);
    verify(delegateService).queueTask(captor.capture());
    DelegateTask delegateTask = captor.getValue();

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData()).isNotNull();
    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames()).isNotNull();
    appNames = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames();
    assertThat(appNames).hasSize(1);
    assertThat(appNames.get(0)).isEqualTo("APP_NAME_SERVICE_NAME_ENV_NAME__1");

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes()).isNotNull();
    routes = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes();
    assertThat(routes).hasSize(2);
    assertThat(routes.contains("R1")).isTrue();
    assertThat(routes.contains("R2")).isTrue();
  }

  @Test
  @Category(UnitTests.class)
  public void testExecute_pcfOldAPP_infra_tempRoute() {
    pcfRouteSwapState.setPcfAppName("${" + PCF_OLD_APP_NAME + "}");
    pcfRouteSwapState.setRoute("${" + INFRA_TEMP_ROUTE + "}");

    on(context).set("serviceTemplateService", serviceTemplateService);
    on(context).set("sweepingOutputService", sweepingOutputService);
    ExecutionResponse executionResponse = pcfRouteSwapState.execute(context);

    assertThat(executionResponse.getExecutionStatus()).isEqualTo(ExecutionStatus.SUCCESS);
    PcfRouteUpdateStateExecutionData stateExecutionData =
        (PcfRouteUpdateStateExecutionData) executionResponse.getStateExecutionData();
    assertThat(stateExecutionData.getCommandName()).isEqualTo(PCF_MAP_ROUTE_COMMAND);
    PcfCommandRouteUpdateRequest pcfCommandRouteUpdateRequest =
        (PcfCommandRouteUpdateRequest) stateExecutionData.getPcfCommandRequest();

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData()).isNotNull();
    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames()).isNotNull();
    List<String> appNames = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames();
    assertThat(appNames).hasSize(1);
    assertThat(appNames.get(0)).isEqualTo("APP_NAME_SERVICE_NAME_ENV_NAME__1");

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes()).isNotNull();
    List<String> routes = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes();
    assertThat(routes).hasSize(1);
    assertThat(routes.contains("R3")).isTrue();

    ArgumentCaptor<DelegateTask> captor = ArgumentCaptor.forClass(DelegateTask.class);
    verify(delegateService).queueTask(captor.capture());
    DelegateTask delegateTask = captor.getValue();

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData()).isNotNull();
    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames()).isNotNull();
    appNames = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames();
    assertThat(appNames).hasSize(1);
    assertThat(appNames.get(0)).isEqualTo("APP_NAME_SERVICE_NAME_ENV_NAME__1");

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes()).isNotNull();
    routes = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes();
    assertThat(routes).hasSize(1);
    assertThat(routes.contains("R3")).isTrue();
  }

  @Test
  @Category(UnitTests.class)
  public void testExecute_pcfAPP_Switch_BG_routes() {
    on(context).set("serviceTemplateService", serviceTemplateService);
    on(context).set("sweepingOutputService", sweepingOutputService);
    ExecutionResponse executionResponse = pcfSwitchBlueGreenRoutes.execute(context);

    assertThat(executionResponse.getExecutionStatus()).isEqualTo(ExecutionStatus.SUCCESS);
    PcfRouteUpdateStateExecutionData stateExecutionData =
        (PcfRouteUpdateStateExecutionData) executionResponse.getStateExecutionData();
    assertThat(stateExecutionData.getCommandName()).isEqualTo(PCF_BG_SWAP_ROUTE_COMMAND);
    PcfCommandRouteUpdateRequest pcfCommandRouteUpdateRequest =
        (PcfCommandRouteUpdateRequest) stateExecutionData.getPcfCommandRequest();

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData()).isNotNull();
    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getNewApplicatiaonName()).isNotNull();
    String appName = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getNewApplicatiaonName();
    assertThat(appName).isEqualTo("APP_NAME_SERVICE_NAME_ENV_NAME__2");

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes()).isNotNull();
    List<String> routes = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes();
    assertThat(routes).hasSize(2);
    assertThat(routes.contains("R1")).isTrue();
    assertThat(routes.contains("R2")).isTrue();

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames()).isNotNull();
    List<String> appNames = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames();
    assertThat(appNames).hasSize(1);
    assertThat(appNames.contains("APP_NAME_SERVICE_NAME_ENV_NAME__1")).isTrue();

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getTempRoutes()).isNotNull();
    routes = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getTempRoutes();
    assertThat(routes).hasSize(1);
    assertThat(routes.contains("R3")).isTrue();

    ArgumentCaptor<DelegateTask> captor = ArgumentCaptor.forClass(DelegateTask.class);
    verify(delegateService).queueTask(captor.capture());
    DelegateTask delegateTask = captor.getValue();

    pcfCommandRouteUpdateRequest = (PcfCommandRouteUpdateRequest) delegateTask.getData().getParameters()[0];
    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData()).isNotNull();
    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getNewApplicatiaonName()).isNotNull();
    appName = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getNewApplicatiaonName();
    assertThat(appName).isEqualTo("APP_NAME_SERVICE_NAME_ENV_NAME__2");

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes()).isNotNull();
    routes = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getFinalRoutes();
    assertThat(routes).hasSize(2);
    assertThat(routes.contains("R1")).isTrue();
    assertThat(routes.contains("R2")).isTrue();

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames()).isNotNull();
    appNames = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getExistingApplicationNames();
    assertThat(appNames).hasSize(1);
    assertThat(appNames.contains("APP_NAME_SERVICE_NAME_ENV_NAME__1")).isTrue();

    assertThat(pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getTempRoutes()).isNotNull();
    routes = pcfCommandRouteUpdateRequest.getPcfRouteUpdateConfigData().getTempRoutes();
    assertThat(routes).hasSize(1);
    assertThat(routes.contains("R3")).isTrue();
  }
}
