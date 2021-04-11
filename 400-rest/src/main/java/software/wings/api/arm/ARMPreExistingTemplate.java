package software.wings.api.arm;

import io.harness.beans.SweepingOutput;
import io.harness.delegate.task.azure.arm.AzureARMPreDeploymentData;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonTypeName("armPreExistingTemplate")
public class ARMPreExistingTemplate implements SweepingOutput {
  private final AzureARMPreDeploymentData preDeploymentData;

  @Override
  public String getType() {
    return "armPreExistingTemplate";
  }
}
