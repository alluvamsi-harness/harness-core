package io.harness.ng;

import static io.harness.AuthorizationServiceHeader.BEARER;
import static io.harness.AuthorizationServiceHeader.DEFAULT;
import static io.harness.AuthorizationServiceHeader.IDENTITY_SERVICE;
import static io.harness.AuthorizationServiceHeader.NG_MANAGER;
import static io.harness.accesscontrol.filter.NGScopeAccessCheckFilter.bypassInterMsvcRequests;
import static io.harness.accesscontrol.filter.NGScopeAccessCheckFilter.bypassInternalApi;
import static io.harness.accesscontrol.filter.NGScopeAccessCheckFilter.bypassPaths;
import static io.harness.accesscontrol.filter.NGScopeAccessCheckFilter.bypassPublicApi;
import static io.harness.annotations.dev.HarnessTeam.PL;
import static io.harness.logging.LoggingInitializer.initializeLogging;
import static io.harness.ng.NextGenConfiguration.getResourceClasses;
import static io.harness.pms.listener.NgOrchestrationNotifyEventListener.NG_ORCHESTRATION;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.stream.Collectors.toSet;

import io.harness.EntityType;
import io.harness.Microservice;
import io.harness.ModuleType;
import io.harness.NgIteratorsConfig;
import io.harness.PipelineServiceUtilityModule;
import io.harness.SCMGrpcClientModule;
import io.harness.accesscontrol.NGAccessDeniedExceptionMapper;
import io.harness.accesscontrol.clients.AccessControlClient;
import io.harness.accesscontrol.filter.NGScopeAccessCheckFilter;
import io.harness.annotations.dev.OwnedBy;
import io.harness.cache.CacheModule;
import io.harness.cdng.creator.CDNGModuleInfoProvider;
import io.harness.cdng.creator.CDNGPlanCreatorProvider;
import io.harness.cdng.creator.filters.CDNGFilterCreationResponseMerger;
import io.harness.cdng.licenserestriction.ServiceRestrictionsUsageImpl;
import io.harness.cdng.orchestration.NgStepRegistrar;
import io.harness.cdng.pipeline.executions.CdngOrchestrationExecutionEventHandlerRegistrar;
import io.harness.cf.AbstractCfModule;
import io.harness.cf.CfClientConfig;
import io.harness.cf.CfMigrationConfig;
import io.harness.connector.ConnectorDTO;
import io.harness.connector.entities.Connector;
import io.harness.connector.gitsync.ConnectorGitSyncHelper;
import io.harness.controller.PrimaryVersionChangeScheduler;
import io.harness.enforcement.client.CustomRestrictionRegisterConfiguration;
import io.harness.enforcement.client.RestrictionUsageRegisterConfiguration;
import io.harness.enforcement.client.custom.CustomRestrictionInterface;
import io.harness.enforcement.client.example.ExampleCustomImpl;
import io.harness.enforcement.client.example.ExampleRateLimitUsageImpl;
import io.harness.enforcement.client.example.ExampleStaticLimitUsageImpl;
import io.harness.enforcement.client.services.EnforcementSdkRegisterService;
import io.harness.enforcement.client.usage.RestrictionUsageInterface;
import io.harness.enforcement.constants.FeatureRestrictionName;
import io.harness.enforcement.executions.DeploymentRestrictionUsageImpl;
import io.harness.enforcement.executions.DeploymentsPerMonthRestrictionUsageImpl;
import io.harness.enforcement.executions.InitialDeploymentRestrictionUsageImpl;
import io.harness.enforcement.services.FeatureRestrictionLoader;
import io.harness.ff.FeatureFlagConfig;
import io.harness.gitsync.AbstractGitSyncModule;
import io.harness.gitsync.AbstractGitSyncSdkModule;
import io.harness.gitsync.GitSdkConfiguration;
import io.harness.gitsync.GitSyncEntitiesConfiguration;
import io.harness.gitsync.GitSyncSdkConfiguration;
import io.harness.gitsync.GitSyncSdkInitHelper;
import io.harness.gitsync.core.fullsync.GitFullSyncEntityIterator;
import io.harness.gitsync.core.runnable.GitChangeSetRunnable;
import io.harness.gitsync.core.webhook.GitSyncEventConsumerService;
import io.harness.gitsync.migration.GitSyncMigrationProvider;
import io.harness.gitsync.server.GitSyncGrpcModule;
import io.harness.gitsync.server.GitSyncServiceConfiguration;
import io.harness.govern.ProviderModule;
import io.harness.health.HealthService;
import io.harness.licensing.migrations.LicenseManagerMigrationProvider;
import io.harness.logstreaming.LogStreamingModule;
import io.harness.maintenance.MaintenanceController;
import io.harness.metrics.MetricRegistryModule;
import io.harness.metrics.jobs.RecordMetricsJob;
import io.harness.metrics.service.api.MetricService;
import io.harness.migration.MigrationProvider;
import io.harness.migration.NGMigrationSdkInitHelper;
import io.harness.migration.NGMigrationSdkModule;
import io.harness.migration.beans.NGMigrationConfiguration;
import io.harness.migrations.InstanceMigrationProvider;
import io.harness.ng.accesscontrol.migrations.AccessControlMigrationJob;
import io.harness.ng.core.CorrelationFilter;
import io.harness.ng.core.EtagFilter;
import io.harness.ng.core.event.NGEventConsumerService;
import io.harness.ng.core.exceptionmappers.GenericExceptionMapperV2;
import io.harness.ng.core.exceptionmappers.JerseyViolationExceptionMapperV2;
import io.harness.ng.core.exceptionmappers.NotFoundExceptionMapper;
import io.harness.ng.core.exceptionmappers.OptimisticLockingFailureExceptionMapper;
import io.harness.ng.core.exceptionmappers.WingsExceptionMapperV2;
import io.harness.ng.core.handler.NGVaultSecretManagerRenewalHandler;
import io.harness.ng.core.migration.NGBeanMigrationProvider;
import io.harness.ng.core.migration.ProjectMigrationProvider;
import io.harness.ng.core.remote.licenserestriction.CloudCostK8sConnectorRestrictionsUsageImpl;
import io.harness.ng.core.remote.licenserestriction.OrgRestrictionsUsageImpl;
import io.harness.ng.core.remote.licenserestriction.ProjectRestrictionsUsageImpl;
import io.harness.ng.core.user.exception.mapper.InvalidUserRemoveRequestExceptionMapper;
import io.harness.ng.migration.NGCoreMigrationProvider;
import io.harness.ng.migration.SourceCodeManagerMigrationProvider;
import io.harness.ng.migration.UserMembershipMigrationProvider;
import io.harness.ng.migration.UserMetadataMigrationProvider;
import io.harness.ng.overview.eventGenerator.DeploymentEventGenerator;
import io.harness.ng.webhook.services.api.WebhookEventProcessingService;
import io.harness.outbox.OutboxEventPollService;
import io.harness.persistence.HPersistence;
import io.harness.pms.contracts.execution.events.OrchestrationEventType;
import io.harness.pms.events.base.PipelineEventConsumerController;
import io.harness.pms.expressions.functors.ImagePullSecretFunctor;
import io.harness.pms.listener.NgOrchestrationNotifyEventListener;
import io.harness.pms.sdk.PmsSdkConfiguration;
import io.harness.pms.sdk.PmsSdkInitHelper;
import io.harness.pms.sdk.PmsSdkModule;
import io.harness.pms.sdk.core.SdkDeployMode;
import io.harness.pms.sdk.core.events.OrchestrationEventHandler;
import io.harness.pms.sdk.core.execution.expression.SdkFunctor;
import io.harness.pms.sdk.execution.events.facilitators.FacilitatorEventRedisConsumer;
import io.harness.pms.sdk.execution.events.interrupts.InterruptEventRedisConsumer;
import io.harness.pms.sdk.execution.events.node.advise.NodeAdviseEventRedisConsumer;
import io.harness.pms.sdk.execution.events.node.resume.NodeResumeEventRedisConsumer;
import io.harness.pms.sdk.execution.events.node.start.NodeStartEventRedisConsumer;
import io.harness.pms.sdk.execution.events.orchestrationevent.OrchestrationEventRedisConsumer;
import io.harness.pms.sdk.execution.events.plan.CreatePartialPlanRedisConsumer;
import io.harness.pms.sdk.execution.events.progress.ProgressEventRedisConsumer;
import io.harness.pms.serializer.jackson.PmsBeansJacksonModule;
import io.harness.polling.service.impl.PollingPerpetualTaskManager;
import io.harness.polling.service.impl.PollingServiceImpl;
import io.harness.polling.service.intfc.PollingService;
import io.harness.queue.QueueListenerController;
import io.harness.queue.QueuePublisher;
import io.harness.redis.RedisConfig;
import io.harness.registrars.CDServiceAdviserRegistrar;
import io.harness.request.RequestContextFilter;
import io.harness.resource.VersionInfoResource;
import io.harness.runnable.InstanceAccountInfoRunnable;
import io.harness.security.InternalApiAuthFilter;
import io.harness.security.NextGenAuthenticationFilter;
import io.harness.security.annotations.InternalApi;
import io.harness.security.annotations.PublicApi;
import io.harness.service.deploymentevent.DeploymentEventListenerRegistrar;
import io.harness.service.impl.DelegateAsyncServiceImpl;
import io.harness.service.impl.DelegateProgressServiceImpl;
import io.harness.service.impl.DelegateSyncServiceImpl;
import io.harness.service.stats.statscollector.InstanceStatsIteratorHandler;
import io.harness.springdata.HMongoTemplate;
import io.harness.threading.ExecutorModule;
import io.harness.threading.ThreadPool;
import io.harness.token.remote.TokenClient;
import io.harness.tracing.MongoRedisTracer;
import io.harness.utils.NGObjectMapperHelper;
import io.harness.waiter.NotifierScheduledExecutorService;
import io.harness.waiter.NotifyEvent;
import io.harness.waiter.NotifyQueuePublisherRegister;
import io.harness.waiter.NotifyResponseCleaner;
import io.harness.waiter.ProgressUpdateService;
import io.harness.yaml.YamlSdkConfiguration;
import io.harness.yaml.YamlSdkInitHelper;

