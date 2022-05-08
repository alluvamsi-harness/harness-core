/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.delegate.clienttools;

import static io.harness.annotations.dev.HarnessTeam.DEL;
import static io.harness.data.structure.EmptyPredicate.isNotEmpty;
import static io.harness.data.structure.HarnessStringUtils.join;
import static io.harness.filesystem.FileIo.createDirectoryIfDoesNotExist;
import static io.harness.network.Http.getBaseUrl;

import static com.google.common.base.Strings.isNullOrEmpty;

import io.harness.annotations.dev.OwnedBy;
import io.harness.delegate.configuration.DelegateConfiguration;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.LogOutputStream;

@UtilityClass
@Slf4j
@OwnedBy(DEL)
public class InstallUtils {
  private static final Table<ClientTool, ClientToolVersion, String> toolPaths = HashBasedTable.create();

  public static String getPath(final ClientTool tool, final ClientToolVersion version) {
    if (toolPaths.contains(tool, version)) {
      return toolPaths.get(tool, version);
    }
    final String msg = String.format("%s is not installed for version %s. Available versions are: %s",
        tool.getBinaryName(), version, toolPaths.row(tool).keySet());
    throw new IllegalArgumentException(msg);
  }

  public static String getLatestVersionPath(final ClientTool tool) {
    return getPath(tool, tool.getLatestVersion());
  }

  public static boolean areClientToolsInstalled() {
    return Arrays.stream(ClientTool.values()).allMatch(InstallUtils::isInstalled);
  }

  public static boolean isInstalled(final ClientTool tool) {
    return !toolPaths.row(tool).isEmpty();
  }

  public static void setupClientTools(final DelegateConfiguration configuration) {
    log.info("Setting up client tools {}", Arrays.toString(ClientTool.values()));
    Arrays.stream(ClientTool.values())
        .forEach(tool -> toolPaths.row(tool).putAll(installTool(tool, tool.getVersions(), configuration)));
    log.info("Initializing client tools");
    Arrays.stream(ClientTool.values())
        .forEach(tool -> toolPaths.row(tool).keySet().forEach(version -> initTool(tool, version)));
  }

  @VisibleForTesting
  static void initTool(final ClientTool tool, final ClientToolVersion version) {
    final String initCommand = getInitCommand(tool, version);
    if (StringUtils.isNotBlank(initCommand)) {
      try {
        final String toolPath = toolPaths.get(tool, version);
        if (StringUtils.isNotBlank(toolPath)) {
          runCommand(Paths.get(toolPath).getParent().toString(), initCommand);
        } else {
          log.warn("Tool {} is not installed for version {}, skipping init", tool, version);
        }
      } catch (final IOException | InterruptedException | TimeoutException e) {
        log.error("Failed to initialize {} for version {}", tool, version, e);
      }
    } else {
      log.info("No init command for {} version {}", tool, version);
    }
  }

  private static String getManagerBaseUrl(String managerUrl) {
    if (managerUrl.contains("localhost") || managerUrl.contains("127.0.0.1")) {
      return "https://app.harness.io/";
    }
    return getBaseUrl(managerUrl);
  }

  private static String getHelmInitCommand(final ClientToolVersion helmVersion) {
    return HelmVersion.V2.equals(helmVersion) ? "./helm init -c --skip-refresh \n" : StringUtils.EMPTY;
  }

  private static Map<ClientToolVersion, String> installTool(
      final ClientTool tool, final List<ClientToolVersion> versions, final DelegateConfiguration configuration) {
    final ImmutableMap.Builder<ClientToolVersion, String> mapBuilder = ImmutableMap.builder();
    for (final ClientToolVersion version : versions) {
      final String versionedDir = installTool(tool, version, configuration);
      if (isNotEmpty(versionedDir)) {
        final String pathToBinary = Paths.get(versionedDir, tool.getBinaryName()).toString();
        mapBuilder.put(version, pathToBinary);
      } else {
        log.error("Failed to install {} for version {}", tool.getBinaryName(), version);
      }
    }
    return mapBuilder.build();
  }

