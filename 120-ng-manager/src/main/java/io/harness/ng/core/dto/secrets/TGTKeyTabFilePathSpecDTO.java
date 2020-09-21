package io.harness.ng.core.dto.secrets;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.harness.ng.core.models.TGTGenerationSpec;
import io.harness.ng.core.models.TGTKeyTabFilePathSpec;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("KeyTabFilePath")
public class TGTKeyTabFilePathSpecDTO extends TGTGenerationSpecDTO {
  private String keyPath;

  @Override
  public TGTGenerationSpec toEntity() {
    return TGTKeyTabFilePathSpec.builder().keyPath(getKeyPath()).build();
  }
}
