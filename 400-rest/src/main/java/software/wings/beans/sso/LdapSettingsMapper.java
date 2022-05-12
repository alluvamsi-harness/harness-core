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
