// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/delegate/ng_task.proto

package io.harness.delegate;

@javax.annotation.Generated(value = "protoc", comments = "annotations:NgTask.java.pb.meta")
public final class NgTask {
  private NgTask() {}
  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor internal_static_io_harness_delegate_NgTaskId_descriptor;
  static final com.google.protobuf.GeneratedMessageV3
      .FieldAccessorTable internal_static_io_harness_delegate_NgTaskId_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor internal_static_io_harness_delegate_NgTaskType_descriptor;
  static final com.google.protobuf.GeneratedMessageV3
      .FieldAccessorTable internal_static_io_harness_delegate_NgTaskType_fieldAccessorTable;
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_delegate_NgTaskSetupAbstractions_descriptor;
  static final com.google.protobuf.GeneratedMessageV3
      .FieldAccessorTable internal_static_io_harness_delegate_NgTaskSetupAbstractions_fieldAccessorTable;
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_delegate_NgTaskSetupAbstractions_ValuesEntry_descriptor;
  static final com.google.protobuf.GeneratedMessageV3
      .FieldAccessorTable internal_static_io_harness_delegate_NgTaskSetupAbstractions_ValuesEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor internal_static_io_harness_delegate_NgTaskDetails_descriptor;
  static final com.google.protobuf.GeneratedMessageV3
      .FieldAccessorTable internal_static_io_harness_delegate_NgTaskDetails_fieldAccessorTable;
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_delegate_NgTaskDetails_ExpressionsEntry_descriptor;
  static final com.google.protobuf.GeneratedMessageV3
      .FieldAccessorTable internal_static_io_harness_delegate_NgTaskDetails_ExpressionsEntry_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
  static {
    java.lang.String[] descriptorData = {"\n!io/harness/delegate/ng_task.proto\022\023io."
        + "harness.delegate\032\036google/protobuf/durati"
        + "on.proto\"\032\n\010NgTaskId\022\016\n\002id\030\001 \001(\tR\002id\" \n\n"
        + "NgTaskType\022\022\n\004type\030\001 \001(\tR\004type\"\246\001\n\027NgTas"
        + "kSetupAbstractions\022P\n\006values\030\001 \003(\01328.io."
        + "harness.delegate.NgTaskSetupAbstractions"
        + ".ValuesEntryR\006values\0329\n\013ValuesEntry\022\020\n\003k"
        + "ey\030\001 \001(\tR\003key\022\024\n\005value\030\002 \001(\tR\005value:\0028\001\""
        + "\313\003\n\rNgTaskDetails\0223\n\004mode\030\001 \001(\0162\037.io.har"
        + "ness.delegate.NgTaskModeR\004mode\0223\n\004type\030\002"
        + " \001(\0132\037.io.harness.delegate.NgTaskTypeR\004t"
        + "ype\022)\n\017kryo_parameters\030\003 \001(\014H\000R\016kryoPara"
        + "meters\022F\n\021execution_timeout\030\004 \001(\0132\031.goog"
        + "le.protobuf.DurationR\020executionTimeout\022U"
        + "\n\013expressions\030\005 \003(\01323.io.harness.delegat"
        + "e.NgTaskDetails.ExpressionsEntryR\013expres"
        + "sions\0228\n\030expression_functor_token\030\006 \001(\003R"
        + "\026expressionFunctorToken\032>\n\020ExpressionsEn"
        + "try\022\020\n\003key\030\001 \001(\tR\003key\022\024\n\005value\030\002 \001(\tR\005va"
        + "lue:\0028\001B\014\n\nparameters*7\n\nNgTaskMode\022\024\n\020M"
        + "ODE_UNSPECIFIED\020\000\022\010\n\004SYNC\020\001\022\t\n\005ASYNC\020\002B\002"
        + "P\001b\006proto3"};
    descriptor = com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
            com.google.protobuf.DurationProto.getDescriptor(),
        });
    internal_static_io_harness_delegate_NgTaskId_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_io_harness_delegate_NgTaskId_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_delegate_NgTaskId_descriptor,
            new java.lang.String[] {
                "Id",
            });
    internal_static_io_harness_delegate_NgTaskType_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_io_harness_delegate_NgTaskType_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_delegate_NgTaskType_descriptor,
            new java.lang.String[] {
                "Type",
            });
    internal_static_io_harness_delegate_NgTaskSetupAbstractions_descriptor = getDescriptor().getMessageTypes().get(2);
    internal_static_io_harness_delegate_NgTaskSetupAbstractions_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_delegate_NgTaskSetupAbstractions_descriptor,
            new java.lang.String[] {
                "Values",
            });
    internal_static_io_harness_delegate_NgTaskSetupAbstractions_ValuesEntry_descriptor =
        internal_static_io_harness_delegate_NgTaskSetupAbstractions_descriptor.getNestedTypes().get(0);
    internal_static_io_harness_delegate_NgTaskSetupAbstractions_ValuesEntry_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_delegate_NgTaskSetupAbstractions_ValuesEntry_descriptor,
            new java.lang.String[] {
                "Key",
                "Value",
            });
    internal_static_io_harness_delegate_NgTaskDetails_descriptor = getDescriptor().getMessageTypes().get(3);
    internal_static_io_harness_delegate_NgTaskDetails_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_delegate_NgTaskDetails_descriptor,
            new java.lang.String[] {
                "Mode",
                "Type",
                "KryoParameters",
                "ExecutionTimeout",
                "Expressions",
                "ExpressionFunctorToken",
                "Parameters",
            });
    internal_static_io_harness_delegate_NgTaskDetails_ExpressionsEntry_descriptor =
        internal_static_io_harness_delegate_NgTaskDetails_descriptor.getNestedTypes().get(0);
    internal_static_io_harness_delegate_NgTaskDetails_ExpressionsEntry_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_delegate_NgTaskDetails_ExpressionsEntry_descriptor,
            new java.lang.String[] {
                "Key",
                "Value",
            });
    com.google.protobuf.DurationProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
