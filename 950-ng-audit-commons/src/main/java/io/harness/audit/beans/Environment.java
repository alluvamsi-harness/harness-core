package io.harness.audit.beans;

import static io.harness.annotations.dev.HarnessTeam.PL;

import io.harness.annotations.dev.OwnedBy;
import io.harness.ng.core.environment.beans.EnvironmentType;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.NotBlank;

@OwnedBy(PL)
@Data
@Builder
@FieldNameConstants(innerTypeName = "EnvironmentKeys")
public class Environment {
  @NotNull EnvironmentType type;
  @NotNull @NotBlank String identifier;
}
