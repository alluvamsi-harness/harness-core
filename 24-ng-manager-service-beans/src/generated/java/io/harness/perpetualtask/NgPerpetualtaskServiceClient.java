// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/perpetualtask/ng_perpetualtask_service_client.proto

package io.harness.perpetualtask;

@javax.annotation.Generated(value = "protoc", comments = "annotations:NgPerpetualtaskServiceClient.java.pb.meta")
public final class NgPerpetualtaskServiceClient {
  private NgPerpetualtaskServiceClient() {}
  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_perpetualtask_ObtainPerpetualTaskValidationDetailsRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_io_harness_perpetualtask_ObtainPerpetualTaskValidationDetailsRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_perpetualtask_ObtainPerpetualTaskValidationDetailsResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_io_harness_perpetualtask_ObtainPerpetualTaskValidationDetailsResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_perpetualtask_ObtainPerpetualTaskExecutionParamsRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_io_harness_perpetualtask_ObtainPerpetualTaskExecutionParamsRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_perpetualtask_ObtainPerpetualTaskExecutionParamsResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_io_harness_perpetualtask_ObtainPerpetualTaskExecutionParamsResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_perpetualtask_ReportPerpetualTaskStateChangeRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_io_harness_perpetualtask_ReportPerpetualTaskStateChangeRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_perpetualtask_PerpetualTaskExecutionResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3
      .FieldAccessorTable internal_static_io_harness_perpetualtask_PerpetualTaskExecutionResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_perpetualtask_ReportPerpetualTaskStateChangeResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_io_harness_perpetualtask_ReportPerpetualTaskStateChangeResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
  static {
    java.lang.String[] descriptorData = {"\n>io/harness/perpetualtask/ng_perpetualt"
        + "ask_service_client.proto\022\030io.harness.per"
        + "petualtask\032\031google/protobuf/any.proto\032!i"
        + "o/harness/delegate/ng_task.proto\0327io/har"
        + "ness/perpetualtask/ng_perpetual_task_cli"
        + "ent.proto\"\277\001\n+ObtainPerpetualTaskValidat"
        + "ionDetailsRequest\022\035\n\naccount_id\030\001 \001(\tR\ta"
        + "ccountId\022\033\n\ttask_type\030\002 \001(\tR\010taskType\022T\n"
        + "\007context\030\004 \001(\0132:.io.harness.perpetualtas"
        + "k.RemotePerpetualTaskClientContextR\007cont"
        + "ext\"\311\001\n,ObtainPerpetualTaskValidationDet"
        + "ailsResponse\022[\n\022setup_abstractions\030\001 \001(\013"
        + "2,.io.harness.delegate.NgTaskSetupAbstra"
        + "ctionsR\021setupAbstractions\022<\n\007details\030\002 \001"
        + "(\0132\".io.harness.delegate.NgTaskDetailsR\007"
        + "details\"\236\001\n)ObtainPerpetualTaskExecution"
        + "ParamsRequest\022\033\n\ttask_type\030\001 \001(\tR\010taskTy"
        + "pe\022T\n\007context\030\003 \001(\0132:.io.harness.perpetu"
        + "altask.RemotePerpetualTaskClientContextR"
        + "\007context\"o\n*ObtainPerpetualTaskExecution"
        + "ParamsResponse\022A\n\021customized_params\030\001 \001("
        + "\0132\024.google.protobuf.AnyR\020customizedParam"
        + "s\"\274\002\n%ReportPerpetualTaskStateChangeRequ"
        + "est\022\033\n\ttask_type\030\001 \001(\tR\010taskType\022*\n\021perp"
        + "etual_task_id\030\002 \001(\tR\017perpetualTaskId\022d\n\021"
        + "new_task_response\030\003 \001(\01328.io.harness.per"
        + "petualtask.PerpetualTaskExecutionRespons"
        + "eR\017newTaskResponse\022d\n\021old_task_response\030"
        + "\004 \001(\01328.io.harness.perpetualtask.Perpetu"
        + "alTaskExecutionResponseR\017oldTaskResponse"
        + "\"\217\001\n\036PerpetualTaskExecutionResponse\022\035\n\nt"
        + "ask_state\030\001 \001(\tR\ttaskState\022#\n\rresponse_c"
        + "ode\030\002 \001(\005R\014responseCode\022)\n\020response_mess"
        + "age\030\003 \001(\tR\017responseMessage\"(\n&ReportPerp"
        + "etualTaskStateChangeResponseB\002P\001b\006proto3"};
    descriptor = com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
            com.google.protobuf.AnyProto.getDescriptor(),
            io.harness.delegate.NgTask.getDescriptor(),
            io.harness.perpetualtask.NgPerpetualTaskClient.getDescriptor(),
        });
    internal_static_io_harness_perpetualtask_ObtainPerpetualTaskValidationDetailsRequest_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_io_harness_perpetualtask_ObtainPerpetualTaskValidationDetailsRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_perpetualtask_ObtainPerpetualTaskValidationDetailsRequest_descriptor,
            new java.lang.String[] {
                "AccountId",
                "TaskType",
                "Context",
            });
    internal_static_io_harness_perpetualtask_ObtainPerpetualTaskValidationDetailsResponse_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_io_harness_perpetualtask_ObtainPerpetualTaskValidationDetailsResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_perpetualtask_ObtainPerpetualTaskValidationDetailsResponse_descriptor,
            new java.lang.String[] {
                "SetupAbstractions",
                "Details",
            });
    internal_static_io_harness_perpetualtask_ObtainPerpetualTaskExecutionParamsRequest_descriptor =
        getDescriptor().getMessageTypes().get(2);
    internal_static_io_harness_perpetualtask_ObtainPerpetualTaskExecutionParamsRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_perpetualtask_ObtainPerpetualTaskExecutionParamsRequest_descriptor,
            new java.lang.String[] {
                "TaskType",
                "Context",
            });
    internal_static_io_harness_perpetualtask_ObtainPerpetualTaskExecutionParamsResponse_descriptor =
        getDescriptor().getMessageTypes().get(3);
    internal_static_io_harness_perpetualtask_ObtainPerpetualTaskExecutionParamsResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_perpetualtask_ObtainPerpetualTaskExecutionParamsResponse_descriptor,
            new java.lang.String[] {
                "CustomizedParams",
            });
    internal_static_io_harness_perpetualtask_ReportPerpetualTaskStateChangeRequest_descriptor =
        getDescriptor().getMessageTypes().get(4);
    internal_static_io_harness_perpetualtask_ReportPerpetualTaskStateChangeRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_perpetualtask_ReportPerpetualTaskStateChangeRequest_descriptor,
            new java.lang.String[] {
                "TaskType",
                "PerpetualTaskId",
                "NewTaskResponse",
                "OldTaskResponse",
            });
    internal_static_io_harness_perpetualtask_PerpetualTaskExecutionResponse_descriptor =
        getDescriptor().getMessageTypes().get(5);
    internal_static_io_harness_perpetualtask_PerpetualTaskExecutionResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_perpetualtask_PerpetualTaskExecutionResponse_descriptor,
            new java.lang.String[] {
                "TaskState",
                "ResponseCode",
                "ResponseMessage",
            });
    internal_static_io_harness_perpetualtask_ReportPerpetualTaskStateChangeResponse_descriptor =
        getDescriptor().getMessageTypes().get(6);
    internal_static_io_harness_perpetualtask_ReportPerpetualTaskStateChangeResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_perpetualtask_ReportPerpetualTaskStateChangeResponse_descriptor,
            new java.lang.String[] {});
    com.google.protobuf.AnyProto.getDescriptor();
    io.harness.delegate.NgTask.getDescriptor();
    io.harness.perpetualtask.NgPerpetualTaskClient.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
