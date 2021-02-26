package io.harness.pms.sdk.registries;

import static io.harness.annotations.dev.HarnessTeam.CDC;

import io.harness.annotations.dev.OwnedBy;
import io.harness.data.structure.EmptyPredicate;
import io.harness.pms.contracts.advisers.AdviserType;
import io.harness.pms.contracts.execution.events.OrchestrationEventType;
import io.harness.pms.contracts.facilitators.FacilitatorType;
import io.harness.pms.contracts.refobjects.RefType;
import io.harness.pms.contracts.steps.StepType;
import io.harness.pms.expression.OrchestrationFieldProcessor;
import io.harness.pms.expression.OrchestrationFieldType;
import io.harness.pms.sdk.PmsSdkConfiguration;
import io.harness.pms.sdk.core.adviser.Adviser;
import io.harness.pms.sdk.core.events.OrchestrationEventHandler;
import io.harness.pms.sdk.core.facilitator.Facilitator;
import io.harness.pms.sdk.core.registries.AdviserRegistry;
import io.harness.pms.sdk.core.registries.FacilitatorRegistry;
import io.harness.pms.sdk.core.registries.OrchestrationEventHandlerRegistry;
import io.harness.pms.sdk.core.registries.OrchestrationFieldRegistry;
import io.harness.pms.sdk.core.registries.ResolverRegistry;
import io.harness.pms.sdk.core.registries.StepRegistry;
import io.harness.pms.sdk.core.registries.registrar.OrchestrationFieldRegistrar;
import io.harness.pms.sdk.core.registries.registrar.ResolverRegistrar;
import io.harness.pms.sdk.core.resolver.Resolver;
import io.harness.pms.sdk.core.steps.Step;
import io.harness.pms.sdk.registries.registrar.local.PmsSdkAdviserRegistrar;
import io.harness.pms.sdk.registries.registrar.local.PmsSdkFacilitatorRegistrar;
import io.harness.pms.sdk.registries.registrar.local.PmsSdkOrchestrationEventRegistrars;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

@OwnedBy(CDC)
@Slf4j
public class PmsSdkRegistryModule extends AbstractModule {
  private final PmsSdkConfiguration config;

  private static PmsSdkRegistryModule instance;

  public static synchronized PmsSdkRegistryModule getInstance(PmsSdkConfiguration config) {
    if (instance == null) {
      instance = new PmsSdkRegistryModule(config);
    }
    return instance;
  }

  public PmsSdkRegistryModule(PmsSdkConfiguration config) {
    this.config = config;
  }

  public void configure() {
    MapBinder.newMapBinder(binder(), String.class, ResolverRegistrar.class);

    MapBinder.newMapBinder(binder(), String.class, OrchestrationFieldRegistrar.class);
  }

  @Provides
  @Singleton
  StepRegistry providesStateRegistry(Injector injector) {
    StepRegistry stepRegistry = new StepRegistry();
    Map<StepType, Class<? extends Step>> engineSteps = config.getEngineSteps();
    if (EmptyPredicate.isNotEmpty(engineSteps)) {
      engineSteps.forEach((k, v) -> stepRegistry.register(k, injector.getInstance(v)));
    }
    return stepRegistry;
  }

  @Provides
  @Singleton
  AdviserRegistry providesAdviserRegistry(Injector injector) {
    AdviserRegistry adviserRegistry = new AdviserRegistry();
    Map<AdviserType, Class<? extends Adviser>> engineAdvisers = config.getEngineAdvisers();
    if (EmptyPredicate.isEmpty(engineAdvisers)) {
      engineAdvisers = new HashMap<>();
    }
    engineAdvisers.putAll(PmsSdkAdviserRegistrar.getEngineAdvisers());
    if (EmptyPredicate.isNotEmpty(engineAdvisers)) {
      engineAdvisers.forEach((k, v) -> adviserRegistry.register(k, injector.getInstance(v)));
    }
    return adviserRegistry;
  }

