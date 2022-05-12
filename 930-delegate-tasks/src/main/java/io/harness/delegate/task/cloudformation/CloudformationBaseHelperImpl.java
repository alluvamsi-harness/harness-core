/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.delegate.task.cloudformation;

import static io.harness.annotations.dev.HarnessTeam.CDP;
import static io.harness.data.structure.EmptyPredicate.isEmpty;
import static io.harness.data.structure.EmptyPredicate.isNotEmpty;

import io.harness.annotations.dev.OwnedBy;
import io.harness.aws.AWSCloudformationClient;
import io.harness.aws.beans.AwsInternalConfig;
import io.harness.aws.cf.DeployStackRequest;

import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.UpdateStackRequest;
import com.google.inject.Inject;
import java.util.List;
import java.util.Optional;

@OwnedBy(CDP)
public class CloudformationBaseHelperImpl implements CloudformationBaseHelper {
  public static final String CLOUDFORMATION_STACK_CREATE_URL = "Create URL";
  public static final String CLOUDFORMATION_STACK_CREATE_BODY = "Create Body";
  public static final String CLOUDFORMATION_STACK_CREATE_GIT = "Create GIT";

  @Inject protected AWSCloudformationClient awsHelperService;

  public Optional<Stack> getIfStackExists(
      String customStackName, String suffix, AwsInternalConfig awsConfig, String region) {
    List<Stack> stacks = awsHelperService.getAllStacks(region, new DescribeStacksRequest(), awsConfig);
    if (isEmpty(stacks)) {
      return Optional.empty();
    }

    if (isNotEmpty(customStackName)) {
      return stacks.stream().filter(stack -> stack.getStackName().equals(customStackName)).findFirst();
    } else {
      return stacks.stream().filter(stack -> stack.getStackName().endsWith(suffix)).findFirst();
    }
  }

  public DeployStackRequest transformToDeployStackRequest(UpdateStackRequest updateStackRequest) {
    return DeployStackRequest.builder()
        .stackName(updateStackRequest.getStackName())
        .parameters(updateStackRequest.getParameters())
        .capabilities(updateStackRequest.getCapabilities())
        .tags(updateStackRequest.getTags())
        .roleARN(updateStackRequest.getRoleARN())
        .templateBody(updateStackRequest.getTemplateBody())
        .templateURL(updateStackRequest.getTemplateURL())
        .build();
  }
}
