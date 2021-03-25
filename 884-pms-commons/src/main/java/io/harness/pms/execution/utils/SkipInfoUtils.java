package io.harness.pms.execution.utils;

import static io.harness.data.structure.HasPredicate.hasSome;

import io.harness.pms.yaml.ParameterField;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SkipInfoUtils {
  public String getSkipCondition(ParameterField<String> skipCondition) {
    if (skipCondition == null) {
      return null;
    }
    if (hasSome(skipCondition.getValue())) {
      return skipCondition.getValue();
    }
    return skipCondition.getExpressionValue();
  }
}
