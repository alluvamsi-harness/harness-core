package io.harness.dashboard.dtos;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
@OwnedBy(HarnessTeam.PL)
public class TopProjectsDashboardInfo<T> {
  ProjectInfo projectInfo;
  OrgInfo orgInfo;
  AccountInfo accountInfo;
  T countDetails;
}
