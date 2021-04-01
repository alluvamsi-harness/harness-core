package io.harness.notification.channeldetails;

import static io.harness.annotations.dev.HarnessTeam.PL;
import static io.harness.data.structure.UUIDGenerator.generateUuid;

import io.harness.NotificationRequest;
import io.harness.Team;
import io.harness.annotations.dev.OwnedBy;
import io.harness.data.structure.CollectionUtils;

import com.google.inject.Inject;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@OwnedBy(PL)
@Data
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__({ @Inject }))
@EqualsAndHashCode(callSuper = true)
public class SlackChannel extends NotificationChannel {
  List<String> webhookUrls;

  @Builder
  public SlackChannel(String accountId, List<String> userGroupIds, List<NotificationRequest.UserGroup> userGroups,
      String templateId, Map<String, String> templateData, Team team, List<String> webhookUrls) {
    super(accountId, userGroupIds, userGroups, templateId, templateData, team);
    this.webhookUrls = webhookUrls;
  }

  @Override
  public NotificationRequest buildNotificationRequest() {
    NotificationRequest.Builder builder = NotificationRequest.newBuilder();
    String notificationId = generateUuid();
    return builder.setId(notificationId)
        .setAccountId(accountId)
        .setTeam(team)
        .setSlack(builder.getSlackBuilder()
                      .addAllSlackWebHookUrls(webhookUrls)
                      .setTemplateId(templateId)
                      .putAllTemplateData(templateData)
                      .addAllUserGroupIds(CollectionUtils.emptyIfNull(userGroupIds))
                      .addAllUserGroup(CollectionUtils.emptyIfNull(userGroups)))
        .build();
  }
}
