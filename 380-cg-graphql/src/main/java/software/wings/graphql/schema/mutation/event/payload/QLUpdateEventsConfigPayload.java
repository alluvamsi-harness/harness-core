package software.wings.graphql.schema.mutation.event.payload;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;

import software.wings.graphql.schema.mutation.QLMutationPayload;
import software.wings.graphql.schema.type.event.QLEventsConfig;
import software.wings.security.PermissionAttribute;
import software.wings.security.annotations.Scope;

import lombok.Builder;
import lombok.Value;

@OwnedBy(HarnessTeam.CDC)
@Value
@Builder
@Scope(PermissionAttribute.ResourceType.APPLICATION)
public class QLUpdateEventsConfigPayload implements QLMutationPayload {
  String clientMutationId;
  QLEventsConfig eventsConfig;
}
