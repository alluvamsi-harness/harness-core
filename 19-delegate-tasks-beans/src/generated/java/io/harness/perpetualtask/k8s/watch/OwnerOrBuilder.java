// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/perpetualtask/k8s/watch/k8s_messages.proto

package io.harness.perpetualtask.k8s.watch;

@javax.annotation.Generated(value = "protoc", comments = "annotations:OwnerOrBuilder.java.pb.meta")
public interface OwnerOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.harness.perpetualtask.k8s.watch.Owner)
    com.google.protobuf.MessageOrBuilder {
  /**
   * <code>string uid = 1[json_name = "uid"];</code>
   * @return The uid.
   */
  java.lang.String getUid();
  /**
   * <code>string uid = 1[json_name = "uid"];</code>
   * @return The bytes for uid.
   */
  com.google.protobuf.ByteString getUidBytes();

  /**
   * <code>string kind = 2[json_name = "kind"];</code>
   * @return The kind.
   */
  java.lang.String getKind();
  /**
   * <code>string kind = 2[json_name = "kind"];</code>
   * @return The bytes for kind.
   */
  com.google.protobuf.ByteString getKindBytes();

  /**
   * <code>string name = 3[json_name = "name"];</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <code>string name = 3[json_name = "name"];</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString getNameBytes();

  /**
   * <code>map&lt;string, string&gt; labels = 4[json_name = "labels"];</code>
   */
  int getLabelsCount();
  /**
   * <code>map&lt;string, string&gt; labels = 4[json_name = "labels"];</code>
   */
  boolean containsLabels(java.lang.String key);
  /**
   * Use {@link #getLabelsMap()} instead.
   */
  @java.lang.Deprecated java.util.Map<java.lang.String, java.lang.String> getLabels();
  /**
   * <code>map&lt;string, string&gt; labels = 4[json_name = "labels"];</code>
   */
  java.util.Map<java.lang.String, java.lang.String> getLabelsMap();
  /**
   * <code>map&lt;string, string&gt; labels = 4[json_name = "labels"];</code>
   */

  java.lang.String getLabelsOrDefault(java.lang.String key, java.lang.String defaultValue);
  /**
   * <code>map&lt;string, string&gt; labels = 4[json_name = "labels"];</code>
   */

  java.lang.String getLabelsOrThrow(java.lang.String key);
}
