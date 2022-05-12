/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.steps.approval.step.custom.entities;

import static io.harness.annotations.dev.HarnessTeam.CDC;

import io.harness.annotations.dev.OwnedBy;
import io.harness.plancreator.steps.TaskSelectorYaml;
import io.harness.plancreator.steps.common.StepElementParameters;
import io.harness.pms.contracts.ambiance.Ambiance;
import io.harness.pms.yaml.ParameterField;
import io.harness.servicenow.TicketNG;
import io.harness.steps.approval.step.beans.CriteriaSpecWrapperDTO;
import io.harness.steps.approval.step.custom.CustomApprovalOutcome;
import io.harness.steps.approval.step.custom.CustomApprovalSpecParameters;
import io.harness.steps.approval.step.custom.beans.CustomApprovalTicketNG;
import io.harness.steps.approval.step.entities.ApprovalInstance;
import io.harness.steps.shellscript.ShellScriptSourceWrapper;
import io.harness.steps.shellscript.ShellScriptStepParameters;
import io.harness.steps.shellscript.ShellType;
import io.harness.yaml.core.timeout.Timeout;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.mongodb.morphia.annotations.Entity;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.annotation.TypeAlias;

@OwnedBy(CDC)
@Data
@Builder
@FieldNameConstants(innerTypeName = "CustomApprovalInstanceKeys")
@EqualsAndHashCode(callSuper = false)
@Entity(value = "approvalInstances", noClassnameStored = true)
@Persistent
@TypeAlias("customApprovalInstances")
public class CustomApprovalInstance extends ApprovalInstance {
  @NotNull ShellType shellType;
  @NotNull ShellScriptSourceWrapper source;
  ParameterField<Timeout> retryInterval;
  Map<String, Object> outputVariables;
  Map<String, Object> environmentVariables;
  ParameterField<List<TaskSelectorYaml>> delegateSelectors;
  @NotNull CriteriaSpecWrapperDTO approvalCriteria;
  CriteriaSpecWrapperDTO rejectionCriteria;
  ParameterField<Timeout> scriptTimeout;

  public static CustomApprovalInstance fromStepParameters(Ambiance ambiance, StepElementParameters stepParameters) {
    if (stepParameters == null) {
      return null;
    }

    CustomApprovalSpecParameters specParameters = (CustomApprovalSpecParameters) stepParameters.getSpec();
    CustomApprovalInstance instance =
        CustomApprovalInstance.builder()
            .shellType(specParameters.getShellType())
            .source(specParameters.getSource())
            .retryInterval(specParameters.getRetryInterval())
            .delegateSelectors(specParameters.getDelegateSelectors())
            .environmentVariables(specParameters.getEnvironmentVariables())
            .outputVariables(specParameters.getOutputVariables())
            .approvalCriteria(
                CriteriaSpecWrapperDTO.fromCriteriaSpecWrapper(specParameters.getApprovalCriteria(), false))
            .rejectionCriteria(
                CriteriaSpecWrapperDTO.fromCriteriaSpecWrapper(specParameters.getRejectionCriteria(), true))
            .scriptTimeout(specParameters.getScriptTimeout())
            .build();
    instance.updateFromStepParameters(ambiance, stepParameters);
    return instance;
  }

  public CustomApprovalOutcome toCustomApprovalOutcome(TicketNG ticketNG) {
    return CustomApprovalOutcome.builder().outputVariables(((CustomApprovalTicketNG) ticketNG).getFields()).build();
  }

  public ShellScriptStepParameters toShellScriptStepParameters() {
    return ShellScriptStepParameters.infoBuilder()
        .environmentVariables(getEnvironmentVariables())
        .shellType(getShellType())
        .delegateSelectors(getDelegateSelectors())
        .outputVariables(getOutputVariables())
        .onDelegate(ParameterField.createValueField(true))
        .source(getSource())
        .uuid(getUuid())
        .build();
  }
}
