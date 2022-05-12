package io.harness.governance;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeName("CUSTOM")
@Data
@EqualsAndHashCode(callSuper = true)
@OwnedBy(HarnessTeam.CDC)
@JsonInclude(NON_NULL)
public class CustomUserGroupFilter extends UserGroupFilter {
  private List<String> userGroups;

  @JsonCreator
  @Builder
  public CustomUserGroupFilter(@JsonProperty("filterType") BlackoutWindowFilterType userGroupFilterType,
      @JsonProperty("userGroups") List<String> userGroups) {
    super(userGroupFilterType);
    this.userGroups = userGroups;
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  @JsonTypeName("CUSTOM")
  public static final class Yaml extends UserGroupFilterYaml {
    private List<String> userGroupNames;

    @Builder
    public Yaml(BlackoutWindowFilterType filterType, List<String> userGroupNames) {
      super(filterType);
      setUserGroupNames(userGroupNames);
    }

    public Yaml() {}
  }
}