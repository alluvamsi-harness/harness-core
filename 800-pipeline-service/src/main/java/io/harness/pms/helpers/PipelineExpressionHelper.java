/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.pms.helpers;

import io.harness.PipelineServiceConfiguration;
import io.harness.account.AccountClient;
import io.harness.data.structure.EmptyPredicate;
import io.harness.pms.contracts.ambiance.Ambiance;
import io.harness.pms.execution.utils.AmbianceUtils;
import io.harness.pms.pipeline.PipelineEntity;
import io.harness.pms.pipeline.PipelineEntityUtils;
import io.harness.pms.pipeline.service.PMSPipelineService;
import io.harness.remote.client.RestClientUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.net.URL;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class PipelineExpressionHelper {
  @Inject PMSPipelineService pmsPipelineService;
  @Inject PipelineServiceConfiguration pipelineServiceConfiguration;
  @Inject private AccountClient accountClient;

  public String generateUrl(Ambiance ambiance) {
    String accountId = AmbianceUtils.getAccountId(ambiance);
    String orgId = AmbianceUtils.getOrgIdentifier(ambiance);
    String projectId = AmbianceUtils.getProjectIdentifier(ambiance);
    String planExecutionId = ambiance.getPlanExecutionId();
    String moduleName = "cd";
    Optional<PipelineEntity> pipelineEntity =
        pmsPipelineService.get(accountId, orgId, projectId, ambiance.getMetadata().getPipelineIdentifier(), false);

    if (!EmptyPredicate.isEmpty(ambiance.getMetadata().getModuleType())) {
      moduleName = ambiance.getMetadata().getModuleType();
    } else if (pipelineEntity.isPresent()) {
      moduleName = PipelineEntityUtils.getModuleNameFromPipelineEntity(pipelineEntity.get(), "cd");
    }
    String vanityUrl = getVanityUrl(accountId);
    String baseUrl = getBaseUrl(pipelineServiceConfiguration.getPipelineServiceBaseUrl(), vanityUrl);
    return String.format("%s/account/%s/%s/orgs/%s/projects/%s/pipelines/%s/executions/%s/pipeline", baseUrl, accountId,
        moduleName, orgId, projectId, ambiance.getMetadata().getPipelineIdentifier(), planExecutionId);
  }

  private static String getBaseUrl(String defaultBaseUrl, String vanityUrl) {
    // e.g Prod Default Base URL - 'https://app.harness.io/ng/#'
    if (EmptyPredicate.isEmpty(vanityUrl)) {
      return defaultBaseUrl;
    }
    String newBaseUrl = vanityUrl;
    if (vanityUrl.endsWith("/")) {
      newBaseUrl = vanityUrl.substring(0, vanityUrl.length() - 1);
    }
    try {
      URL url = new URL(defaultBaseUrl);
      String hostUrl = String.format("%s://%s", url.getProtocol(), url.getHost());
      return newBaseUrl + defaultBaseUrl.substring(hostUrl.length());
    } catch (Exception e) {
      log.warn("There was error while generating vanity URL", e);
      return defaultBaseUrl;
    }
  }

  private String getVanityUrl(String accountIdentifier) {
    return RestClientUtils.getResponse(accountClient.getVanityUrl(accountIdentifier));
  }
}
