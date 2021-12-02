package io.harness.plancreator.steps;

import static io.harness.annotations.dev.HarnessTeam.PIPELINE;
import static io.harness.pms.yaml.YAMLFieldNameConstants.STEP;

import io.harness.annotations.dev.OwnedBy;
import io.harness.data.structure.EmptyPredicate;
import io.harness.pms.sdk.core.plan.creation.beans.PlanCreationContext;
import io.harness.pms.sdk.core.plan.creation.beans.PlanCreationResponse;
import io.harness.pms.sdk.core.plan.creation.creators.PartialPlanCreator;
import io.harness.serializer.KryoSerializer;

import com.google.inject.Inject;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@OwnedBy(PIPELINE)
public abstract class AbstractStepPlanCreator<T extends AbstractStepNode> implements PartialPlanCreator<T> {
  @Inject protected KryoSerializer kryoSerializer;

  public abstract Set<String> getSupportedStepTypes();

  @Override public abstract Class<T> getFieldClass();

  @Override
  public Map<String, Set<String>> getSupportedTypes() {
    Set<String> stepTypes = getSupportedStepTypes();
    if (EmptyPredicate.isEmpty(stepTypes)) {
      return Collections.emptyMap();
    }
    return Collections.singletonMap(STEP, stepTypes);
  }

  @Override public abstract PlanCreationResponse createPlanForField(PlanCreationContext ctx, T stepElement);
}
