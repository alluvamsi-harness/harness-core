package io.harness.governance;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, property = "filterType", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = CustomUserGroupFilter.class, name = "CUSTOM")
  , @JsonSubTypes.Type(value = AllUserGroupFilter.class, name = "ALL")
})
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@OwnedBy(HarnessTeam.CDC)
public abstract class UserGroupFilter implements BlackoutWindowFilter {
  private BlackoutWindowFilterType filterType;

  @JsonCreator
  public UserGroupFilter(@JsonProperty("filterType") BlackoutWindowFilterType filterType) {
    this.filterType = filterType;
  }
}
