package io.harness.ng.core.infrastructure.mappers;

import static io.harness.annotations.dev.HarnessTeam.CDC;
import static io.harness.ng.core.mapper.TagMapper.convertToList;
import static io.harness.ng.core.mapper.TagMapper.convertToMap;

import io.harness.annotations.dev.OwnedBy;
import io.harness.ng.core.infrastructure.dto.InfrastructureRequestDTO;
import io.harness.ng.core.infrastructure.dto.InfrastructureResponse;
import io.harness.ng.core.infrastructure.dto.InfrastructureResponseDTO;
import io.harness.ng.core.infrastructure.entity.InfrastructureEntity;

import lombok.experimental.UtilityClass;

@OwnedBy(CDC)
@UtilityClass
public class InfrastructureMapper {
  public InfrastructureEntity toInfrastructureEntity(
      String accountId, InfrastructureRequestDTO infrastructureRequestDTO) {
    return InfrastructureEntity.builder()
        .identifier(infrastructureRequestDTO.getIdentifier())
        .accountId(accountId)
        .orgIdentifier(infrastructureRequestDTO.getOrgIdentifier())
        .projectIdentifier(infrastructureRequestDTO.getProjectIdentifier())
        .envIdentifier(infrastructureRequestDTO.getEnvIdentifier())
        .name(infrastructureRequestDTO.getName())
        .description(infrastructureRequestDTO.getDescription())
        .tags(convertToList(infrastructureRequestDTO.getTags()))
        .yaml(infrastructureRequestDTO.getYaml())
        .build();
  }

  public InfrastructureResponse toResponseWrapper(InfrastructureEntity infrastructureEntity) {
    return InfrastructureResponse.builder()
        .infrastructure(writeDTO(infrastructureEntity))
        .createdAt(infrastructureEntity.getCreatedAt())
        .lastModifiedAt(infrastructureEntity.getLastModifiedAt())
        .build();
  }

  public InfrastructureResponseDTO writeDTO(InfrastructureEntity infrastructureEntity) {
    return InfrastructureResponseDTO.builder()
        .accountId(infrastructureEntity.getAccountId())
        .orgIdentifier(infrastructureEntity.getOrgIdentifier())
        .projectIdentifier(infrastructureEntity.getProjectIdentifier())
        .envIdentifier(infrastructureEntity.getEnvIdentifier())
        .identifier(infrastructureEntity.getIdentifier())
        .name(infrastructureEntity.getName())
        .description(infrastructureEntity.getDescription())
        .tags(convertToMap(infrastructureEntity.getTags()))
        .yaml(infrastructureEntity.getYaml())
        .build();
  }
}