import software.wings.app.CharsetResponseFilter;
import software.wings.jersey.KryoFeature;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ServiceManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.model.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;

@OwnedBy(PL)
@Slf4j
public class NextGenApplication extends Application<NextGenConfiguration> {
  private static final SecureRandom random = new SecureRandom();
  private static final String APPLICATION_NAME = "CD NextGen Application";

  private final MetricRegistry metricRegistry = new MetricRegistry();

  public static void main(String[] args) throws Exception {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      log.info("Shutdown hook, entering maintenance...");
      MaintenanceController.forceMaintenance(true);
    }));
    new NextGenApplication().run(args);
  }

  @Override
  public String getName() {
    return APPLICATION_NAME;
  }

  @Override
  public void initialize(Bootstrap<NextGenConfiguration> bootstrap) {
    Instant start = Instant.now();
    initializeLogging();
    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Initializing logging started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    bootstrap.addCommand(new InspectCommand<>(this));
    bootstrap.addCommand(new ScanClasspathMetadataCommand());
    // Enable variable substitution with environment variables
    bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
        bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
    start = Instant.now();
    configureObjectMapper(bootstrap.getObjectMapper());
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "Configure object mapper started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    bootstrap.addBundle(new SwaggerBundle<NextGenConfiguration>() {
      @Override
      protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(NextGenConfiguration appConfig) {
        return appConfig.getSwaggerBundleConfiguration();
      }
    });
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "Configure swagger bundle started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    bootstrap.setMetricRegistry(metricRegistry);
  }

  public static void configureObjectMapper(final ObjectMapper mapper) {
    NGObjectMapperHelper.configureNGObjectMapper(mapper);
    mapper.registerModule(new PmsBeansJacksonModule());
  }

  @Override
  public void run(NextGenConfiguration appConfig, Environment environment) {
    log.info("Starting Next Gen Application ...");
    Instant start = Instant.now();
    ExecutorModule.getInstance().setExecutorService(ThreadPool.create(appConfig.getCommonPoolConfig().getCorePoolSize(),
        appConfig.getCommonPoolConfig().getMaxPoolSize(), appConfig.getCommonPoolConfig().getIdleTime(),
        appConfig.getCommonPoolConfig().getTimeUnit(),
        new ThreadFactoryBuilder().setNameFormat("main-app-pool-%d").build()));
    MaintenanceController.forceMaintenance(true);
    List<Module> modules = new ArrayList<>();
    modules.add(new NextGenModule(appConfig));
    modules.add(new AbstractModule() {
      @Override
      protected void configure() {
        bind(MetricRegistry.class).toInstance(metricRegistry);
      }
    });
    modules.add(new ProviderModule() {
      @Provides
      @Singleton
      public GitSyncServiceConfiguration gitSyncServiceConfiguration() {
        return GitSyncServiceConfiguration.builder().grpcServerConfig(appConfig.getGitSyncGrpcServerConfig()).build();
      }
    });
    modules.add(new MetricRegistryModule(metricRegistry));
    modules.add(NGMigrationSdkModule.getInstance());
    modules.add(new LogStreamingModule(appConfig.getLogStreamingServiceConfig().getBaseUrl()));
    modules.add(new AbstractCfModule() {
      @Override
      public CfClientConfig cfClientConfig() {
        return appConfig.getCfClientConfig();
      }

      @Override
      public CfMigrationConfig cfMigrationConfig() {
        return CfMigrationConfig.builder().build();
      }

      @Override
      public FeatureFlagConfig featureFlagConfig() {
        return appConfig.getFeatureFlagConfig();
      }
    });
    if (appConfig.getShouldDeployWithGitSync()) {
      modules.add(GitSyncGrpcModule.getInstance());
      GitSyncSdkConfiguration gitSyncSdkConfiguration = getGitSyncConfiguration(appConfig);
      modules.add(new AbstractGitSyncSdkModule() {
        @Override
        public GitSyncSdkConfiguration getGitSyncSdkConfiguration() {
          return gitSyncSdkConfiguration;
        }
      });
      modules.add(new AbstractGitSyncModule() {
        @Override
        public RedisConfig getRedisConfig() {
          return appConfig.getEventsFrameworkConfiguration().getRedisConfig();
        }
      });
    } else {
      modules.add(new SCMGrpcClientModule(appConfig.getGitSdkConfiguration().getScmConnectionConfig()));
    }

    // Pipeline Service Modules
    PmsSdkConfiguration pmsSdkConfiguration = getPmsSdkConfiguration(appConfig);
    modules.add(PmsSdkModule.getInstance(pmsSdkConfiguration));
    modules.add(PipelineServiceUtilityModule.getInstance());

    CacheModule cacheModule = new CacheModule(appConfig.getCacheConfig());
    modules.add(cacheModule);
    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Adding modules started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    Injector injector = Guice.createInjector(modules);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Creating injector started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    // Will create collections and Indexes
    start = Instant.now();
    injector.getInstance(HPersistence.class);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Index Manager started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerCorsFilter(appConfig, environment);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "CORS filter registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerResources(environment, injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Resource registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerJerseyProviders(environment, injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Jersey provider registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed,
        finish);

    start = Instant.now();
    registerJerseyFeatures(environment);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Jersey features registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed,
        finish);

    start = Instant.now();
    registerCharsetResponseFilter(environment, injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Charset Response Filter registration started at {}, took {} milliseconds and ended at {}.", start,
        timeElapsed, finish);

    start = Instant.now();
    registerCorrelationFilter(environment, injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Correlation Filter registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed,
        finish);

    start = Instant.now();
    registerEtagFilter(environment, injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "Etag Filter registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerScheduleJobs(injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "Scheduled Jobs registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerWaitEnginePublishers(injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Wait Engine publishers registration started at {}, took {} milliseconds and ended at {}.", start,
        timeElapsed, finish);

    start = Instant.now();
    registerAuthFilters(appConfig, environment, injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "Auth Filter registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerScopeAccessCheckFilter(appConfig, environment, injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Scope access check filter registration started at {}, took {} milliseconds and ended at {}.", start,
        timeElapsed, finish);

    start = Instant.now();
    registerRequestContextFilter(environment);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Request context filter registration started at {}, took {} milliseconds and ended at {}.", start,
        timeElapsed, finish);

    start = Instant.now();
    registerPipelineSDK(appConfig, injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "Pipeline SDK registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerYamlSdk(injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("YAML SDK registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerHealthCheck(environment, injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "Health Check registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerIterators(appConfig.getNgIteratorsConfig(), injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Iterators registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerJobs(injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Jobs registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerQueueListeners(injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "Queue Listener registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerPmsSdkEvents(injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "PMS SDK Events registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    initializeMonitoring(appConfig, injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "Monitoring registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerObservers(injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Observers registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerOasResource(appConfig, environment, injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "Oas resource registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerManagedBeans(environment, injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "Managed beans registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    initializeEnforcementService(injector, appConfig);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Enforcement service registration started at {}, took {} milliseconds and ended at {}.", start,
        timeElapsed, finish);

    start = Instant.now();
    initializeEnforcementSdk(injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Enforcement sdk registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed,
        finish);

    start = Instant.now();
    if (appConfig.getShouldDeployWithGitSync()) {
      intializeGitSync(injector);
      GitSyncSdkInitHelper.initGitSyncSdk(injector, environment, getGitSyncConfiguration(appConfig));
    }
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
        "Git Sync initialization started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    start = Instant.now();
    registerMigrations(injector);
    finish = Instant.now();
    timeElapsed = Duration.between(start, finish).toMillis();
    log.info("Migration registration started at {}, took {} milliseconds and ended at {}.", start, timeElapsed, finish);

    MaintenanceController.forceMaintenance(false);
  }

  private void registerOasResource(NextGenConfiguration appConfig, Environment environment, Injector injector) {
    OpenApiResource openApiResource = injector.getInstance(OpenApiResource.class);
    openApiResource.setOpenApiConfiguration(getOasConfig(appConfig));
    environment.jersey().register(openApiResource);
  }

  private void registerQueueListeners(Injector injector) {
    log.info("Initializing queue listeners...");
    QueueListenerController queueListenerController = injector.getInstance(QueueListenerController.class);
    queueListenerController.register(injector.getInstance(NgOrchestrationNotifyEventListener.class), 1);
  }

  private static void registerObservers(Injector injector) {
    // register Polling Framework Observer
    PollingServiceImpl pollingService = (PollingServiceImpl) injector.getInstance(Key.get(PollingService.class));
    pollingService.getSubject().register(injector.getInstance(Key.get(PollingPerpetualTaskManager.class)));

    HMongoTemplate mongoTemplate = (HMongoTemplate) injector.getInstance(MongoTemplate.class);
    mongoTemplate.getTracerSubject().register(injector.getInstance(MongoRedisTracer.class));
  }

  private void registerMigrations(Injector injector) {
    NGMigrationConfiguration config = getMigrationSdkConfiguration();
    NGMigrationSdkInitHelper.initialize(injector, config);
  }

  private NGMigrationConfiguration getMigrationSdkConfiguration() {
    return NGMigrationConfiguration.builder()
        .microservice(Microservice.CORE)
        .migrationProviderList(new ArrayList<Class<? extends MigrationProvider>>() {
          { add(NGCoreMigrationProvider.class); } // Add all migration provider classes here
          { add(ProjectMigrationProvider.class); }
          { add(UserMembershipMigrationProvider.class); }
          { add(NGBeanMigrationProvider.class); }
          { add(InstanceMigrationProvider.class); }
          { add(UserMetadataMigrationProvider.class); }
          { add(LicenseManagerMigrationProvider.class); }
          { add(SourceCodeManagerMigrationProvider.class); }
          { add(GitSyncMigrationProvider.class); }
        })
        .build();
  }

  private void initializeMonitoring(NextGenConfiguration appConfig, Injector injector) {
    if (appConfig.isExportMetricsToStackDriver()) {
      injector.getInstance(MetricService.class).initializeMetrics();
      injector.getInstance(RecordMetricsJob.class).scheduleMetricsTasks();
    }
  }

  private GitSyncSdkConfiguration getGitSyncConfiguration(NextGenConfiguration config) {
    final Supplier<List<EntityType>> sortOrder = () -> Collections.singletonList(EntityType.CONNECTORS);
    ObjectMapper ngObjectMapper = new ObjectMapper(new YAMLFactory());
    configureObjectMapper(ngObjectMapper);
    Set<GitSyncEntitiesConfiguration> gitSyncEntitiesConfigurations = new HashSet<>();
    gitSyncEntitiesConfigurations.add(GitSyncEntitiesConfiguration.builder()
                                          .entityType(EntityType.CONNECTORS)
                                          .yamlClass(ConnectorDTO.class)
                                          .entityClass(Connector.class)
                                          .entityHelperClass(ConnectorGitSyncHelper.class)
                                          .build());
    final GitSdkConfiguration gitSdkConfiguration = config.getGitSdkConfiguration();
    return GitSyncSdkConfiguration.builder()
        .gitSyncSortOrder(sortOrder)
        .grpcClientConfig(gitSdkConfiguration.getGitManagerGrpcClientConfig())
        // In process server so server config not required.
        //        .grpcServerConfig(config.getGitSyncGrpcServerConfig())
        .deployMode(GitSyncSdkConfiguration.DeployMode.IN_PROCESS)
        .microservice(Microservice.CORE)
        .scmConnectionConfig(gitSdkConfiguration.getScmConnectionConfig())
        .eventsRedisConfig(config.getEventsFrameworkConfiguration().getRedisConfig())
        .serviceHeader(NG_MANAGER)
        .gitSyncEntitiesConfiguration(gitSyncEntitiesConfigurations)
        .objectMapper(ngObjectMapper)
        .build();
  }

  private void registerRequestContextFilter(Environment environment) {
    environment.jersey().register(new RequestContextFilter());
    environment.jersey().register(new JsonProcessingExceptionMapper(true));
  }

  private void intializeGitSync(Injector injector) {
    log.info("Initializing gRPC server for git sync...");
    ServiceManager serviceManager =
        injector.getInstance(Key.get(ServiceManager.class, Names.named("git-sync"))).startAsync();
    serviceManager.awaitHealthy();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> serviceManager.stopAsync().awaitStopped()));
    log.info("Git Sync SDK registration complete.");
  }

  public void registerIterators(NgIteratorsConfig ngIteratorsConfig, Injector injector) {
    injector.getInstance(NGVaultSecretManagerRenewalHandler.class)
        .registerIterators(ngIteratorsConfig.getNgVaultSecretManagerRenewalIteratorConfig().getThreadPoolSize());
    injector.getInstance(WebhookEventProcessingService.class)
        .registerIterators(ngIteratorsConfig.getWebhookEventProcessingServiceIteratorConfig().getThreadPoolSize());
    injector.getInstance(InstanceStatsIteratorHandler.class).registerIterators();
    injector.getInstance(GitFullSyncEntityIterator.class)
        .registerIterators(ngIteratorsConfig.getGitFullSyncEntityIteratorConfig().getThreadPoolSize());
  }

  public void registerJobs(Injector injector) {
    injector.getInstance(PrimaryVersionChangeScheduler.class).registerExecutors();
  }

  private void registerHealthCheck(Environment environment, Injector injector) {
    final HealthService healthService = injector.getInstance(HealthService.class);
    environment.healthChecks().register("Next Gen Manager", healthService);
    healthService.registerMonitor(injector.getInstance(HPersistence.class));
  }

  private void createConsumerThreadsToListenToEvents(Environment environment, Injector injector) {
    environment.lifecycle().manage(injector.getInstance(NGEventConsumerService.class));
    environment.lifecycle().manage(injector.getInstance(GitSyncEventConsumerService.class));
    environment.lifecycle().manage(injector.getInstance(PipelineEventConsumerController.class));
  }

  private void registerPmsSdkEvents(Injector injector) {
    log.info("Initializing sdk redis abstract consumers...");
    PipelineEventConsumerController pipelineEventConsumerController =
        injector.getInstance(PipelineEventConsumerController.class);
    pipelineEventConsumerController.register(injector.getInstance(InterruptEventRedisConsumer.class), 1);
    pipelineEventConsumerController.register(injector.getInstance(OrchestrationEventRedisConsumer.class), 1);
    pipelineEventConsumerController.register(injector.getInstance(FacilitatorEventRedisConsumer.class), 1);
    pipelineEventConsumerController.register(injector.getInstance(NodeStartEventRedisConsumer.class), 2);
    pipelineEventConsumerController.register(injector.getInstance(ProgressEventRedisConsumer.class), 1);
    pipelineEventConsumerController.register(injector.getInstance(NodeAdviseEventRedisConsumer.class), 2);
    pipelineEventConsumerController.register(injector.getInstance(NodeResumeEventRedisConsumer.class), 2);
    pipelineEventConsumerController.register(injector.getInstance(CreatePartialPlanRedisConsumer.class), 2);
  }

  private void registerYamlSdk(Injector injector) {
    YamlSdkConfiguration yamlSdkConfiguration = YamlSdkConfiguration.builder()
                                                    .requireSchemaInit(true)
                                                    .requireSnippetInit(true)
                                                    .requireValidatorInit(true)
                                                    .build();
    YamlSdkInitHelper.initialize(injector, yamlSdkConfiguration);
  }

  public void registerPipelineSDK(NextGenConfiguration appConfig, Injector injector) {
    PmsSdkConfiguration sdkConfig = getPmsSdkConfiguration(appConfig);
    if (sdkConfig.getDeploymentMode().equals(SdkDeployMode.REMOTE)) {
      try {
        PmsSdkInitHelper.initializeSDKInstance(injector, sdkConfig);
      } catch (Exception e) {
        log.error("Failed To register pipeline sdk", e);
        System.exit(1);
      }
    }
  }

  private PmsSdkConfiguration getPmsSdkConfiguration(NextGenConfiguration appConfig) {
    boolean remote = false;
    if (appConfig.getShouldConfigureWithPMS() != null && appConfig.getShouldConfigureWithPMS()) {
      remote = true;
    }

    return PmsSdkConfiguration.builder()
        .deploymentMode(remote ? SdkDeployMode.REMOTE : SdkDeployMode.LOCAL)
        .moduleType(ModuleType.CD)
        .grpcServerConfig(appConfig.getPmsSdkGrpcServerConfig())
        .pmsGrpcClientConfig(appConfig.getPmsGrpcClientConfig())
        .pipelineServiceInfoProviderClass(CDNGPlanCreatorProvider.class)
        .staticAliases(getStaticAliases())
        .sdkFunctors(getSdkFunctors())
        .filterCreationResponseMerger(new CDNGFilterCreationResponseMerger())
        .engineSteps(NgStepRegistrar.getEngineSteps())
        .engineAdvisers(CDServiceAdviserRegistrar.getEngineAdvisers())
        .executionSummaryModuleInfoProviderClass(CDNGModuleInfoProvider.class)
        .eventsFrameworkConfiguration(appConfig.getEventsFrameworkConfiguration())
        .engineEventHandlersMap(getOrchestrationEventHandlers())
        .executionPoolConfig(appConfig.getPmsSdkExecutionPoolConfig())
        .orchestrationEventPoolConfig(appConfig.getPmsSdkOrchestrationEventPoolConfig())
        .build();
  }

  private Map<String, String> getStaticAliases() {
    Map<String, String> aliases = new HashMap<>();
    aliases.put("serviceConfig", "stage.spec.serviceConfig");
    aliases.put("serviceDefinition", "stage.spec.serviceConfig.serviceDefinition");
    aliases.put("artifact", "stage.spec.serviceConfig.serviceDefinition.spec.artifacts.primary.output");
    aliases.put("infra", "stage.spec.infrastructure.output");
    aliases.put("INFRA_KEY", "stage.spec.infrastructure.output.infrastructureKey");
    return aliases;
  }

  private Map<String, Class<? extends SdkFunctor>> getSdkFunctors() {
    Map<String, Class<? extends SdkFunctor>> sdkFunctorMap = new HashMap<>();
    sdkFunctorMap.put(ImagePullSecretFunctor.IMAGE_PULL_SECRET, ImagePullSecretFunctor.class);
    return sdkFunctorMap;
  }

  private Map<OrchestrationEventType, Set<Class<? extends OrchestrationEventHandler>>> getOrchestrationEventHandlers() {
    Map<OrchestrationEventType, Set<Class<? extends OrchestrationEventHandler>>> orchestrationEventTypeSetHashMap =
        new HashMap<>();
    List<Map<OrchestrationEventType, Set<Class<? extends OrchestrationEventHandler>>>> orchestrationEventHandlersList =
        new ArrayList<>(Arrays.asList(CdngOrchestrationExecutionEventHandlerRegistrar.getEngineEventHandlers(),
            DeploymentEventListenerRegistrar.getEngineEventHandlers(),
            DeploymentEventGenerator.getEngineEventHandlers()));
    orchestrationEventHandlersList.forEach(
        orchestrationEventHandlers -> mergeEventHandlers(orchestrationEventTypeSetHashMap, orchestrationEventHandlers));
    return orchestrationEventTypeSetHashMap;
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

  private void registerManagedBeans(Environment environment, Injector injector) {
    environment.lifecycle().manage(injector.getInstance(QueueListenerController.class));
    environment.lifecycle().manage(injector.getInstance(NotifierScheduledExecutorService.class));
    environment.lifecycle().manage(injector.getInstance(OutboxEventPollService.class));
    environment.lifecycle().manage(injector.getInstance(AccessControlMigrationJob.class));
    createConsumerThreadsToListenToEvents(environment, injector);
  }

  private void registerCorsFilter(NextGenConfiguration appConfig, Environment environment) {
    FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
    String allowedOrigins = String.join(",", appConfig.getAllowedOrigins());
    cors.setInitParameters(of("allowedOrigins", allowedOrigins, "allowedHeaders",
        "X-Requested-With,Content-Type,Accept,Origin,Authorization,X-api-key", "allowedMethods",
        "OPTIONS,GET,PUT,POST,DELETE,HEAD", "preflightMaxAge", "86400"));
    cors.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
  }

  private void registerResources(Environment environment, Injector injector) {
    for (Class<?> resource : getResourceClasses()) {
      if (Resource.isAcceptable(resource)) {
        environment.jersey().register(injector.getInstance(resource));
      }
    }
    environment.jersey().register(injector.getInstance(VersionInfoResource.class));
  }

  private OpenAPIConfiguration getOasConfig(NextGenConfiguration appConfig) {
    OpenAPI oas = new OpenAPI();
    Info info =
        new Info()
            .title("CD NextGen API Reference")
            .description(
                "This is the Open Api Spec 3 for the NextGen Manager. This is under active development. Beware of the breaking change with respect to the generated code stub")
            .termsOfService("https://harness.io/terms-of-use/")
            .version("3.0")
            .contact(new Contact().email("contact@harness.io"));
    oas.info(info);
    URL baseurl = null;
    try {
      baseurl = new URL("https", appConfig.getHostname(), appConfig.getBasePathPrefix());
      Server server = new Server();
      server.setUrl(baseurl.toString());
      oas.servers(Collections.singletonList(server));
    } catch (MalformedURLException e) {
      log.error("failed to set baseurl for server, {}/{}", appConfig.hostname, appConfig.getBasePathPrefix());
    }
    final Set<String> resourceClasses =
        getOAS3ResourceClassesOnly().stream().map(Class::getCanonicalName).collect(toSet());
    return new SwaggerConfiguration()
        .openAPI(oas)
        .prettyPrint(true)
        .resourceClasses(resourceClasses)
        .scannerClass("io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner");
  }

  private void registerJerseyProviders(Environment environment, Injector injector) {
    environment.jersey().register(injector.getInstance(KryoFeature.class));
    environment.jersey().register(JerseyViolationExceptionMapperV2.class);
    environment.jersey().register(OptimisticLockingFailureExceptionMapper.class);
    environment.jersey().register(NotFoundExceptionMapper.class);
    environment.jersey().register(InvalidUserRemoveRequestExceptionMapper.class);
    environment.jersey().register(NGAccessDeniedExceptionMapper.class);
    environment.jersey().register(WingsExceptionMapperV2.class);
    environment.jersey().register(GenericExceptionMapperV2.class);
  }

  private void registerJerseyFeatures(Environment environment) {
    environment.jersey().register(MultiPartFeature.class);
  }

  private void registerCharsetResponseFilter(Environment environment, Injector injector) {
    environment.jersey().register(injector.getInstance(CharsetResponseFilter.class));
  }

  private void registerCorrelationFilter(Environment environment, Injector injector) {
    environment.jersey().register(injector.getInstance(CorrelationFilter.class));
  }

  private void registerEtagFilter(Environment environment, Injector injector) {
    environment.jersey().register(injector.getInstance(EtagFilter.class));
  }

  private void registerWaitEnginePublishers(Injector injector) {
    final QueuePublisher<NotifyEvent> publisher =
        injector.getInstance(Key.get(new TypeLiteral<QueuePublisher<NotifyEvent>>() {}));
    final NotifyQueuePublisherRegister notifyQueuePublisherRegister =
        injector.getInstance(NotifyQueuePublisherRegister.class);
    notifyQueuePublisherRegister.register(
        NG_ORCHESTRATION, payload -> publisher.send(Arrays.asList(NG_ORCHESTRATION), payload));
  }

  private void registerScheduleJobs(Injector injector) {
    log.info("Initializing scheduled jobs...");
    injector.getInstance(NotifierScheduledExecutorService.class)
        .scheduleWithFixedDelay(
            injector.getInstance(NotifyResponseCleaner.class), random.nextInt(300), 300L, TimeUnit.SECONDS);
    injector.getInstance(Key.get(ScheduledExecutorService.class, Names.named("gitChangeSet")))
        .scheduleWithFixedDelay(
            injector.getInstance(GitChangeSetRunnable.class), random.nextInt(4), 4L, TimeUnit.SECONDS);
    injector.getInstance(Key.get(ScheduledExecutorService.class, Names.named("taskPollExecutor")))
        .scheduleWithFixedDelay(injector.getInstance(DelegateSyncServiceImpl.class), 0L, 2L, TimeUnit.SECONDS);
    injector.getInstance(Key.get(ScheduledExecutorService.class, Names.named("taskPollExecutor")))
        .scheduleWithFixedDelay(injector.getInstance(DelegateAsyncServiceImpl.class), 0L, 5L, TimeUnit.SECONDS);
    injector.getInstance(Key.get(ScheduledExecutorService.class, Names.named("taskPollExecutor")))
        .scheduleWithFixedDelay(injector.getInstance(DelegateProgressServiceImpl.class), 0L, 5L, TimeUnit.SECONDS);
    injector.getInstance(Key.get(ScheduledExecutorService.class, Names.named("taskPollExecutor")))
        .scheduleWithFixedDelay(injector.getInstance(ProgressUpdateService.class), 0L, 5L, TimeUnit.SECONDS);
    injector.getInstance(Key.get(ScheduledExecutorService.class, Names.named("taskPollExecutor")))
        .scheduleWithFixedDelay(injector.getInstance(InstanceAccountInfoRunnable.class), 0, 6, TimeUnit.HOURS);
  }

  private void registerAuthFilters(NextGenConfiguration configuration, Environment environment, Injector injector) {
    if (configuration.isEnableAuth()) {
      registerNextGenAuthFilter(
          configuration, environment, injector.getInstance(Key.get(TokenClient.class, Names.named("PRIVILEGED"))));
      registerInternalApiAuthFilter(configuration, environment);
    }
  }

  private void registerScopeAccessCheckFilter(
      NextGenConfiguration configuration, Environment environment, Injector injector) {
    if (configuration.isScopeAccessCheckEnabled()) {
      AccessControlClient accessControlClient = injector.getInstance(AccessControlClient.class);
      environment.jersey().register(
          new NGScopeAccessCheckFilter(Arrays.asList(bypassInterMsvcRequests(), bypassPublicApi(), bypassInternalApi(),
                                           bypassPaths(Arrays.asList("/version", "/swagger", "/swagger.json"))),
              accessControlClient));
    }
  }

  private void registerNextGenAuthFilter(
      NextGenConfiguration configuration, Environment environment, TokenClient tokenClient) {
    Predicate<Pair<ResourceInfo, ContainerRequestContext>> predicate =
        (getAuthenticationExemptedRequestsPredicate().negate())
            .and((getAuthFilterPredicate(InternalApi.class)).negate());
    Map<String, String> serviceToSecretMapping = new HashMap<>();
    serviceToSecretMapping.put(BEARER.getServiceId(), configuration.getNextGenConfig().getJwtAuthSecret());
    serviceToSecretMapping.put(
        IDENTITY_SERVICE.getServiceId(), configuration.getNextGenConfig().getJwtIdentityServiceSecret());
    serviceToSecretMapping.put(DEFAULT.getServiceId(), configuration.getNextGenConfig().getNgManagerServiceSecret());
    environment.jersey().register(
        new NextGenAuthenticationFilter(predicate, null, serviceToSecretMapping, tokenClient));
  }

  private void registerInternalApiAuthFilter(NextGenConfiguration configuration, Environment environment) {
    Map<String, String> serviceToSecretMapping = new HashMap<>();
    serviceToSecretMapping.put(DEFAULT.getServiceId(), configuration.getNextGenConfig().getNgManagerServiceSecret());
    environment.jersey().register(
        new InternalApiAuthFilter(getAuthFilterPredicate(InternalApi.class), null, serviceToSecretMapping));
  }

  private Predicate<Pair<ResourceInfo, ContainerRequestContext>> getAuthenticationExemptedRequestsPredicate() {
    return getAuthFilterPredicate(PublicApi.class)
        .or(resourceInfoAndRequest
            -> resourceInfoAndRequest.getValue().getUriInfo().getAbsolutePath().getPath().endsWith("/version")
                || resourceInfoAndRequest.getValue().getUriInfo().getAbsolutePath().getPath().endsWith("/swagger")
                || resourceInfoAndRequest.getValue().getUriInfo().getAbsolutePath().getPath().endsWith("/swagger.json")
                || resourceInfoAndRequest.getValue().getUriInfo().getAbsolutePath().getPath().endsWith("/openapi.json")
                || resourceInfoAndRequest.getValue().getUriInfo().getAbsolutePath().getPath().endsWith(
                    "/swagger.yaml"));
  }

  private Predicate<Pair<ResourceInfo, ContainerRequestContext>> getAuthFilterPredicate(
      Class<? extends Annotation> annotation) {
    return resourceInfoAndRequest
        -> (resourceInfoAndRequest.getKey().getResourceMethod() != null
               && resourceInfoAndRequest.getKey().getResourceMethod().getAnnotation(annotation) != null)
        || (resourceInfoAndRequest.getKey().getResourceClass() != null
            && resourceInfoAndRequest.getKey().getResourceClass().getAnnotation(annotation) != null);
  }

  private void initializeEnforcementService(Injector injector, NextGenConfiguration configuration) {
    injector.getInstance(FeatureRestrictionLoader.class).run(configuration);
  }

  private void initializeEnforcementSdk(Injector injector) {
    RestrictionUsageRegisterConfiguration restrictionUsageRegisterConfiguration =
        RestrictionUsageRegisterConfiguration.builder()
            .restrictionNameClassMap(
                ImmutableMap.<FeatureRestrictionName, Class<? extends RestrictionUsageInterface>>builder()
                    .put(FeatureRestrictionName.TEST2, ExampleStaticLimitUsageImpl.class)
                    .put(FeatureRestrictionName.TEST3, ExampleRateLimitUsageImpl.class)
                    .put(FeatureRestrictionName.MULTIPLE_PROJECTS, ProjectRestrictionsUsageImpl.class)
                    .put(FeatureRestrictionName.MULTIPLE_ORGANIZATIONS, OrgRestrictionsUsageImpl.class)
                    .put(FeatureRestrictionName.SERVICES, ServiceRestrictionsUsageImpl.class)
                    .put(FeatureRestrictionName.CCM_K8S_CLUSTERS, CloudCostK8sConnectorRestrictionsUsageImpl.class)
                    .put(FeatureRestrictionName.DEPLOYMENTS_PER_MONTH, DeploymentsPerMonthRestrictionUsageImpl.class)
                    .put(FeatureRestrictionName.INITIAL_DEPLOYMENTS, InitialDeploymentRestrictionUsageImpl.class)
                    .build())
            .build();
    CustomRestrictionRegisterConfiguration customConfig =
        CustomRestrictionRegisterConfiguration.builder()
            .customRestrictionMap(
                ImmutableMap.<FeatureRestrictionName, Class<? extends CustomRestrictionInterface>>builder()
                    .put(FeatureRestrictionName.TEST4, ExampleCustomImpl.class)
                    .put(FeatureRestrictionName.DEPLOYMENTS, DeploymentRestrictionUsageImpl.class)
                    .build())
            .build();

    injector.getInstance(EnforcementSdkRegisterService.class)
        .initialize(restrictionUsageRegisterConfiguration, customConfig);
  }

  public static Collection<Class<?>> getOAS3ResourceClassesOnly() {
    return getResourceClasses().stream().filter(x -> x.isAnnotationPresent(Tag.class)).collect(Collectors.toList());
  }
}
