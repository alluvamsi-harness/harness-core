package software.wings.utils;

import static io.harness.annotations.dev.HarnessTeam.CDP;
import static io.harness.data.structure.HasPredicate.hasSome;
import static io.harness.shell.AccessType.KEY_SUDO_APP_USER;
import static io.harness.shell.AccessType.KEY_SU_APP_USER;
import static io.harness.shell.AccessType.USER_PASSWORD;
import static io.harness.shell.AuthenticationScheme.KERBEROS;
import static io.harness.shell.ExecutorType.BASTION_HOST;
import static io.harness.shell.ExecutorType.KEY_AUTH;
import static io.harness.shell.ExecutorType.PASSWORD_AUTH;
import static io.harness.shell.SshSessionConfig.Builder.aSshSessionConfig;

import io.harness.annotations.dev.OwnedBy;
import io.harness.shell.AccessType;
import io.harness.shell.AuthenticationScheme;
import io.harness.shell.ExecutorType;
import io.harness.shell.KerberosConfig;
import io.harness.shell.SshSessionConfig;
import io.harness.shell.SshSessionConfig.Builder;

import software.wings.beans.BastionConnectionAttributes;
import software.wings.beans.HostConnectionAttributes;
import software.wings.beans.SSHExecutionCredential;
import software.wings.beans.SettingAttribute;
import software.wings.beans.command.CommandExecutionContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by anubhaw on 2/23/17.
 */

@Slf4j
@OwnedBy(CDP)
public class SshHelperUtils {
  private static ExecutorType getExecutorType(
      SettingAttribute hostConnectionSetting, SettingAttribute bastionHostConnectionSetting) {
    ExecutorType executorType;
    if (bastionHostConnectionSetting != null) {
      executorType = BASTION_HOST;
    } else {
      HostConnectionAttributes hostConnectionAttributes = (HostConnectionAttributes) hostConnectionSetting.getValue();
      AccessType accessType = hostConnectionAttributes.getAccessType();
      if (accessType == AccessType.KEY || accessType == KEY_SU_APP_USER || accessType == KEY_SUDO_APP_USER) {
        executorType = KEY_AUTH;
      } else {
        executorType = PASSWORD_AUTH;
      }
    }
    return executorType;
  }

  public static SshSessionConfig createSshSessionConfig(SettingAttribute settingAttribute, String hostName) {
    Builder builder = aSshSessionConfig().withAccountId(settingAttribute.getAccountId()).withHost(hostName);
    populateBuilderWithCredentials(builder, settingAttribute, null);
    return builder.build();
  }
  public static SshSessionConfig createSshSessionConfig(String commandName, CommandExecutionContext context) {
    SSHExecutionCredential sshExecutionCredential = (SSHExecutionCredential) context.getExecutionCredential();

    String hostName = context.getHost().getPublicDns();
    Builder builder = aSshSessionConfig()
                          .withAccountId(context.getAccountId())
                          .withAppId(context.getAppId())
                          .withExecutionId(context.getActivityId())
                          .withHost(hostName)
                          .withCommandUnitName(commandName);

    // TODO: The following can be removed as we do not support username and password from context anymore
    if (sshExecutionCredential != null) {
      builder.withUserName(sshExecutionCredential.getSshUser())
          .withPassword(sshExecutionCredential.getSshPassword())
          .withSudoAppName(sshExecutionCredential.getAppAccount())
          .withSudoAppPassword(sshExecutionCredential.getAppAccountPassword());
    }

    populateBuilderWithCredentials(
        builder, context.getHostConnectionAttributes(), context.getBastionConnectionAttributes());
    return builder.build();
  }

  public static void populateBuilderWithCredentials(
      Builder builder, SettingAttribute hostConnectionSetting, SettingAttribute bastionHostConnectionSetting) {
    ExecutorType executorType = getExecutorType(hostConnectionSetting, bastionHostConnectionSetting);

    builder.withExecutorType(executorType);
    HostConnectionAttributes hostConnectionAttributes = (HostConnectionAttributes) hostConnectionSetting.getValue();

    if (executorType == KEY_AUTH) {
      if (hasSome(hostConnectionAttributes.getKey())) {
        builder.withKey(new String(hostConnectionAttributes.getKey()).toCharArray());
      }

      if (hasSome(hostConnectionAttributes.getPassphrase())) {
        builder.withKeyPassphrase(new String(hostConnectionAttributes.getPassphrase()).toCharArray());
      }

      builder.withUserName(hostConnectionAttributes.getUserName())
          .withPort(hostConnectionAttributes.getSshPort())
          .withKeyName(hostConnectionSetting.getUuid())
          .withPassword(null)
          .withKeyLess(hostConnectionAttributes.isKeyless())
          .withKeyPath(hostConnectionAttributes.getKeyPath())
          .withVaultSSH(hostConnectionAttributes.isVaultSSH())
          .withPublicKey(hostConnectionAttributes.getPublicKey())
          .withSignedPublicKey(hostConnectionAttributes.getSignedPublicKey());
    } else if (KERBEROS == hostConnectionAttributes.getAuthenticationScheme()) {
      KerberosConfig kerberosConfig = hostConnectionAttributes.getKerberosConfig();

      if (hasSome(hostConnectionAttributes.getKerberosPassword())) {
        builder.withPassword(new String(hostConnectionAttributes.getKerberosPassword()).toCharArray());
      }

      builder.withAuthenticationScheme(KERBEROS)
          .withKerberosConfig(kerberosConfig)
          .withPort(hostConnectionAttributes.getSshPort());
    } else if (USER_PASSWORD == hostConnectionAttributes.getAccessType()) {
      if (hasSome(hostConnectionAttributes.getSshPassword())) {
        builder.withSshPassword(new String(hostConnectionAttributes.getSshPassword()).toCharArray());
      }

      builder.withAuthenticationScheme(AuthenticationScheme.SSH_KEY)
          .withAccessType(hostConnectionAttributes.getAccessType())
          .withUserName(hostConnectionAttributes.getUserName())
          .withPort(hostConnectionAttributes.getSshPort());
    }

    if (bastionHostConnectionSetting != null) {
      BastionConnectionAttributes bastionAttrs = (BastionConnectionAttributes) bastionHostConnectionSetting.getValue();
      Builder sshSessionConfig = aSshSessionConfig()
                                     .withHost(bastionAttrs.getHostName())
                                     .withKeyName(bastionHostConnectionSetting.getUuid())
                                     .withUserName(bastionAttrs.getUserName())
                                     .withPort(bastionAttrs.getSshPort());

      if (hasSome(bastionAttrs.getKey())) {
        sshSessionConfig.withKey(new String(bastionAttrs.getKey()).toCharArray());
      }

      if (hasSome(bastionAttrs.getPassphrase())) {
        sshSessionConfig.withKeyPassphrase(new String(bastionAttrs.getPassphrase()).toCharArray());
      }

      builder.withBastionHostConfig(sshSessionConfig.build());
    }
  }
}