  private static String installTool(
      final ClientTool tool, final ClientToolVersion version, final DelegateConfiguration configuration) {
    // 1. Check if custom path is configured for a tool (assume it's installed there)
    final String customPath = getCustomPath(tool, version, configuration);
    if (!isNullOrEmpty(customPath)) {
      final String customDir = Paths.get(customPath).normalize().toAbsolutePath().getParent().toString();
      if (validateToolExists(customDir, tool)) {
        log.info("Custom {} is installed at {}", tool, customDir);
        return customDir;
      }
      log.warn("Custom path configured for {} at {}, but the tool does not exist there.", tool, customPath);
      return StringUtils.EMPTY;
    }
    // 2. Check if tool is already installed
    final String versionedDirectory = getVersionedDirectory(tool, version);
    if (validateToolExists(versionedDirectory, tool)) {
      log.info("{} already installed at {}", tool.getBinaryName(), versionedDirectory);
      return versionedDirectory;
    }

    // 3. Download the tool
    if (!configuration.isClientToolsDownloadDisabled()) {
      try {
        log.info("{} not found at {}. Installing.", tool.getBinaryName(), versionedDirectory);
        createDirectoryIfDoesNotExist(versionedDirectory);

        final String downloadUrl = getDownloadUrl(tool, version, configuration);
        log.info("{} download url is {}", tool.getBinaryName(), downloadUrl);
        final String permissionsCommand = "chmod +x " + tool.getBinaryName();

        final String script = "curl $MANAGER_PROXY_CURL -kLO " + downloadUrl + "\n" + permissionsCommand;

        final boolean isInstalled = runCommand(versionedDirectory, script);
        if (isInstalled) {
          if (validateToolExists(versionedDirectory, tool)) {
            log.info("{} successfully installed to {}", tool.getBinaryName(), versionedDirectory);
            return versionedDirectory;
          } else {
            log.error("{} not validated after download {}", tool.getBinaryName(), versionedDirectory);
            return StringUtils.EMPTY;
          }
        } else {
          log.error("Failed installing {} to {}", tool.getBinaryName(), versionedDirectory);
          return StringUtils.EMPTY;
        }
      } catch (final Exception e) {
        log.error("Exception installing " + tool.getBinaryName(), e);
        return StringUtils.EMPTY;
      }
    } else {
      log.info("{} download disabled. Skipping install.", tool.getBinaryName());
      return StringUtils.EMPTY;
    }
  }

  private static String getVersionedDirectory(final ClientTool tool, final ClientToolVersion toolVersion) {
    return Paths.get(tool.getBaseDir(), toolVersion.getVersion()).toAbsolutePath().normalize().toString();
  }

  private static String getDownloadUrl(
      final ClientTool tool, final ClientToolVersion toolVersion, final DelegateConfiguration configuration) {
    if (configuration.isUseCdn()) {
      return join(
          "/", configuration.getCdnUrl(), String.format(tool.getCdnPath(), toolVersion.getVersion(), getOsPath()));
    }
    return getManagerBaseUrl(configuration.getManagerUrl())
        + String.format(tool.getOnPremPath(), toolVersion.getVersion(), getOsPath());
  }

  private static String getCustomPath(
      final ClientTool tool, final ClientToolVersion version, final DelegateConfiguration configuration) {
    switch (tool) {
      case HELM:
        return HelmVersion.V2.equals(version) ? configuration.getHelmPath() : configuration.getHelm3Path();
      case KUBECTL:
        return configuration.getKubectlPath();
      case KUSTOMIZE:
        return configuration.getKustomizePath();
      case OC:
        return configuration.getOcPath();
      case SCM:
      case TERRAFORM_CONFIG_INSPECT:
      case GO_TEMPLATE:
      case HARNESS_PYWINRM:
      case CHARTMUSEUM:
        return StringUtils.EMPTY;
      default:
        throw new IllegalArgumentException("Unknown tool: " + tool);
    }
  }

  private static String getInitCommand(final ClientTool tool, final ClientToolVersion version) {
    switch (tool) {
      case HELM:
        return getHelmInitCommand(version);
      case KUBECTL:
      case KUSTOMIZE:
      case OC:
      case SCM:
      case TERRAFORM_CONFIG_INSPECT:
      case GO_TEMPLATE:
      case HARNESS_PYWINRM:
      case CHARTMUSEUM:
        return StringUtils.EMPTY;
      default:
        throw new IllegalArgumentException("Unknown tool: " + tool);
    }
  }

  @VisibleForTesting
  static boolean validateToolExists(final String toolDirectory, final ClientTool tool) {
    try {
      if (!Files.exists(Paths.get(toolDirectory, tool.getBinaryName()))) {
        return false;
      }

      return runCommand(toolDirectory, tool.getValidateCommand());
    } catch (final Exception e) {
      log.error("Error validating if tool {} exists using {}", tool, tool.getValidateCommand(), e);
      return false;
    }
  }

  private static boolean runCommand(final String toolDirectory, final String script)
      throws IOException, InterruptedException, TimeoutException {
    final ProcessExecutor processExecutor = new ProcessExecutor()
                                                .timeout(5, TimeUnit.MINUTES)
                                                .directory(new File(toolDirectory))
                                                .command("/bin/bash", "-c", script)
                                                .readOutput(true)
                                                .redirectOutput(new LogOutputStream() {
                                                  @Override
                                                  protected void processLine(final String line) {
                                                    log.info(line);
                                                  }
                                                })
                                                .redirectError(new LogOutputStream() {
                                                  @Override
                                                  protected void processLine(final String line) {
                                                    log.error(line);
                                                  }
                                                });
    final ProcessResult result = processExecutor.execute();

    if (result.getExitValue() == 0) {
      log.info(result.outputUTF8());
      return true;
    } else {
      log.error(result.outputUTF8());
      return false;
    }
  }

  private static String getOsPath() {
    if (SystemUtils.IS_OS_WINDOWS) {
      throw new UnsupportedOperationException("Windows is not supported");
    }
    if (SystemUtils.IS_OS_MAC) {
      return "darwin";
    }
    return "linux";
  }
}
