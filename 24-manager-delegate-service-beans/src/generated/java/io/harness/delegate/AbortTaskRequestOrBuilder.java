// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/delegate/ng_delegate_task.proto

package io.harness.delegate;

@javax.annotation.Generated(value = "protoc", comments = "annotations:AbortTaskRequestOrBuilder.java.pb.meta")
public interface AbortTaskRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.harness.delegate.AbortTaskRequest)
    com.google.protobuf.MessageOrBuilder {
  /**
   * <code>.io.harness.delegate.NgAccountId account_id = 1[json_name = "accountId"];</code>
   * @return Whether the accountId field is set.
   */
  boolean hasAccountId();
  /**
   * <code>.io.harness.delegate.NgAccountId account_id = 1[json_name = "accountId"];</code>
   * @return The accountId.
   */
  io.harness.delegate.NgAccountId getAccountId();
  /**
   * <code>.io.harness.delegate.NgAccountId account_id = 1[json_name = "accountId"];</code>
   */
  io.harness.delegate.NgAccountIdOrBuilder getAccountIdOrBuilder();

  /**
   * <code>.io.harness.delegate.NgTaskId task_id = 2[json_name = "taskId"];</code>
   * @return Whether the taskId field is set.
   */
  boolean hasTaskId();
  /**
   * <code>.io.harness.delegate.NgTaskId task_id = 2[json_name = "taskId"];</code>
   * @return The taskId.
   */
  io.harness.delegate.NgTaskId getTaskId();
  /**
   * <code>.io.harness.delegate.NgTaskId task_id = 2[json_name = "taskId"];</code>
   */
  io.harness.delegate.NgTaskIdOrBuilder getTaskIdOrBuilder();
}
