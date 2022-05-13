package software.wings.beans.container;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HelmChartSpecificationDTO {
  @NotNull private String chartUrl;
  @NotNull private String chartName;
  @NotNull private String chartVersion;
}
