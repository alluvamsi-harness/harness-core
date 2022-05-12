/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.aws;

import static io.harness.rule.OwnerRule.ARVIND;
import static io.harness.rule.OwnerRule.NGONZALEZ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.harness.CategoryTest;
import io.harness.aws.beans.AwsInternalConfig;
import io.harness.aws.cf.DeployStackRequest;
import io.harness.aws.cf.DeployStackResult;
import io.harness.aws.cf.Status;
import io.harness.category.element.UnitTests;
import io.harness.concurent.HTimeLimiterMocker;
import io.harness.logging.LogCallback;
import io.harness.rule.Owner;

import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.CreateChangeSetRequest;
import com.amazonaws.services.cloudformation.model.CreateChangeSetResult;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import com.amazonaws.services.cloudformation.model.DeleteStackRequest;
import com.amazonaws.services.cloudformation.model.DescribeChangeSetRequest;
import com.amazonaws.services.cloudformation.model.DescribeChangeSetResult;
import com.amazonaws.services.cloudformation.model.DescribeStackEventsRequest;
import com.amazonaws.services.cloudformation.model.DescribeStackEventsResult;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.cloudformation.model.ExecuteChangeSetRequest;
import com.amazonaws.services.cloudformation.model.Parameter;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.StackEvent;
import com.amazonaws.services.cloudformation.model.Tag;
import com.amazonaws.services.cloudformation.model.UpdateStackRequest;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class AWSCloudformationClientTest extends CategoryTest {
  private AmazonCloudFormationClient mockClient;
  private LogCallback logCallback;
  @InjectMocks @Inject @Spy private AWSCloudformationClientImpl service;
  @Mock private AwsCallTracker mockTracker;
  @Mock private TimeLimiter mockTimeLimiter;

  private static final char[] accessKey = "abcd".toCharArray();
  private static final char[] secretKey = "pqrs".toCharArray();

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    mockClient = mock(AmazonCloudFormationClient.class);
    doReturn(mockClient).when(service).getAmazonCloudFormationClient(any(), any());
    logCallback = mock(LogCallback.class);
  }

  @Test
  @Owner(developers = NGONZALEZ)
  @Category(UnitTests.class)
  public void shouldUpdateStack() {
    String stackName = "Stack Name";
    String region = "us-west-1";
    UpdateStackRequest request = new UpdateStackRequest().withStackName(stackName);
    doNothing().when(mockTracker).trackCFCall(anyString());
    service.updateStack(region, request, AwsInternalConfig.builder().accessKey(accessKey).secretKey(secretKey).build());
    verify(mockClient).updateStack(request);
  }

  @Test
  @Owner(developers = NGONZALEZ)
  @Category(UnitTests.class)
  public void shouldDeleteStack() {
    String stackName = "Stack Name";
    String region = "us-west-1";
    DeleteStackRequest request = new DeleteStackRequest().withStackName(stackName);
    doNothing().when(mockTracker).trackCFCall(anyString());
    service.deleteStack(region, request, AwsInternalConfig.builder().accessKey(accessKey).secretKey(secretKey).build());
    verify(mockClient).deleteStack(request);
  }

  @Test
  @Owner(developers = NGONZALEZ)
  @Category(UnitTests.class)
  public void shouldDescribeStack() {
    String stackName = "Stack Name";
    String region = "us-west-1";
    DescribeStacksRequest request = new DescribeStacksRequest().withStackName(stackName);
    DescribeStacksResult result = new DescribeStacksResult().withStacks(new Stack().withStackName(stackName));
    doReturn(result).when(mockClient).describeStacks(request);
    doNothing().when(mockTracker).trackCFCall(anyString());
    DescribeStacksResult actual = service.describeStacks(
        region, request, AwsInternalConfig.builder().accessKey(accessKey).secretKey(secretKey).build());
    assertThat(actual).isNotNull();
    assertThat(actual.getStacks().size()).isEqualTo(1);
    assertThat(actual.getStacks().get(0).getStackName()).isEqualTo(stackName);
  }

  @Test
  @Owner(developers = NGONZALEZ)
  @Category(UnitTests.class)
  public void shouldGetAllEvents() {
    String stackName = "Stack Name";
    String region = "us-west-1";
    DescribeStackEventsRequest request = new DescribeStackEventsRequest().withStackName(stackName);
    DescribeStackEventsResult result =
        new DescribeStackEventsResult().withStackEvents(new StackEvent().withStackName(stackName).withEventId("id"));
    doReturn(result).when(mockClient).describeStackEvents(request);
    doNothing().when(mockTracker).trackCFCall(anyString());
    List<StackEvent> events = service.getAllStackEvents(
        region, request, AwsInternalConfig.builder().accessKey(accessKey).secretKey(secretKey).build());
    assertThat(events).isNotNull();
    assertThat(events.size()).isEqualTo(1);
    assertThat(events.get(0).getStackName()).isEqualTo(stackName);
    assertThat(events.get(0).getEventId()).isEqualTo("id");
  }

  @Test
  @Owner(developers = NGONZALEZ)
  @Category(UnitTests.class)
  public void shouldCreateStack() {
    String stackName = "Stack Name";
    String region = "us-west-1";
    CreateStackRequest request = new CreateStackRequest().withStackName(stackName);
    doNothing().when(mockTracker).trackCFCall(anyString());
    service.createStack(region, request, AwsInternalConfig.builder().accessKey(accessKey).secretKey(secretKey).build());
    verify(mockClient).createStack(request);
  }

  @Test
  @Owner(developers = NGONZALEZ)
  @Category(UnitTests.class)
  public void shouldListStacks() {
    String stackName = "Stack Name";
    String region = "us-west-1";
    DescribeStacksRequest request = new DescribeStacksRequest().withStackName(stackName);
    DescribeStacksResult result = new DescribeStacksResult().withStacks(new Stack().withStackName(stackName));
    doReturn(result).when(mockClient).describeStacks(request);
    doNothing().when(mockTracker).trackCFCall(anyString());
    List<Stack> stacks = service.getAllStacks(
        region, request, AwsInternalConfig.builder().accessKey(accessKey).secretKey(secretKey).build());
    assertThat(stacks).isNotNull();
    assertThat(stacks.size()).isEqualTo(1);
    assertThat(stacks.get(0).getStackName()).isEqualTo(stackName);
  }

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testDeployStack() throws Exception {
    String stackName = "Stack Name";
    String region = "us-west-1";
    String changeSetName;
    DeployStackRequest deployStackRequest = DeployStackRequest.builder()
                                                .stackName(stackName)
                                                .parameters(Arrays.asList(new Parameter()
                                                                              .withParameterKey("K1")
                                                                              .withParameterValue("V1")
                                                                              .withResolvedValue("RV1")
                                                                              .withUsePreviousValue(true)))
                                                .tags(Arrays.asList(new Tag().withKey("K1").withValue("V1")))
                                                .roleARN("ROLE")
                                                .capabilities(Arrays.asList("CAPABILITY_IAM"))
                                                .templateURL("TEMPLATE_URL")
                                                .templateBody("TEMPLATE_BODY")
                                                .build();
    ArgumentCaptor<CreateChangeSetRequest> changeSetRequestArgumentCaptor =
        ArgumentCaptor.forClass(CreateChangeSetRequest.class);
    CreateChangeSetResult createChangeSetResult = new CreateChangeSetResult().withId("ID").withStackId("STACK_ID");
    doReturn(createChangeSetResult).when(mockClient).createChangeSet(changeSetRequestArgumentCaptor.capture());

    ArgumentCaptor<DescribeChangeSetRequest> describeChangeSetRequestArgumentCaptor =
        ArgumentCaptor.forClass(DescribeChangeSetRequest.class);
    DescribeChangeSetResult describeChangeSetResult = new DescribeChangeSetResult().withStatus("CREATE_COMPLETE");
    doReturn(describeChangeSetResult)
        .when(mockClient)
        .describeChangeSet(describeChangeSetRequestArgumentCaptor.capture());

    ArgumentCaptor<ExecuteChangeSetRequest> executeChangeSetRequestArgumentCaptor =
        ArgumentCaptor.forClass(ExecuteChangeSetRequest.class);
    doReturn(null).when(mockClient).executeChangeSet(executeChangeSetRequestArgumentCaptor.capture());

    HTimeLimiterMocker.mockCallInterruptible(mockTimeLimiter).thenReturn(null);
    DeployStackResult deployStackResult = service.deployStack(region, deployStackRequest,
        AwsInternalConfig.builder().accessKey(accessKey).secretKey(secretKey).build(), Duration.ofDays(1), logCallback);

    assertThat(deployStackResult.getStatus()).isEqualTo(Status.SUCCESS);
    assertThat(deployStackResult.isNoUpdatesToPerform()).isEqualTo(false);

    CreateChangeSetRequest createChangeSetRequest = changeSetRequestArgumentCaptor.getValue();
    assertThat(createChangeSetRequest.getStackName()).isEqualTo(stackName);
    assertThat(createChangeSetRequest.getRoleARN()).isEqualTo("ROLE");
    assertThat(createChangeSetRequest.getCapabilities()).containsExactly("CAPABILITY_IAM");
    assertThat(createChangeSetRequest.getTemplateURL()).isEqualTo("TEMPLATE_URL");
    assertThat(createChangeSetRequest.getTemplateBody()).isEqualTo("TEMPLATE_BODY");
    assertThat(createChangeSetRequest.getTags())
        .containsExactly(new com.amazonaws.services.cloudformation.model.Tag().withKey("K1").withValue("V1"));
    assertThat(createChangeSetRequest.getParameters())
        .containsExactly(new com.amazonaws.services.cloudformation.model.Parameter()
                             .withParameterKey("K1")
                             .withParameterValue("V1")
                             .withResolvedValue("RV1")
                             .withUsePreviousValue(true));
    changeSetName = createChangeSetRequest.getChangeSetName();

    DescribeChangeSetRequest describeChangeSetRequest = describeChangeSetRequestArgumentCaptor.getValue();
    assertThat(describeChangeSetRequest.getChangeSetName()).isEqualTo(changeSetName);
    assertThat(describeChangeSetRequest.getStackName()).isEqualTo(stackName);

    ExecuteChangeSetRequest executeChangeSetRequest = executeChangeSetRequestArgumentCaptor.getValue();
    assertThat(executeChangeSetRequest.getChangeSetName()).isEqualTo(changeSetName);
    assertThat(executeChangeSetRequest.getStackName()).isEqualTo(stackName);
  }

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testDeployStackFailureWithNoReason() throws Exception {
    String stackName = "Stack Name";
    String region = "us-west-1";
    DeployStackRequest deployStackRequest = DeployStackRequest.builder().stackName(stackName).build();
    CreateChangeSetResult createChangeSetResult = new CreateChangeSetResult().withId("ID").withStackId("STACK_ID");
    doReturn(createChangeSetResult).when(mockClient).createChangeSet(any());

    DescribeChangeSetResult describeChangeSetResult = new DescribeChangeSetResult().withStatus("FAILED");
    doReturn(describeChangeSetResult).when(mockClient).describeChangeSet(any());

    doReturn(null).when(mockClient).executeChangeSet(any());

    HTimeLimiterMocker.mockCallInterruptible(mockTimeLimiter).thenReturn(null);
    DeployStackResult deployStackResult = service.deployStack(region, deployStackRequest,
        AwsInternalConfig.builder().accessKey(accessKey).secretKey(secretKey).build(), Duration.ofDays(1), logCallback);

    assertThat(deployStackResult.getStatus()).isEqualTo(Status.FAILURE);
    assertThat(deployStackResult.isNoUpdatesToPerform()).isEqualTo(false);
    assertThat(deployStackResult.getStatusReason()).isNull();
  }

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testDeployStackFailureWithReason() throws Exception {
    String stackName = "Stack Name";
    String region = "us-west-1";
    DeployStackRequest deployStackRequest = DeployStackRequest.builder().stackName(stackName).build();
    CreateChangeSetResult createChangeSetResult = new CreateChangeSetResult().withId("ID").withStackId("STACK_ID");
    doReturn(createChangeSetResult).when(mockClient).createChangeSet(any());

    DescribeChangeSetResult describeChangeSetResult =
        new DescribeChangeSetResult().withStatus("FAILED").withStatusReason("R1");
    doReturn(describeChangeSetResult).when(mockClient).describeChangeSet(any());

    doReturn(null).when(mockClient).executeChangeSet(any());

    HTimeLimiterMocker.mockCallInterruptible(mockTimeLimiter).thenReturn(null);
    DeployStackResult deployStackResult = service.deployStack(region, deployStackRequest,
        AwsInternalConfig.builder().accessKey(accessKey).secretKey(secretKey).build(), Duration.ofDays(1), logCallback);

    assertThat(deployStackResult.getStatus()).isEqualTo(Status.FAILURE);
    assertThat(deployStackResult.isNoUpdatesToPerform()).isEqualTo(false);
    assertThat(deployStackResult.getStatusReason()).isEqualTo("R1");
  }

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testDeployStackFailureWithNoChanges() throws Exception {
    String stackName = "Stack Name";
    String region = "us-west-1";
    DeployStackRequest deployStackRequest = DeployStackRequest.builder().stackName(stackName).build();
    CreateChangeSetResult createChangeSetResult = new CreateChangeSetResult().withId("ID").withStackId("STACK_ID");
    doReturn(createChangeSetResult).when(mockClient).createChangeSet(any());

    DescribeChangeSetResult describeChangeSetResult =
        new DescribeChangeSetResult().withStatus("FAILED").withStatusReason("No updates are to be performed.");
    doReturn(describeChangeSetResult).when(mockClient).describeChangeSet(any());

    doReturn(null).when(mockClient).executeChangeSet(any());

    HTimeLimiterMocker.mockCallInterruptible(mockTimeLimiter).thenReturn(null);
    DeployStackResult deployStackResult = service.deployStack(region, deployStackRequest,
        AwsInternalConfig.builder().accessKey(accessKey).secretKey(secretKey).build(), Duration.ofDays(1), logCallback);

    assertThat(deployStackResult.getStatus()).isEqualTo(Status.SUCCESS);
    assertThat(deployStackResult.isNoUpdatesToPerform()).isEqualTo(true);
    assertThat(deployStackResult.getStatusReason()).isEqualTo("No updates are to be performed.");
  }
}
