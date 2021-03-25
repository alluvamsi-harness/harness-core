package software.wings.sm.states.azure.appservices;

import static io.harness.data.structure.HasPredicate.hasNone;

import static software.wings.beans.command.CommandUnitDetails.CommandUnitType.AZURE_APP_SERVICE_SLOT_SWAP;
import static software.wings.sm.StateType.AZURE_WEBAPP_SLOT_SWAP;

import io.harness.azure.model.AzureConstants;
import io.harness.beans.ExecutionStatus;
import io.harness.delegate.task.azure.AzureTaskExecutionResponse;
import io.harness.delegate.task.azure.appservice.webapp.request.AzureWebAppSwapSlotsParameters;
import io.harness.delegate.task.azure.appservice.webapp.response.AzureWebAppSwapSlotsResponse;

import software.wings.beans.Activity;
import software.wings.beans.command.AzureWebAppCommandUnit;
import software.wings.beans.command.CommandUnit;
import software.wings.beans.command.CommandUnitDetails.CommandUnitType;
import software.wings.service.impl.azure.manager.AzureTaskExecutionRequest;
import software.wings.sm.ExecutionContext;
import software.wings.sm.StateExecutionData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class AzureWebAppSlotSwap extends AbstractAzureAppServiceState {
  public static final String APP_SERVICE_SLOT_SWAP = "App Service Slot Swap";

  public AzureWebAppSlotSwap(String name) {
    super(name, AZURE_WEBAPP_SLOT_SWAP);
  }

  @Override
  protected boolean shouldExecute(ExecutionContext context) {
    if (verifyIfContextElementExist(context)) {
      AzureAppServiceSlotSetupContextElement contextElement = readContextElement(context);
      String targetSlot = contextElement.getTargetSlot();
      return !hasNone(targetSlot);
    }
    return false;
  }

  @Override
  public String skipMessage() {
    return "No Target slot detail is found, hence skipping swap slot step";
  }

  @Override
  protected AzureTaskExecutionRequest buildTaskExecutionRequest(
      ExecutionContext context, AzureAppServiceStateData azureAppServiceStateData, Activity activity) {
    AzureWebAppSwapSlotsParameters swapSlotsParameters =
        buildSlotSwapParams(context, azureAppServiceStateData, activity);

    return AzureTaskExecutionRequest.builder()
        .azureConfigDTO(azureVMSSStateHelper.createAzureConfigDTO(azureAppServiceStateData.getAzureConfig()))
        .azureConfigEncryptionDetails(azureAppServiceStateData.getAzureEncryptedDataDetails())
        .azureTaskParameters(swapSlotsParameters)
        .build();
  }

  @Override
  protected StateExecutionData buildPreStateExecutionData(
      Activity activity, ExecutionContext context, AzureAppServiceStateData azureAppServiceStateData) {
    AzureAppServiceSlotSetupContextElement contextElement = readContextElement(context);
    return AzureAppServiceSlotSwapExecutionData.builder()
        .activityId(activity.getUuid())
        .infrastructureMappingId(azureAppServiceStateData.getInfrastructureMapping().getUuid())
        .resourceGroup(contextElement.getResourceGroup())
        .appServiceName(contextElement.getWebApp())
        .deploymentSlot(contextElement.getDeploymentSlot())
        .targetSlot(contextElement.getTargetSlot())
        .build();
  }

  @Override
  protected StateExecutionData buildPostStateExecutionData(
      ExecutionContext context, AzureTaskExecutionResponse executionResponse, ExecutionStatus executionStatus) {
    AzureAppServiceSlotSwapExecutionData stateExecutionData = context.getStateExecutionData();
    AzureWebAppSwapSlotsResponse swapSlotsResponse =
        (AzureWebAppSwapSlotsResponse) executionResponse.getAzureTaskResponse();

    stateExecutionData.setStatus(executionStatus);
    stateExecutionData.setErrorMsg(executionResponse.getErrorMessage());
    stateExecutionData.setDelegateMetaInfo(executionResponse.getDelegateMetaInfo());
    stateExecutionData.setAppServiceName(swapSlotsResponse.getPreDeploymentData().getAppName());
    stateExecutionData.setDeploymentSlot(swapSlotsResponse.getPreDeploymentData().getSlotName());
    return stateExecutionData;
  }

  @Override
  protected String commandType() {
    return APP_SERVICE_SLOT_SWAP;
  }

  @NotNull
  @Override
  protected CommandUnitType commandUnitType() {
    return AZURE_APP_SERVICE_SLOT_SWAP;
  }

  @Override
  protected List<CommandUnit> commandUnits() {
    return ImmutableList.of(new AzureWebAppCommandUnit(AzureConstants.SLOT_SWAP),
        new AzureWebAppCommandUnit(AzureConstants.DEPLOYMENT_STATUS));
  }

  private AzureWebAppSwapSlotsParameters buildSlotSwapParams(
      ExecutionContext context, AzureAppServiceStateData azureAppServiceStateData, Activity activity) {
    AzureAppServiceSlotSetupContextElement contextElement = readContextElement(context);

    return AzureWebAppSwapSlotsParameters.builder()
        .accountId(azureAppServiceStateData.getApplication().getAccountId())
        .appId(azureAppServiceStateData.getApplication().getAppId())
        .activityId(activity.getUuid())
        .commandName(APP_SERVICE_SLOT_SWAP)
        .timeoutIntervalInMin(contextElement.getAppServiceSlotSetupTimeOut())
        .subscriptionId(contextElement.getSubscriptionId())
        .resourceGroupName(contextElement.getResourceGroup())
        .webAppName(contextElement.getWebApp())
        .sourceSlotName(contextElement.getDeploymentSlot())
        .targetSlotName(contextElement.getTargetSlot())
        .preDeploymentData(contextElement.getPreDeploymentData())
        .build();
  }
}
