// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/event/payloads/k8s_utilization_messages.proto

package io.harness.event.payloads;

/**
 * Protobuf type {@code io.harness.event.payloads.AggregatedUsage}
 */
@javax.annotation.Generated(value = "protoc", comments = "annotations:AggregatedUsage.java.pb.meta")
public final class AggregatedUsage extends com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:io.harness.event.payloads.AggregatedUsage)
    AggregatedUsageOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use AggregatedUsage.newBuilder() to construct.
  private AggregatedUsage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private AggregatedUsage() {}

  @java.
  lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new AggregatedUsage();
  }

  @java.
  lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  private AggregatedUsage(
      com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {
            avgCpuNano_ = input.readInt64();
            break;
          }
          case 16: {
            maxCpuNano_ = input.readInt64();
            break;
          }
          case 24: {
            avgMemoryByte_ = input.readInt64();
            break;
          }
          case 32: {
            maxMemoryByte_ = input.readInt64();
            break;
          }
          default: {
            if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return io.harness.event.payloads.K8SUtilizationMessages
        .internal_static_io_harness_event_payloads_AggregatedUsage_descriptor;
  }

  @java.
  lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return io.harness.event.payloads.K8SUtilizationMessages
        .internal_static_io_harness_event_payloads_AggregatedUsage_fieldAccessorTable.ensureFieldAccessorsInitialized(
            io.harness.event.payloads.AggregatedUsage.class, io.harness.event.payloads.AggregatedUsage.Builder.class);
  }

  public static final int AVG_CPU_NANO_FIELD_NUMBER = 1;
  private long avgCpuNano_;
  /**
   * <code>int64 avg_cpu_nano = 1[json_name = "avgCpuNano"];</code>
   * @return The avgCpuNano.
   */
  public long getAvgCpuNano() {
    return avgCpuNano_;
  }

  public static final int MAX_CPU_NANO_FIELD_NUMBER = 2;
  private long maxCpuNano_;
  /**
   * <code>int64 max_cpu_nano = 2[json_name = "maxCpuNano"];</code>
   * @return The maxCpuNano.
   */
  public long getMaxCpuNano() {
    return maxCpuNano_;
  }

  public static final int AVG_MEMORY_BYTE_FIELD_NUMBER = 3;
  private long avgMemoryByte_;
  /**
   * <code>int64 avg_memory_byte = 3[json_name = "avgMemoryByte"];</code>
   * @return The avgMemoryByte.
   */
  public long getAvgMemoryByte() {
    return avgMemoryByte_;
  }

  public static final int MAX_MEMORY_BYTE_FIELD_NUMBER = 4;
  private long maxMemoryByte_;
  /**
   * <code>int64 max_memory_byte = 4[json_name = "maxMemoryByte"];</code>
   * @return The maxMemoryByte.
   */
  public long getMaxMemoryByte() {
    return maxMemoryByte_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1)
      return true;
    if (isInitialized == 0)
      return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
    if (avgCpuNano_ != 0L) {
      output.writeInt64(1, avgCpuNano_);
    }
    if (maxCpuNano_ != 0L) {
      output.writeInt64(2, maxCpuNano_);
    }
    if (avgMemoryByte_ != 0L) {
      output.writeInt64(3, avgMemoryByte_);
    }
    if (maxMemoryByte_ != 0L) {
      output.writeInt64(4, maxMemoryByte_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1)
      return size;

    size = 0;
    if (avgCpuNano_ != 0L) {
      size += com.google.protobuf.CodedOutputStream.computeInt64Size(1, avgCpuNano_);
    }
    if (maxCpuNano_ != 0L) {
      size += com.google.protobuf.CodedOutputStream.computeInt64Size(2, maxCpuNano_);
    }
    if (avgMemoryByte_ != 0L) {
      size += com.google.protobuf.CodedOutputStream.computeInt64Size(3, avgMemoryByte_);
    }
    if (maxMemoryByte_ != 0L) {
      size += com.google.protobuf.CodedOutputStream.computeInt64Size(4, maxMemoryByte_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof io.harness.event.payloads.AggregatedUsage)) {
      return super.equals(obj);
    }
    io.harness.event.payloads.AggregatedUsage other = (io.harness.event.payloads.AggregatedUsage) obj;

    if (getAvgCpuNano() != other.getAvgCpuNano())
      return false;
    if (getMaxCpuNano() != other.getMaxCpuNano())
      return false;
    if (getAvgMemoryByte() != other.getAvgMemoryByte())
      return false;
    if (getMaxMemoryByte() != other.getMaxMemoryByte())
      return false;
    if (!unknownFields.equals(other.unknownFields))
      return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + AVG_CPU_NANO_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getAvgCpuNano());
    hash = (37 * hash) + MAX_CPU_NANO_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getMaxCpuNano());
    hash = (37 * hash) + AVG_MEMORY_BYTE_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getAvgMemoryByte());
    hash = (37 * hash) + MAX_MEMORY_BYTE_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getMaxMemoryByte());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static io.harness.event.payloads.AggregatedUsage parseFrom(java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.harness.event.payloads.AggregatedUsage parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.harness.event.payloads.AggregatedUsage parseFrom(com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.harness.event.payloads.AggregatedUsage parseFrom(
      com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.harness.event.payloads.AggregatedUsage parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.harness.event.payloads.AggregatedUsage parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.harness.event.payloads.AggregatedUsage parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }
  public static io.harness.event.payloads.AggregatedUsage parseFrom(java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static io.harness.event.payloads.AggregatedUsage parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }
  public static io.harness.event.payloads.AggregatedUsage parseDelimitedFrom(java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static io.harness.event.payloads.AggregatedUsage parseFrom(com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }
  public static io.harness.event.payloads.AggregatedUsage parseFrom(com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() {
    return newBuilder();
  }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(io.harness.event.payloads.AggregatedUsage prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code io.harness.event.payloads.AggregatedUsage}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:io.harness.event.payloads.AggregatedUsage)
      io.harness.event.payloads.AggregatedUsageOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return io.harness.event.payloads.K8SUtilizationMessages
          .internal_static_io_harness_event_payloads_AggregatedUsage_descriptor;
    }

    @java.
    lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return io.harness.event.payloads.K8SUtilizationMessages
          .internal_static_io_harness_event_payloads_AggregatedUsage_fieldAccessorTable.ensureFieldAccessorsInitialized(
              io.harness.event.payloads.AggregatedUsage.class, io.harness.event.payloads.AggregatedUsage.Builder.class);
    }

    // Construct using io.harness.event.payloads.AggregatedUsage.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      avgCpuNano_ = 0L;

      maxCpuNano_ = 0L;

      avgMemoryByte_ = 0L;

      maxMemoryByte_ = 0L;

      return this;
    }

    @java.
    lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return io.harness.event.payloads.K8SUtilizationMessages
          .internal_static_io_harness_event_payloads_AggregatedUsage_descriptor;
    }

    @java.
    lang.Override
    public io.harness.event.payloads.AggregatedUsage getDefaultInstanceForType() {
      return io.harness.event.payloads.AggregatedUsage.getDefaultInstance();
    }

    @java.
    lang.Override
    public io.harness.event.payloads.AggregatedUsage build() {
      io.harness.event.payloads.AggregatedUsage result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.
    lang.Override
    public io.harness.event.payloads.AggregatedUsage buildPartial() {
      io.harness.event.payloads.AggregatedUsage result = new io.harness.event.payloads.AggregatedUsage(this);
      result.avgCpuNano_ = avgCpuNano_;
      result.maxCpuNano_ = maxCpuNano_;
      result.avgMemoryByte_ = avgMemoryByte_;
      result.maxMemoryByte_ = maxMemoryByte_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof io.harness.event.payloads.AggregatedUsage) {
        return mergeFrom((io.harness.event.payloads.AggregatedUsage) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(io.harness.event.payloads.AggregatedUsage other) {
      if (other == io.harness.event.payloads.AggregatedUsage.getDefaultInstance())
        return this;
      if (other.getAvgCpuNano() != 0L) {
        setAvgCpuNano(other.getAvgCpuNano());
      }
      if (other.getMaxCpuNano() != 0L) {
        setMaxCpuNano(other.getMaxCpuNano());
      }
      if (other.getAvgMemoryByte() != 0L) {
        setAvgMemoryByte(other.getAvgMemoryByte());
      }
      if (other.getMaxMemoryByte() != 0L) {
        setMaxMemoryByte(other.getMaxMemoryByte());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      io.harness.event.payloads.AggregatedUsage parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (io.harness.event.payloads.AggregatedUsage) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private long avgCpuNano_;
    /**
     * <code>int64 avg_cpu_nano = 1[json_name = "avgCpuNano"];</code>
     * @return The avgCpuNano.
     */
    public long getAvgCpuNano() {
      return avgCpuNano_;
    }
    /**
     * <code>int64 avg_cpu_nano = 1[json_name = "avgCpuNano"];</code>
     * @param value The avgCpuNano to set.
     * @return This builder for chaining.
     */
    public Builder setAvgCpuNano(long value) {
      avgCpuNano_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 avg_cpu_nano = 1[json_name = "avgCpuNano"];</code>
     * @return This builder for chaining.
     */
    public Builder clearAvgCpuNano() {
      avgCpuNano_ = 0L;
      onChanged();
      return this;
    }

    private long maxCpuNano_;
    /**
     * <code>int64 max_cpu_nano = 2[json_name = "maxCpuNano"];</code>
     * @return The maxCpuNano.
     */
    public long getMaxCpuNano() {
      return maxCpuNano_;
    }
    /**
     * <code>int64 max_cpu_nano = 2[json_name = "maxCpuNano"];</code>
     * @param value The maxCpuNano to set.
     * @return This builder for chaining.
     */
    public Builder setMaxCpuNano(long value) {
      maxCpuNano_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 max_cpu_nano = 2[json_name = "maxCpuNano"];</code>
     * @return This builder for chaining.
     */
    public Builder clearMaxCpuNano() {
      maxCpuNano_ = 0L;
      onChanged();
      return this;
    }

    private long avgMemoryByte_;
    /**
     * <code>int64 avg_memory_byte = 3[json_name = "avgMemoryByte"];</code>
     * @return The avgMemoryByte.
     */
    public long getAvgMemoryByte() {
      return avgMemoryByte_;
    }
    /**
     * <code>int64 avg_memory_byte = 3[json_name = "avgMemoryByte"];</code>
     * @param value The avgMemoryByte to set.
     * @return This builder for chaining.
     */
    public Builder setAvgMemoryByte(long value) {
      avgMemoryByte_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 avg_memory_byte = 3[json_name = "avgMemoryByte"];</code>
     * @return This builder for chaining.
     */
    public Builder clearAvgMemoryByte() {
      avgMemoryByte_ = 0L;
      onChanged();
      return this;
    }

    private long maxMemoryByte_;
    /**
     * <code>int64 max_memory_byte = 4[json_name = "maxMemoryByte"];</code>
     * @return The maxMemoryByte.
     */
    public long getMaxMemoryByte() {
      return maxMemoryByte_;
    }
    /**
     * <code>int64 max_memory_byte = 4[json_name = "maxMemoryByte"];</code>
     * @param value The maxMemoryByte to set.
     * @return This builder for chaining.
     */
    public Builder setMaxMemoryByte(long value) {
      maxMemoryByte_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 max_memory_byte = 4[json_name = "maxMemoryByte"];</code>
     * @return This builder for chaining.
     */
    public Builder clearMaxMemoryByte() {
      maxMemoryByte_ = 0L;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }

    // @@protoc_insertion_point(builder_scope:io.harness.event.payloads.AggregatedUsage)
  }

  // @@protoc_insertion_point(class_scope:io.harness.event.payloads.AggregatedUsage)
  private static final io.harness.event.payloads.AggregatedUsage DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new io.harness.event.payloads.AggregatedUsage();
  }

  public static io.harness.event.payloads.AggregatedUsage getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<AggregatedUsage> PARSER =
      new com.google.protobuf.AbstractParser<AggregatedUsage>() {
        @java.lang.Override
        public AggregatedUsage parsePartialFrom(
            com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new AggregatedUsage(input, extensionRegistry);
        }
      };

  public static com.google.protobuf.Parser<AggregatedUsage> parser() {
    return PARSER;
  }

  @java.
  lang.Override
  public com.google.protobuf.Parser<AggregatedUsage> getParserForType() {
    return PARSER;
  }

  @java.
  lang.Override
  public io.harness.event.payloads.AggregatedUsage getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
