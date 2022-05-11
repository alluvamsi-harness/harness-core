/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.serializer.morphia;

import io.harness.beans.EncryptedData;
import io.harness.beans.MigrateSecretTask;
import io.harness.beans.SecretChangeLog;
import io.harness.beans.SecretKey;
import io.harness.beans.SecretManagerConfig;
import io.harness.beans.SecretUsageLog;
import io.harness.morphia.MorphiaRegistrar;
import io.harness.morphia.MorphiaRegistrarHelperPut;

import software.wings.api.AwsLambdaContextElement;
import software.wings.api.AwsLambdaExecutionData;
import software.wings.api.ScriptStateExecutionData;
import software.wings.api.ScriptStateExecutionSummary;
import software.wings.api.ServiceNowExecutionData;
import software.wings.api.TerraformExecutionData;
import software.wings.api.jira.JiraExecutionData;
import software.wings.api.shellscript.provision.ShellScriptProvisionExecutionData;
import software.wings.beans.APMVerificationConfig;
import software.wings.beans.AppDynamicsConfig;
import software.wings.beans.AwsSecretsManagerConfig;
import software.wings.beans.AzureConfig;
import software.wings.beans.AzureVaultConfig;
import software.wings.beans.BambooConfig;
import software.wings.beans.BaseVaultConfig;
import software.wings.beans.BastionConnectionAttributes;
import software.wings.beans.BugsnagConfig;
import software.wings.beans.CyberArkConfig;
import software.wings.beans.DockerConfig;
import software.wings.beans.DynaTraceConfig;
import software.wings.beans.ElkConfig;
import software.wings.beans.GcpKmsConfig;
import software.wings.beans.GcpSecretsManagerConfig;
import software.wings.beans.HostConnectionAttributes;
import software.wings.beans.InstanaConfig;
import software.wings.beans.JiraConfig;
import software.wings.beans.KmsConfig;
import software.wings.beans.KubernetesClusterConfig;
import software.wings.beans.LocalEncryptionConfig;
import software.wings.beans.NewRelicConfig;
import software.wings.beans.PcfConfig;
import software.wings.beans.SSHExecutionCredential;
import software.wings.beans.SSHVaultConfig;
import software.wings.beans.SecretManagerRuntimeParameters;
import software.wings.beans.ServiceNowConfig;
import software.wings.beans.SftpConfig;
import software.wings.beans.SmbConfig;
import software.wings.beans.SplunkConfig;
import software.wings.beans.SumoConfig;
import software.wings.beans.VaultConfig;
import software.wings.beans.config.ArtifactoryConfig;
import software.wings.beans.config.LogzConfig;
import software.wings.beans.config.NexusConfig;
import software.wings.beans.settings.azureartifacts.AzureArtifactsPATConfig;
import software.wings.beans.yaml.GitFetchFilesFromMultipleRepoResult;
import software.wings.delegatetasks.buildsource.BuildSourceExecutionResponse;
import software.wings.delegatetasks.cv.beans.CustomLogResponseMapper;
import software.wings.delegatetasks.validation.DelegateConnectionResult;
import software.wings.delegatetasks.validation.capabilities.ClusterMasterUrlValidationCapability;
import software.wings.delegatetasks.validation.capabilities.GitConnectionCapability;
import software.wings.delegatetasks.validation.capabilities.SSHHostValidationCapability;
import software.wings.delegatetasks.validation.capabilities.ShellConnectionCapability;
import software.wings.delegatetasks.validation.capabilities.WinrmHostValidationCapability;
import software.wings.helpers.ext.external.comm.CollaborationProviderResponse;
import software.wings.helpers.ext.helm.response.HelmCollectChartResponse;
import software.wings.helpers.ext.mail.EmailData;
import software.wings.helpers.ext.mail.SmtpConfig;
import software.wings.service.impl.analysis.CustomLogDataCollectionInfo;
import software.wings.service.impl.analysis.DataCollectionTaskResult;
import software.wings.service.impl.aws.model.AwsAmiServiceDeployResponse;
import software.wings.service.impl.aws.model.AwsAmiServiceSetupResponse;
import software.wings.service.impl.aws.model.AwsAmiSwitchRoutesResponse;
import software.wings.service.impl.aws.model.AwsLambdaExecuteFunctionResponse;
import software.wings.service.impl.aws.model.AwsLambdaExecuteWfResponse;
import software.wings.service.impl.cloudwatch.CloudWatchMetric;
import software.wings.service.impl.elk.ElkDataCollectionInfo;
import software.wings.service.impl.elk.ElkDataCollectionInfoV2;
import software.wings.service.impl.instana.InstanaDataCollectionInfo;
import software.wings.service.impl.logz.LogzDataCollectionInfo;
import software.wings.service.impl.newrelic.NewRelicDataCollectionInfoV2;
import software.wings.service.impl.newrelic.NewRelicMetricDataRecord;
import software.wings.service.impl.splunk.SplunkDataCollectionInfoV2;
import software.wings.service.impl.stackdriver.StackDriverDataCollectionInfo;
import software.wings.service.impl.stackdriver.StackDriverLogDataCollectionInfo;
import software.wings.service.impl.sumo.SumoDataCollectionInfo;
import software.wings.sm.StateExecutionData;
import software.wings.sm.WorkflowStandardParams;

