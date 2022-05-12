package io.harness.aws.cf;

import com.amazonaws.services.cloudformation.model.Parameter;
import com.amazonaws.services.cloudformation.model.Tag;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeployStackRequest {
  private String stackName;
  private String templateBody;
  private String templateURL;
  private List<Parameter> parameters;
  private List<String> capabilities;
  private String roleARN;
  private List<Tag> tags;
}
