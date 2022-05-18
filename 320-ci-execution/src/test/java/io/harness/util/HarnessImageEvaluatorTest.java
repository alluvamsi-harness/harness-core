/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.util;

import static io.harness.rule.OwnerRule.RAGHAV_GUPTA;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.joor.Reflect.on;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.when;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.sweepingoutputs.K8StageInfraDetails;
import io.harness.beans.sweepingoutputs.VmStageInfraDetails;
import io.harness.beans.yaml.extended.infrastrucutre.Infrastructure;
import io.harness.beans.yaml.extended.infrastrucutre.K8sDirectInfraYaml;
import io.harness.beans.yaml.extended.infrastrucutre.K8sDirectInfraYaml.K8sDirectInfraYamlSpec;
import io.harness.category.element.UnitTests;
import io.harness.delegate.beans.ci.pod.ConnectorDetails;
import io.harness.exception.InvalidRequestException;
import io.harness.executionplan.CIExecutionTestBase;
import io.harness.pms.contracts.ambiance.Ambiance;
import io.harness.pms.execution.utils.AmbianceUtils;
import io.harness.pms.yaml.ParameterField;
import io.harness.rule.Owner;
import io.harness.stateutils.buildstate.ConnectorUtils;

import com.google.inject.Inject;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.groovy.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;

@Slf4j
@OwnedBy(HarnessTeam.CI)
public class HarnessImageEvaluatorTest extends CIExecutionTestBase {
  private final Ambiance ambiance = Ambiance.newBuilder()
                                        .putAllSetupAbstractions(Maps.of("accountId", "accountId", "projectIdentifier",
                                            "projectIdentifier", "orgIdentifier", "orgIdentifier"))
                                        .build();

  @Mock private ConnectorUtils connectorUtils;
  @Mock private ConnectorDetails connectorDetails;
  @Inject private HarnessImageEvaluator harnessImageEvaluator;

  @Before
  public void setUp() {
    on(harnessImageEvaluator).set("connectorUtils", connectorUtils);
  }

  @Test
  @Owner(developers = RAGHAV_GUPTA)
  @Category(UnitTests.class)
  public void testHarnessImageEvaluateForK8() {
    String connectorRefValue = "docker";
    Infrastructure infrastructure =
        K8sDirectInfraYaml.builder()
            .spec(K8sDirectInfraYamlSpec.builder()
                      .harnessImageConnectorRef(ParameterField.createValueField(connectorRefValue))
                      .build())
            .build();
    when(connectorUtils.getConnectorDetails(any(), matches(connectorRefValue))).thenReturn(connectorDetails);
    when(connectorDetails.getIdentifier()).thenReturn(connectorRefValue);
    Optional<ConnectorDetails> optionalHarnessImageConnector =
        harnessImageEvaluator.evaluate(AmbianceUtils.getNgAccess(ambiance), infrastructure);
    assertThat(true).isEqualTo(optionalHarnessImageConnector.isPresent());
    assertThat(connectorRefValue).isEqualTo(optionalHarnessImageConnector.get().getIdentifier());
  }

  @Test
  @Owner(developers = RAGHAV_GUPTA)
  @Category(UnitTests.class)
  public void testHarnessImageEvaluateForK8WithoutConnectorRef() {
    Infrastructure infrastructure = K8sDirectInfraYaml.builder().spec(K8sDirectInfraYamlSpec.builder().build()).build();
    Optional<ConnectorDetails> optionalHarnessImageConnector =
        harnessImageEvaluator.evaluate(AmbianceUtils.getNgAccess(ambiance), infrastructure);
    assertThat(false).isEqualTo(optionalHarnessImageConnector.isPresent());
  }

  @Test
  @Owner(developers = RAGHAV_GUPTA)
  @Category(UnitTests.class)
  public void testHarnessImageEvaluateForVM() {
    String connectorRefValue = "docker";
    VmStageInfraDetails vmStageInfraDetails =
        VmStageInfraDetails.builder()
            .harnessImageConnectorRef(ParameterField.createValueField(connectorRefValue))
            .build();
    when(connectorUtils.getConnectorDetails(any(), matches(connectorRefValue))).thenReturn(connectorDetails);
    when(connectorDetails.getIdentifier()).thenReturn(connectorRefValue);
    Optional<ConnectorDetails> optionalHarnessImageConnector =
        harnessImageEvaluator.evaluate(AmbianceUtils.getNgAccess(ambiance), vmStageInfraDetails);
    assertThat(true).isEqualTo(optionalHarnessImageConnector.isPresent());
    assertThat(connectorRefValue).isEqualTo(optionalHarnessImageConnector.get().getIdentifier());
  }

  @Test
  @Owner(developers = RAGHAV_GUPTA)
  @Category(UnitTests.class)
  public void testHarnessImageEvaluateForVMWithoutConnectorRef() {
    VmStageInfraDetails vmStageInfraDetails = VmStageInfraDetails.builder().build();
    Optional<ConnectorDetails> optionalHarnessImageConnector =
        harnessImageEvaluator.evaluate(AmbianceUtils.getNgAccess(ambiance), vmStageInfraDetails);
    assertThat(false).isEqualTo(optionalHarnessImageConnector.isPresent());
  }

  @Test
  @Owner(developers = RAGHAV_GUPTA)
  @Category(UnitTests.class)
  public void testHarnessImageEvaluateFork8InfraDetails() {
    K8StageInfraDetails k8StageInfraDetails = K8StageInfraDetails.builder().build();
    assertThatThrownBy(() -> harnessImageEvaluator.evaluate(AmbianceUtils.getNgAccess(ambiance), k8StageInfraDetails))
        .isInstanceOf(InvalidRequestException.class);
  }
}
