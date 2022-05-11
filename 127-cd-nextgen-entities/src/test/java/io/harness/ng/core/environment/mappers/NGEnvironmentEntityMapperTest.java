/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ng.core.environment.mappers;

import static io.harness.ng.core.environment.beans.EnvironmentType.PreProduction;
import static io.harness.yaml.core.variables.NGVariableType.STRING;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import io.harness.category.element.UnitTests;
import io.harness.ng.core.common.beans.NGTag;
import io.harness.ng.core.environment.beans.Environment;
import io.harness.ng.core.environment.dto.EnvironmentRequestDTO;
import io.harness.ng.core.environment.yaml.NGEnvironmentConfig;
import io.harness.ng.core.environment.yaml.NGEnvironmentInfoConfig;
import io.harness.rule.Owner;
import io.harness.rule.OwnerRule;
import io.harness.yaml.core.variables.NGServiceOverrides;
import io.harness.yaml.core.variables.StringNGVariable;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class NGEnvironmentEntityMapperTest {
  @Test
  @Owner(developers = OwnerRule.YOGESH)
  @Category(UnitTests.class)
  public void testToYaml() {
    NGEnvironmentConfig cfg =
        NGEnvironmentConfig.builder()
            .ngEnvironmentInfoConfig(
                NGEnvironmentInfoConfig.builder()
                    .identifier("id1")
                    .name("name")
                    .orgIdentifier("orgId")
                    .projectIdentifier("projId")
                    .description("desc")
                    .type(PreProduction)
                    .tags(ImmutableMap.of("k1", "v2"))
                    .variables(asList(StringNGVariable.builder().name("v1").type(STRING).build()))
                    .serviceOverrides(
                        asList(NGServiceOverrides.builder()
                                   .serviceRef("s1")
                                   .variables(asList(StringNGVariable.builder().name("v1").type(STRING).build()))
                                   .build()))
                    .build())
            .build();
    String yaml = NGEnvironmentEntityMapper.toYaml(cfg);
    assertThat(yaml).isEqualTo("environment:\n"
        + "  orgIdentifier: \"orgId\"\n"
        + "  projectIdentifier: \"projId\"\n"
        + "  identifier: \"id1\"\n"
        + "  tags:\n"
        + "    k1: \"v2\"\n"
        + "  name: \"name\"\n"
        + "  description: \"desc\"\n"
        + "  type: \"PreProduction\"\n"
        + "  variables:\n"
        + "  - !<String>\n"
        + "    name: \"v1\"\n"
        + "    type: \"String\"\n"
        + "    required: false\n"
        + "    currentValue: null\n"
        + "  serviceOverrides:\n"
        + "  - serviceRef: \"s1\"\n"
        + "    variables:\n"
        + "    - !<String>\n"
        + "      name: \"v1\"\n"
        + "      type: \"String\"\n"
        + "      required: false\n"
        + "      currentValue: null\n");
  }

  @Test
  @Owner(developers = OwnerRule.YOGESH)
  @Category(UnitTests.class)
  public void testToNGEnvironmentConfig() {
    Environment entity = Environment.builder()
                             .type(PreProduction)
                             .accountId("accountId")
                             .orgIdentifier("orgId")
                             .projectIdentifier("projId")
                             .identifier("id")
                             .name("name")
                             .tag(NGTag.builder().key("k1").value("v1").build())
                             .build();
    NGEnvironmentConfig ngEnvironmentConfig = NGEnvironmentEntityMapper.toNGEnvironmentConfig(entity);

    NGEnvironmentInfoConfig cfg = ngEnvironmentConfig.getNgEnvironmentInfoConfig();

    assertThat(cfg.getIdentifier()).isEqualTo("id");
    assertThat(cfg.getOrgIdentifier()).isEqualTo("orgId");
    assertThat(cfg.getProjectIdentifier()).isEqualTo("projId");
    assertThat(cfg.getName()).isEqualTo("name");
    assertThat(cfg.getType()).isEqualTo(PreProduction);
    assertThat(cfg.getTags().get("k1")).isEqualTo("v1");
    assertThat(cfg.getServiceOverrides()).isNull();
    assertThat(cfg.getVariables()).isNull();
  }

  @Test
  @Owner(developers = OwnerRule.YOGESH)
  @Category(UnitTests.class)
  public void testTestToNGEnvironmentConfig() {
    EnvironmentRequestDTO dto = EnvironmentRequestDTO.builder()
                                    .identifier("id")
                                    .name("name")
                                    .orgIdentifier("orgId")
                                    .projectIdentifier("projId")
                                    .description("desc")
                                    .type(PreProduction)
                                    .tags(ImmutableMap.of("k1", "v1"))
                                    .build();

    NGEnvironmentConfig ngEnvironmentConfig = NGEnvironmentEntityMapper.toNGEnvironmentConfig(dto);

    NGEnvironmentInfoConfig cfg = ngEnvironmentConfig.getNgEnvironmentInfoConfig();

    assertThat(cfg.getIdentifier()).isEqualTo("id");
    assertThat(cfg.getOrgIdentifier()).isEqualTo("orgId");
    assertThat(cfg.getProjectIdentifier()).isEqualTo("projId");
    assertThat(cfg.getName()).isEqualTo("name");
    assertThat(cfg.getType()).isEqualTo(PreProduction);
    assertThat(cfg.getTags().get("k1")).isEqualTo("v1");
    assertThat(cfg.getServiceOverrides()).isNull();
    assertThat(cfg.getVariables()).isNull();
  }
}
