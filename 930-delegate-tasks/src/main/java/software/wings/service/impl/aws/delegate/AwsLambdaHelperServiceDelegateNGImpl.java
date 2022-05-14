package software.wings.service.impl.aws.delegate;

import static io.harness.annotations.dev.HarnessTeam.CDP;
import static io.harness.serverless.model.AwsLambdaFunctionDetails.AwsLambdaFunctionDetailsBuilder;

import io.harness.annotations.dev.OwnedBy;
import io.harness.aws.beans.AwsInternalConfig;
import io.harness.exception.ExceptionUtils;
import io.harness.exception.InvalidRequestException;
import io.harness.serverless.model.AwsLambdaFunctionDetails;

import software.wings.service.impl.aws.client.CloseableAmazonWebServiceClient;
import software.wings.service.intfc.aws.delegate.AwsLambdaHelperServiceDelegateNG;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.lambda.model.GetFunctionRequest;
import com.amazonaws.services.lambda.model.GetFunctionResult;
import com.amazonaws.services.lambda.model.ResourceNotFoundException;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
@OwnedBy(CDP)
public class AwsLambdaHelperServiceDelegateNGImpl
    extends AwsHelperServiceDelegateBaseNG implements AwsLambdaHelperServiceDelegateNG {
  public AWSLambdaClient getAmazonLambdaClient(String region, AwsInternalConfig awsInternalConfig) {
    AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard().withRegion(region);
    attachCredentialsAndBackoffPolicy(builder, awsInternalConfig);
    return (AWSLambdaClient) builder.build();
  }
  @Override
  public AwsLambdaFunctionDetails getAwsLambdaFunctionDetails(
      AwsInternalConfig awsInternalConfig, String function, String region) {
    GetFunctionResult getFunctionResult = null;
    try (CloseableAmazonWebServiceClient<AWSLambdaClient> closeableAWSLambdaClient =
             new CloseableAmazonWebServiceClient(getAmazonLambdaClient(region, awsInternalConfig))) {
      try {
        getFunctionResult =
            closeableAWSLambdaClient.getClient().getFunction(new GetFunctionRequest().withFunctionName(function));
      } catch (ResourceNotFoundException rnfe) {
        log.info("No function found with name =[{}]. Error Msg is [{}]", function, rnfe.getMessage());
        return null;
      }

    } catch (AmazonServiceException amazonServiceException) {
      handleAmazonServiceException(amazonServiceException);
    } catch (AmazonClientException amazonClientException) {
      handleAmazonClientException(amazonClientException);
    } catch (Exception e) {
      throw new InvalidRequestException(ExceptionUtils.getMessage(e), e);
    }

    AwsLambdaFunctionDetailsBuilder awsLambdaFunctionDetailsBuilder = AwsLambdaFunctionDetails.builder();

    if (getFunctionResult != null && getFunctionResult.getConfiguration() != null) {
      FunctionConfiguration functionConfiguration = getFunctionResult.getConfiguration();
      awsLambdaFunctionDetailsBuilder.functionName(functionConfiguration.getFunctionName());
      awsLambdaFunctionDetailsBuilder.handler(functionConfiguration.getHandler());
      awsLambdaFunctionDetailsBuilder.runTime(functionConfiguration.getRuntime());
      awsLambdaFunctionDetailsBuilder.timeout(functionConfiguration.getTimeout());
    }

    return awsLambdaFunctionDetailsBuilder.build();
  }
}
