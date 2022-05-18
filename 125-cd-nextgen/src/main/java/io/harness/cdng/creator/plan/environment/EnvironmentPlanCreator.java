/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.cdng.creator.plan.environment;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.cdng.infra.steps.InfraSectionStepParameters;
import io.harness.cdng.visitor.YamlTypes;
import io.harness.data.structure.UUIDGenerator;
import io.harness.ng.core.environment.yaml.NGEnvironmentInfoConfig;
import io.harness.pms.plan.creation.PlanCreatorUtils;
import io.harness.pms.sdk.core.adviser.success.OnSuccessAdviserParameters;
import io.harness.pms.sdk.core.plan.PlanNode;
import io.harness.pms.sdk.core.plan.creation.beans.PlanCreationContext;
import io.harness.pms.sdk.core.plan.creation.beans.PlanCreationResponse;
import io.harness.pms.sdk.core.plan.creation.creators.PartialPlanCreator;
import io.harness.serializer.KryoSerializer;

import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@OwnedBy(HarnessTeam.CDC)
public class EnvironmentPlanCreator implements PartialPlanCreator<NGEnvironmentInfoConfig> {
  @Inject KryoSerializer kryoSerializer;

  @Override
  public Class<NGEnvironmentInfoConfig> getFieldClass() {
    return NGEnvironmentInfoConfig.class;
  }

  @Override
  public Map<String, Set<String>> getSupportedTypes() {
    return Collections.singletonMap(YamlTypes.ENVIRONMENT_YAML, Collections.singleton(PlanCreatorUtils.ANY_TYPE));
  }

  @Override
  public PlanCreationResponse createPlanForField(
      PlanCreationContext ctx, NGEnvironmentInfoConfig environmentInfoConfig) {
    /*
    EnvironmentPlanCreator is dependent on infraSectionStepParameters and serviceSpecNodeUuid which is used as advisor
     */
    InfraSectionStepParameters infraSectionStepParameters =
        (InfraSectionStepParameters) kryoSerializer.asInflatedObject(
            ctx.getDependency().getMetadataMap().get(YamlTypes.INFRASTRUCTURE_STEP_PARAMETERS).toByteArray());

    String serviceSpecNodeUuid = (String) kryoSerializer.asInflatedObject(
        ctx.getDependency().getMetadataMap().get(YamlTypes.SERVICE_SPEC).toByteArray());

    ByteString advisorParameters = ByteString.copyFrom(
        kryoSerializer.asBytes(OnSuccessAdviserParameters.builder().nextNodeId(serviceSpecNodeUuid).build()));
    PlanNode planNode = EnvironmentPlanCreatorHelper.getPlanNode(
        UUIDGenerator.generateUuid(), infraSectionStepParameters, advisorParameters);
    return PlanCreationResponse.builder().planNode(planNode).build();
  }
}