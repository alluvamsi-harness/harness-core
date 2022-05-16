/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cdng.creator.plan.environment;

import static io.harness.annotations.dev.HarnessTeam.CDP;

import io.harness.annotations.dev.OwnedBy;
import io.harness.cdng.creator.plan.PlanCreatorConstants;
import io.harness.cdng.infra.steps.EnvironmentStep;
import io.harness.cdng.visitor.YamlTypes;
import io.harness.data.structure.UUIDGenerator;
import io.harness.pms.contracts.advisers.AdviserObtainment;
import io.harness.pms.contracts.advisers.AdviserType;
import io.harness.pms.contracts.facilitators.FacilitatorObtainment;
import io.harness.pms.contracts.facilitators.FacilitatorType;
import io.harness.pms.execution.OrchestrationFacilitatorType;
import io.harness.pms.sdk.core.adviser.OrchestrationAdviserTypes;
import io.harness.pms.sdk.core.plan.PlanNode;
import io.harness.pms.sdk.core.steps.io.StepParameters;

import com.google.protobuf.ByteString;
import lombok.experimental.UtilityClass;

@UtilityClass
@OwnedBy(CDP)
public class EnvironmentPlanCreatorHelper {
  public PlanNode getPlanNode(StepParameters infraSectionStepParameters, ByteString advisorParameters) {
    return PlanNode.builder()
        .uuid(UUIDGenerator.generateUuid())
        .stepType(EnvironmentStep.STEP_TYPE)
        .name(PlanCreatorConstants.ENVIRONMENT_NODE_NAME)
        .identifier(YamlTypes.ENVIRONMENT_YAML)
        .stepParameters(infraSectionStepParameters)
        .facilitatorObtainment(
            FacilitatorObtainment.newBuilder()
                .setType(FacilitatorType.newBuilder().setType(OrchestrationFacilitatorType.SYNC).build())
                .build())
        .adviserObtainment(
            AdviserObtainment.newBuilder()
                .setType(AdviserType.newBuilder().setType(OrchestrationAdviserTypes.ON_SUCCESS.name()).build())
                .setParameters(advisorParameters)
                .build())
        .skipExpressionChain(false)
        .build();
  }
}
