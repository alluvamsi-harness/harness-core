// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/delegate/ng_delegate_task.proto

package io.harness.delegate;

@javax.annotation.Generated(value = "protoc", comments = "annotations:SendTaskAsyncRequestOrBuilder.java.pb.meta")
public interface SendTaskAsyncRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.harness.delegate.SendTaskAsyncRequest)
    com.google.protobuf.MessageOrBuilder {
  /**
   * <code>.io.harness.delegate.AccountId account_id = 2[json_name = "accountId"];</code>
   * @return Whether the accountId field is set.
   */
  boolean hasAccountId();
  /**
   * <code>.io.harness.delegate.AccountId account_id = 2[json_name = "accountId"];</code>
   * @return The accountId.
   */
  io.harness.delegate.AccountId getAccountId();
  /**
   * <code>.io.harness.delegate.AccountId account_id = 2[json_name = "accountId"];</code>
   */
  io.harness.delegate.AccountIdOrBuilder getAccountIdOrBuilder();

  /**
   * <code>.io.harness.delegate.TaskSetupAbstractions setup_abstractions = 3[json_name = "setupAbstractions"];</code>
   * @return Whether the setupAbstractions field is set.
   */
  boolean hasSetupAbstractions();
  /**
   * <code>.io.harness.delegate.TaskSetupAbstractions setup_abstractions = 3[json_name = "setupAbstractions"];</code>
   * @return The setupAbstractions.
   */
  io.harness.delegate.TaskSetupAbstractions getSetupAbstractions();
  /**
   * <code>.io.harness.delegate.TaskSetupAbstractions setup_abstractions = 3[json_name = "setupAbstractions"];</code>
   */
  io.harness.delegate.TaskSetupAbstractionsOrBuilder getSetupAbstractionsOrBuilder();

  /**
   * <code>.io.harness.delegate.TaskDetails details = 4[json_name = "details"];</code>
   * @return Whether the details field is set.
   */
  boolean hasDetails();
  /**
   * <code>.io.harness.delegate.TaskDetails details = 4[json_name = "details"];</code>
   * @return The details.
   */
  io.harness.delegate.TaskDetails getDetails();
  /**
   * <code>.io.harness.delegate.TaskDetails details = 4[json_name = "details"];</code>
   */
  io.harness.delegate.TaskDetailsOrBuilder getDetailsOrBuilder();
}
