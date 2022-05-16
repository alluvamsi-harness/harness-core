package io.harness.ng.accesscontrol.scopes;

import static io.harness.annotations.dev.HarnessTeam.PL;

import io.harness.annotations.dev.OwnedBy;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@OwnedBy(PL)
@Value
@Builder
@ApiModel(value = "ScopeName")
@Schema(name = "ScopeName")
public class ScopeNameDTO {
  String accountIdentifier;
  String orgName;
  String orgIdentifier;
  String projectName;
  String projectIdentifier;
}
