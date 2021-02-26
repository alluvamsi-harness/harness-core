package software.wings.graphql.schema.mutation.execution.input;

import static io.harness.annotations.dev.HarnessTeam.CDC;

import io.harness.annotations.dev.Module;
import io.harness.annotations.dev.OwnedBy;
import io.harness.annotations.dev.TargetModule;

import software.wings.security.PermissionAttribute;
import software.wings.security.annotations.Scope;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

@OwnedBy(CDC)
@Value
@Builder
@FieldNameConstants(innerTypeName = "QLArtifactValueInputKeys")
@Scope(PermissionAttribute.ResourceType.DEPLOYMENT)
@TargetModule(Module._380_CG_GRAPHQL)
public class QLArtifactValueInput {
  QLArtifactInputType valueType;
  QLArtifactIdInput artifactId;
  QLBuildNumberInput buildNumber;
  QLParameterizedArtifactSourceInput parameterizedArtifactSource;
}
