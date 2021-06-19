package io.harness.helper;

import static io.harness.data.structure.EmptyPredicate.isNotEmpty;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.delegate.beans.connector.scm.GitAuthType;
import io.harness.delegate.beans.connector.scm.ScmConnector;
import io.harness.delegate.beans.connector.scm.adapter.ScmConnectorMapper;
import io.harness.delegate.beans.connector.scm.genericgitconnector.GitConfigDTO;
import io.harness.delegate.beans.executioncapability.ExecutionCapability;
import io.harness.delegate.beans.executioncapability.HttpConnectionExecutionCapability;
import io.harness.delegate.beans.executioncapability.SelectorCapability;
import io.harness.exception.InvalidRequestException;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
@OwnedBy(HarnessTeam.DX)
public class ScmGitCapabilityHelper {
  public List<ExecutionCapability> getHttpConnectionCapability(ScmConnector scmConnector) {
    GitConfigDTO gitConfigDTO = ScmConnectorMapper.toGitConfigDTO(scmConnector);
    if (gitConfigDTO.getGitAuthType().equals(GitAuthType.HTTP)) {
      List<ExecutionCapability> executionCapabilities = new ArrayList<>();
      executionCapabilities.add(HttpConnectionExecutionCapability.builder().url(scmConnector.getUrl()).build());
      if (isNotEmpty(gitConfigDTO.getDelegateSelectors())) {
        executionCapabilities.add(SelectorCapability.builder().selectors(gitConfigDTO.getDelegateSelectors()).build());
      }
      return executionCapabilities;
    }

    throw new InvalidRequestException("HTTP authentication is required");
  }
}
