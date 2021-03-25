package software.wings.beans.approval;

import static io.harness.annotations.dev.HarnessTeam.CDC;
import static io.harness.data.structure.HasPredicate.hasNone;
import static io.harness.data.structure.HasPredicate.hasSome;

import io.harness.annotations.dev.OwnedBy;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@OwnedBy(CDC)
@AllArgsConstructor
public class Criteria {
  @Setter Map<String, List<String>> conditions;
  @Getter @Setter ConditionalOperator operator;

  public Criteria() {
    conditions = new HashMap<>();
    operator = ConditionalOperator.AND;
  }

  public Map<String, List<String>> fetchConditions() {
    return this.conditions;
  }

  public String conditionsString() {
    if (hasNone(conditions)) {
      return "";
    }
    return conditions.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(
            Comparator.comparing(Map.Entry::getKey))) // To make sure state gets displayed before approval
        .map(condition
            -> StringUtils.capitalize(condition.getKey()) + " should be "
                + (condition.getValue().size() > 1 ? "any of " + String.join("/", condition.getValue())
                                                   : condition.getValue().get(0)))
        .collect(Collectors.joining(" " + operator.name().toLowerCase() + "\n"));
  }

  public boolean satisfied(Map<String, String> currentStatus) {
    if (hasSome(conditions) && operator != null) {
      List<Boolean> truthValues =
          conditions.entrySet()
              .stream()
              .map(condition -> condition.getValue().contains(currentStatus.get(condition.getKey())))
              .collect(Collectors.toList());
      return operator.applyOperator(truthValues);
    }
    return false;
  }
}
