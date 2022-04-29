/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.ng.core.dto;

import static io.harness.annotations.dev.HarnessTeam.PL;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import io.harness.annotations.dev.OwnedBy;
import io.harness.data.validator.EntityIdentifier;
import io.harness.data.validator.NGEntityName;
import io.harness.ng.core.account.AuthenticationMechanism;
import io.harness.ng.core.account.DefaultExperience;
import io.harness.ng.core.account.ServiceAccountConfig;
import io.harness.yaml.core.VariableExpression;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
@ApiModel(value = "AccountDTO")
@OwnedBy(PL)
@Schema(name = "Account", description = "This is the view of an Account defined in Harness")
public class AccountDTO {
  @Schema(description = "Identifier of the account.")
  @EntityIdentifier(allowBlank = false)
  @VariableExpression(skipVariableExpression = true)
  String identifier;
  @Schema(description = "Name of the account.") @NGEntityName String name;
  @Schema(description = "Name of the company name.") String companyName;
  @Schema(description = "Name of the cluster associated with this account.")
  @VariableExpression(skipVariableExpression = true)
  String cluster;
  @Schema(description = "Specifies the default experience of the account.")
  @VariableExpression(skipVariableExpression = true)
  DefaultExperience defaultExperience;
  @Schema(description = "Authentication mechanism associated with the account.")
  @VariableExpression(skipVariableExpression = true)
  AuthenticationMechanism authenticationMechanism;
  @Schema(description = "Configuration of the service account associated with the account.")
  @VariableExpression(skipVariableExpression = true)
  ServiceAccountConfig serviceAccountConfig;
  @Schema(description = "Specifies whether or not Nextgen is enabled to the account.")
  @VariableExpression(skipVariableExpression = true)
  boolean isNextGenEnabled;

  @Builder
  public AccountDTO(String identifier, String name, String companyName, String cluster,
      DefaultExperience defaultExperience, AuthenticationMechanism authenticationMechanism,
      ServiceAccountConfig serviceAccountConfig, boolean isNextGenEnabled) {
    this.identifier = identifier;
    this.name = name;
    this.companyName = companyName;
    this.cluster = cluster;
    this.defaultExperience = defaultExperience;
    this.authenticationMechanism = authenticationMechanism;
    this.isNextGenEnabled = isNextGenEnabled;
    this.serviceAccountConfig = serviceAccountConfig;
  }
}
