/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ng.core.environment.mappers;

import static io.harness.annotations.dev.HarnessTeam.PIPELINE;
import static io.harness.data.structure.EmptyPredicate.isNotEmpty;
import static io.harness.ng.core.mapper.TagMapper.convertToMap;

import io.harness.annotations.dev.OwnedBy;
import io.harness.exception.InvalidRequestException;
import io.harness.ng.core.environment.beans.Environment;
import io.harness.ng.core.environment.dto.EnvironmentRequestDTO;
import io.harness.ng.core.environment.yaml.NGEnvironmentConfig;
import io.harness.ng.core.environment.yaml.NGEnvironmentInfoConfig;
import io.harness.pms.yaml.YamlUtils;
import io.harness.utils.YamlPipelineUtils;

import java.io.IOException;
import lombok.experimental.UtilityClass;

@OwnedBy(PIPELINE)
@UtilityClass
public class NGEnvironmentEntityMapper {
  public String toYaml(NGEnvironmentConfig ngEnvironmentConfig) {
    try {
      return YamlPipelineUtils.getYamlString(ngEnvironmentConfig);
    } catch (IOException e) {
      throw new InvalidRequestException("Cannot create environment entity due to " + e.getMessage());
    }
  }

  public NGEnvironmentConfig toNGEnvironmentConfig(Environment environmentEntity) {
    if (isNotEmpty(environmentEntity.getYaml())) {
      try {
        return YamlUtils.read(environmentEntity.getYaml(), NGEnvironmentConfig.class);
      } catch (IOException e) {
        throw new InvalidRequestException("Cannot create environment config due to " + e.getMessage());
      }
    }
    return NGEnvironmentConfig.builder()
        .ngEnvironmentInfoConfig(NGEnvironmentInfoConfig.builder()
                                     .name(environmentEntity.getName())
                                     .identifier(environmentEntity.getIdentifier())
                                     .orgIdentifier(environmentEntity.getOrgIdentifier())
                                     .projectIdentifier(environmentEntity.getProjectIdentifier())
                                     .description(environmentEntity.getDescription())
                                     .tags(convertToMap(environmentEntity.getTags()))
                                     .type(environmentEntity.getType())
                                     .build())
        .build();
  }

  public NGEnvironmentConfig toNGEnvironmentConfig(EnvironmentRequestDTO dto) {
    if (isNotEmpty(dto.getYaml())) {
      try {
        return YamlUtils.read(dto.getYaml(), NGEnvironmentConfig.class);
      } catch (IOException e) {
        throw new InvalidRequestException("Cannot create environment config due to " + e.getMessage());
      }
    }
    return NGEnvironmentConfig.builder()
        .ngEnvironmentInfoConfig(NGEnvironmentInfoConfig.builder()
                                     .name(dto.getName())
                                     .identifier(dto.getIdentifier())
                                     .orgIdentifier(dto.getOrgIdentifier())
                                     .projectIdentifier(dto.getProjectIdentifier())
                                     .description(dto.getDescription())
                                     .tags(dto.getTags())
                                     .type(dto.getType())
                                     .build())
        .build();
  }
}