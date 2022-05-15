/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package software.wings.beans.sso;

import io.harness.security.encryption.EncryptedDataDetail;

import software.wings.helpers.ext.ldap.LdapConstants;
import software.wings.service.intfc.security.EncryptionService;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
@Builder
public class LdapSettingsDTO {
  @NotBlank String accountId;
  @NotNull @Valid LdapConnectionSettings connectionSettings;
  @Valid List<LdapUserSettings> userSettingsList;

  @Valid List<LdapGroupSettings> groupSettingsList;

  @NotBlank protected String displayName;

  @Valid @Deprecated LdapUserSettings userSettings;

  @Valid @Deprecated LdapGroupSettings groupSettings;

  private String uuid;

  public void decryptFields(
      @NotNull EncryptedDataDetail encryptedDataDetail, @NotNull EncryptionService encryptionService) {
    if (connectionSettings.getBindPassword().equals(LdapConstants.MASKED_STRING)) {
      String bindPassword = new String(encryptionService.getDecryptedValue(encryptedDataDetail, false));
      connectionSettings.setBindPassword(bindPassword);
    }
  }
}
