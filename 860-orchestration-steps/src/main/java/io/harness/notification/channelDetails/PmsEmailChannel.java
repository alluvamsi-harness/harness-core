package io.harness.notification.channelDetails;

import io.harness.Team;
import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.notification.channeldetails.EmailChannel;
import io.harness.notification.channeldetails.NotificationChannel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
@OwnedBy(HarnessTeam.PIPELINE)
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonTypeName(NotificationChannelType.EMAIL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PmsEmailChannel extends PmsNotificationChannel {
  List<String> userGroups;
  List<String> recipients;

  @Override
  public NotificationChannel toNotificationChannel(String accountId, String orgIdentifier, String projectIdentifier,
      String templateId, Map<String, String> templateData) {
    return EmailChannel.builder()
        .accountId(accountId)
        .recipients(recipients)
        .userGroups(userGroups.stream()
                        .map(e -> NotificationChannelUtils.getUserGroups(e, orgIdentifier, projectIdentifier))
                        .collect(Collectors.toList()))
        .team(Team.PIPELINE)
        .templateData(templateData)
        .templateId(templateId)
        .build();
  }
}
