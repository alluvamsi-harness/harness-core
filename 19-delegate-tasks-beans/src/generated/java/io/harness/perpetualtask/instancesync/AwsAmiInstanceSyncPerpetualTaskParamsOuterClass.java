// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/perpetualtask/instancesync/aws_ami_instance_sync_perpetual_task_params.proto

package io.harness.perpetualtask.instancesync;

@javax.annotation.
Generated(value = "protoc", comments = "annotations:AwsAmiInstanceSyncPerpetualTaskParamsOuterClass.java.pb.meta")
public final class AwsAmiInstanceSyncPerpetualTaskParamsOuterClass {
  private AwsAmiInstanceSyncPerpetualTaskParamsOuterClass() {}
  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_io_harness_perpetualtask_instancesync_AwsAmiInstanceSyncPerpetualTaskParams_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_io_harness_perpetualtask_instancesync_AwsAmiInstanceSyncPerpetualTaskParams_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
  static {
    java.lang.String[] descriptorData = {"\nWio/harness/perpetualtask/instancesync/"
        + "aws_ami_instance_sync_perpetual_task_par"
        + "ams.proto\022%io.harness.perpetualtask.inst"
        + "ancesync\"r\n%AwsAmiInstanceSyncPerpetualT"
        + "askParams\022\016\n\006region\030\001 \001(\t\022\021\n\tawsConfig\030\002"
        + " \001(\014\022\017\n\007asgName\030\003 \001(\t\022\025\n\rencryptedData\030\004"
        + " \001(\014B\002P\001b\006proto3"};
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
        descriptorData, new com.google.protobuf.Descriptors.FileDescriptor[] {}, assigner);
    internal_static_io_harness_perpetualtask_instancesync_AwsAmiInstanceSyncPerpetualTaskParams_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_io_harness_perpetualtask_instancesync_AwsAmiInstanceSyncPerpetualTaskParams_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_perpetualtask_instancesync_AwsAmiInstanceSyncPerpetualTaskParams_descriptor,
            new java.lang.String[] {
                "Region",
                "AwsConfig",
                "AsgName",
                "EncryptedData",
            });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
