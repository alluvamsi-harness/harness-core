/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.aws;

import static io.harness.annotations.dev.HarnessTeam.CDP;
import static io.harness.data.structure.EmptyPredicate.isNotEmpty;

import static java.util.Collections.emptyList;

import io.harness.annotations.dev.OwnedBy;
import io.harness.aws.beans.AwsInternalConfig;
import io.harness.exception.ExceptionUtils;
import io.harness.exception.InvalidRequestException;

import software.wings.service.impl.AwsApiHelperService;
import software.wings.service.impl.aws.client.CloseableAmazonWebServiceClient;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import com.amazonaws.services.cloudformation.model.CreateStackResult;
import com.amazonaws.services.cloudformation.model.DeleteStackRequest;
import com.amazonaws.services.cloudformation.model.DescribeStackEventsRequest;
import com.amazonaws.services.cloudformation.model.DescribeStackEventsResult;
import com.amazonaws.services.cloudformation.model.DescribeStackResourcesRequest;
import com.amazonaws.services.cloudformation.model.DescribeStackResourcesResult;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.StackEvent;
import com.amazonaws.services.cloudformation.model.StackResource;
import com.amazonaws.services.cloudformation.model.UpdateStackRequest;
import com.amazonaws.services.cloudformation.model.UpdateStackResult;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@OwnedBy(CDP)
@Singleton
@Slf4j

public class AWSCloudformationClientImpl implements AWSCloudformationClient {
  @Inject AwsApiHelperService awsApiHelperService;
  @Inject private AwsCallTracker tracker;

  @Override
  public Optional<Stack> getStack(
      String region, DescribeStacksRequest describeStacksRequest, AwsInternalConfig awsConfig) {
    List<Stack> stacks = getAllStacks(region, describeStacksRequest, awsConfig);
    return isNotEmpty(stacks) ? Optional.of(stacks.get(0)) : Optional.empty();
  }

  @Override
  public List<Stack> getAllStacks(
      String region, DescribeStacksRequest describeStacksRequest, AwsInternalConfig awsConfig) {
    try (CloseableAmazonWebServiceClient<AmazonCloudFormationClient> closeableAmazonCloudFormationClient =
             new CloseableAmazonWebServiceClient(getAmazonCloudFormationClient(Regions.fromName(region), awsConfig))) {
      List<Stack> stacks = new ArrayList<>();
      String nextToken = null;
      do {
        describeStacksRequest.withNextToken(nextToken);
        tracker.trackCFCall("Describe Stacks");
        DescribeStacksResult result =
            closeableAmazonCloudFormationClient.getClient().describeStacks(describeStacksRequest);
        nextToken = result.getNextToken();
        stacks.addAll(result.getStacks());
      } while (nextToken != null);
      return stacks;
    } catch (AmazonServiceException amazonServiceException) {
      awsApiHelperService.handleAmazonServiceException(amazonServiceException);
    } catch (AmazonClientException amazonClientException) {
      awsApiHelperService.handleAmazonClientException(amazonClientException);
    } catch (Exception e) {
      log.error("Exception getAllStacks", e);
      throw new InvalidRequestException(ExceptionUtils.getMessage(e), e);
    }
    return emptyList();
  }

  @Override
  public void deleteStack(String region, DeleteStackRequest deleteStackRequest, AwsInternalConfig awsConfig) {
    try (CloseableAmazonWebServiceClient<AmazonCloudFormationClient> closeableAmazonCloudFormationClient =
             new CloseableAmazonWebServiceClient(getAmazonCloudFormationClient(Regions.fromName(region), awsConfig))) {
      tracker.trackCFCall("Delete Stack");
      closeableAmazonCloudFormationClient.getClient().deleteStack(deleteStackRequest);
    } catch (AmazonServiceException amazonServiceException) {
      awsApiHelperService.handleAmazonServiceException(amazonServiceException);
    } catch (AmazonClientException amazonClientException) {
      awsApiHelperService.handleAmazonClientException(amazonClientException);
    } catch (Exception e) {
      log.error("Exception deleteStack", e);
      throw new InvalidRequestException(ExceptionUtils.getMessage(e), e);
    }
  }

  @Override
  public List<StackResource> getAllStackResources(
      String region, DescribeStackResourcesRequest describeStackResourcesRequest, AwsInternalConfig awsConfig) {
    AmazonCloudFormationClient cloudFormationClient =
        getAmazonCloudFormationClient(Regions.fromName(region), awsConfig);
    try {
      tracker.trackCFCall("Describe Stack Events");
      DescribeStackResourcesResult result = cloudFormationClient.describeStackResources(describeStackResourcesRequest);
      return result.getStackResources();
    } catch (AmazonServiceException amazonServiceException) {
      awsApiHelperService.handleAmazonServiceException(amazonServiceException);
    } catch (AmazonClientException amazonClientException) {
      awsApiHelperService.handleAmazonClientException(amazonClientException);
    }
    return emptyList();
  }

