/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ng.core.envGroup.beans;

import static io.harness.annotations.dev.HarnessTeam.PIPELINE;

import io.harness.annotation.RecasterAlias;
import io.harness.annotations.dev.OwnedBy;
import io.harness.data.validator.EntityIdentifier;
import io.harness.data.validator.EntityName;
import io.harness.gitsync.beans.YamlDTO;
import io.harness.ng.core.common.beans.NGTag;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@OwnedBy(PIPELINE)
@Data
@Builder
@RecasterAlias("io.harness.ng.core.envGroup.beans.EnvironmentGroupConfig")
public class EnvironmentGroupConfig implements YamlDTO {
  @EntityName String name;
  @EntityIdentifier String identifier;

  String orgIdentifier;
  String projectIdentifier;

  String description;
  String color;
  List<NGTag> tags;

  private List<String> envIdentifiers;
}
