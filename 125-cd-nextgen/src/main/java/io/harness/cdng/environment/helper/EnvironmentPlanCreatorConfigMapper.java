/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.cdng.environment.helper;

import static io.harness.annotations.dev.HarnessTeam.CDC;

import io.harness.annotations.dev.OwnedBy;
import io.harness.cdng.environment.yaml.EnvironmentPlanCreatorConfig;
import io.harness.cdng.infra.mapper.InfrastructureEntityConfigMapper;
import io.harness.ng.core.environment.beans.Environment;
import io.harness.ng.core.environment.mappers.EnvironmentMapper;
import io.harness.ng.core.environment.yaml.NGEnvironmentInfoConfig;
import io.harness.ng.core.infrastructure.entity.InfrastructureEntity;

import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
@OwnedBy(CDC)
public class EnvironmentPlanCreatorConfigMapper {
  public static EnvironmentPlanCreatorConfig toEnvironmentPlanCreatorConfig(
      Environment environment, List<InfrastructureEntity> infrastructureEntity) {
    NGEnvironmentInfoConfig ngEnvironmentInfoConfig =
        EnvironmentMapper.toNGEnvironmentConfig(environment).getNgEnvironmentInfoConfig();
    return EnvironmentPlanCreatorConfig.builder()
        .environmentRef(ngEnvironmentInfoConfig.getIdentifier())
        .identifier(ngEnvironmentInfoConfig.getIdentifier())
        .projectIdentifier(ngEnvironmentInfoConfig.getProjectIdentifier())
        .orgIdentifier(ngEnvironmentInfoConfig.getOrgIdentifier())
        .description(ngEnvironmentInfoConfig.getDescription())
        .name(ngEnvironmentInfoConfig.getName())
        .tags(ngEnvironmentInfoConfig.getTags())
        .type(ngEnvironmentInfoConfig.getType())
        .variables(ngEnvironmentInfoConfig.getVariables())
        .serviceOverrides(ngEnvironmentInfoConfig.getServiceOverrides())
        .infrastructureDefinitions(
            InfrastructureEntityConfigMapper.toInfrastructurePlanCreatorConfig(infrastructureEntity))
        .build();
  }
}
