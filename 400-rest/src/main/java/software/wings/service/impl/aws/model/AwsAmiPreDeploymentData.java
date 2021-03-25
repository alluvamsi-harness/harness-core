package software.wings.service.impl.aws.model;

import static io.harness.annotations.dev.HarnessTeam.CDP;
import static io.harness.data.structure.HasPredicate.hasSome;

import static java.util.Collections.emptyList;

import io.harness.annotations.dev.HarnessModule;
import io.harness.annotations.dev.OwnedBy;
import io.harness.annotations.dev.TargetModule;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TargetModule(HarnessModule._950_DELEGATE_TASKS_BEANS)
@OwnedBy(CDP)
public class AwsAmiPreDeploymentData {
  private String oldAsgName;
  private Integer minCapacity;
  private Integer desiredCapacity;
  private List<String> scalingPolicyJSON;

  public static final int DEFAULT_DESIRED_COUNT = 10;

  public int getPreDeploymentMinCapacity() {
    return minCapacity == null ? 0 : minCapacity;
  }

  public int getPreDeploymentDesiredCapacity() {
    return desiredCapacity == null ? DEFAULT_DESIRED_COUNT : desiredCapacity;
  }

  public boolean hasAsgReachedPreDeploymentCount(int desiredCount) {
    if (desiredCapacity == null) {
      return false;
    }

    return desiredCapacity.intValue() == desiredCount;
  }

  public List<String> getPreDeploymenyScalingPolicyJSON() {
    return hasSome(scalingPolicyJSON) ? scalingPolicyJSON : emptyList();
  }
}
