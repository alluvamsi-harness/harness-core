package software.wings.resources;

import static io.harness.data.structure.EmptyPredicate.isEmpty;
import static software.wings.beans.SearchFilter.Builder.aSearchFilter;
import static software.wings.dl.PageRequest.Builder.aPageRequest;

import com.google.inject.Inject;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import software.wings.beans.Application;
import software.wings.beans.ApprovalDetails;
import software.wings.beans.ExecutionArgs;
import software.wings.beans.Graph.Node;
import software.wings.beans.RequiredExecutionArgs;
import software.wings.beans.RestResponse;
import software.wings.beans.SearchFilter;
import software.wings.beans.SearchFilter.Operator;
import software.wings.beans.WorkflowExecution;
import software.wings.beans.WorkflowType;
import software.wings.common.Constants;
import software.wings.dl.PageRequest;
import software.wings.dl.PageResponse;
import software.wings.security.PermissionAttribute.ResourceType;
import software.wings.security.annotations.AuthRule;
import software.wings.service.intfc.AppService;
import software.wings.service.intfc.WorkflowExecutionService;
import software.wings.sm.ExecutionInterrupt;

import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
/**
 * The Class ExecutionResource.
 */
@Api("executions")
@Path("/executions")
@AuthRule(ResourceType.APPLICATION)
@Produces("application/json")
public class ExecutionResource {
  private AppService appService;
  private WorkflowExecutionService workflowExecutionService;

  /**
   * Instantiates a new execution resource.
   *
   * @param appService               the app service
   * @param workflowExecutionService the workflow service
   */
  @Inject
  public ExecutionResource(AppService appService, WorkflowExecutionService workflowExecutionService) {
    this.appService = appService;
    this.workflowExecutionService = workflowExecutionService;
  }

  /**
   * List.
   *
   * @param appIds           the app ids
   * @param envId           the env id
   * @param orchestrationId the orchestration id
   * @param pageRequest     the page request
   * @param includeGraph    the include graph\
   * @return the rest response
   */
  @GET
  @Timed
  @ExceptionMetered
  public RestResponse<PageResponse<WorkflowExecution>> listExecutions(@QueryParam("accountId") String accountId,
      @QueryParam("appId") List<String> appIds, @QueryParam("envId") String envId,
      @QueryParam("orchestrationId") String orchestrationId, @BeanParam PageRequest<WorkflowExecution> pageRequest,
      @DefaultValue("false") @QueryParam("includeGraph") boolean includeGraph,
      @QueryParam("workflowType") List<String> workflowTypes,
      @DefaultValue("false") @QueryParam("includeIndirectExecutions") boolean includeIndirectExecutions) {
    SearchFilter filter = new SearchFilter();
    filter.setFieldName("appId");

    List<String> authorizedAppIds = null;
    if (CollectionUtils.isNotEmpty(appIds)) {
      authorizedAppIds = appIds; //(List<String>) CollectionUtils.intersection(authorizedAppIds, appIds);
    } else {
      PageRequest<Application> applicationPageRequest = aPageRequest()
                                                            .addFieldsIncluded("uuid")
                                                            .addFilter("accountId", Operator.EQ, accountId)
                                                            .withLimit(PageRequest.UNLIMITED)
                                                            .build();
      PageResponse<Application> res = appService.list(applicationPageRequest, false, 0, 0);
      if (isEmpty(res)) {
        return new RestResponse<>(new PageResponse<>());
      }
      authorizedAppIds = res.stream().map(Application::getUuid).collect(Collectors.toList());
    }
    filter.setFieldValues(authorizedAppIds.toArray());
    filter.setOp(Operator.IN);
    pageRequest.addFilter(filter);

    if (pageRequest.getPageSize() > Constants.DEFAULT_RUNTIME_ENTITY_PAGESIZE) {
      pageRequest.setLimit(Constants.DEFAULT_RUNTIME_ENTITY_PAGESIZE_STR);
    }

    if (CollectionUtils.isNotEmpty(workflowTypes)) {
      pageRequest.addFilter(aSearchFilter().withField("workflowType", Operator.IN, workflowTypes.toArray()).build());
    }

    // No need to show child executions unless includeIndirectExecutions is true
    if (!includeIndirectExecutions) {
      pageRequest.addFilter(aSearchFilter().withField("pipelineExecutionId", Operator.NOT_EXISTS).build());
    }

    if (StringUtils.isNotBlank(orchestrationId)) {
      filter = new SearchFilter();
      filter.setFieldName("workflowId");
      filter.setFieldValues(orchestrationId);
      filter.setOp(Operator.EQ);
      pageRequest.addFilter(filter);
    }
    return new RestResponse<>(workflowExecutionService.listExecutions(pageRequest, includeGraph, true, true, false));
  }

