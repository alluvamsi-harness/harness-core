package io.harness.aws.cf;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeployStackResult {
  private boolean noUpdatesToPerform;
  private Status status;
  private String statusReason;
}