  @Override
  public List<StackEvent> getAllStackEvents(
      String region, DescribeStackEventsRequest describeStackEventsRequest, AwsInternalConfig awsConfig) {
    try (CloseableAmazonWebServiceClient<AmazonCloudFormationClient> closeableAmazonCloudFormationClient =
             new CloseableAmazonWebServiceClient(getAmazonCloudFormationClient(Regions.fromName(region), awsConfig))) {
      List<StackEvent> stacksEvents = new ArrayList<>();
      String nextToken = null;
      do {
        describeStackEventsRequest.withNextToken(nextToken);
        tracker.trackCFCall("Describe Stack Events");
        DescribeStackEventsResult result =
            closeableAmazonCloudFormationClient.getClient().describeStackEvents(describeStackEventsRequest);
        nextToken = result.getNextToken();
        stacksEvents.addAll(result.getStackEvents());
      } while (nextToken != null);
      return stacksEvents;
    } catch (AmazonServiceException amazonServiceException) {
      awsApiHelperService.handleAmazonServiceException(amazonServiceException);
    } catch (AmazonClientException amazonClientException) {
      awsApiHelperService.handleAmazonClientException(amazonClientException);
    } catch (Exception e) {
      log.error("Exception getAllStackEvents", e);
      throw new InvalidRequestException(ExceptionUtils.getMessage(e), e);
    }
    return emptyList();
  }

  @Override
  public CreateStackResult createStack(
      String region, CreateStackRequest createStackRequest, AwsInternalConfig awsConfig) {
    try (CloseableAmazonWebServiceClient<AmazonCloudFormationClient> closeableAmazonCloudFormationClient =
             new CloseableAmazonWebServiceClient(getAmazonCloudFormationClient(Regions.fromName(region), awsConfig))) {
      tracker.trackCFCall("Create Stack");
      return closeableAmazonCloudFormationClient.getClient().createStack(createStackRequest);
    } catch (AmazonServiceException amazonServiceException) {
      awsApiHelperService.handleAmazonServiceException(amazonServiceException);
    } catch (AmazonClientException amazonClientException) {
      awsApiHelperService.handleAmazonClientException(amazonClientException);
    } catch (Exception e) {
      log.error("Exception createStack", e);
      throw new InvalidRequestException(ExceptionUtils.getMessage(e), e);
    }
    return new CreateStackResult();
  }

  @Override
  public UpdateStackResult updateStack(
      String region, UpdateStackRequest updateStackRequest, AwsInternalConfig awsConfig) {
    try (CloseableAmazonWebServiceClient<AmazonCloudFormationClient> closeableAmazonCloudFormationClient =
             new CloseableAmazonWebServiceClient(getAmazonCloudFormationClient(Regions.fromName(region), awsConfig))) {
      tracker.trackCFCall("Update Stack");
      return closeableAmazonCloudFormationClient.getClient().updateStack(updateStackRequest);
    } catch (AmazonServiceException amazonServiceException) {
      awsApiHelperService.handleAmazonServiceException(amazonServiceException);
    } catch (AmazonClientException amazonClientException) {
      awsApiHelperService.handleAmazonClientException(amazonClientException);
    } catch (Exception e) {
      log.error("Exception updateStack", e);
      throw new InvalidRequestException(ExceptionUtils.getMessage(e), e);
    }
    return new UpdateStackResult();
  }

  @Override
  public DeployStackResult deployStack(
      String region, DeployStackRequest deployStackRequest, AwsInternalConfig awsConfig) {
    throw new InvalidRequestException("To be implemented");
  }

  @Override
  public DescribeStacksResult describeStacks(
      String region, DescribeStacksRequest describeStacksRequest, AwsInternalConfig awsConfig) {
    try (CloseableAmazonWebServiceClient<AmazonCloudFormationClient> closeableAmazonCloudFormationClient =
             new CloseableAmazonWebServiceClient(getAmazonCloudFormationClient(Regions.fromName(region), awsConfig))) {
      tracker.trackCFCall("Describe Stacks");
      return closeableAmazonCloudFormationClient.getClient().describeStacks(describeStacksRequest);
    } catch (AmazonServiceException amazonServiceException) {
      awsApiHelperService.handleAmazonServiceException(amazonServiceException);
    } catch (AmazonClientException amazonClientException) {
      awsApiHelperService.handleAmazonClientException(amazonClientException);
    } catch (Exception e) {
      log.error("Exception describeStacks", e);
      throw new InvalidRequestException(ExceptionUtils.getMessage(e), e);
    }
    return new DescribeStacksResult();
  }

  @VisibleForTesting
  AmazonCloudFormationClient getAmazonCloudFormationClient(Regions region, AwsInternalConfig awsConfig) {
    AmazonCloudFormationClientBuilder builder = AmazonCloudFormationClientBuilder.standard().withRegion(region);
    awsApiHelperService.attachCredentialsAndBackoffPolicy(builder, awsConfig);
    return (AmazonCloudFormationClient) builder.build();
  }
}
