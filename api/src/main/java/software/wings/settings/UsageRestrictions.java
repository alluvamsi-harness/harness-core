package software.wings.settings;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.wings.security.EnvFilter;
import software.wings.security.GenericEntityFilter;

import java.util.Set;

@Data
@NoArgsConstructor
public class UsageRestrictions {
  private Set<AppEnvRestriction> appEnvRestrictions;

  @Data
  @Builder
  public static class AppEnvRestriction {
    private GenericEntityFilter appFilter;
    private EnvFilter envFilter;
  }
}
