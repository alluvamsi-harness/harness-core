package io.harness.beans;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.FeatureFlag.Scope;

import lombok.Getter;

/**
 * Add your feature name here. When the feature is fully launched and no longer needs to be flagged,
 * delete the feature name.
 */
@OwnedBy(HarnessTeam.PL)
public enum FeatureName {
  APP_TELEMETRY,
  APPD_CV_TASK,
  ARTIFACT_PERPETUAL_TASK,
  ARTIFACT_PERPETUAL_TASK_MIGRATION,
  ARTIFACT_STREAM_REFACTOR,
  ARTIFACT_STREAM_DELEGATE_SCOPING,
  ARTIFACT_STREAM_DELEGATE_TIMEOUT,
  AZURE_US_GOV_CLOUD,
  AZURE_VMSS,
  AZURE_WEBAPP,
  AZURE_ARM,
  AUDIT_TRAIL_ENHANCEMENT,
  BIND_FETCH_FILES_TASK_TO_DELEGATE,
  CDNG_ENABLED,
  CENG_ENABLED,
  CE_AS_KUBERNETES_ENABLED,
  CE_ANOMALY_DETECTION,
  CE_INVENTORY_DASHBOARD,
  CE_AZURE_BILLING_CONNECTOR_DETAIL,
  CE_BILLING_DATA_PRE_AGGREGATION,
  CE_BILLING_DATA_HOURLY_PRE_AGGREGATION,
  CE_SAMPLE_DATA_GENERATION,
  CE_AWS_BILLING_CONNECTOR_DETAIL,
  CE_AZURE_SUPPORT,
  CFNG_ENABLED,
  CF_CUSTOM_EXTRACTION,
  CING_ENABLED,
  CLOUD_FORMATION_CREATE_REFACTOR,
  CUSTOM_APM_24_X_7_CV_TASK,
  CUSTOM_APM_CV_TASK,
  CUSTOM_DASHBOARD,
  CUSTOM_DEPLOYMENT,
  CUSTOM_MAX_PAGE_SIZE,
  CUSTOM_SECRETS_MANAGER,
  CVNG_ENABLED,
  CV_DEMO,
  CV_FEEDBACKS,
  CV_HOST_SAMPLING,
  CV_SUCCEED_FOR_ANOMALY,
  DEFAULT_ARTIFACT,
  DELEGATE_PROFILE_SCOPES,
  DELEGATE_SCALING_GROUP,
  DELEGATE_SCOPE_REVAMP,
  DELEGATE_SCOPE_TAG_SELECTORS,
  DELEGATE_INSIGHTS_ENABLED,
  DEPLOY_TO_SPECIFIC_HOSTS,
  DISABLE_ADDING_SERVICE_VARS_TO_ECS_SPEC,
  DISABLE_DELEGATE_SELECTION_LOG,
  DISABLE_LOGML_NEURAL_NET,
  DISABLE_METRIC_NAME_CURLY_BRACE_CHECK,
  DISABLE_SERVICEGUARD_LOG_ALERTS,
  DISABLE_WINRM_COMMAND_ENCODING,
  DISABLE_WINRM_ENV_VARIABLES,
  DO_DELEGATE_PHYSICAL_DELETE,
  WINRM_COPY_CONFIG_OPTIMIZE,
  ECS_MULTI_LBS,
  ENTITY_AUDIT_RECORD,
  EXPORT_TF_PLAN,
  GCB_CI_SYSTEM,
  GCP_WORKLOAD_IDENTITY,
  GIT_ACCOUNT_SUPPORT,
  GIT_HTTPS_KERBEROS,
  GIT_HOST_CONNECTIVITY,
  GLOBAL_COMMAND_LIBRARY,
  GLOBAL_CV_DASH,
  GLOBAL_DISABLE_HEALTH_CHECK(Scope.GLOBAL),
  GRAPHQL_DEV,
  HARNESS_TAGS,
  HELM_CHART_AS_ARTIFACT,
  HELM_STEADY_STATE_CHECK_1_16,
  HELM_CHART_NAME_SPLIT,
  HELM_MERGE_CAPABILITIES,
  INLINE_SSH_COMMAND,
  DELEGATE_ADD_WILDCARD_SCOPING,
  IGNORE_PCF_CONNECTION_CONTEXT_CACHE,
  LIMIT_PCF_THREADS,
  PCF_OLD_APP_RESIZE,
  LOCAL_DELEGATE_CONFIG_OVERRIDE,
  LOG_STREAMING_INTEGRATION,
  LOGS_V2_247,
  MOVE_AWS_AMI_INSTANCE_SYNC_TO_PERPETUAL_TASK,
  MOVE_AWS_AMI_SPOT_INST_INSTANCE_SYNC_TO_PERPETUAL_TASK,
  MOVE_AWS_CODE_DEPLOY_INSTANCE_SYNC_TO_PERPETUAL_TASK,
  MOVE_AWS_LAMBDA_INSTANCE_SYNC_TO_PERPETUAL_TASK,
  MOVE_AWS_SSH_INSTANCE_SYNC_TO_PERPETUAL_TASK,
  MOVE_CONTAINER_INSTANCE_SYNC_TO_PERPETUAL_TASK,
  MOVE_PCF_INSTANCE_SYNC_TO_PERPETUAL_TASK,
  NEW_INSTANCE_TIMESERIES,
  NEW_RELIC_CV_TASK,
  NEWRELIC_24_7_CV_TASK,
  NEXT_GEN_ENABLED,
  NG_AUTH_SETTINGS,
  NG_RBAC_ENABLED,
  ENFORCE_NG_ACCESS_CONTROL,
  NG_DASHBOARDS,
  NG_SERVICE_ACCOUNT,
  NG_SHOW_DELEGATE,
  NG_CG_TASK_ASSIGNMENT_ISOLATION,
  NG_USERPROFILE,
  NODE_RECOMMENDATION_1,
  NODE_RECOMMENDATION_AGGREGATE,
  ON_NEW_ARTIFACT_TRIGGER_WITH_LAST_COLLECTED_FILTER,
  OUTAGE_CV_DISABLE,
  PIPELINE_GOVERNANCE,
  PRUNE_KUBERNETES_RESOURCES,
  REJECT_TRIGGER_IF_ARTIFACTS_NOT_MATCH,
  ROLLBACK_NONE_ARTIFACT,
  RUNTIME_INPUT_PIPELINE,
  SCIM_INTEGRATION,
  SEARCH(Scope.GLOBAL),
  SEARCH_REQUEST,
  SEND_LOG_ANALYSIS_COMPRESSED,
  SEND_SLACK_NOTIFICATION_FROM_DELEGATE,
  SIDE_NAVIGATION,
  SLACK_APPROVALS,
  STOP_INSTANCE_SYNC_VIA_ITERATOR_FOR_AWS_AMI_DEPLOYMENTS,
  STOP_INSTANCE_SYNC_VIA_ITERATOR_FOR_AWS_AMI_SPOT_INST_DEPLOYMENTS,
  STOP_INSTANCE_SYNC_VIA_ITERATOR_FOR_AWS_CODE_DEPLOY_DEPLOYMENTS,
  STOP_INSTANCE_SYNC_VIA_ITERATOR_FOR_AWS_LAMBDA_DEPLOYMENTS,
  STOP_INSTANCE_SYNC_VIA_ITERATOR_FOR_AWS_SSH_DEPLOYMENTS,
  STOP_INSTANCE_SYNC_VIA_ITERATOR_FOR_AZURE_INFRA_DEPLOYMENTS,
  STOP_INSTANCE_SYNC_VIA_ITERATOR_FOR_CONTAINER_DEPLOYMENTS,
  STOP_INSTANCE_SYNC_VIA_ITERATOR_FOR_PCF_DEPLOYMENTS,
  SUPERVISED_TS_THRESHOLD,
  TEMPLATIZED_SECRET_MANAGER,
  TERRAGRUNT,
  THREE_PHASE_SECRET_DECRYPTION,
  TIME_RANGE_FREEZE_GOVERNANCE,
  TRIGGER_FOR_ALL_ARTIFACTS,
  TRIGGER_YAML,
  UI_ALLOW_K8S_V1,
  USE_CDN_FOR_STORAGE_FILES,
  USE_NEXUS3_PRIVATE_APIS,
  WEEKLY_WINDOW,
  ENABLE_CVNG_INTEGRATION,
  YAML_RBAC,
  DELEGATE_OWNERS,
  DYNATRACE_MULTI_SERVICE,
  REFACTOR_STATEMACHINEXECUTOR,
  WORKFLOW_DATA_COLLECTION_ITERATOR,
  HELM_REMOTE_MANIFEST_COMMAND_FLAG,
  ENABLE_CERT_VALIDATION,
  RESOURCE_CONSTRAINT_MAX_QUEUE,
  HIDE_SCOPE_COMMAND_OPTION,
  AWS_OVERRIDE_REGION,
  SHOW_TASK_SETUP_ABSTRACTIONS,
  CLEAN_UP_OLD_MANAGER_VERSIONS(Scope.PER_ACCOUNT),
  ECS_AUTOSCALAR_REDESIGN,
  SAVE_TERRAFORM_OUTPUTS_TO_SWEEPING_OUTPUT,
  TRIGGER_PROFILE_SCRIPT_EXECUTION_WF,
  NEW_DEPLOYMENT_FREEZE,
  PER_AGENT_CAPABILITIES,
  ECS_REGISTER_TASK_DEFINITION_TAGS,
  GCP_SECRETS_MANAGER,
  CUSTOM_DASHBOARD_INSTANCE_FETCH_LONGER_RETENTION_DATA,
  CUSTOM_DASHBOARD_DEPLOYMENT_FETCH_LONGER_RETENTION_DATA,
  CUSTOM_DASHBOARD_ENABLE_REALTIME_INSTANCE_AGGREGATION,
  CUSTOM_DASHBOARD_ENABLE_REALTIME_DEPLOYMENT_MIGRATION,
  CUSTOM_DASHBOARD_ENABLE_CRON_INSTANCE_DATA_MIGRATION,
  CUSTOM_DASHBOARD_ENABLE_CRON_DEPLOYMENT_DATA_MIGRATION,
  SSH_SECRET_ENGINE,
  ACTIVE_MIGRATION_FROM_LOCAL_TO_GCP_KMS,
  ACTIVE_MIGRATION_FROM_AWS_KMS_TO_GCP_KMS,
  WHITELIST_PUBLIC_API,
  WHITELIST_GRAPHQL,
  TIMEOUT_FAILURE_SUPPORT,
  LOG_APP_DEFAULTS,
  ENABLE_LOGIN_AUDITS,
  CUSTOM_MANIFEST,
  WEBHOOK_TRIGGER_AUTHORIZATION,
  GIT_SYNC_NG,
  NG_HARNESS_APPROVAL,
  NG_BARRIERS,
  NG_HELM_SOURCE_REPO,
  ENHANCED_GCR_CONNECTIVITY_CHECK,
  USE_TF_CLIENT,
  AWS_SM_ASSUME_IAM_ROLE,
  SERVICE_DASHBOARD_NG,
  GITHUB_WEBHOOK_AUTHENTICATION,
  VAULT_NAMESPACE,
  NG_SIGNUP(Scope.GLOBAL),
  ECS_BG_DOWNSIZE,
  LIMITED_ACCESS_FOR_HARNESS_USER_GROUP,
  REMOVE_STENCIL_MANUAL_INTERVENTION,
  PROCESS_DELEGATE_TASK_EVENTS_ASYNC,
  SHOW_CUSTOM_DELEGATE_TOKENS,
  USE_CUSTOM_DELEGATE_TOKENS,
  SKIP_BASED_ON_STACK_STATUSES,
  NG_SCHEMA_VALIDATION,
  WF_VAR_MULTI_SELECT_ALLOWED_VALUES,
  LDAP_GROUP_SYNC_JOB_ITERATOR,
  PIPELINE_MONITORING,
  USE_DELEGATE_SERVICE_APP,
  VAULT_AGENT,
  CF_CLI7,
  CF_APP_NON_VERSIONING_INACTIVE_ROLLBACK,
  HTTP_HEADERS_CAPABILITY_CHECK,
  AMI_IN_SERVICE_HEALTHY_WAIT,
  COMPARE_SERVICE_BY_ENV;

  FeatureName() {
    scope = Scope.PER_ACCOUNT;
  }

  FeatureName(Scope scope) {
    this.scope = scope;
  }

  @Getter private FeatureFlag.Scope scope;
}