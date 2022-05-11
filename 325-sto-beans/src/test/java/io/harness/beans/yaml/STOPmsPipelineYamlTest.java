/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.beans.yaml;

import static io.harness.annotations.dev.HarnessTeam.STO;
import static io.harness.rule.OwnerRule.SERGEY;

import static org.assertj.core.api.Assertions.assertThat;

import io.harness.STOBeansTestBase;
import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.stages.IntegrationStageConfig;
import io.harness.category.element.UnitTests;
import io.harness.plancreator.execution.ExecutionWrapperConfig;
import io.harness.plancreator.pipeline.PipelineConfig;
import io.harness.plancreator.stages.StageElementWrapperConfig;
import io.harness.plancreator.stages.stage.StageElementConfig;
import io.harness.plancreator.steps.StepElementConfig;
import io.harness.rule.Owner;
import io.harness.utils.YamlPipelineUtils;
import io.harness.yaml.core.properties.CIProperties;
import io.harness.yaml.core.properties.NGProperties;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Slf4j
@OwnedBy(STO)
public class STOPmsPipelineYamlTest extends STOBeansTestBase {
  @Test
  @Owner(developers = SERGEY)
  @Category(UnitTests.class)
  public void testPipelineConversion() throws IOException {
    ClassLoader classLoader = this.getClass().getClassLoader();
    final URL testFile = classLoader.getResource("securitypms.yml");
    PipelineConfig ngPipelineActual = YamlPipelineUtils.read(testFile, PipelineConfig.class);
    assertThat(ngPipelineActual).isNotNull();

    NGProperties properties = ngPipelineActual.getPipelineInfoConfig().getProperties();
    CIProperties ciProperties = properties.getCi();
    log.info(ciProperties.toString());

    List<StageElementWrapperConfig> stages = ngPipelineActual.getPipelineInfoConfig().getStages();
    for (StageElementWrapperConfig stage : stages) {
      StageElementConfig stageElementConfig =
          YamlPipelineUtils.read(stage.getStage().toString(), StageElementConfig.class);

      assertThat(stageElementConfig).isNotNull();

      IntegrationStageConfig integrationStageConfig = (IntegrationStageConfig) stageElementConfig.getStageType();
      if (integrationStageConfig.getExecution() != null) {
        for (ExecutionWrapperConfig executionWrapperConfig : integrationStageConfig.getExecution().getSteps()) {
          StepElementConfig stepElementConfig =
              YamlPipelineUtils.read(executionWrapperConfig.getStep().toString(), StepElementConfig.class);
          assertThat(stepElementConfig).isNotNull();
        }
      }
    }
  }
}
