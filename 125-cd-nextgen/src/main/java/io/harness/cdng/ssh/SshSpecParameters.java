/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.cdng.ssh;

import static io.harness.annotations.dev.HarnessTeam.CDP;

import io.harness.annotations.dev.OwnedBy;
import io.harness.plancreator.steps.common.SpecParameters;
import io.harness.pms.yaml.ParameterField;
import io.harness.ssh.SshCommandUnitConstants;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

@OwnedBy(CDP)
public interface SshSpecParameters extends SpecParameters {
  @JsonIgnore ParameterField<List<String>> getDelegateSelectors();

  @Nonnull
  @JsonIgnore
  default List<String> getCommandUnits() {
    return Arrays.asList(SshCommandUnitConstants.Init, SshCommandUnitConstants.Exec, SshCommandUnitConstants.Cleanup);
  }
}
