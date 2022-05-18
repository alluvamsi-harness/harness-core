/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.pms.helpers;

import io.harness.PipelineServiceConfiguration;
import io.harness.data.structure.EmptyPredicate;
import io.harness.pms.contracts.ambiance.Ambiance;
import io.harness.pms.execution.utils.AmbianceUtils;
import io.harness.pms.plan.execution.beans.PipelineExecutionSummaryEntity;
import io.harness.pms.plan.execution.service.PmsExecutionSummaryService;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class PipelineExpressionHelper {
  @Inject PmsExecutionSummaryService pmsExecutionSummaryService;
  @Inject PipelineServiceConfiguration pipelineServiceConfiguration;

  public String generateUrl(Ambiance ambiance) {
    String accountId = AmbianceUtils.getAccountId(ambiance);
    String orgId = AmbianceUtils.getOrgIdentifier(ambiance);
    String projectId = AmbianceUtils.getProjectIdentifier(ambiance);
    String planExecutionId = ambiance.getPlanExecutionId();
    String moduleName = "cd";

    if (!EmptyPredicate.isEmpty(ambiance.getMetadata().getModuleType())) {
      moduleName = ambiance.getMetadata().getModuleType();
    } else {
      Optional<PipelineExecutionSummaryEntity> optional = pmsExecutionSummaryService.getPipelineExecutionSummary(
          accountId, orgId, projectId, ambiance.getPlanExecutionId());
      if (optional.isPresent()) {
        moduleName = getModuleName(optional.get(), moduleName);
      }
    }
    return String.format("%s/account/%s/%s/orgs/%s/projects/%s/pipelines/%s/executions/%s/pipeline",
        pipelineServiceConfiguration.getPipelineServiceBaseUrl(), accountId, moduleName, orgId, projectId,
        ambiance.getMetadata().getPipelineIdentifier(), planExecutionId);
  }

  String getModuleName(PipelineExecutionSummaryEntity executionSummaryEntity, String defaultValue) {
    String moduleName = "";
    List<String> modules = executionSummaryEntity.getModules();
    if (!EmptyPredicate.isEmpty(modules)) {
      if (!modules.get(0).equals("pms")) {
        moduleName = modules.get(0);
      } else if (modules.size() > 1) {
        moduleName = modules.get(1);
      }
    }
    return EmptyPredicate.isEmpty(moduleName) ? defaultValue : moduleName;
  }
}
