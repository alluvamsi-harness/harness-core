package io.harness.governance;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.yaml.BaseYaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, property = "filterType", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = AllUserGroupFilter.Yaml.class, name = "ALL")
  , @JsonSubTypes.Type(value = CustomUserGroupFilter.Yaml.class, name = "CUSTOM")
})
@OwnedBy(HarnessTeam.CDC)
public abstract class UserGroupFilterYaml extends BaseYaml {
  private BlackoutWindowFilterType filterType;

  public UserGroupFilterYaml(@JsonProperty("filterType") BlackoutWindowFilterType filterType) {
    this.filterType = filterType;
  }
}
