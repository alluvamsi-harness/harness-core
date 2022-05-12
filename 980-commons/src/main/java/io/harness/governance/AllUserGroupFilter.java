package io.harness.governance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public class AllUserGroupFilter extends UserGroupFilter {
  @JsonCreator
  @Builder
  public AllUserGroupFilter(@JsonProperty("filterType") BlackoutWindowFilterType userGroupFilterType) {
    super(userGroupFilterType);
  }
}
