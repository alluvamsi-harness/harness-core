package software.wings.beans.servicenow;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceNowMetaDTO {
  private String id;
  private String displayName;
}
