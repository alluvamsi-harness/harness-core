/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.steps.resourcerestraint.service;

import static io.harness.rule.OwnerRule.FERNANDOD;

import static org.assertj.core.api.Assertions.assertThat;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.category.element.UnitTests;
import io.harness.rule.Owner;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@OwnedBy(HarnessTeam.PIPELINE)
public class ResourceRestraintInstanceServiceTest {
  private static final String TEST_RELEASE_ENTITY_ID = "PLAN_EXEC_ID|SETUP_NODE_ID";

  @Test
  @Owner(developers = FERNANDOD)
  @Category(UnitTests.class)
  public void shouldCreateValidReleaseEntityIdUsingPlanExecIdAndSetupNodeId() {
    String value = ResourceRestraintInstanceService.getReleaseEntityId("PLAN_EXEC_ID", "SETUP_NODE_ID");
    assertThat(value).isEqualTo(TEST_RELEASE_ENTITY_ID);
  }

  @Test
  @Owner(developers = FERNANDOD)
  @Category(UnitTests.class)
  public void shouldGetPlanExecutionIdFromReleaseEntitiyId() {
    String value = ResourceRestraintInstanceService.getPlanExecutionIdFromReleaseEntityId(TEST_RELEASE_ENTITY_ID);
    assertThat("PLAN_EXEC_ID").isEqualTo(value);
  }

  @Test
  @Owner(developers = FERNANDOD)
  @Category(UnitTests.class)
  public void verifyGetSetupNodeIdFromReleaseEntityId() {
    String value = ResourceRestraintInstanceService.getSetupNodeIdFromReleaseEntityId(TEST_RELEASE_ENTITY_ID);
    assertThat("SETUP_NODE_ID").isEqualTo(value);
  }
}
