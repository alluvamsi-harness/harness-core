package io.harness.dto;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.eraro.ResponseMessage;
import io.harness.exception.FailureType;

import java.util.EnumSet;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@OwnedBy(HarnessTeam.PIPELINE)
@Value
@Builder
public class FailureInfoDTO {
  String message;
  EnumSet<FailureType> failureTypeList;
  List<ResponseMessage> responseMessages;
}
