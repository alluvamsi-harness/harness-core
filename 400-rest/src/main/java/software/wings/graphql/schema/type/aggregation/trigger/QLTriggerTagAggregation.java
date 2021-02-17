package software.wings.graphql.schema.type.aggregation.trigger;

import io.harness.annotations.dev.Module;
import io.harness.annotations.dev.TargetModule;

import software.wings.graphql.schema.type.aggregation.TagAggregation;

import lombok.Builder;
import lombok.Value;

/**
 * @author rktummala on 09/05/19
 */
@Value
@Builder
@TargetModule(Module._380_CG_GRAPHQL)
public class QLTriggerTagAggregation implements TagAggregation<QLTriggerTagType> {
  private QLTriggerTagType entityType;
  private String tagName;
}
