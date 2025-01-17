/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package software.wings.sm.states.azure.appservices;

import static io.harness.annotations.dev.HarnessTeam.CDC;

import io.harness.annotations.dev.OwnedBy;

import software.wings.api.ContextElementParamMapper;
import software.wings.sm.ExecutionContext;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;

@OwnedBy(CDC)
public class AzureAppServiceSlotSetupContextElementParamMapper implements ContextElementParamMapper {
  private final AzureAppServiceSlotSetupContextElement element;

  public AzureAppServiceSlotSetupContextElementParamMapper(AzureAppServiceSlotSetupContextElement element) {
    this.element = element;
  }

  @Override
  public Map<String, Object> paramMap(ExecutionContext context) {
    Map<String, Object> map = new HashMap<>();
    map.put("webApp", this.element.getWebApp());
    map.put("deploymentSlot", this.element.getDeploymentSlot());

    return ImmutableMap.of("azurewebapp", map);
  }
}