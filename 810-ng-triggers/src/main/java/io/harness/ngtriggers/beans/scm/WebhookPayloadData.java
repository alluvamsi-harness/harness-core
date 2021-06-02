package io.harness.ngtriggers.beans.scm;

import io.harness.beans.Repository;
import io.harness.beans.WebhookEvent;
import io.harness.beans.WebhookGitUser;
import io.harness.ngtriggers.beans.entity.TriggerWebhookEvent;
import io.harness.product.ci.scm.proto.ParseWebhookResponse;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class WebhookPayloadData {
  WebhookGitUser webhookGitUser;
  Repository repository;
  WebhookEvent webhookEvent;
  TriggerWebhookEvent originalEvent;
  ParseWebhookResponse parseWebhookResponse;
}
