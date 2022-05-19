/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.cdng.creator.plan.environment;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.cdng.creator.plan.PlanCreatorConstants;
import io.harness.cdng.environment.steps.EnvironmentStepParameters;
import io.harness.cdng.environment.yaml.EnvironmentPlanCreatorConfig;
import io.harness.cdng.infra.steps.EnvironmentStep;
import io.harness.cdng.infra.steps.InfraSectionStepParameters;
import io.harness.cdng.visitor.YamlTypes;
import io.harness.data.structure.UUIDGenerator;
import io.harness.ng.core.environment.beans.EnvironmentType;
import io.harness.pms.contracts.advisers.AdviserObtainment;
import io.harness.pms.contracts.advisers.AdviserType;
import io.harness.pms.contracts.facilitators.FacilitatorObtainment;
import io.harness.pms.contracts.facilitators.FacilitatorType;
import io.harness.pms.execution.OrchestrationFacilitatorType;
import io.harness.pms.sdk.core.adviser.OrchestrationAdviserTypes;
import io.harness.pms.sdk.core.adviser.success.OnSuccessAdviserParameters;
import io.harness.pms.sdk.core.plan.PlanNode;
import io.harness.pms.sdk.core.plan.creation.beans.PlanCreationContext;
import io.harness.pms.sdk.core.plan.creation.beans.PlanCreationResponse;
import io.harness.pms.sdk.core.plan.creation.creators.PartialPlanCreator;
import io.harness.serializer.KryoSerializer;

import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@OwnedBy(HarnessTeam.CDC)
public class EnvironmentPlanCreator implements PartialPlanCreator<EnvironmentPlanCreatorConfig> {
  @Inject KryoSerializer kryoSerializer;

  @Override
  public Class<EnvironmentPlanCreatorConfig> getFieldClass() {
    return EnvironmentPlanCreatorConfig.class;
  }

  @Override
  public Map<String, Set<String>> getSupportedTypes() {
    return Collections.singletonMap(YamlTypes.ENVIRONMENT_YAML,
        new HashSet<>(Arrays.asList(EnvironmentType.PreProduction.name(), EnvironmentType.Production.name())));
  }

  @Override
  public PlanCreationResponse createPlanForField(
      PlanCreationContext ctx, EnvironmentPlanCreatorConfig environmentPlanCreatorConfig) {
    /*
    EnvironmentPlanCreator is dependent on infraSectionStepParameters and serviceSpecNodeUuid which is used as advisor
     */
    EnvironmentStepParameters environmentStepParameters =
        EnvironmentPlanCreatorConfigHelper.toEnvironmentStepParameters(environmentPlanCreatorConfig);

    String serviceSpecNodeUuid = (String) kryoSerializer.asInflatedObject(
        ctx.getDependency().getMetadataMap().get(YamlTypes.SERVICE_SPEC).toByteArray());

    ByteString advisorParameters = ByteString.copyFrom(
        kryoSerializer.asBytes(OnSuccessAdviserParameters.builder().nextNodeId(serviceSpecNodeUuid).build()));
    PlanNode planNode =
        PlanNode.builder()
            .uuid(ctx.getCurrentField().getNode().getUuid())
            .stepType(EnvironmentStep.STEP_TYPE)
            .name(PlanCreatorConstants.ENVIRONMENT_NODE_NAME)
            .identifier(YamlTypes.ENVIRONMENT_YAML)
            .stepParameters(environmentStepParameters)
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
    return PlanCreationResponse.builder().planNode(planNode).build();
  }
}