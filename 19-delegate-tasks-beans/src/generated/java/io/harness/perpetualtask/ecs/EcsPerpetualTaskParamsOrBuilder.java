// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/perpetualtask/ecs/ecs_task.proto

package io.harness.perpetualtask.ecs;

@javax.annotation.Generated(value = "protoc", comments = "annotations:EcsPerpetualTaskParamsOrBuilder.java.pb.meta")
public interface EcsPerpetualTaskParamsOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.harness.perpetualtask.ecs.EcsPerpetualTaskParams)
    com.google.protobuf.MessageOrBuilder {
  /**
   * <code>string cluster_name = 1[json_name = "clusterName"];</code>
   * @return The clusterName.
   */
  java.lang.String getClusterName();
  /**
   * <code>string cluster_name = 1[json_name = "clusterName"];</code>
   * @return The bytes for clusterName.
   */
  com.google.protobuf.ByteString getClusterNameBytes();

  /**
   * <code>string region = 2[json_name = "region"];</code>
   * @return The region.
   */
  java.lang.String getRegion();
  /**
   * <code>string region = 2[json_name = "region"];</code>
   * @return The bytes for region.
   */
  com.google.protobuf.ByteString getRegionBytes();

  /**
   * <code>bytes aws_config = 3[json_name = "awsConfig"];</code>
   * @return The awsConfig.
   */
  com.google.protobuf.ByteString getAwsConfig();

  /**
   * <code>bytes encryption_detail = 4[json_name = "encryptionDetail"];</code>
   * @return The encryptionDetail.
   */
  com.google.protobuf.ByteString getEncryptionDetail();

  /**
   * <code>string cluster_id = 5[json_name = "clusterId"];</code>
   * @return The clusterId.
   */
  java.lang.String getClusterId();
  /**
   * <code>string cluster_id = 5[json_name = "clusterId"];</code>
   * @return The bytes for clusterId.
   */
  com.google.protobuf.ByteString getClusterIdBytes();

  /**
   * <code>string setting_id = 6[json_name = "settingId"];</code>
   * @return The settingId.
   */
  java.lang.String getSettingId();
  /**
   * <code>string setting_id = 6[json_name = "settingId"];</code>
   * @return The bytes for settingId.
   */
  com.google.protobuf.ByteString getSettingIdBytes();
}
