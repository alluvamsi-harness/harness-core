package io.harness.governance;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.yaml.BaseYaml;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@OwnedBy(HarnessTeam.CDC)
public class UserGroupFilterYaml extends BaseYaml {
  private BlackoutWindowFilterType filterType;
  private List<String> userGroupNames;
}