  /**
   * Gets execution details.
   *
   * @param appId               the app id
   * @param envId               the env id
   * @param workflowExecutionId the workflow execution id
   * @param expandedGroupIds    the expanded group ids
   * @return the execution details
   */
  @GET
  @Path("{workflowExecutionId}")
  @Timed
  @ExceptionMetered
  public RestResponse<WorkflowExecution> getExecutionDetails(@QueryParam("appId") String appId,
      @QueryParam("envId") String envId, @PathParam("workflowExecutionId") String workflowExecutionId,
      @QueryParam("expandedGroupId") List<String> expandedGroupIds) {
    return new RestResponse<>(
        workflowExecutionService.getExecutionDetails(appId, workflowExecutionId, expandedGroupIds));
  }

  /**
   * Save.
   *
   * @param appId         the app id
   * @param envId         the env id
   * @param executionArgs the execution args
   * @return the rest response
   */
  @POST
  @Timed
  @ExceptionMetered
  public RestResponse<WorkflowExecution> triggerExecution(@QueryParam("appId") String appId,
      @QueryParam("envId") String envId, @QueryParam("pipelineId") String pipelineId, ExecutionArgs executionArgs) {
    if (pipelineId != null && executionArgs.getWorkflowType() == WorkflowType.PIPELINE) {
      executionArgs.setPipelineId(pipelineId);
    }
    return new RestResponse<>(workflowExecutionService.triggerEnvExecution(appId, envId, executionArgs));
  }

  /**
   * Update execution details rest response.
   *
   * @param appId               the app id
   * @param workflowExecutionId the workflow execution id
   * @param executionInterrupt      the execution event
   * @return the rest response
   */
  @PUT
  @Path("{workflowExecutionId}")
  @Timed
  @ExceptionMetered
  public RestResponse<ExecutionInterrupt> triggerWorkflowExecutionInterrupt(@QueryParam("appId") String appId,
      @PathParam("workflowExecutionId") String workflowExecutionId, ExecutionInterrupt executionInterrupt) {
    executionInterrupt.setAppId(appId);
    executionInterrupt.setExecutionUuid(workflowExecutionId);

    return new RestResponse<>(workflowExecutionService.triggerExecutionInterrupt(executionInterrupt));
  }

  /**
   * Trigger execution rest response.
   *
   * @param appId         the app id
   * @param workflowExecutionId    the workflowExecutionId
   * @param executionArgs the Execution Args
   * @return the rest response
   */
  @PUT
  @Path("{workflowExecutionId}/notes")
  @Timed
  @ExceptionMetered
  public RestResponse<Boolean> approveOrRejectExecution(@QueryParam("appId") String appId,
      @PathParam("workflowExecutionId") String workflowExecutionId, ExecutionArgs executionArgs) {
    return new RestResponse<>(workflowExecutionService.updateNotes(appId, workflowExecutionId, executionArgs));
  }

  /**
   * Trigger execution rest response.
   *
   * @param appId         the app id
   * @param workflowExecutionId    the workflowExecutionId
   * @param approvalDetails the Approval User details
   * @return the rest response
   */
  @PUT
  @Path("{workflowExecutionId}/approval")
  @Timed
  @ExceptionMetered
  public RestResponse approveOrRejectExecution(@QueryParam("appId") String appId,
      @PathParam("workflowExecutionId") String workflowExecutionId, ApprovalDetails approvalDetails) {
    return new RestResponse<>(
        workflowExecutionService.approveOrRejectExecution(appId, workflowExecutionId, approvalDetails));
  }

  /**
   * Required args rest response.
   *
   * @param appId         the app id
   * @param envId         the env id
   * @param executionArgs the execution args
   * @return the rest response
   */
  @POST
  @Path("required-args")
  @Timed
  @ExceptionMetered
  public RestResponse<RequiredExecutionArgs> requiredArgs(
      @QueryParam("appId") String appId, @QueryParam("envId") String envId, ExecutionArgs executionArgs) {
    return new RestResponse<>(workflowExecutionService.getRequiredExecutionArgs(appId, envId, executionArgs));
  }

  /**
   * Gets execution node details.
   *
   * @param appId                    the app id
   * @param envId                    the env id
   * @param workflowExecutionId      the workflow execution id
   * @param stateExecutionInstanceId the state execution instance id
   * @return the execution node details
   */
  @GET
  @Path("{workflowExecutionId}/node/{stateExecutionInstanceId}")
  @Timed
  @ExceptionMetered
  public RestResponse<Node> getExecutionNodeDetails(@QueryParam("appId") String appId,
      @QueryParam("envId") String envId, @PathParam("workflowExecutionId") String workflowExecutionId,
      @PathParam("stateExecutionInstanceId") String stateExecutionInstanceId) {
    return new RestResponse<>(
        workflowExecutionService.getExecutionDetailsForNode(appId, workflowExecutionId, stateExecutionInstanceId));
  }
}
