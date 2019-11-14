package software.wings.resources;

import static io.harness.rule.OwnerRule.JATIN;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static software.wings.beans.Application.Builder.anApplication;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import io.harness.category.element.IntegrationTests;
import io.harness.limits.ActionType;
import io.harness.limits.configuration.LimitConfigurationService;
import io.harness.limits.impl.model.StaticLimit;
import io.harness.rule.OwnerRule.Owner;
import io.harness.rule.Repeat;
import lombok.val;
import migrations.InitializeAppCounters;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.wings.beans.Application;
import software.wings.beans.Application.ApplicationKeys;
import software.wings.dl.WingsPersistence;
import software.wings.integration.BaseIntegrationTest;
import software.wings.integration.IntegrationTestUtils;
import software.wings.utils.WingsIntegrationTestConstants;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response.Status;

public class AppResourceIntegrationTest extends BaseIntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(AppResourceIntegrationTest.class);

  @Inject private LimitConfigurationService limitConfigurationService;
  @Inject private WingsPersistence persistence;
  @Inject private InitializeAppCounters initializeAppCounters;

  private final ExecutorService executors = Executors.newFixedThreadPool(5);

  @Before
  public void init() throws Exception {
    initializeAppCounters.migrate();
    super.setUp();
    loginAdminUser();
  }

  @After
  public void cleanUp() {
    final Query<Application> query = fetchAppsQuery();
    val ds = persistence.getDatastore(query.getEntityClass());
    ds.delete(query);
    initializeAppCounters.migrate();
  }

  @Test
  @Owner(developers = JATIN)
  @Repeat(times = 10, successes = 10)
  @Category(IntegrationTests.class)
  @Ignore("TODO: please provide clear motivation why this test is ignored")
  public void testLimitsEnforcement() throws Exception {
    long appCount = appCount(WingsIntegrationTestConstants.INTEGRATION_TEST_ACCOUNT_ID);

    // configure limit to restrict number of apps
    int maxApps = 10;
    val configured = limitConfigurationService.configure(WingsIntegrationTestConstants.INTEGRATION_TEST_ACCOUNT_ID,
        ActionType.CREATE_APPLICATION, new StaticLimit(maxApps));
    assertThat(configured).isTrue();

    val url = IntegrationTestUtils.buildAbsoluteUrl(
        "/api/apps", ImmutableMap.of("accountId", WingsIntegrationTestConstants.INTEGRATION_TEST_ACCOUNT_ID));
    log.debug("Create URL to hit: {}", url);
    WebTarget target = client.target(url);

    Application app = null;
    for (int i = 0; i < maxApps - appCount; i++) {
      app = sampleApp();
      int status = getRequestBuilderWithAuthHeader(target).post(entity(app, APPLICATION_JSON)).getStatus();
      assertThat(status).isEqualTo(Status.OK.getStatusCode());
    }

    int status = getRequestBuilderWithAuthHeader(target).post(entity(sampleApp(), APPLICATION_JSON)).getStatus();
    assertThat(status).isEqualTo(Status.FORBIDDEN.getStatusCode());

    // delete an app
    String deleteUrl = IntegrationTestUtils.buildAbsoluteUrl("/api/apps/" + app.getAppId(), new HashMap<>());
    WebTarget deleteTarget = client.target(deleteUrl);
    log.debug("Delete URL to hit: {}", deleteUrl);

    status = getRequestBuilderWithAuthHeader(deleteTarget).delete().getStatus();
    assertThat(Status.OK.getStatusCode()).isEqualTo(status);

    // user should be able to create new app now after an existing app is deleted
    status = getRequestBuilderWithAuthHeader(target).post(entity(sampleApp(), APPLICATION_JSON)).getStatus();
    assertThat(Status.OK.getStatusCode()).isEqualTo(status);
  }

  private long appCount(String accountId) {
    return wingsPersistence.createQuery(Application.class).filter(ApplicationKeys.accountId, accountId).count();
  }

  @Test
  @Owner(developers = JATIN)
  @Repeat(times = 10, successes = 10)
  @Category(IntegrationTests.class)
  @Ignore("TODO: please provide clear motivation why this test is ignored")
  public void testLimitsEnforcementConcurrent() throws Exception {
    long appCount = appCount(WingsIntegrationTestConstants.INTEGRATION_TEST_ACCOUNT_ID);

    // configure limit to restrict number of apps
    int maxApps = 10;
    val configured = limitConfigurationService.configure(WingsIntegrationTestConstants.INTEGRATION_TEST_ACCOUNT_ID,
        ActionType.CREATE_APPLICATION, new StaticLimit(maxApps));
    assertThat(configured).isTrue();

    val url = IntegrationTestUtils.buildAbsoluteUrl(
        "/api/apps", ImmutableMap.of("accountId", WingsIntegrationTestConstants.INTEGRATION_TEST_ACCOUNT_ID));
    log.debug("Create URL to hit: {}", url);
    WebTarget target = client.target(url);

    concurrentConsume(target, maxApps - appCount);

    int status = getRequestBuilderWithAuthHeader(target).post(entity(sampleApp(), APPLICATION_JSON)).getStatus();
    assertThat(status).isEqualTo(Status.FORBIDDEN.getStatusCode());

    // delete an app
    Application appToDelete = fetchAppsQuery().get();
    String deleteUrl = IntegrationTestUtils.buildAbsoluteUrl("/api/apps/" + appToDelete.getAppId(), new HashMap<>());
    WebTarget deleteTarget = client.target(deleteUrl);
    log.debug("Delete URL to hit: {}", deleteUrl);

    status = getRequestBuilderWithAuthHeader(deleteTarget).delete().getStatus();
    assertThat(Status.OK.getStatusCode()).isEqualTo(status);

    // user should be able to create new app now after an existing app is deleted
    status = getRequestBuilderWithAuthHeader(target).post(entity(sampleApp(), APPLICATION_JSON)).getStatus();
    assertThat(Status.OK.getStatusCode()).isEqualTo(status);
  }

  private void concurrentConsume(WebTarget target, long times) throws Exception {
    List<Future> futures = new LinkedList<>();

    for (int i = 0; i < times; i++) {
      Application app = sampleApp();
      Future f = executors.submit(() -> {
        int status = getRequestBuilderWithAuthHeader(target).post(entity(app, APPLICATION_JSON)).getStatus();
        assertThat(status).isEqualTo(Status.OK.getStatusCode());
      });

      futures.add(f);
    }

    for (Future f : futures) {
      f.get(1, TimeUnit.MINUTES);
    }
  }

  // get apps created by this test case
  private Query<Application> fetchAppsQuery() {
    return persistence.createQuery(Application.class)
        .field("_id")
        .endsWithIgnoreCase(AppResourceIntegrationTest.class.getSimpleName());
  }

  private Application sampleApp() throws InterruptedException {
    val rand = ThreadLocalRandom.current().nextInt(1000, 10000) + "-" + RandomStringUtils.randomAlphanumeric(6);
    val appName = "appName-" + rand + "-" + AppResourceIntegrationTest.class.getSimpleName();
    val appId = "appId-" + rand + "-" + AppResourceIntegrationTest.class.getSimpleName();

    return anApplication()
        .name(appName)
        .accountId(WingsIntegrationTestConstants.INTEGRATION_TEST_ACCOUNT_ID)
        .uuid(appId)
        .appId(appId)
        .build();
  }
}