package io.harness.governance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class AllUserGroupFilter extends UserGroupFilter {
  @JsonCreator
  @Builder
  public AllUserGroupFilter(@JsonProperty("filterType") BlackoutWindowFilterType userGroupFilterType) {
    super(userGroupFilterType);
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  @JsonTypeName("ALL")
  public static final class Yaml extends UserGroupFilterYaml {
    @Builder
    public Yaml(BlackoutWindowFilterType filterType) {
      super(filterType);
    }

    public Yaml() {}
  }
}
