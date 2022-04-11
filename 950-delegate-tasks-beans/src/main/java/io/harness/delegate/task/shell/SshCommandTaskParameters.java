/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.delegate.task.shell;

import static io.harness.annotations.dev.HarnessTeam.CDP;

import io.harness.annotations.dev.OwnedBy;
import io.harness.delegate.task.ssh.SshInfraDelegateConfig;

import java.util.List;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Value
@OwnedBy(CDP)
public class SshCommandTaskParameters extends CommandTaskParameters {
  SshInfraDelegateConfig sshInfraDelegateConfig;
  List<TailFilePatternDto> tailFilePatterns;
}
