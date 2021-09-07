package io.harness.registries;

import static io.harness.rule.OwnerRule.SAHIL;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import io.harness.OrchestrationTestBase;
import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.category.element.UnitTests;
import io.harness.event.handlers.AddExecutableResponseRequestProcessor;
import io.harness.event.handlers.AddStepDetailsInstanceRequestProcessor;
import io.harness.event.handlers.AdviserResponseRequestProcessor;
import io.harness.event.handlers.ErrorEventRequestProcessor;
import io.harness.event.handlers.FacilitateResponseRequestProcessor;
import io.harness.event.handlers.HandleProgressRequestProcessor;
import io.harness.event.handlers.HandleStepResponseRequestProcessor;
import io.harness.event.handlers.ResumeNodeExecutionRequestProcessor;
import io.harness.event.handlers.SpawnChildRequestProcessor;
import io.harness.event.handlers.SpawnChildrenRequestProcessor;
import io.harness.event.handlers.SuspendChainRequestProcessor;
import io.harness.pms.contracts.execution.events.SdkResponseEventType;
import io.harness.rule.Owner;

import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@OwnedBy(HarnessTeam.PIPELINE)
public class SdkResponseProcessorFactoryTest extends OrchestrationTestBase {
  @Mock Injector injector;

  @InjectMocks SdkResponseProcessorFactory sdkNodeExecutionEventHandlerFactory;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void verifyMocks() {
    verifyNoMoreInteractions(injector);
  }

  @Test
  @Owner(developers = SAHIL)
  @Category(UnitTests.class)
  public void testGetHandler() {
    sdkNodeExecutionEventHandlerFactory.getHandler(SdkResponseEventType.ADD_EXECUTABLE_RESPONSE);
    verify(injector).getInstance(AddExecutableResponseRequestProcessor.class);

    sdkNodeExecutionEventHandlerFactory.getHandler(SdkResponseEventType.HANDLE_STEP_RESPONSE);
    verify(injector).getInstance(HandleStepResponseRequestProcessor.class);

    sdkNodeExecutionEventHandlerFactory.getHandler(SdkResponseEventType.RESUME_NODE_EXECUTION);
    verify(injector).getInstance(ResumeNodeExecutionRequestProcessor.class);

    sdkNodeExecutionEventHandlerFactory.getHandler(SdkResponseEventType.HANDLE_EVENT_ERROR);
    verify(injector).getInstance(ErrorEventRequestProcessor.class);

    sdkNodeExecutionEventHandlerFactory.getHandler(SdkResponseEventType.HANDLE_ADVISER_RESPONSE);
    verify(injector).getInstance(AdviserResponseRequestProcessor.class);

    sdkNodeExecutionEventHandlerFactory.getHandler(SdkResponseEventType.HANDLE_FACILITATE_RESPONSE);
    verify(injector).getInstance(FacilitateResponseRequestProcessor.class);

    sdkNodeExecutionEventHandlerFactory.getHandler(SdkResponseEventType.SUSPEND_CHAIN);
    verify(injector).getInstance(SuspendChainRequestProcessor.class);

    sdkNodeExecutionEventHandlerFactory.getHandler(SdkResponseEventType.SPAWN_CHILD);
    verify(injector).getInstance(SpawnChildRequestProcessor.class);

    sdkNodeExecutionEventHandlerFactory.getHandler(SdkResponseEventType.SPAWN_CHILDREN);
    verify(injector).getInstance(SpawnChildrenRequestProcessor.class);

    sdkNodeExecutionEventHandlerFactory.getHandler(SdkResponseEventType.HANDLE_PROGRESS);
    verify(injector).getInstance(HandleProgressRequestProcessor.class);

    sdkNodeExecutionEventHandlerFactory.getHandler(SdkResponseEventType.ADD_STEP_DETAILS_INSTANCE_REQUEST);
    verify(injector).getInstance(AddStepDetailsInstanceRequestProcessor.class);
  }
}