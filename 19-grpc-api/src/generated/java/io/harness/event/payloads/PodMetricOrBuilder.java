// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/event/payloads/k8s_utilization_messages.proto

package io.harness.event.payloads;

@javax.annotation.Generated(value = "protoc", comments = "annotations:PodMetricOrBuilder.java.pb.meta")
public interface PodMetricOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.harness.event.payloads.PodMetric)
    com.google.protobuf.MessageOrBuilder {
  /**
   * <code>string cloud_provider_id = 1[json_name = "cloudProviderId"];</code>
   * @return The cloudProviderId.
   */
  java.lang.String getCloudProviderId();
  /**
   * <code>string cloud_provider_id = 1[json_name = "cloudProviderId"];</code>
   * @return The bytes for cloudProviderId.
   */
  com.google.protobuf.ByteString getCloudProviderIdBytes();

  /**
   * <code>string name = 2[json_name = "name"];</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <code>string name = 2[json_name = "name"];</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString getNameBytes();

  /**
   * <code>string namespace = 3[json_name = "namespace"];</code>
   * @return The namespace.
   */
  java.lang.String getNamespace();
  /**
   * <code>string namespace = 3[json_name = "namespace"];</code>
   * @return The bytes for namespace.
   */
  com.google.protobuf.ByteString getNamespaceBytes();

  /**
   * <code>.google.protobuf.Timestamp timestamp = 4[json_name = "timestamp"];</code>
   * @return Whether the timestamp field is set.
   */
  boolean hasTimestamp();
  /**
   * <code>.google.protobuf.Timestamp timestamp = 4[json_name = "timestamp"];</code>
   * @return The timestamp.
   */
  com.google.protobuf.Timestamp getTimestamp();
  /**
   * <code>.google.protobuf.Timestamp timestamp = 4[json_name = "timestamp"];</code>
   */
  com.google.protobuf.TimestampOrBuilder getTimestampOrBuilder();

  /**
   * <code>.google.protobuf.Duration window = 5[json_name = "window"];</code>
   * @return Whether the window field is set.
   */
  boolean hasWindow();
  /**
   * <code>.google.protobuf.Duration window = 5[json_name = "window"];</code>
   * @return The window.
   */
  com.google.protobuf.Duration getWindow();
  /**
   * <code>.google.protobuf.Duration window = 5[json_name = "window"];</code>
   */
  com.google.protobuf.DurationOrBuilder getWindowOrBuilder();

  /**
   * <code>string cluster_id = 7[json_name = "clusterId"];</code>
   * @return The clusterId.
   */
  java.lang.String getClusterId();
  /**
   * <code>string cluster_id = 7[json_name = "clusterId"];</code>
   * @return The bytes for clusterId.
   */
  com.google.protobuf.ByteString getClusterIdBytes();

  /**
   * <code>string kube_system_uid = 8[json_name = "kubeSystemUid"];</code>
   * @return The kubeSystemUid.
   */
  java.lang.String getKubeSystemUid();
  /**
   * <code>string kube_system_uid = 8[json_name = "kubeSystemUid"];</code>
   * @return The bytes for kubeSystemUid.
   */
  com.google.protobuf.ByteString getKubeSystemUidBytes();

  /**
   * <code>.io.harness.event.payloads.AggregatedUsage aggregated_usage = 9[json_name = "aggregatedUsage"];</code>
   * @return Whether the aggregatedUsage field is set.
   */
  boolean hasAggregatedUsage();
  /**
   * <code>.io.harness.event.payloads.AggregatedUsage aggregated_usage = 9[json_name = "aggregatedUsage"];</code>
   * @return The aggregatedUsage.
   */
  io.harness.event.payloads.AggregatedUsage getAggregatedUsage();
  /**
   * <code>.io.harness.event.payloads.AggregatedUsage aggregated_usage = 9[json_name = "aggregatedUsage"];</code>
   */
  io.harness.event.payloads.AggregatedUsageOrBuilder getAggregatedUsageOrBuilder();
}
