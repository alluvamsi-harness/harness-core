package io.harness.yaml.core.failurestrategy.retry;

import io.harness.annotations.dev.HarnessModule;
import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.annotations.dev.TargetModule;
import io.harness.beans.common.SwaggerConstants;
import io.harness.pms.yaml.ParameterField;
import io.harness.yaml.core.timeout.Timeout;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@OwnedBy(HarnessTeam.PIPELINE)
// TODO this should go to yaml commons
@TargetModule(HarnessModule._884_PMS_COMMONS)
public class RetryFailureSpecConfig {
  @NotNull @ApiModelProperty(dataType = SwaggerConstants.INTEGER_CLASSPATH) ParameterField<Integer> retryCount;
  @NotNull
  @ApiModelProperty(dataType = SwaggerConstants.STRING_LIST_CLASSPATH)
  ParameterField<List<Timeout>> retryIntervals;
  @NotNull OnRetryFailureConfig onRetryFailure;
}
