package software.wings.beans.appmanifest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ManifestFileDTO {
  private String fileName;
  private String fileContent;
  private String applicationManifestId;
  private String accountId;
}
