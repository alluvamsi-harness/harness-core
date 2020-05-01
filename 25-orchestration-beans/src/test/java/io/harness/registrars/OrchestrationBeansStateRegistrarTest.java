package io.harness.registrars;

import static io.harness.rule.OwnerRule.PRASHANT;

import io.harness.OrchestrationBeansTest;
import io.harness.category.element.UnitTests;
import io.harness.rule.Owner;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class OrchestrationBeansStateRegistrarTest extends OrchestrationBeansTest {
  @Test
  @Owner(developers = PRASHANT)
  @Category(UnitTests.class)
  public void shouldTestRegister() {
    new OrchestrationBeansStateRegistrar().testClassesModule();
  }
}