  @Provides
  @Singleton
  ResolverRegistry providesResolverRegistry(Injector injector, Map<String, ResolverRegistrar> resolverRegistrarMap) {
    Set<Pair<RefType, Resolver<?>>> classes = new HashSet<>();
    resolverRegistrarMap.values().forEach(resolverRegistrar -> resolverRegistrar.register(classes));
    ResolverRegistry resolverRegistry = new ResolverRegistry();
    injector.injectMembers(resolverRegistry);
    classes.forEach(pair -> { resolverRegistry.register(pair.getLeft(), pair.getRight()); });
    return resolverRegistry;
  }

  @Provides
  @Singleton
  FacilitatorRegistry providesFacilitatorRegistry(Injector injector) {
    FacilitatorRegistry facilitatorRegistry = new FacilitatorRegistry();
    Map<FacilitatorType, Class<? extends Facilitator>> engineFacilitators = config.getEngineFacilitators();
    if (EmptyPredicate.isEmpty(engineFacilitators)) {
      engineFacilitators = new HashMap<>();
    }
    engineFacilitators.putAll(PmsSdkFacilitatorRegistrar.getEngineFacilitators());
    engineFacilitators.forEach((k, v) -> facilitatorRegistry.register(k, injector.getInstance(v)));
    return facilitatorRegistry;
  }

  @Provides
  @Singleton
  OrchestrationEventHandlerRegistry providesEventHandlerRegistry(Injector injector) {
    OrchestrationEventHandlerRegistry handlerRegistry = new OrchestrationEventHandlerRegistry();
    Map<OrchestrationEventType, Set<Class<? extends OrchestrationEventHandler>>> engineEventHandlersMap =
        config.getEngineEventHandlersMap();
    if (EmptyPredicate.isNotEmpty(engineEventHandlersMap)) {
      mergeEventHandlers(engineEventHandlersMap, PmsSdkOrchestrationEventRegistrars.getHandlers());
      engineEventHandlersMap.forEach((key, value) -> {
        Set<OrchestrationEventHandler> eventHandlerSet = new HashSet<>();
        value.forEach(v -> eventHandlerSet.add(injector.getInstance(v)));
        handlerRegistry.register(key, eventHandlerSet);
      });
    } else {
      PmsSdkOrchestrationEventRegistrars.getHandlers().forEach((key, value) -> {
        Set<OrchestrationEventHandler> eventHandlerSet = new HashSet<>();
        value.forEach(v -> eventHandlerSet.add(injector.getInstance(v)));
        handlerRegistry.register(key, eventHandlerSet);
      });
    }
    return handlerRegistry;
  }

  @Provides
  @Singleton
  OrchestrationFieldRegistry providesOrchestrationFieldRegistry(
      Injector injector, Map<String, OrchestrationFieldRegistrar> orchestrationFieldRegistrarMap) {
    Set<Pair<OrchestrationFieldType, OrchestrationFieldProcessor>> classes = new HashSet<>();
    orchestrationFieldRegistrarMap.values().forEach(
        orchestrationFieldRegistrar -> orchestrationFieldRegistrar.register(classes));
    OrchestrationFieldRegistry orchestrationFieldRegistry = new OrchestrationFieldRegistry();
    injector.injectMembers(orchestrationFieldRegistry);
    classes.forEach(pair -> orchestrationFieldRegistry.register(pair.getLeft(), pair.getRight()));
    return orchestrationFieldRegistry;
  }

  private void mergeEventHandlers(
      Map<OrchestrationEventType, Set<Class<? extends OrchestrationEventHandler>>> finalHandlers,
      Map<OrchestrationEventType, Set<Class<? extends OrchestrationEventHandler>>> handlers) {
    for (Map.Entry<OrchestrationEventType, Set<Class<? extends OrchestrationEventHandler>>> entry :
        handlers.entrySet()) {
      if (finalHandlers.containsKey(entry.getKey())) {
        Set<Class<? extends OrchestrationEventHandler>> existing = finalHandlers.get(entry.getKey());
        existing.addAll(entry.getValue());
        finalHandlers.put(entry.getKey(), existing);
      } else {
        finalHandlers.put(entry.getKey(), entry.getValue());
      }
    }
  }
}
