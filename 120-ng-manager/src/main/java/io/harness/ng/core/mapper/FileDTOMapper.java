/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ng.core.mapper;

import static io.harness.annotations.dev.HarnessTeam.CDP;

import io.harness.annotations.dev.OwnedBy;
import io.harness.data.structure.EmptyPredicate;
import io.harness.file.beans.NGBaseFile;
import io.harness.ng.core.entities.NGFile;
import io.harness.ng.core.filestore.dto.FileDTO;

import java.util.Collections;
import lombok.experimental.UtilityClass;

@OwnedBy(CDP)
@UtilityClass
public class FileDTOMapper {
  public NGFile getNGFileFromDTO(FileDTO fileDto) {
    if (fileDto.isFolder()) {
      return NGFile.builder()
          .accountIdentifier(fileDto.getAccountIdentifier())
          .orgIdentifier(fileDto.getOrgIdentifier())
          .projectIdentifier(fileDto.getProjectIdentifier())
          .identifier(fileDto.getIdentifier())
          .parentIdentifier(fileDto.getParentIdentifier())
          .name(fileDto.getName())
          .type(fileDto.getType())
          .createdBy(fileDto.getCreatedBy())
          .build();
    }

    return NGFile.builder()
        .accountIdentifier(fileDto.getAccountIdentifier())
        .orgIdentifier(fileDto.getOrgIdentifier())
        .projectIdentifier(fileDto.getProjectIdentifier())
        .identifier(fileDto.getIdentifier())
        .name(fileDto.getName())
        .fileUsage(fileDto.getFileUsage())
        .type(fileDto.getType())
        .parentIdentifier(fileDto.getParentIdentifier())
        .description(fileDto.getDescription())
        .tags(!EmptyPredicate.isEmpty(fileDto.getTags()) ? fileDto.getTags() : Collections.emptyList())
        .mimeType(fileDto.getMimeType())
        .draft(fileDto.getDraft())
        .createdBy(fileDto.getCreatedBy())
        .build();
  }

  public FileDTO getFileDTOFromNGFile(NGFile ngFile) {
    if (ngFile.isFolder()) {
      return FileDTO.builder()
          .accountIdentifier(ngFile.getAccountIdentifier())
          .orgIdentifier(ngFile.getOrgIdentifier())
          .projectIdentifier(ngFile.getProjectIdentifier())
          .identifier(ngFile.getIdentifier())
          .name(ngFile.getName())
          .type(ngFile.getType())
          .parentIdentifier(ngFile.getParentIdentifier())
          .createdBy(ngFile.getCreatedBy())
          .build();
    }

    return FileDTO.builder()
        .accountIdentifier(ngFile.getAccountIdentifier())
        .orgIdentifier(ngFile.getOrgIdentifier())
        .projectIdentifier(ngFile.getProjectIdentifier())
        .identifier(ngFile.getIdentifier())
        .name(ngFile.getName())
        .fileUsage(ngFile.getFileUsage())
        .type(ngFile.getType())
        .parentIdentifier(ngFile.getParentIdentifier())
        .description(ngFile.getDescription())
        .tags(ngFile.getTags())
        .mimeType(ngFile.getMimeType())
        .draft(ngFile.getDraft())
        .createdBy(ngFile.getCreatedBy())
        .build();
  }

  public NGFile updateNGFile(FileDTO fileDto, NGFile file) {
    if (fileDto.isFolder()) {
      file.setType(fileDto.getType());
      file.setParentIdentifier(fileDto.getParentIdentifier());
      file.setName(fileDto.getName());
      file.setCreatedBy(fileDto.getCreatedBy());
      return file;
    }

    file.setFileUsage(fileDto.getFileUsage());
    file.setType(fileDto.getType());
    file.setParentIdentifier(fileDto.getParentIdentifier());
    file.setDescription(fileDto.getDescription());
    file.setTags(!EmptyPredicate.isEmpty(fileDto.getTags()) ? fileDto.getTags() : Collections.emptyList());
    file.setName(fileDto.getName());
    file.setMimeType(fileDto.getMimeType());
    file.setDraft(fileDto.getDraft());
    file.setCreatedBy(fileDto.getCreatedBy());
    return file;
  }

  public NGBaseFile getNgBaseFileFromFileDTO(FileDTO fileDto) {
    NGBaseFile baseFile = new NGBaseFile();
    baseFile.setFileName(fileDto.getName());
    baseFile.setMimeType(fileDto.getMimeType());
    baseFile.setAccountId(fileDto.getAccountIdentifier());
    return baseFile;
  }
}
