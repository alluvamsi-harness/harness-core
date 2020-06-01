// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/perpetualtask/k8s/watch/k8s_messages.proto

package io.harness.perpetualtask.k8s.watch;

@javax.annotation.Generated(value = "protoc", comments = "annotations:K8sClusterEventOrBuilder.java.pb.meta")
public interface K8sClusterEventOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.harness.perpetualtask.k8s.watch.K8sClusterEvent)
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
   * <code>string reason = 4[json_name = "reason"];</code>
   * @return The reason.
   */
  java.lang.String getReason();
  /**
   * <code>string reason = 4[json_name = "reason"];</code>
   * @return The bytes for reason.
   */
  com.google.protobuf.ByteString getReasonBytes();

  /**
   * <code>string message = 5[json_name = "message"];</code>
   * @return The message.
   */
  java.lang.String getMessage();
  /**
   * <code>string message = 5[json_name = "message"];</code>
   * @return The bytes for message.
   */
  com.google.protobuf.ByteString getMessageBytes();

  /**
   * <code>string source_component = 6[json_name = "sourceComponent"];</code>
   * @return The sourceComponent.
   */
  java.lang.String getSourceComponent();
  /**
   * <code>string source_component = 6[json_name = "sourceComponent"];</code>
   * @return The bytes for sourceComponent.
   */
  com.google.protobuf.ByteString getSourceComponentBytes();

  /**
   * <code>.io.harness.perpetualtask.k8s.watch.K8sObjectReference involved_object = 7[json_name =
   * "involvedObject"];</code>
   * @return Whether the involvedObject field is set.
   */
  boolean hasInvolvedObject();
  /**
   * <code>.io.harness.perpetualtask.k8s.watch.K8sObjectReference involved_object = 7[json_name =
   * "involvedObject"];</code>
   * @return The involvedObject.
   */
  io.harness.perpetualtask.k8s.watch.K8sObjectReference getInvolvedObject();
  /**
   * <code>.io.harness.perpetualtask.k8s.watch.K8sObjectReference involved_object = 7[json_name =
   * "involvedObject"];</code>
   */
  io.harness.perpetualtask.k8s.watch.K8sObjectReferenceOrBuilder getInvolvedObjectOrBuilder();

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
}
