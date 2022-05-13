package software.wings.utils;

import software.wings.beans.container.HelmChartSpecification;
import software.wings.beans.container.HelmChartSpecificationDTO;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HelmChartSpecificationMapper {
  public HelmChartSpecificationDTO helmChartSpecificationDTO(HelmChartSpecification helmChartSpecification) {
    if (helmChartSpecification == null) {
      return null;
    }
    return HelmChartSpecificationDTO.builder()
        .chartName(helmChartSpecification.getChartName())
        .chartUrl(helmChartSpecification.getChartUrl())
        .chartVersion(helmChartSpecification.getChartVersion())
        .build();
  }
}
