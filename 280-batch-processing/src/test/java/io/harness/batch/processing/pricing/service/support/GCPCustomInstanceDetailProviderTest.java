package io.harness.batch.processing.pricing.service.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;

import io.harness.CategoryTest;
import io.harness.batch.processing.pricing.data.VMComputePricingInfo;
import io.harness.category.element.UnitTests;
import io.harness.rule.Owner;
import io.harness.rule.OwnerRule;

import org.junit.Test;
import org.junit.experimental.categories.Category;

public class GCPCustomInstanceDetailProviderTest extends CategoryTest {
  private static final double MAX_RELATIVE_ERROR_PCT = 5;
  private static final String REGION = "us-west-1";

  @Test
  @Owner(developers = OwnerRule.UTSAV)
  @Category(UnitTests.class)
  public void testGetE2CustomVMPricingInfo() throws Exception {
    final String instanceType = "e2-custom-12-32768";

    final double cpuUnit = 12.0D;
    final double memoryUnit = 32.0D;

    VMComputePricingInfo pricingInfo = GCPCustomInstanceDetailProvider.getCustomVMPricingInfo(instanceType, REGION);

    assertThat(pricingInfo.getCpusPerVm()).isCloseTo(cpuUnit, withinPercentage(MAX_RELATIVE_ERROR_PCT));
    assertThat(pricingInfo.getMemPerVm()).isCloseTo(memoryUnit, withinPercentage(MAX_RELATIVE_ERROR_PCT));

    double onDemandPrice = cpuUnit * 0.022890D + memoryUnit * 0.003067D;
    assertThat(pricingInfo.getOnDemandPrice()).isCloseTo(onDemandPrice, withinPercentage(MAX_RELATIVE_ERROR_PCT));

    double spotPrice = cpuUnit * 0.006867D + memoryUnit * 0.000920D;
    assertThat(pricingInfo.getSpotPrice().get(0).getPrice())
        .isCloseTo(spotPrice, withinPercentage(MAX_RELATIVE_ERROR_PCT));
  }

  @Test
  @Owner(developers = OwnerRule.UTSAV)
  @Category(UnitTests.class)
  public void testGetN2CustomVMPricingInfo() throws Exception {
    final String instanceType = "n2-custom-6-3072";

    final double cpuUnit = 6.0D;
    final double memoryUnit = 3.0D;

    VMComputePricingInfo pricingInfo = GCPCustomInstanceDetailProvider.getCustomVMPricingInfo(instanceType, REGION);

    assertThat(pricingInfo.getCpusPerVm()).isCloseTo(cpuUnit, withinPercentage(MAX_RELATIVE_ERROR_PCT));
    assertThat(pricingInfo.getMemPerVm()).isCloseTo(memoryUnit, withinPercentage(MAX_RELATIVE_ERROR_PCT));

    double onDemandPrice = cpuUnit * 0.033174 + memoryUnit * 0.004446;
    assertThat(pricingInfo.getOnDemandPrice()).isCloseTo(onDemandPrice, withinPercentage(MAX_RELATIVE_ERROR_PCT));

    double spotPrice = cpuUnit * 0.00802 + memoryUnit * 0.00108;
    assertThat(pricingInfo.getSpotPrice().get(0).getPrice())
        .isCloseTo(spotPrice, withinPercentage(MAX_RELATIVE_ERROR_PCT));
  }

  @Test
  @Owner(developers = OwnerRule.UTSAV)
  @Category(UnitTests.class)
  public void testHardCodedInstanceType() throws Exception {
    final String instanceType = "n2-standard-16";

    VMComputePricingInfo pricingInfo = GCPCustomInstanceDetailProvider.getCustomVMPricingInfo(instanceType, REGION);

    assertThat(pricingInfo.getCpusPerVm()).isCloseTo(16D, withinPercentage(MAX_RELATIVE_ERROR_PCT));
    assertThat(pricingInfo.getMemPerVm()).isCloseTo(64D, withinPercentage(MAX_RELATIVE_ERROR_PCT));

    assertThat(pricingInfo.getOnDemandPrice()).isCloseTo(0.7769D, withinPercentage(MAX_RELATIVE_ERROR_PCT));
    assertThat(pricingInfo.getSpotPrice().get(0).getPrice())
        .isCloseTo(0.1880D, withinPercentage(MAX_RELATIVE_ERROR_PCT));
  }
}