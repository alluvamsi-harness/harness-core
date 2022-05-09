/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cvng.core.utils.template;

import io.harness.cvng.core.beans.params.ProjectParams;
import io.harness.ng.core.template.TemplateApplyRequestDTO;
import io.harness.ng.core.template.TemplateMergeResponseDTO;
import io.harness.remote.client.NGRestUtils;
import io.harness.template.remote.TemplateResourceClient;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TemplateFacade {
  @Inject private TemplateResourceClient templateResourceClient;

  public String resolveYaml(ProjectParams projectParams, String yaml) {
    TemplateMergeResponseDTO templateMergeResponseDTO =
        NGRestUtils.getResponse(templateResourceClient.applyTemplatesOnGivenYaml(projectParams.getAccountIdentifier(),
            projectParams.getOrgIdentifier(), projectParams.getProjectIdentifier(), null, null, null,
            TemplateApplyRequestDTO.builder().originalEntityYaml(yaml).checkForAccess(false).build()));
    return templateMergeResponseDTO.getMergedPipelineYaml();
  }
}
