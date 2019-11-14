package software.wings.service.intfc.aws;

import static io.harness.rule.OwnerRule.UNKNOWN;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;

import com.amazonaws.services.applicationautoscaling.model.PredefinedMetricSpecification;
import com.amazonaws.services.applicationautoscaling.model.ScalableTarget;
import com.amazonaws.services.applicationautoscaling.model.ScalingPolicy;
import com.amazonaws.services.applicationautoscaling.model.TargetTrackingScalingPolicyConfiguration;
import io.harness.category.element.UnitTests;
import io.harness.rule.OwnerRule.Owner;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import software.wings.WingsBaseTest;
import software.wings.service.intfc.aws.delegate.AwsAppAutoScalingHelperServiceDelegate;

import java.util.List;

public class AwsAppAutoScalingHelperServiceDelegateTest extends WingsBaseTest {
  @Inject private AwsAppAutoScalingHelperServiceDelegate scalingHelperServiceDelegate;

  @Test
  @Owner(developers = UNKNOWN)
  @Category(UnitTests.class)
  public void testGetJsonForAwsScalableTarget() throws Exception {
    String json = "{\n"
        + "            \"ServiceNamespace\": \"ecs\",\n"
        + "            \"ScalableDimension\": \"ecs:service:DesiredCount\",\n"
        + "            \"MinCapacity\": 2,\n"
        + "            \"MaxCapacity\": 5,\n"
        + "\"RoleARN\":\"RollARN\"\n"
        + "        }";

    ScalableTarget scalableTarget = scalingHelperServiceDelegate.getScalableTargetFromJson(json);
    assertThat(scalableTarget).isNotNull();
    assertThat(scalableTarget.getServiceNamespace()).isEqualTo("ecs");
    assertThat(scalableTarget.getScalableDimension()).isEqualTo("ecs:service:DesiredCount");
    assertThat(scalableTarget.getMinCapacity().intValue()).isEqualTo(2);
    assertThat(scalableTarget.getMaxCapacity().intValue()).isEqualTo(5);
    assertThat(scalableTarget.getRoleARN()).isEqualTo("RollARN");
  }

  @Test
  @Owner(developers = UNKNOWN)
  @Category(UnitTests.class)
  public void testGetJsonForAwsScalingPolicy() throws Exception {
    String json = "{\n"
        + "            \"ScalableDimension\": \"ecs:service:DesiredCount\",\n"
        + "            \"ServiceNamespace\": \"ecs\",\n"
        + "            \"PolicyName\": \"TrackingPolicyTest\",\n"
        + "            \"PolicyType\": \"TargetTrackingScaling\",\n"
        + "            \"TargetTrackingScalingPolicyConfiguration\": {\n"
        + "                \"TargetValue\": 60.0,\n"
        + "                \"PredefinedMetricSpecification\": {\n"
        + "                    \"PredefinedMetricType\": \"ECSServiceAverageCPUUtilization\"\n"
        + "                },\n"
        + "                \"ScaleOutCooldown\": 300,\n"
        + "                \"ScaleInCooldown\": 300\n"
        + "            }"
        + "        }";

    List<ScalingPolicy> scalingPolicies = scalingHelperServiceDelegate.getScalingPolicyFromJson(json);
    assertThat(scalingPolicies).isNotNull();
    assertThat(scalingPolicies).hasSize(1);

    ScalingPolicy scalingPolicy = scalingPolicies.get(0);
    validateScalingPolicy("TrackingPolicyTest", "ECSServiceAverageCPUUtilization", scalingPolicy);
  }

  @Test
  @Owner(developers = UNKNOWN)
  @Category(UnitTests.class)
  public void testGetJsonForAwsScalingPolicies() throws Exception {
    String json = "  [ {\n"
        + "            \"ScalableDimension\": \"ecs:service:DesiredCount\",\n"
        + "            \"ServiceNamespace\": \"ecs\",\n"
        + "            \"PolicyName\": \"TrackingPolicyTest\",\n"
        + "            \"PolicyType\": \"TargetTrackingScaling\",\n"
        + "            \"TargetTrackingScalingPolicyConfiguration\": {\n"
        + "                \"TargetValue\": 60.0,\n"
        + "                \"PredefinedMetricSpecification\": {\n"
        + "                    \"PredefinedMetricType\": \"ECSServiceAverageCPUUtilization\"\n"
        + "                },\n"
        + "                \"ScaleOutCooldown\": 300,\n"
        + "                \"ScaleInCooldown\": 300\n"
        + "            }"
        + "        },{"
        + "            \"ScalableDimension\": \"ecs:service:DesiredCount\",\n"
        + "            \"ServiceNamespace\": \"ecs\",\n"
        + "            \"PolicyName\": \"TrackingPolicyTest2\",\n"
        + "            \"PolicyType\": \"TargetTrackingScaling\",\n"
        + "            \"TargetTrackingScalingPolicyConfiguration\": {\n"
        + "                \"TargetValue\": 60.0,\n"
        + "                \"PredefinedMetricSpecification\": {\n"
        + "                    \"PredefinedMetricType\": \"ECSServiceAverageMemoryUtilization\"\n"
        + "                },\n"
        + "                \"ScaleOutCooldown\": 300,\n"
        + "                \"ScaleInCooldown\": 300\n"
        + "            }"
        + "        }]  ";

    List<ScalingPolicy> scalingPolicies = scalingHelperServiceDelegate.getScalingPolicyFromJson(json);
    assertThat(scalingPolicies).isNotNull();
    assertThat(scalingPolicies).hasSize(2);

    ScalingPolicy scalingPolicy = scalingPolicies.get(0);
    validateScalingPolicy("TrackingPolicyTest", "ECSServiceAverageCPUUtilization", scalingPolicy);

    scalingPolicy = scalingPolicies.get(1);
    validateScalingPolicy("TrackingPolicyTest2", "ECSServiceAverageMemoryUtilization", scalingPolicy);
  }

  private void validateScalingPolicy(String name, String predefinedMetricType, ScalingPolicy scalingPolicy) {
    assertThat(scalingPolicy.getPolicyName()).isEqualTo(name);
    assertThat(scalingPolicy.getServiceNamespace()).isEqualTo("ecs");
    assertThat(scalingPolicy.getScalableDimension()).isEqualTo("ecs:service:DesiredCount");

    assertThat(scalingPolicy.getTargetTrackingScalingPolicyConfiguration()).isNotNull();
    TargetTrackingScalingPolicyConfiguration configuration =
        scalingPolicy.getTargetTrackingScalingPolicyConfiguration();

    assertThat(configuration.getTargetValue().intValue()).isEqualTo(60);
    assertThat(configuration.getScaleInCooldown().intValue()).isEqualTo(300);
    assertThat(configuration.getScaleOutCooldown().intValue()).isEqualTo(300);

    assertThat(configuration.getPredefinedMetricSpecification()).isNotNull();
    PredefinedMetricSpecification metricSpecification = configuration.getPredefinedMetricSpecification();
    assertThat(metricSpecification.getPredefinedMetricType()).isEqualTo(predefinedMetricType);
  }
}
