package software.wings.scheduler;

import static software.wings.scheduler.ServiceNowApprovalJob.scheduleJob;
import static software.wings.service.ApprovalUtils.checkApproval;

import com.google.inject.Inject;

import io.harness.beans.ExecutionStatus;
import io.harness.scheduler.PersistentScheduler;
import io.harness.waiter.WaitNotifyEngine;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import software.wings.api.jira.JiraExecutionData;
import software.wings.beans.approval.JiraApprovalParams;
import software.wings.service.impl.JiraHelperService;
import software.wings.service.intfc.StateExecutionService;
import software.wings.service.intfc.WorkflowExecutionService;
import software.wings.service.intfc.servicenow.ServiceNowService;

@Slf4j
public class JiraPollingJob implements Job {
  public static final String GROUP = "JIRA_POLLING_CRON_JOB";
  private static final String CONNECTOR_ID = "connectorId";
  private static final String ISSUE_ID = "issueId";
  private static final String APPROVAL_ID = "approvalId";
  private static final String WORKFLOW_EXECUTION_ID = "workflowExecutionId";
  private static final String STATE_EXECUTION_INSTANCE_ID = "stateExecutionInstanceId";
  private static final String ACCOUNT_ID_KEY = "accountId";
  private static final String APP_ID_KEY = "appId";

  @Inject private JiraHelperService jiraHelperService;
  @Inject private ServiceNowService serviceNowService;
  @Inject WorkflowExecutionService workflowExecutionService;
  @Inject StateExecutionService stateExecutionService;
  @Inject WaitNotifyEngine waitNotifyEngine;

  public static void doPollingJob(PersistentScheduler jobScheduler, JiraApprovalParams jiraApprovalParams,
      String approvalExecutionId, String accountId, String appId, String workflowExecutionId,
      String stateExecutionInstanceId) {
    JobDetail job = JobBuilder.newJob(JiraPollingJob.class)
                        .withIdentity(approvalExecutionId, GROUP)
                        .usingJobData(CONNECTOR_ID, jiraApprovalParams.getJiraConnectorId())
                        .usingJobData(ACCOUNT_ID_KEY, accountId)
                        .usingJobData(APP_ID_KEY, appId)
                        .usingJobData(ISSUE_ID, jiraApprovalParams.getIssueId())
                        .usingJobData(APPROVAL_ID, approvalExecutionId)
                        .usingJobData("approvalField", jiraApprovalParams.getApprovalField())
                        .usingJobData("approvalValue", jiraApprovalParams.getApprovalValue())
                        .usingJobData("rejectionField", jiraApprovalParams.getRejectionField())
                        .usingJobData("rejectionValue", jiraApprovalParams.getRejectionValue())
                        .usingJobData(WORKFLOW_EXECUTION_ID, workflowExecutionId)
                        .usingJobData(STATE_EXECUTION_INSTANCE_ID, stateExecutionInstanceId)
                        .build();
    logger.info("Issue Id in the JiraPollingJob = {}", jiraApprovalParams.getIssueId());
    scheduleJob(jobScheduler, approvalExecutionId, job, GROUP);
  }

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    String connectorId = jobExecutionContext.getMergedJobDataMap().getString(CONNECTOR_ID);
    String accountId = jobExecutionContext.getMergedJobDataMap().getString(ACCOUNT_ID_KEY);
    String appId = jobExecutionContext.getMergedJobDataMap().getString(APP_ID_KEY);
    String approvalId = jobExecutionContext.getMergedJobDataMap().getString(APPROVAL_ID);
    String issueId = jobExecutionContext.getMergedJobDataMap().getString(ISSUE_ID);
    String approvalField = jobExecutionContext.getMergedJobDataMap().getString("approvalField");
    String approvalValue = jobExecutionContext.getMergedJobDataMap().getString("approvalValue");
    String rejectionField = jobExecutionContext.getMergedJobDataMap().getString("rejectionField");
    String rejectionValue = jobExecutionContext.getMergedJobDataMap().getString("rejectionValue");
    String workflowExecutionId = jobExecutionContext.getMergedJobDataMap().getString(WORKFLOW_EXECUTION_ID);
    String stateExecutionInstanceId = jobExecutionContext.getMergedJobDataMap().getString(STATE_EXECUTION_INSTANCE_ID);

    logger.info(
        "Polling Approval Status for approvalId {}, issueId {}, approvalField {}, approvalValue {} , rejectionField {}, RejectionValue {}",
        approvalId, issueId, approvalField, approvalValue, rejectionField, rejectionValue);

    JiraExecutionData jiraExecutionData = null;
    try {
      jiraExecutionData = jiraHelperService.getApprovalStatus(
          connectorId, accountId, appId, issueId, approvalField, approvalValue, rejectionField, rejectionValue);
    } catch (Exception ex) {
      logger.warn(
          "Error occurred while polling JIRA status. Continuing to poll next minute. approvalId: {}, workflowExecutionId: {} , issueId: {}",
          approvalId, workflowExecutionId, issueId, ex);
      return;
    }

    ExecutionStatus issueStatus = jiraExecutionData.getExecutionStatus();
    logger.info("Issue: {} Status from JIRA: {} Current Status {} for approvalId: {}, workflowExecutionId: {} ",
        issueId, issueStatus, jiraExecutionData.getCurrentStatus(), approvalId, workflowExecutionId);

    checkApproval(workflowExecutionService, stateExecutionService, waitNotifyEngine, appId, approvalId, issueId,
        workflowExecutionId, stateExecutionInstanceId, jiraExecutionData.getCurrentStatus(),
        jiraExecutionData.getErrorMessage(), issueStatus);
  }

  public static void deleteJob(PersistentScheduler jobScheduler, String approvalId) {
    jobScheduler.deleteJob(approvalId, GROUP);
  }
}
