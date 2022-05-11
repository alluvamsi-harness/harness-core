/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ng.core.mapper;

import static io.harness.annotations.dev.HarnessTeam.CDP;
import static io.harness.ng.core.mapper.EmbeddedUserDTOMapper.fromEmbeddedUser;

import io.harness.annotations.dev.OwnedBy;
import io.harness.ng.core.dto.filestore.node.FileNodeDTO;
import io.harness.ng.core.dto.filestore.node.FolderNodeDTO;
import io.harness.ng.core.entities.NGFile;

import lombok.experimental.UtilityClass;

@OwnedBy(CDP)
@UtilityClass
public class FileStoreNodeDTOMapper {
  public FileNodeDTO getFileNodeDTO(NGFile ngFile) {
    return FileNodeDTO.builder()
        .identifier(ngFile.getIdentifier())
        .name(ngFile.getName())
        .fileUsage(ngFile.getFileUsage())
        .description(ngFile.getDescription())
        .tags(ngFile.getTags())
        .lastModifiedAt(ngFile.getLastModifiedAt())
        .lastModifiedBy(fromEmbeddedUser(ngFile.getLastUpdatedBy()))
        .build();
  }

  public FolderNodeDTO getFolderNodeDTO(NGFile ngFile) {
    return FolderNodeDTO.builder()
        .identifier(ngFile.getIdentifier())
        .name(ngFile.getName())
        .lastModifiedAt(ngFile.getLastModifiedAt())
        .lastModifiedBy(fromEmbeddedUser(ngFile.getLastUpdatedBy()))
        .build();
  }
}
