package io.harness.encryptors.managerproxy;

import static io.harness.beans.shared.tasks.NgSetupFields.OWNER;
import static io.harness.eraro.ErrorCode.SECRET_MANAGEMENT_ERROR;
import static io.harness.exception.WingsException.USER;

import static software.wings.beans.TaskType.FETCH_SECRET;
import static software.wings.beans.TaskType.VALIDATE_SECRET_MANAGER_CONFIGURATION;
import static software.wings.beans.TaskType.VALIDATE_SECRET_REFERENCE;

import io.harness.beans.DelegateTask;
import io.harness.delegate.beans.DelegateResponseData;
import io.harness.delegate.beans.TaskData;
import io.harness.delegate.utils.TaskSetupAbstractionHelper;
import io.harness.delegatetasks.FetchSecretTaskParameters;
import io.harness.delegatetasks.FetchSecretTaskResponse;
import io.harness.delegatetasks.ValidateSecretManagerConfigurationTaskParameters;
import io.harness.delegatetasks.ValidateSecretManagerConfigurationTaskResponse;
import io.harness.delegatetasks.ValidateSecretReferenceTaskParameters;
import io.harness.delegatetasks.ValidateSecretReferenceTaskResponse;
import io.harness.encryptors.DelegateTaskUtils;
import io.harness.exception.SecretManagementException;
import io.harness.ng.core.NGAccess;
import io.harness.security.encryption.EncryptedRecord;
import io.harness.security.encryption.EncryptionConfig;

import software.wings.service.intfc.DelegateService;

import com.google.inject.Inject;

public class ManagerEncryptorHelper {
  private final DelegateService delegateService;
  private final TaskSetupAbstractionHelper taskSetupAbstractionHelper;

  @Inject
  public ManagerEncryptorHelper(
      DelegateService delegateService, TaskSetupAbstractionHelper taskSetupAbstractionHelper) {
    this.delegateService = delegateService;
    this.taskSetupAbstractionHelper = taskSetupAbstractionHelper;
  }

  public String getOwner(EncryptionConfig encryptionConfig) {
    String owner = null;
    if (encryptionConfig instanceof NGAccess) {
      NGAccess ngAccess = (NGAccess) encryptionConfig;
      owner = taskSetupAbstractionHelper.getOwner(
          encryptionConfig.getAccountId(), ngAccess.getOrgIdentifier(), ngAccess.getProjectIdentifier());
    }
    return owner;
  }

  public char[] fetchSecretValue(String accountId, EncryptedRecord encryptedRecord, EncryptionConfig encryptionConfig) {
    FetchSecretTaskParameters parameters =
        FetchSecretTaskParameters.builder().encryptedRecord(encryptedRecord).encryptionConfig(encryptionConfig).build();

    DelegateTask delegateTask = DelegateTask.builder()
                                    .data(TaskData.builder()
                                              .async(false)
                                              .taskType(FETCH_SECRET.name())
                                              .parameters(new Object[] {parameters})
                                              .timeout(TaskData.DEFAULT_SYNC_CALL_TIMEOUT)
                                              .build())
                                    .accountId(accountId)
                                    .setupAbstraction(OWNER, getOwner(encryptionConfig))
                                    .build();
    try {
      DelegateResponseData delegateResponseData = delegateService.executeTask(delegateTask);
      DelegateTaskUtils.validateDelegateTaskResponse(delegateResponseData);
      if (!(delegateResponseData instanceof FetchSecretTaskResponse)) {
        throw new SecretManagementException(SECRET_MANAGEMENT_ERROR, "Unknown Response from delegate", USER);
      }
      FetchSecretTaskResponse responseData = (FetchSecretTaskResponse) delegateResponseData;
      return responseData.getSecretValue();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      String message = String.format(
          "Interrupted while fetch secret value with encryption config %s", parameters.getEncryptionConfig().getName());
      throw new SecretManagementException(SECRET_MANAGEMENT_ERROR, message, USER);
    }
  }

  public boolean validateReference(String accountId, ValidateSecretReferenceTaskParameters parameters) {
    DelegateTask delegateTask = DelegateTask.builder()
                                    .data(TaskData.builder()
                                              .async(false)
                                              .taskType(VALIDATE_SECRET_REFERENCE.name())
                                              .parameters(new Object[] {parameters})
                                              .timeout(TaskData.DEFAULT_SYNC_CALL_TIMEOUT)
                                              .build())
                                    .accountId(accountId)
                                    .setupAbstraction(OWNER, getOwner(parameters.getEncryptionConfig()))
                                    .build();
    try {
      DelegateResponseData delegateResponseData = delegateService.executeTask(delegateTask);
      DelegateTaskUtils.validateDelegateTaskResponse(delegateResponseData);
      if (!(delegateResponseData instanceof ValidateSecretReferenceTaskResponse)) {
        throw new SecretManagementException(SECRET_MANAGEMENT_ERROR, "Unknown Response from delegate", USER);
      }
      ValidateSecretReferenceTaskResponse responseData = (ValidateSecretReferenceTaskResponse) delegateResponseData;
      return responseData.isReferenceValid();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      String message = String.format("Interrupted while validating reference with encryption config %s",
          parameters.getEncryptionConfig().getName());
      throw new SecretManagementException(SECRET_MANAGEMENT_ERROR, message, USER);
    }
  }

  public boolean validateConfiguration(String accountId, ValidateSecretManagerConfigurationTaskParameters parameters) {
    DelegateTask delegateTask = DelegateTask.builder()
                                    .data(TaskData.builder()
                                              .async(false)
                                              .taskType(VALIDATE_SECRET_MANAGER_CONFIGURATION.name())
                                              .parameters(new Object[] {parameters})
                                              .timeout(TaskData.DEFAULT_SYNC_CALL_TIMEOUT)
                                              .build())
                                    .accountId(accountId)
                                    .setupAbstraction(OWNER, getOwner(parameters.getEncryptionConfig()))
                                    .build();

    try {
      DelegateResponseData delegateResponseData = delegateService.executeTask(delegateTask);
      DelegateTaskUtils.validateDelegateTaskResponse(delegateResponseData);
      if (!(delegateResponseData instanceof ValidateSecretManagerConfigurationTaskResponse)) {
        throw new SecretManagementException(SECRET_MANAGEMENT_ERROR, "Unknown Response from delegate", USER);
      }
      ValidateSecretManagerConfigurationTaskResponse responseData =
          (ValidateSecretManagerConfigurationTaskResponse) delegateResponseData;
      return responseData.isConfigurationValid();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      String message = String.format("Interrupted while validating configuration with encryption config %s",
          parameters.getEncryptionConfig().getName());
      throw new SecretManagementException(SECRET_MANAGEMENT_ERROR, message, USER);
    }
  }
}
