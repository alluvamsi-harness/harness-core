// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/perpetualtask/instancesync/aws_code_deploy_instance_sync_perpetual_task_params.proto

package io.harness.perpetualtask.instancesync;

@javax.annotation.Generated(
    value = "protoc", comments = "annotations:AwsCodeDeployInstanceSyncPerpetualTaskParamsOuterClass.java.pb.meta")
public final class AwsCodeDeployInstanceSyncPerpetualTaskParamsOuterClass {
  private AwsCodeDeployInstanceSyncPerpetualTaskParamsOuterClass() {}
  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_io_harness_perpetualtask_instancesync_AwsCodeDeployInstanceSyncPerpetualTaskParams_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_io_harness_perpetualtask_instancesync_AwsCodeDeployInstanceSyncPerpetualTaskParams_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
  static {
    java.lang.String[] descriptorData = {"\n_io/harness/perpetualtask/instancesync/"
        + "aws_code_deploy_instance_sync_perpetual_"
        + "task_params.proto\022%io.harness.perpetualt"
        + "ask.instancesync\"\244\001\n,AwsCodeDeployInstan"
        + "ceSyncPerpetualTaskParams\022\026\n\006region\030\001 \001("
        + "\tR\006region\022\026\n\006filter\030\002 \001(\014R\006filter\022\035\n\naws"
        + "_config\030\003 \001(\014R\tawsConfig\022%\n\016encrypted_da"
        + "ta\030\004 \001(\014R\rencryptedDataB\002P\001b\006proto3"};
    descriptor = com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
        descriptorData, new com.google.protobuf.Descriptors.FileDescriptor[] {});
    internal_static_io_harness_perpetualtask_instancesync_AwsCodeDeployInstanceSyncPerpetualTaskParams_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_io_harness_perpetualtask_instancesync_AwsCodeDeployInstanceSyncPerpetualTaskParams_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_perpetualtask_instancesync_AwsCodeDeployInstanceSyncPerpetualTaskParams_descriptor,
            new java.lang.String[] {
                "Region",
                "Filter",
                "AwsConfig",
                "EncryptedData",
            });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
