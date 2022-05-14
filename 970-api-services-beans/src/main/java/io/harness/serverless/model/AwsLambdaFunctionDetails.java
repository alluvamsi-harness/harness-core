package io.harness.serverless.model;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;

import lombok.Builder;
import lombok.Data;

@OwnedBy(HarnessTeam.CDP)
@Data
@Builder
public class AwsLambdaFunctionDetails {
  private String functionName;
  private String handler;
  private String memorySize;
  private String runTime;
  private Integer timeout;
}
