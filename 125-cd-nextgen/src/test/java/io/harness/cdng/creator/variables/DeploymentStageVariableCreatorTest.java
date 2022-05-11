/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cdng.creator.variables;

import static io.harness.rule.OwnerRule.HINGER;

import static org.assertj.core.api.Assertions.assertThat;

import io.harness.CategoryTest;
import io.harness.category.element.UnitTests;
import io.harness.cdng.creator.plan.stage.DeploymentStageNode;
import io.harness.pms.contracts.plan.YamlProperties;
import io.harness.pms.sdk.core.variables.beans.VariableCreationContext;
import io.harness.pms.sdk.core.variables.beans.VariableCreationResponse;
import io.harness.pms.yaml.YamlField;
import io.harness.pms.yaml.YamlUtils;
import io.harness.rule.Owner;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class DeploymentStageVariableCreatorTest extends CategoryTest {
  DeploymentStageVariableCreator deploymentStageVariableCreator = new DeploymentStageVariableCreator();

  @Test
  @Owner(developers = HINGER)
  @Category(UnitTests.class)
  public void getClassType() {
    assertThat(deploymentStageVariableCreator.getFieldClass()).isEqualTo(DeploymentStageNode.class);
  }

  @Test
  @Owner(developers = HINGER)
  @Category(UnitTests.class)
  public void createVariablesForParentNodes() throws IOException {
    ClassLoader classLoader = this.getClass().getClassLoader();
    final URL testFile = classLoader.getResource("pipelineWithServiceInfraVariableCreatorJson.yaml");
    String pipelineJson = Resources.toString(testFile, Charsets.UTF_8);
    YamlField fullYamlField = YamlUtils.readTree(pipelineJson);

    // Pipeline Node
    YamlField stageField = fullYamlField.getNode()
                               .getField("pipeline")
                               .getNode()
                               .getField("stages")
                               .getNode()
                               .asArray()
                               .get(0)
                               .getField("stage");
    // yaml input expressions
    VariableCreationResponse variablesForParentNodeV2 = deploymentStageVariableCreator.createVariablesForParentNodeV2(
        VariableCreationContext.builder().currentField(stageField).build(),
        YamlUtils.read(stageField.getNode().toString(), DeploymentStageNode.class));
    List<String> fqnPropertiesList = variablesForParentNodeV2.getYamlProperties()
                                         .values()
                                         .stream()
                                         .map(YamlProperties::getFqn)
                                         .collect(Collectors.toList());
    assertThat(fqnPropertiesList)
        .containsAll(Arrays.asList("pipeline.stages.dep.spec.infrastructure.infrastructureDefinition.spec.releaseName",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.manifests.testsvc.spec.chartVersion",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.manifests.testsvc.spec.skipResourceVersioning",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.manifests.testsvc.spec.chartName",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.manifests.testsvc.spec.store.spec.connectorRef",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.artifacts.sidecars.postgres.spec.tagRegex",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.artifacts.sidecars.postgres.spec.imagePath",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.artifacts.sidecars.postgres.spec.tag",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.artifacts.sidecars.postgres.spec.connectorRef",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.artifacts.primary.spec.connectorRef",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.artifacts.primary.spec.tag",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.artifacts.primary.spec.imagePath",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.artifacts.primary.spec.tagRegex",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.variables.service_var2",
            "pipeline.stages.dep.spec.serviceConfig.serviceDefinition.spec.variables.service_var1",
            "pipeline.stages.dep.spec.infrastructure.allowSimultaneousDeployments",
            "pipeline.stages.dep.spec.infrastructure.infrastructureDefinition.spec.namespace",
            "pipeline.stages.dep.spec.infrastructure.infrastructureDefinition.spec.connectorRef",
            "pipeline.stages.dep.description", "pipeline.stages.dep.delegateSelectors",
            "pipeline.stages.dep.spec.serviceConfig.serviceRef", "pipeline.stages.dep.name",
            "pipeline.stages.dep.spec.infrastructure.environmentRef",
            "pipeline.stages.dep.spec.infrastructure.infrastructureKey"));

    LinkedHashMap<String, VariableCreationResponse> variablesForChildrenNodesV2 =
        deploymentStageVariableCreator.createVariablesForChildrenNodesV2(
            VariableCreationContext.builder().currentField(stageField).build(), null);

    String uuidForProvisionerInsideInfrastructureStep = "uvlCGOSEScCPv5Y4_-LbVg";
    String uuidForProvisionerStep = "mbSWldzKSzyN-n2VzcV_jg";
    String uuidForProvisionerRollbackStep = "R9VL1m9eQcuoeS2PH_or0g";

    assertThat(variablesForChildrenNodesV2.containsKey(uuidForProvisionerInsideInfrastructureStep)).isTrue();

    Map<String, String> provisionerMap = variablesForChildrenNodesV2.get(uuidForProvisionerInsideInfrastructureStep)
                                             .getDependencies()
                                             .getDependenciesMap();

    assertThat(provisionerMap.get(uuidForProvisionerStep)).containsSubsequence("provisioner/rollbackSteps");
    assertThat(provisionerMap.get(uuidForProvisionerRollbackStep)).containsSubsequence("provisioner/steps");

    String uuidForExecutionNode = "jN1lz-uVSIW8vFKXWumINA";
    assertThat(variablesForChildrenNodesV2.containsKey(uuidForExecutionNode)).isTrue();
  }
}
