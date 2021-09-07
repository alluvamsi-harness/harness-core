package software.wings.helpers.ext.azure.devops;

import static io.harness.annotations.dev.HarnessModule._960_API_SERVICES;
import static io.harness.annotations.dev.HarnessTeam.CDC;

import io.harness.annotations.dev.OwnedBy;
import io.harness.annotations.dev.TargetModule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@OwnedBy(CDC)
@TargetModule(_960_API_SERVICES)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureArtifactsPackageFileInfo {
  private String name;
  private long size;

  public AzureArtifactsPackageFileInfo(String name, long size) {
    this.name = name;
    this.size = size;
  }
}
