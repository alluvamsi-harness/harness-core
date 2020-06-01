// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/perpetualtask/k8s/watch/k8s_messages.proto

package io.harness.perpetualtask.k8s.watch;

@javax.annotation.Generated(value = "protoc", comments = "annotations:K8SClusterSyncEventOrBuilder.java.pb.meta")
public interface K8SClusterSyncEventOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.harness.perpetualtask.k8s.watch.K8SClusterSyncEvent)
    com.google.protobuf.MessageOrBuilder {
  /**
   * <code>string cluster_id = 1[json_name = "clusterId"];</code>
   * @return The clusterId.
   */
  java.lang.String getClusterId();
  /**
   * <code>string cluster_id = 1[json_name = "clusterId"];</code>
   * @return The bytes for clusterId.
   */
  com.google.protobuf.ByteString getClusterIdBytes();

  /**
   * <code>string cluster_name = 2[json_name = "clusterName"];</code>
   * @return The clusterName.
   */
  java.lang.String getClusterName();
  /**
   * <code>string cluster_name = 2[json_name = "clusterName"];</code>
   * @return The bytes for clusterName.
   */
  com.google.protobuf.ByteString getClusterNameBytes();

  /**
   * <code>string cloud_provider_id = 3[json_name = "cloudProviderId"];</code>
   * @return The cloudProviderId.
   */
  java.lang.String getCloudProviderId();
  /**
   * <code>string cloud_provider_id = 3[json_name = "cloudProviderId"];</code>
   * @return The bytes for cloudProviderId.
   */
  com.google.protobuf.ByteString getCloudProviderIdBytes();

  /**
   * <code>repeated string active_pod_uids = 4[json_name = "activePodUids"];</code>
   * @return A list containing the activePodUids.
   */
  java.util.List<java.lang.String> getActivePodUidsList();
  /**
   * <code>repeated string active_pod_uids = 4[json_name = "activePodUids"];</code>
   * @return The count of activePodUids.
   */
  int getActivePodUidsCount();
  /**
   * <code>repeated string active_pod_uids = 4[json_name = "activePodUids"];</code>
   * @param index The index of the element to return.
   * @return The activePodUids at the given index.
   */
  java.lang.String getActivePodUids(int index);
  /**
   * <code>repeated string active_pod_uids = 4[json_name = "activePodUids"];</code>
   * @param index The index of the value to return.
   * @return The bytes of the activePodUids at the given index.
   */
  com.google.protobuf.ByteString getActivePodUidsBytes(int index);

  /**
   * <code>repeated string active_node_uids = 5[json_name = "activeNodeUids"];</code>
   * @return A list containing the activeNodeUids.
   */
  java.util.List<java.lang.String> getActiveNodeUidsList();
  /**
   * <code>repeated string active_node_uids = 5[json_name = "activeNodeUids"];</code>
   * @return The count of activeNodeUids.
   */
  int getActiveNodeUidsCount();
  /**
   * <code>repeated string active_node_uids = 5[json_name = "activeNodeUids"];</code>
   * @param index The index of the element to return.
   * @return The activeNodeUids at the given index.
   */
  java.lang.String getActiveNodeUids(int index);
  /**
   * <code>repeated string active_node_uids = 5[json_name = "activeNodeUids"];</code>
   * @param index The index of the value to return.
   * @return The bytes of the activeNodeUids at the given index.
   */
  com.google.protobuf.ByteString getActiveNodeUidsBytes(int index);

  /**
   * <code>.google.protobuf.Timestamp last_processed_timestamp = 6[json_name = "lastProcessedTimestamp"];</code>
   * @return Whether the lastProcessedTimestamp field is set.
   */
  boolean hasLastProcessedTimestamp();
  /**
   * <code>.google.protobuf.Timestamp last_processed_timestamp = 6[json_name = "lastProcessedTimestamp"];</code>
   * @return The lastProcessedTimestamp.
   */
  com.google.protobuf.Timestamp getLastProcessedTimestamp();
  /**
   * <code>.google.protobuf.Timestamp last_processed_timestamp = 6[json_name = "lastProcessedTimestamp"];</code>
   */
  com.google.protobuf.TimestampOrBuilder getLastProcessedTimestampOrBuilder();

  /**
   * <code>string kube_system_uid = 7[json_name = "kubeSystemUid"];</code>
   * @return The kubeSystemUid.
   */
  java.lang.String getKubeSystemUid();
  /**
   * <code>string kube_system_uid = 7[json_name = "kubeSystemUid"];</code>
   * @return The bytes for kubeSystemUid.
   */
  com.google.protobuf.ByteString getKubeSystemUidBytes();
}
