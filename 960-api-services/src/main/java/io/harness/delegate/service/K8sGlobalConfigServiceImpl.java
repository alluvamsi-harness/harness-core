/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.delegate.service;

import static io.harness.delegate.clienttools.ClientTool.CHARTMUSEUM;
import static io.harness.delegate.clienttools.ClientTool.GO_TEMPLATE;
import static io.harness.delegate.clienttools.ClientTool.HELM;
import static io.harness.delegate.clienttools.ClientTool.KUBECTL;
import static io.harness.delegate.clienttools.ClientTool.KUSTOMIZE;
import static io.harness.delegate.clienttools.ClientTool.OC;
import static io.harness.delegate.clienttools.ClientTool.SCM;
import static io.harness.delegate.clienttools.InstallUtils.getPath;
import static io.harness.k8s.model.HelmVersion.V2;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.delegate.clienttools.ChartmuseumVersion;
import io.harness.delegate.clienttools.ClientTool;
import io.harness.delegate.clienttools.ClientToolVersion;
import io.harness.delegate.clienttools.GoTemplateVersion;
import io.harness.delegate.clienttools.InstallUtils;
import io.harness.delegate.clienttools.KubectlVersion;
import io.harness.delegate.clienttools.KustomizeVersion;
import io.harness.delegate.clienttools.OcVersion;
import io.harness.delegate.clienttools.ScmVersion;
import io.harness.exception.InvalidRequestException;
import io.harness.k8s.K8sGlobalConfigService;
import io.harness.k8s.model.HelmVersion;

import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

/**
 * @deprecated use {@link InstallUtils#getPath(ClientTool, ClientToolVersion)} directly instead
 */
@Slf4j
@OwnedBy(HarnessTeam.CDP)
@Deprecated
public class K8sGlobalConfigServiceImpl implements K8sGlobalConfigService {
  @Override
  public String getKubectlPath(boolean useNewKubectlVersion) {
    return useNewKubectlVersion ? getPath(KUBECTL, KubectlVersion.V1_19) : getPath(KUBECTL, KubectlVersion.V1_13);
  }

  @Override
  public String getGoTemplateClientPath() {
    return InstallUtils.getPath(GO_TEMPLATE, GoTemplateVersion.V0_4);
  }

  /*
  For all helm commands run through the binary installed through InstallUtils, we want to default to v3.
   */
  public String getHelmPath(@Nullable HelmVersion helmVersion) {
    if (helmVersion == null) {
      log.error("Did not expect null value of helmVersion, defaulting to V2");
      helmVersion = V2;
    }
    log.info("[HELM]: picked helm binary corresponding to version {}", helmVersion);
    switch (helmVersion) {
      case V2:
        return InstallUtils.getPath(HELM, io.harness.delegate.clienttools.HelmVersion.V2);
      case V3:
        return InstallUtils.getPath(HELM, io.harness.delegate.clienttools.HelmVersion.V3);
      case V380:
        return InstallUtils.getPath(HELM, io.harness.delegate.clienttools.HelmVersion.V3_8);
      default:
        throw new InvalidRequestException("Unsupported Helm Version:" + helmVersion);
    }
  }

  @Override
  public String getChartMuseumPath(final boolean useLatestVersion) {
    if (useLatestVersion) {
      return InstallUtils.getLatestVersionPath(CHARTMUSEUM);
    }
    return InstallUtils.getPath(CHARTMUSEUM, ChartmuseumVersion.V0_8);
  }

  @Override
  public String getOcPath() {
    return InstallUtils.getPath(OC, OcVersion.V4_2);
  }

  @Override
  public String getKustomizePath(boolean useLatestVersion) {
    if (useLatestVersion) {
      return InstallUtils.getLatestVersionPath(KUSTOMIZE);
    }
    return InstallUtils.getPath(KUSTOMIZE, KustomizeVersion.V3);
  }

  @Override
  public String getScmPath() {
    return InstallUtils.getPath(SCM, ScmVersion.DEFAULT);
  }
}
