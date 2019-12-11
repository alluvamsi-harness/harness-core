// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/event/payloads/k8s_utilization_messages.proto

package io.harness.event.payloads;

@javax.annotation.Generated(value = "protoc", comments = "annotations:K8SUtilizationMessages.java.pb.meta")
public final class K8SUtilizationMessages {
  private K8SUtilizationMessages() {}
  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor internal_static_io_harness_event_payloads_Usage_descriptor;
  static final com.google.protobuf.GeneratedMessageV3
      .FieldAccessorTable internal_static_io_harness_event_payloads_Usage_fieldAccessorTable;
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_event_payloads_NodeMetric_descriptor;
  static final com.google.protobuf.GeneratedMessageV3
      .FieldAccessorTable internal_static_io_harness_event_payloads_NodeMetric_fieldAccessorTable;
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_event_payloads_PodMetric_descriptor;
  static final com.google.protobuf.GeneratedMessageV3
      .FieldAccessorTable internal_static_io_harness_event_payloads_PodMetric_fieldAccessorTable;
  static final com.google.protobuf.Descriptors
      .Descriptor internal_static_io_harness_event_payloads_PodMetric_Container_descriptor;
  static final com.google.protobuf.GeneratedMessageV3
      .FieldAccessorTable internal_static_io_harness_event_payloads_PodMetric_Container_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
  static {
    java.lang.String[] descriptorData = {"\n8io/harness/event/payloads/k8s_utilizat"
        + "ion_messages.proto\022\031io.harness.event.pay"
        + "loads\032\036google/protobuf/duration.proto\032\037g"
        + "oogle/protobuf/timestamp.proto\".\n\005Usage\022"
        + "\020\n\010cpu_nano\030\001 \001(\003\022\023\n\013memory_byte\030\002 \001(\003\"\300"
        + "\001\n\nNodeMetric\022\031\n\021cloud_provider_id\030\001 \001(\t"
        + "\022\014\n\004name\030\002 \001(\t\022-\n\ttimestamp\030\003 \001(\0132\032.goog"
        + "le.protobuf.Timestamp\022)\n\006window\030\004 \001(\0132\031."
        + "google.protobuf.Duration\022/\n\005usage\030\005 \001(\0132"
        + " .io.harness.event.payloads.Usage\"\261\002\n\tPo"
        + "dMetric\022\031\n\021cloud_provider_id\030\001 \001(\t\022\014\n\004na"
        + "me\030\002 \001(\t\022\021\n\tnamespace\030\003 \001(\t\022-\n\ttimestamp"
        + "\030\004 \001(\0132\032.google.protobuf.Timestamp\022)\n\006wi"
        + "ndow\030\005 \001(\0132\031.google.protobuf.Duration\022B\n"
        + "\ncontainers\030\006 \003(\0132..io.harness.event.pay"
        + "loads.PodMetric.Container\032J\n\tContainer\022\014"
        + "\n\004name\030\001 \001(\t\022/\n\005usage\030\002 \001(\0132 .io.harness"
        + ".event.payloads.UsageB\002P\001b\006proto3"};
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
            com.google.protobuf.DurationProto.getDescriptor(),
            com.google.protobuf.TimestampProto.getDescriptor(),
        },
        assigner);
    internal_static_io_harness_event_payloads_Usage_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_io_harness_event_payloads_Usage_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_event_payloads_Usage_descriptor,
            new java.lang.String[] {
                "CpuNano",
                "MemoryByte",
            });
    internal_static_io_harness_event_payloads_NodeMetric_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_io_harness_event_payloads_NodeMetric_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_event_payloads_NodeMetric_descriptor,
            new java.lang.String[] {
                "CloudProviderId",
                "Name",
                "Timestamp",
                "Window",
                "Usage",
            });
    internal_static_io_harness_event_payloads_PodMetric_descriptor = getDescriptor().getMessageTypes().get(2);
    internal_static_io_harness_event_payloads_PodMetric_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_event_payloads_PodMetric_descriptor,
            new java.lang.String[] {
                "CloudProviderId",
                "Name",
                "Namespace",
                "Timestamp",
                "Window",
                "Containers",
            });
    internal_static_io_harness_event_payloads_PodMetric_Container_descriptor =
        internal_static_io_harness_event_payloads_PodMetric_descriptor.getNestedTypes().get(0);
    internal_static_io_harness_event_payloads_PodMetric_Container_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_io_harness_event_payloads_PodMetric_Container_descriptor,
            new java.lang.String[] {
                "Name",
                "Usage",
            });
    com.google.protobuf.DurationProto.getDescriptor();
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
