package software.wings.service.intfc.aws.delegate;

import static io.harness.annotations.dev.HarnessTeam.CDP;

import io.harness.annotations.dev.OwnedBy;
import io.harness.aws.beans.AwsInternalConfig;
import io.harness.serverless.model.AwsLambdaFunctionDetails;

@OwnedBy(CDP)
public interface AwsLambdaHelperServiceDelegateNG {
  AwsLambdaFunctionDetails getAwsLambdaFunctionDetails(
      AwsInternalConfig awsInternalConfig, String function, String region);
}
