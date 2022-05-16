/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package software.wings.beans.sso;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LdapSettingsMapper {
  public LdapSettingsDTO ldapSettingsDTO(LdapSettings ldapSettings) {
    return LdapSettingsDTO.builder()
        .accountId(ldapSettings.getAccountId())
        .connectionSettings(ldapSettings.getConnectionSettings())
        .userSettingsList(ldapSettings.getUserSettingsList())
        .groupSettingsList(ldapSettings.getGroupSettingsList())
        .displayName(ldapSettings.getDisplayName())
        .uuid(ldapSettings.getUuid())
        .userSettings(ldapSettings.getUserSettings())
        .groupSettings(ldapSettings.getGroupSettings())
        .build();
  }
}
