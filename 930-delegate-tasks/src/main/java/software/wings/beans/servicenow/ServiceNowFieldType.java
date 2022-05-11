package software.wings.beans.servicenow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

public enum ServiceNowFieldType {
  DATE_TIME(Arrays.asList("glide_date_time", "due_date", "glide_date", "glide_time")),
  INTEGER(Collections.singletonList("integer")),
  BOOLEAN(Collections.singletonList("boolean")),
  STRING(Collections.singletonList("string"));
  @Getter private List<String> snowInternalTypes;
  ServiceNowFieldType(List<String> types) {
    snowInternalTypes = types;
  }
}