import java.util.Set;

public class DelegateTasksMorphiaRegistrar implements MorphiaRegistrar {
  @Override
  public void registerClasses(Set<Class> set) {
    set.add(NewRelicMetricDataRecord.class);
    set.add(DelegateConnectionResult.class);
    set.add(AwsSecretsManagerConfig.class);
    set.add(AzureVaultConfig.class);
    set.add(CyberArkConfig.class);
    set.add(GcpKmsConfig.class);
    set.add(GcpSecretsManagerConfig.class);
    set.add(KmsConfig.class);
    set.add(LocalEncryptionConfig.class);
    set.add(VaultConfig.class);
    set.add(SecretManagerRuntimeParameters.class);
    set.add(BaseVaultConfig.class);
    set.add(SecretManagerConfig.class);
    set.add(SSHVaultConfig.class);
    set.add(SecretChangeLog.class);
    set.add(EncryptedData.class);
    set.add(SecretUsageLog.class);
    set.add(MigrateSecretTask.class);
    set.add(SecretKey.class);
    set.add(EmailData.class);
  }

  @Override
  public void registerImplementationClasses(MorphiaRegistrarHelperPut h, MorphiaRegistrarHelperPut w) {
    w.put("service.impl.analysis.DataCollectionTaskResult", DataCollectionTaskResult.class);
    w.put("service.impl.analysis.CustomLogDataCollectionInfo", CustomLogDataCollectionInfo.class);
    w.put("delegatetasks.cv.beans.CustomLogResponseMapper", CustomLogResponseMapper.class);
    w.put("beans.AppDynamicsConfig", AppDynamicsConfig.class);
    w.put("beans.NewRelicConfig", NewRelicConfig.class);
    w.put("beans.DynaTraceConfig", DynaTraceConfig.class);
    w.put("beans.SumoConfig", SumoConfig.class);
    w.put("service.impl.sumo.SumoDataCollectionInfo", SumoDataCollectionInfo.class);
    w.put("beans.config.LogzConfig", LogzConfig.class);
    w.put("beans.ElkConfig", ElkConfig.class);
    w.put("service.impl.elk.ElkDataCollectionInfo", ElkDataCollectionInfo.class);
    w.put("service.impl.logz.LogzDataCollectionInfo", LogzDataCollectionInfo.class);
    w.put("beans.AwsSecretsManagerConfig", AwsSecretsManagerConfig.class);
    w.put("beans.AzureVaultConfig", AzureVaultConfig.class);
    w.put("beans.CyberArkConfig", CyberArkConfig.class);
    w.put("beans.GcpKmsConfig", GcpKmsConfig.class);
    w.put("beans.GcpSecretsManagerConfig", GcpSecretsManagerConfig.class);
    w.put("beans.KmsConfig", KmsConfig.class);
    w.put("beans.LocalEncryptionConfig", LocalEncryptionConfig.class);
    w.put("beans.SecretManagerConfig", SecretManagerConfig.class);
    w.put("beans.VaultConfig", VaultConfig.class);
    w.put("beans.BastionConnectionAttributes", BastionConnectionAttributes.class);
    w.put("beans.HostConnectionAttributes", HostConnectionAttributes.class);
    w.put("beans.SSHExecutionCredential", SSHExecutionCredential.class);
    w.put("delegatetasks.validation.capabilities.ClusterMasterUrlValidationCapability",
        ClusterMasterUrlValidationCapability.class);
    w.put("beans.yaml.GitFetchFilesFromMultipleRepoResult", GitFetchFilesFromMultipleRepoResult.class);
    w.put("beans.KubernetesClusterConfig", KubernetesClusterConfig.class);
    w.put("delegatetasks.validation.capabilities.GitConnectionCapability", GitConnectionCapability.class);
    w.put("beans.SSHVaultConfig", SSHVaultConfig.class);
    w.put("beans.BaseVaultConfig", BaseVaultConfig.class);
    w.put("beans.ServiceNowConfig", ServiceNowConfig.class);
    w.put("beans.DockerConfig", DockerConfig.class);
    w.put("beans.config.NexusConfig", NexusConfig.class);
    w.put("beans.JiraConfig", JiraConfig.class);
    w.put("beans.SplunkConfig", SplunkConfig.class);
    w.put("beans.settings.azureartifacts.AzureArtifactsPATConfig", AzureArtifactsPATConfig.class);
    w.put("beans.SftpConfig", SftpConfig.class);
    w.put("beans.InstanaConfig", InstanaConfig.class);
    w.put("beans.PcfConfig", PcfConfig.class);
    w.put("beans.AzureConfig", AzureConfig.class);
    w.put("beans.BambooConfig", BambooConfig.class);
    w.put("beans.SmbConfig", SmbConfig.class);
    w.put("beans.config.ArtifactoryConfig", ArtifactoryConfig.class);
    w.put("helpers.ext.mail.SmtpConfig", SmtpConfig.class);
    w.put("helpers.ext.helm.response.HelmCollectChartResponse", HelmCollectChartResponse.class);
    w.put("delegatetasks.validation.capabilities.WinrmHostValidationCapability", WinrmHostValidationCapability.class);
    w.put("delegatetasks.validation.capabilities.ShellConnectionCapability", ShellConnectionCapability.class);
    w.put("delegatetasks.validation.capabilities.SSHHostValidationCapability", SSHHostValidationCapability.class);
    w.put("helpers.ext.external.comm.CollaborationProviderResponse", CollaborationProviderResponse.class);
    w.put("service.impl.stackdriver.StackDriverLogDataCollectionInfo", StackDriverLogDataCollectionInfo.class);
    w.put("service.impl.stackdriver.StackDriverDataCollectionInfo", StackDriverDataCollectionInfo.class);
    w.put("service.impl.splunk.SplunkDataCollectionInfoV2", SplunkDataCollectionInfoV2.class);
    w.put("beans.APMVerificationConfig", APMVerificationConfig.class);
    w.put("service.impl.newrelic.NewRelicDataCollectionInfoV2", NewRelicDataCollectionInfoV2.class);
    w.put("service.impl.instana.InstanaDataCollectionInfo", InstanaDataCollectionInfo.class);
    w.put("service.impl.elk.ElkDataCollectionInfoV2", ElkDataCollectionInfoV2.class);
    w.put("service.impl.cloudwatch.CloudWatchMetric", CloudWatchMetric.class);
    w.put("sm.WorkflowStandardParams", WorkflowStandardParams.class);
    w.put("sm.StateExecutionData", StateExecutionData.class);
    w.put("api.AwsLambdaContextElement", AwsLambdaContextElement.class);
    w.put("api.AwsLambdaExecutionData", AwsLambdaExecutionData.class);
    w.put("service.impl.aws.model.AwsLambdaExecuteFunctionResponse", AwsLambdaExecuteFunctionResponse.class);
    w.put("service.impl.aws.model.AwsLambdaExecuteWfResponse", AwsLambdaExecuteWfResponse.class);
    w.put("beans.BugsnagConfig", BugsnagConfig.class);
    w.put("delegatetasks.buildsource.BuildSourceExecutionResponse", BuildSourceExecutionResponse.class);
    w.put("api.TerraformExecutionData", TerraformExecutionData.class);
    w.put("service.impl.aws.model.AwsAmiServiceDeployResponse", AwsAmiServiceDeployResponse.class);
    w.put("service.impl.aws.model.AwsAmiServiceSetupResponse", AwsAmiServiceSetupResponse.class);
    w.put("service.impl.aws.model.AwsAmiSwitchRoutesResponse", AwsAmiSwitchRoutesResponse.class);
    w.put("api.jira.JiraExecutionData", JiraExecutionData.class);
    w.put("api.JiraExecutionData", JiraExecutionData.class);
    w.put("api.ScriptStateExecutionData", ScriptStateExecutionData.class);
    w.put("api.ScriptStateExecutionSummary", ScriptStateExecutionSummary.class);
    w.put("api.shellscript.provision.ShellScriptProvisionExecutionData", ShellScriptProvisionExecutionData.class);
    w.put("api.ServiceNowExecutionData", ServiceNowExecutionData.class);
  }
}
