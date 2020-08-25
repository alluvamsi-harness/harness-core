package io.harness.cvng.core.entities;

import static io.harness.data.structure.UUIDGenerator.generateUuid;
import static io.harness.rule.OwnerRule.KAMAL;
import static io.harness.rule.OwnerRule.NEMANJA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.harness.CategoryTest;
import io.harness.category.element.UnitTests;
import io.harness.cvng.beans.CVMonitoringCategory;
import io.harness.cvng.core.beans.TimeRange;
import io.harness.cvng.models.VerificationType;
import io.harness.rule.Owner;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.Instant;

public class LogCVConfigTest extends CategoryTest {
  private String accountId;
  private String connectorId;
  private String productName;
  private String groupId;
  private String serviceInstanceIdentifier;

  @Before
  public void setup() {
    this.accountId = generateUuid();
    this.connectorId = generateUuid();
    this.productName = generateUuid();
    this.groupId = generateUuid();
    this.serviceInstanceIdentifier = generateUuid();
  }

  @Test
  @Owner(developers = NEMANJA)
  @Category(UnitTests.class)
  public void testValidateParams_whenQueryIsUndefined() {
    LogCVConfig logCVConfig = createCVConfig();
    assertThatThrownBy(() -> logCVConfig.validate())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("query should not be null");
  }

  @Test
  @Owner(developers = KAMAL)
  @Category(UnitTests.class)
  public void testGetBaseline_whenCreatedAtIsNotSet() {
    LogCVConfig logCVConfig = createCVConfig();
    assertThatThrownBy(() -> logCVConfig.getBaseline())
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("CreatedAt needs to be set to get the baseline");
  }

  @Test
  @Owner(developers = KAMAL)
  @Category(UnitTests.class)
  public void testGetBaseline_whenCreatedAtIsSet() {
    LogCVConfig logCVConfig = createCVConfig();
    logCVConfig.setCreatedAt(Instant.parse("2020-04-22T10:02:06Z").toEpochMilli());
    assertThat(logCVConfig.getBaseline())
        .isEqualTo(TimeRange.builder()
                       .endTime(Instant.parse("2020-04-22T10:00:00Z"))
                       .startTime(Instant.parse("2020-04-22T09:30:00Z"))
                       .build());
  }

  @Test
  @Owner(developers = KAMAL)
  @Category(UnitTests.class)
  public void testGetFirstTimeDataCollectionTimeRange_whenCreatedAtIsSet() {
    LogCVConfig logCVConfig = createCVConfig();
    logCVConfig.setCreatedAt(Instant.parse("2020-04-22T10:02:06Z").toEpochMilli());
    assertThat(logCVConfig.getFirstTimeDataCollectionTimeRange())
        .isEqualTo(TimeRange.builder()
                       .endTime(Instant.parse("2020-04-22T09:35:00Z"))
                       .startTime(Instant.parse("2020-04-22T09:30:00Z"))
                       .build());
  }

  private LogCVConfig createCVConfig() {
    SplunkCVConfig cvConfig = new SplunkCVConfig();
    fillCommon(cvConfig);
    cvConfig.setServiceInstanceIdentifier(serviceInstanceIdentifier);
    return cvConfig;
  }

  private void fillCommon(LogCVConfig cvConfig) {
    cvConfig.setVerificationType(VerificationType.LOG);
    cvConfig.setAccountId(accountId);
    cvConfig.setConnectorId(connectorId);
    cvConfig.setServiceIdentifier(generateUuid());
    cvConfig.setEnvIdentifier(generateUuid());
    cvConfig.setProjectIdentifier(generateUuid());
    cvConfig.setGroupId(groupId);
    cvConfig.setCategory(CVMonitoringCategory.PERFORMANCE);
    cvConfig.setProductName(productName);
  }
}
