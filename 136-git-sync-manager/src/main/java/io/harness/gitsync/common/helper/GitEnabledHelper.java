package io.harness.gitsync.common.helper;

import static io.harness.annotations.dev.HarnessTeam.DX;

import io.harness.annotations.dev.OwnedBy;
import io.harness.gitsync.common.dtos.ConnectivityMode;
import io.harness.gitsync.common.dtos.GitEnabledDTO;
import io.harness.gitsync.common.dtos.GitEnabledDTO.GitEnabledDTOBuilder;
import io.harness.gitsync.common.dtos.GitSyncSettingsDTO;
import io.harness.gitsync.common.service.GitSyncSettingsService;
import io.harness.gitsync.common.service.YamlGitConfigService;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Optional;
import lombok.AllArgsConstructor;

@Singleton
@AllArgsConstructor(onConstructor = @__({ @Inject }))
@OwnedBy(DX)
public class GitEnabledHelper {
  private final GitSyncSettingsService gitSyncSettingsService;
  private final YamlGitConfigService yamlGitConfigService;

  public GitEnabledDTO getGitEnabledDTO(
      String projectIdentifier, String organizationIdentifier, String accountIdentifier) {
    final boolean gitSyncEnabled =
        yamlGitConfigService.isGitSyncEnabled(accountIdentifier, organizationIdentifier, projectIdentifier);
    final GitEnabledDTOBuilder gitEnabledDTOBuilder = GitEnabledDTO.builder().isGitSyncEnabled(gitSyncEnabled);
    if (gitSyncEnabled) {
      final Optional<GitSyncSettingsDTO> gitSyncSettingsDTO =
          gitSyncSettingsService.get(accountIdentifier, organizationIdentifier, projectIdentifier);
      final ConnectivityMode connectivityMode = gitSyncSettingsDTO.filter(settings -> !settings.isExecuteOnDelegate())
                                                    .map(settings -> ConnectivityMode.MANAGER)
                                                    .orElse(ConnectivityMode.DELEGATE);
      gitEnabledDTOBuilder.connectivityMode(connectivityMode);
    }
    return gitEnabledDTOBuilder.build();
  }
}
