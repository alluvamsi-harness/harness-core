package io.harness.steps.customstage;

import io.harness.filters.GenericStageFilterJsonCreatorV2;
import io.harness.pms.filter.creation.FilterCreationResponse;
import io.harness.pms.pipeline.filter.PipelineFilter;
import io.harness.pms.sdk.core.filter.creation.beans.FilterCreationContext;
import io.harness.steps.StepSpecTypeConstants;

import java.util.Collections;
import java.util.Set;

public class CustomStageFilterCreator extends GenericStageFilterJsonCreatorV2<CustomStageNode> {
  @Override
  public Set<String> getSupportedStageTypes() {
    return Collections.singleton(StepSpecTypeConstants.CUSTOM_STAGE);
  }

  @Override
  public PipelineFilter getFilter(FilterCreationContext filterCreationContext, CustomStageNode stageNode) {
    // TODO: @vaibhav.si handle filters
    return null;
  }

  @Override
  public Class<CustomStageNode> getFieldClass() {
    return CustomStageNode.class;
  }

  @Override
  public FilterCreationResponse handleNode(FilterCreationContext filterCreationContext, CustomStageNode stageNode) {
    // TODO: @vaibhav.si handle
    return FilterCreationResponse.builder().build();
  }
}
