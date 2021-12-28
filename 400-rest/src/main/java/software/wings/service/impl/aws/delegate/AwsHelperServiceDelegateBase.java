package software.wings.service.impl.aws.delegate;

import static software.wings.service.impl.aws.model.AwsConstants.AWS_DEFAULT_REGION;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import io.harness.annotations.dev.HarnessModule;
import io.harness.annotations.dev.TargetModule;
import io.harness.aws.AwsCallTracker;
import io.harness.aws.beans.AwsInternalConfig;

import software.wings.beans.AwsConfig;
import software.wings.cloudprovider.aws.AwsClusterService;
import software.wings.service.impl.delegate.AwsEcrApiHelperServiceDelegateBase;
import software.wings.service.intfc.security.EncryptionService;
import software.wings.service.mappers.artifact.AwsConfigToInternalMapper;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.autoscaling.model.TagDescription;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@TargetModule(HarnessModule._930_DELEGATE_TASKS)
class AwsHelperServiceDelegateBase {
  @VisibleForTesting static final String HARNESS_AUTOSCALING_GROUP_TAG = "HARNESS_REVISION";
  @Inject protected EncryptionService encryptionService;
  @Inject protected AwsCallTracker tracker;
  @Inject protected AwsEcrApiHelperServiceDelegateBase awsEcrApiHelperServiceDelegateBase;
  @Inject protected AwsClusterService awsClusterService;

  protected void attachCredentialsAndBackoffPolicy(AwsClientBuilder builder, AwsConfig awsConfig) {
    awsEcrApiHelperServiceDelegateBase.attachCredentialsAndBackoffPolicy(
        builder, AwsConfigToInternalMapper.toAwsInternalConfig(awsConfig));
  }

  protected final <T extends AwsClientBuilder<T, U>, U> void attachCredentialsAndBackoffPolicy(
      AwsClientBuilder<T, U> builder, AwsInternalConfig awsInternalConfig) {
    awsEcrApiHelperServiceDelegateBase.attachCredentialsAndBackoffPolicy(builder, awsInternalConfig);
  }

  @VisibleForTesting
  void handleAmazonClientException(AmazonClientException amazonClientException) {
    awsEcrApiHelperServiceDelegateBase.handleAmazonClientException(amazonClientException);
  }

  @VisibleForTesting
  void handleAmazonServiceException(AmazonServiceException amazonServiceException) {
    awsEcrApiHelperServiceDelegateBase.handleAmazonServiceException(amazonServiceException);
  }
  protected boolean isHarnessManagedTag(String infraMappingId, TagDescription tagDescription) {
    return tagDescription.getKey().equals(HARNESS_AUTOSCALING_GROUP_TAG)
        && tagDescription.getValue().startsWith(infraMappingId);
  }
  protected String getRegion(AwsConfig awsConfig) {
    if (isNotBlank(awsConfig.getDefaultRegion())) {
      return awsConfig.getDefaultRegion();
    } else {
      return AWS_DEFAULT_REGION;
    }
  }
}
