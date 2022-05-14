/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.dtos.instanceinfo;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.util.InstanceSyncKey;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@OwnedBy(HarnessTeam.CDP)
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class ServerlessAwsLambdaInstanceInfoDTO extends InstanceInfoDTO {
  @NotNull private String serviceName;
  @NotNull private String functionName;
  @NotNull private String region;
  private String stage;
  private String handler;
  private String memorySize;
  private String runTime;
  private Integer timeout;

  @Override
  public String prepareInstanceKey() {
    return InstanceSyncKey.builder()
        .clazz(ServerlessAwsLambdaInstanceInfoDTO.class)
        .part(serviceName)
        .part(functionName)
        .build()
        .toString();
    // todo: need to confirm this
  }

  @Override
  public String prepareInstanceSyncHandlerKey() {
    return InstanceSyncKey.builder().part(serviceName).build().toString();
    // todo: need to check and change if required
  }

  @Override
  public String getPodName() {
    return functionName;
  }
}
