package software.wings.search.entities.workflow;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import software.wings.beans.Workflow;
import software.wings.search.framework.changestreams.ChangeEvent;
import software.wings.search.framework.changestreams.ChangeEvent.ChangeEventBuilder;
import software.wings.search.framework.changestreams.ChangeType;

public class WorkflowEntityTestUtils {
  public static Workflow createWorkflow(
      String accountId, String appId, String workflowId, String envId, String workflowName) {
    Workflow workflow = new Workflow();
    workflow.setUuid(workflowId);
    workflow.setAppId(appId);
    workflow.setAccountId(accountId);
    workflow.setName(workflowName);
    workflow.setEnvId(envId);
    return workflow;
  }

  private static DBObject getWorkflowChanges() {
    BasicDBObject basicDBObject = new BasicDBObject();
    basicDBObject.put("name", "edited_name");
    basicDBObject.put("appId", "appId");
    basicDBObject.put("envId", "envId");

    return basicDBObject;
  }

  public static ChangeEvent createWorkflowChangeEvent(Workflow workflow, ChangeType changeType) {
    ChangeEventBuilder changeEventBuilder = ChangeEvent.builder();
    changeEventBuilder = changeEventBuilder.changeType(changeType)
                             .fullDocument(workflow)
                             .token("token")
                             .uuid(workflow.getUuid())
                             .entityType(Workflow.class);

    if (changeType.equals(ChangeType.UPDATE)) {
      changeEventBuilder = changeEventBuilder.changes(getWorkflowChanges());
    }

    return changeEventBuilder.build();
  }
}
