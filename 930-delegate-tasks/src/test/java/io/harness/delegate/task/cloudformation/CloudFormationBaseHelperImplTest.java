/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.delegate.task.cloudformation;

import static io.harness.annotations.dev.HarnessTeam.CDP;
import static io.harness.rule.OwnerRule.ARVIND;
import static io.harness.rule.OwnerRule.TMACARI;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

import io.harness.CategoryTest;
import io.harness.annotations.dev.HarnessModule;
import io.harness.annotations.dev.OwnedBy;
import io.harness.annotations.dev.TargetModule;
import io.harness.aws.AWSCloudformationClient;
import io.harness.aws.beans.AwsInternalConfig;
import io.harness.aws.cf.DeployStackRequest;
import io.harness.category.element.UnitTests;
import io.harness.rule.Owner;

import software.wings.service.intfc.security.EncryptionService;

import com.amazonaws.services.cloudformation.model.Parameter;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.Tag;
import com.amazonaws.services.cloudformation.model.UpdateStackRequest;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@OwnedBy(CDP)
@TargetModule(HarnessModule._930_DELEGATE_TASKS)
public class CloudFormationBaseHelperImplTest extends CategoryTest {
  @Mock private AWSCloudformationClient awsHelperService;
  @Mock protected EncryptionService encryptionService;
  @InjectMocks private CloudformationBaseHelperImpl cloudFormationBaseHelper;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @Owner(developers = TMACARI)
  @Category(UnitTests.class)
  public void testGetIfStackExists() {
    String customStackName = "CUSTOM_STACK_NAME";
    String stackId = "STACK_ID";
    doReturn(singletonList(new Stack().withStackId(stackId).withStackName(customStackName)))
        .when(awsHelperService)
        .getAllStacks(anyString(), any(), any());
    Optional<Stack> stack = cloudFormationBaseHelper.getIfStackExists(
        customStackName, "foo", AwsInternalConfig.builder().build(), "us-east-1");
    assertThat(stack.isPresent()).isTrue();
    assertThat(stackId).isEqualTo(stack.get().getStackId());
  }

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testMapToDeployStackRequest() {
    Parameter parameter =
        new Parameter().withParameterKey("V1").withParameterValue("V2").withResolvedValue("V3").withUsePreviousValue(
            true);
    Tag tag = new Tag().withKey("K1").withValue("VAL1");
    UpdateStackRequest updateStackRequest = new UpdateStackRequest()
                                                .withStackName("S1")
                                                .withParameters(parameter)
                                                .withCapabilities("CAPABILITY_IAM")
                                                .withTags(tag)
                                                .withRoleARN("ROLE")
                                                .withTemplateBody("TemplateBody")
                                                .withTemplateURL("TemplateUrl");

    DeployStackRequest deployStackRequest = cloudFormationBaseHelper.transformToDeployStackRequest(updateStackRequest);
    assertThat(deployStackRequest.getStackName()).isEqualTo("S1");
    assertThat(deployStackRequest.getRoleARN()).isEqualTo("ROLE");
    assertThat(deployStackRequest.getTemplateBody()).isEqualTo("TemplateBody");
    assertThat(deployStackRequest.getTemplateURL()).isEqualTo("TemplateUrl");
    assertThat(deployStackRequest.getTags()).containsExactly(tag);
    assertThat(deployStackRequest.getParameters()).containsExactly(parameter);
  }
}
