// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/perpetualtask/k8s/watch/k8s_messages.proto

package io.harness.perpetualtask.k8s.watch;

@javax.annotation.Generated(value = "protoc", comments = "annotations:ResourceOrBuilder.java.pb.meta")
public interface ResourceOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.harness.perpetualtask.k8s.watch.Resource)
    com.google.protobuf.MessageOrBuilder {
  /**
   * <code>map&lt;string, .io.harness.perpetualtask.k8s.watch.Resource.Quantity&gt; requests = 1[json_name =
   * "requests"];</code>
   */
  int getRequestsCount();
  /**
   * <code>map&lt;string, .io.harness.perpetualtask.k8s.watch.Resource.Quantity&gt; requests = 1[json_name =
   * "requests"];</code>
   */
  boolean containsRequests(java.lang.String key);
  /**
   * Use {@link #getRequestsMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, io.harness.perpetualtask.k8s.watch.Resource.Quantity> getRequests();
  /**
   * <code>map&lt;string, .io.harness.perpetualtask.k8s.watch.Resource.Quantity&gt; requests = 1[json_name =
   * "requests"];</code>
   */
  java.util.Map<java.lang.String, io.harness.perpetualtask.k8s.watch.Resource.Quantity> getRequestsMap();
  /**
   * <code>map&lt;string, .io.harness.perpetualtask.k8s.watch.Resource.Quantity&gt; requests = 1[json_name =
   * "requests"];</code>
   */

  io.harness.perpetualtask.k8s.watch.Resource.Quantity getRequestsOrDefault(
      java.lang.String key, io.harness.perpetualtask.k8s.watch.Resource.Quantity defaultValue);
  /**
   * <code>map&lt;string, .io.harness.perpetualtask.k8s.watch.Resource.Quantity&gt; requests = 1[json_name =
   * "requests"];</code>
   */

  io.harness.perpetualtask.k8s.watch.Resource.Quantity getRequestsOrThrow(java.lang.String key);

  /**
   * <code>map&lt;string, .io.harness.perpetualtask.k8s.watch.Resource.Quantity&gt; limits = 2[json_name =
   * "limits"];</code>
   */
  int getLimitsCount();
  /**
   * <code>map&lt;string, .io.harness.perpetualtask.k8s.watch.Resource.Quantity&gt; limits = 2[json_name =
   * "limits"];</code>
   */
  boolean containsLimits(java.lang.String key);
  /**
   * Use {@link #getLimitsMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, io.harness.perpetualtask.k8s.watch.Resource.Quantity> getLimits();
  /**
   * <code>map&lt;string, .io.harness.perpetualtask.k8s.watch.Resource.Quantity&gt; limits = 2[json_name =
   * "limits"];</code>
   */
  java.util.Map<java.lang.String, io.harness.perpetualtask.k8s.watch.Resource.Quantity> getLimitsMap();
  /**
   * <code>map&lt;string, .io.harness.perpetualtask.k8s.watch.Resource.Quantity&gt; limits = 2[json_name =
   * "limits"];</code>
   */

  io.harness.perpetualtask.k8s.watch.Resource.Quantity getLimitsOrDefault(
      java.lang.String key, io.harness.perpetualtask.k8s.watch.Resource.Quantity defaultValue);
  /**
   * <code>map&lt;string, .io.harness.perpetualtask.k8s.watch.Resource.Quantity&gt; limits = 2[json_name =
   * "limits"];</code>
   */

  io.harness.perpetualtask.k8s.watch.Resource.Quantity getLimitsOrThrow(java.lang.String key);
}
