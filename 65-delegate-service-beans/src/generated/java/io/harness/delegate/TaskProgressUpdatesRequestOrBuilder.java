// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/delegate/delegate_service.proto

package io.harness.delegate;

@javax.annotation.Generated(value = "protoc", comments = "annotations:TaskProgressUpdatesRequestOrBuilder.java.pb.meta")
public interface TaskProgressUpdatesRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.harness.delegate.TaskProgressUpdatesRequest)
    com.google.protobuf.MessageOrBuilder {
  /**
   * <code>.io.harness.delegate.TaskId task_id = 1[json_name = "taskId"];</code>
   * @return Whether the taskId field is set.
   */
  boolean hasTaskId();
  /**
   * <code>.io.harness.delegate.TaskId task_id = 1[json_name = "taskId"];</code>
   * @return The taskId.
   */
  io.harness.delegate.TaskId getTaskId();
  /**
   * <code>.io.harness.delegate.TaskId task_id = 1[json_name = "taskId"];</code>
   */
  io.harness.delegate.TaskIdOrBuilder getTaskIdOrBuilder();
}
