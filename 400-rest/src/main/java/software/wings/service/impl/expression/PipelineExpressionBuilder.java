package software.wings.service.impl.expression;

import static io.harness.annotations.dev.HarnessTeam.CDC;
import static io.harness.data.structure.HasPredicate.hasNone;

import io.harness.annotations.dev.OwnedBy;

import software.wings.beans.Pipeline;
import software.wings.beans.Variable;
import software.wings.service.intfc.PipelineService;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@OwnedBy(CDC)
@Singleton
public class PipelineExpressionBuilder extends ExpressionBuilder {
  @Inject private PipelineService pipelineService;

  @Override
  public Set<String> getExpressions(String appId, String entityId) {
    SortedSet<String> expressions = new TreeSet<>();
    Pipeline pipeline = pipelineService.readPipelineWithVariables(appId, entityId);
    if (pipeline == null || hasNone(pipeline.getPipelineVariables())) {
      return expressions;
    }
    for (Variable variable : pipeline.getPipelineVariables()) {
      if (variable.getName() != null) {
        expressions.add("workflow.variables." + variable.getName());
      }
    }
    return expressions;
  }

  @Override
  public Set<String> getDynamicExpressions(String appId, String entityId) {
    return new TreeSet<>();
  }
}
