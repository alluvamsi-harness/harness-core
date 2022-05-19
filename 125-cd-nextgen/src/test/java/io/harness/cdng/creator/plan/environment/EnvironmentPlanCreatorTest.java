/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.cdng.creator.plan.environment;

import static io.harness.rule.OwnerRule.PRASHANTSHARMA;

import static org.assertj.core.api.Assertions.assertThat;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.category.element.UnitTests;
import io.harness.cdng.CDNGTestBase;
import io.harness.cdng.creator.plan.PlanCreatorConstants;
import io.harness.cdng.environment.yaml.EnvironmentPlanCreatorConfig;
import io.harness.cdng.infra.steps.EnvironmentStep;
import io.harness.cdng.infra.steps.InfraSectionStepParameters;
import io.harness.cdng.visitor.YamlTypes;
import io.harness.ng.core.environment.beans.EnvironmentType;
import io.harness.pms.contracts.plan.Dependency;
import io.harness.pms.sdk.core.plan.PlanNode;
import io.harness.pms.sdk.core.plan.creation.beans.PlanCreationContext;
import io.harness.pms.sdk.core.plan.creation.beans.PlanCreationResponse;
import io.harness.pms.yaml.YamlField;
import io.harness.pms.yaml.YamlUtils;
import io.harness.rule.Owner;
import io.harness.serializer.KryoSerializer;

import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;

@OwnedBy(HarnessTeam.CDC)
public class EnvironmentPlanCreatorTest extends CDNGTestBase {
  @Inject KryoSerializer kryoSerializer;
  @Inject @InjectMocks EnvironmentPlanCreator environmentPlanCreator;

  @Test
  @Owner(developers = PRASHANTSHARMA)
  @Category(UnitTests.class)
  public void testGetFieldClass() {
    assertThat(environmentPlanCreator.getFieldClass()).isEqualTo(EnvironmentPlanCreatorConfig.class);
  }

  @Test
  @Owner(developers = PRASHANTSHARMA)
  @Category(UnitTests.class)
  public void testGetSupportedTypes() {
    Map<String, Set<String>> supportedTypes = environmentPlanCreator.getSupportedTypes();
    assertThat(supportedTypes.containsKey(YamlTypes.ENVIRONMENT_YAML)).isEqualTo(true);
    assertThat(supportedTypes.get(YamlTypes.ENVIRONMENT_YAML).size()).isEqualTo(2);
    assertThat(supportedTypes.get(YamlTypes.ENVIRONMENT_YAML).contains(EnvironmentType.PreProduction.name()))
        .isEqualTo(true);
    assertThat(supportedTypes.get(YamlTypes.ENVIRONMENT_YAML).contains(EnvironmentType.Production.name()))
        .isEqualTo(true);
  }

  @Test
  @Owner(developers = PRASHANTSHARMA)
  @Category(UnitTests.class)
  public void testCreatePlanNode() throws IOException {
    ClassLoader classLoader = this.getClass().getClassLoader();
    InputStream yamlFile = classLoader.getResourceAsStream("cdng/plan/environment/environment.yml");
    assertThat(yamlFile).isNotNull();

    String yaml = new Scanner(yamlFile, "UTF-8").useDelimiter("\\A").next();
    yaml = YamlUtils.injectUuid(yaml);
    YamlField environmentYaml = YamlUtils.readTree(yaml);

    HashMap<String, ByteString> metadataDependency = new HashMap<>();
    metadataDependency.put(YamlTypes.INFRASTRUCTURE_STEP_PARAMETERS,
        ByteString.copyFrom(kryoSerializer.asDeflatedBytes(InfraSectionStepParameters.builder().build())));
    metadataDependency.put(YamlTypes.SERVICE_SPEC, ByteString.copyFrom(kryoSerializer.asDeflatedBytes("service_spec")));
    PlanCreationContext ctx = PlanCreationContext.builder()
                                  .currentField(environmentYaml)
                                  .dependency(Dependency.newBuilder().putAllMetadata(metadataDependency).build())
                                  .build();

    PlanCreationResponse planForField =
        environmentPlanCreator.createPlanForField(ctx, EnvironmentPlanCreatorConfig.builder().build());
    assertThat(planForField).isNotNull();
    assertThat(planForField.getPlanNode()).isNotNull();
    PlanNode planNode = planForField.getPlanNode();
    assertThat(planNode.getStepType()).isEqualTo(EnvironmentStep.STEP_TYPE);
    assertThat(planNode.getName()).isEqualTo(PlanCreatorConstants.ENVIRONMENT_NODE_NAME);
    assertThat(planNode.getIdentifier()).isEqualTo(YamlTypes.ENVIRONMENT_YAML);
  }
}