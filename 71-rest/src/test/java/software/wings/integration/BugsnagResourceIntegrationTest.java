package software.wings.integration;

import static io.harness.data.structure.UUIDGenerator.generateUuid;
import static io.harness.rule.OwnerRule.UNKNOWN;
import static javax.ws.rs.client.Entity.entity;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.assertj.core.api.Assertions.assertThat;
import static software.wings.beans.Application.Builder.anApplication;
import static software.wings.beans.Workflow.WorkflowBuilder.aWorkflow;
import static software.wings.sm.StateExecutionInstance.Builder.aStateExecutionInstance;

import com.google.inject.Inject;

import io.harness.beans.ExecutionStatus;
import io.harness.category.element.IntegrationTests;
import io.harness.rest.RestResponse;
import io.harness.rule.OwnerRule.Owner;
import io.harness.scm.ScmSecret;
import io.harness.scm.SecretName;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import software.wings.beans.BugsnagConfig;
import software.wings.beans.SettingAttribute.Builder;
import software.wings.beans.WorkflowExecution;
import software.wings.service.impl.bugsnag.BugsnagApplication;
import software.wings.service.impl.bugsnag.BugsnagSetupTestData;
import software.wings.service.intfc.analysis.LogAnalysisResource;
import software.wings.sm.StateType;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Pranjal on 04/09/2019
 */
public class BugsnagResourceIntegrationTest extends BaseIntegrationTest {
  private String settingId;
  private String appId;
  private String workflowId;
  private String workflowExecutionId;
  @Inject private ScmSecret scmSecret;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    loginAdminUser();

    appId = wingsPersistence.save(anApplication().accountId(accountId).name(generateUuid()).build());
    settingId = wingsPersistence.save(
        Builder.aSettingAttribute()
            .withName(generateUuid())
            .withAccountId(accountId)
            .withValue(BugsnagConfig.builder()
                           .url("https://api.bugsnag.com/")
                           .authToken(scmSecret.decryptToCharArray(new SecretName("bugsnag_config_auth_token")))
                           .accountId(accountId)
                           .build())
            .build());

    workflowId = wingsPersistence.save(aWorkflow().appId(appId).name(generateUuid()).build());
    workflowExecutionId = wingsPersistence.save(
        WorkflowExecution.builder().appId(appId).workflowId(workflowId).status(ExecutionStatus.SUCCESS).build());
    wingsPersistence.save(aStateExecutionInstance()
                              .executionUuid(workflowExecutionId)
                              .stateType(StateType.PHASE.name())
                              .appId(appId)
                              .displayName(generateUuid())
                              .build());
  }

  @Test
  @Owner(developers = UNKNOWN)
  @Category(IntegrationTests.class)
  public void testGetBugsnagApplications() {
    WebTarget target = client.target(API_BASE + "/bugsnag"
        + "/applications"
        + "?accountId=" + accountId + "&settingId=" + settingId + "&organizationId="
        + "5c524cedbd0fa2001672ad26");
    RestResponse<Set<BugsnagApplication>> restResponse =
        getRequestBuilderWithAuthHeader(target).get(new GenericType<RestResponse<Set<BugsnagApplication>>>() {});

    assertThat(restResponse.getResponseMessages()).isEmpty();
    assertThat(restResponse.getResource().isEmpty()).isFalse();

    for (BugsnagApplication app : restResponse.getResource()) {
      assertThat(isBlank(app.getName())).isFalse();
    }
  }

  @Test
  @Owner(developers = UNKNOWN)
  @Category(IntegrationTests.class)
  public void testGetBugsnagOrganizations() {
    WebTarget target = client.target(API_BASE + "/bugsnag"
        + "/orgs"
        + "?accountId=" + accountId + "&settingId=" + settingId);
    RestResponse<Set<BugsnagApplication>> restResponse =
        getRequestBuilderWithAuthHeader(target).get(new GenericType<RestResponse<Set<BugsnagApplication>>>() {});

    assertThat(restResponse.getResponseMessages()).isEmpty();
    assertThat(restResponse.getResource().isEmpty()).isFalse();

    for (BugsnagApplication app : restResponse.getResource()) {
      assertThat(isBlank(app.getName())).isFalse();
    }
  }

  @Test
  @Owner(developers = UNKNOWN)
  @Category(IntegrationTests.class)
  public void testGetLogRecords() {
    BugsnagSetupTestData testNodedata = getBugsnagSampledata();
    WebTarget target =
        client.target(API_BASE + "/bugsnag" + LogAnalysisResource.TEST_NODE_DATA + "?accountId=" + accountId);
    Response restResponse =
        getRequestBuilderWithAuthHeader(target).post(entity(testNodedata, MediaType.APPLICATION_JSON));
    String responseString = restResponse.readEntity(String.class);
    JSONObject jsonResponseObject = new JSONObject(responseString);

    JSONObject response = jsonResponseObject.getJSONObject("resource");
    assertThat(HttpStatus.SC_OK).isEqualTo(restResponse.getStatus());
    assertThat(Boolean.valueOf(response.get("providerReachable").toString())).isTrue();
  }

  private BugsnagSetupTestData getBugsnagSampledata() {
    long toTime = System.currentTimeMillis();
    long fromTime = toTime - TimeUnit.MINUTES.toMillis(180);
    return BugsnagSetupTestData.builder()
        .query("*exception*")
        .browserApplication(true)
        .appId(appId)
        .orgId("5c524cedbd0fa2001672ad26")
        .projectId("5ccb6cbfe837a900163d315b")
        .settingId(settingId)
        .instanceName("testHost")
        .stateType(StateType.BUG_SNAG)
        .toTime(toTime)
        .fromTime(fromTime)
        .workflowId(workflowId)
        .build();
  }
}