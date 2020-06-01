// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/delegate/delegate_service.proto

package io.harness.delegate;

/**
 * Protobuf type {@code io.harness.delegate.ResetPerpetualTaskRequest}
 */
@javax.annotation.Generated(value = "protoc", comments = "annotations:ResetPerpetualTaskRequest.java.pb.meta")
public final class ResetPerpetualTaskRequest extends com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:io.harness.delegate.ResetPerpetualTaskRequest)
    ResetPerpetualTaskRequestOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use ResetPerpetualTaskRequest.newBuilder() to construct.
  private ResetPerpetualTaskRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ResetPerpetualTaskRequest() {}

  @java.
  lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new ResetPerpetualTaskRequest();
  }

  @java.
  lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  private ResetPerpetualTaskRequest(
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
          case 10: {
            io.harness.delegate.AccountId.Builder subBuilder = null;
            if (accountId_ != null) {
              subBuilder = accountId_.toBuilder();
            }
            accountId_ = input.readMessage(io.harness.delegate.AccountId.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(accountId_);
              accountId_ = subBuilder.buildPartial();
            }

            break;
          }
          case 18: {
            io.harness.perpetualtask.PerpetualTaskId.Builder subBuilder = null;
            if (perpetualTaskId_ != null) {
              subBuilder = perpetualTaskId_.toBuilder();
            }
            perpetualTaskId_ = input.readMessage(io.harness.perpetualtask.PerpetualTaskId.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(perpetualTaskId_);
              perpetualTaskId_ = subBuilder.buildPartial();
            }

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
    return io.harness.delegate.DelegateServiceOuterClass
        .internal_static_io_harness_delegate_ResetPerpetualTaskRequest_descriptor;
  }

  @java.
  lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return io.harness.delegate.DelegateServiceOuterClass
        .internal_static_io_harness_delegate_ResetPerpetualTaskRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(io.harness.delegate.ResetPerpetualTaskRequest.class,
            io.harness.delegate.ResetPerpetualTaskRequest.Builder.class);
  }

  public static final int ACCOUNT_ID_FIELD_NUMBER = 1;
  private io.harness.delegate.AccountId accountId_;
  /**
   * <code>.io.harness.delegate.AccountId account_id = 1[json_name = "accountId"];</code>
   * @return Whether the accountId field is set.
   */
  public boolean hasAccountId() {
    return accountId_ != null;
  }
  /**
   * <code>.io.harness.delegate.AccountId account_id = 1[json_name = "accountId"];</code>
   * @return The accountId.
   */
  public io.harness.delegate.AccountId getAccountId() {
    return accountId_ == null ? io.harness.delegate.AccountId.getDefaultInstance() : accountId_;
  }
  /**
   * <code>.io.harness.delegate.AccountId account_id = 1[json_name = "accountId"];</code>
   */
  public io.harness.delegate.AccountIdOrBuilder getAccountIdOrBuilder() {
    return getAccountId();
  }

  public static final int PERPETUAL_TASK_ID_FIELD_NUMBER = 2;
  private io.harness.perpetualtask.PerpetualTaskId perpetualTaskId_;
  /**
   * <code>.io.harness.perpetualtask.PerpetualTaskId perpetual_task_id = 2[json_name = "perpetualTaskId"];</code>
   * @return Whether the perpetualTaskId field is set.
   */
  public boolean hasPerpetualTaskId() {
    return perpetualTaskId_ != null;
  }
  /**
   * <code>.io.harness.perpetualtask.PerpetualTaskId perpetual_task_id = 2[json_name = "perpetualTaskId"];</code>
   * @return The perpetualTaskId.
   */
  public io.harness.perpetualtask.PerpetualTaskId getPerpetualTaskId() {
    return perpetualTaskId_ == null ? io.harness.perpetualtask.PerpetualTaskId.getDefaultInstance() : perpetualTaskId_;
  }
  /**
   * <code>.io.harness.perpetualtask.PerpetualTaskId perpetual_task_id = 2[json_name = "perpetualTaskId"];</code>
   */
  public io.harness.perpetualtask.PerpetualTaskIdOrBuilder getPerpetualTaskIdOrBuilder() {
    return getPerpetualTaskId();
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
    if (accountId_ != null) {
      output.writeMessage(1, getAccountId());
    }
    if (perpetualTaskId_ != null) {
      output.writeMessage(2, getPerpetualTaskId());
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1)
      return size;

    size = 0;
    if (accountId_ != null) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, getAccountId());
    }
    if (perpetualTaskId_ != null) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(2, getPerpetualTaskId());
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
    if (!(obj instanceof io.harness.delegate.ResetPerpetualTaskRequest)) {
      return super.equals(obj);
    }
    io.harness.delegate.ResetPerpetualTaskRequest other = (io.harness.delegate.ResetPerpetualTaskRequest) obj;

    if (hasAccountId() != other.hasAccountId())
      return false;
    if (hasAccountId()) {
      if (!getAccountId().equals(other.getAccountId()))
        return false;
    }
    if (hasPerpetualTaskId() != other.hasPerpetualTaskId())
      return false;
    if (hasPerpetualTaskId()) {
      if (!getPerpetualTaskId().equals(other.getPerpetualTaskId()))
        return false;
    }
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
    if (hasAccountId()) {
      hash = (37 * hash) + ACCOUNT_ID_FIELD_NUMBER;
      hash = (53 * hash) + getAccountId().hashCode();
    }
    if (hasPerpetualTaskId()) {
      hash = (37 * hash) + PERPETUAL_TASK_ID_FIELD_NUMBER;
      hash = (53 * hash) + getPerpetualTaskId().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static io.harness.delegate.ResetPerpetualTaskRequest parseFrom(java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.harness.delegate.ResetPerpetualTaskRequest parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.harness.delegate.ResetPerpetualTaskRequest parseFrom(com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.harness.delegate.ResetPerpetualTaskRequest parseFrom(
      com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.harness.delegate.ResetPerpetualTaskRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.harness.delegate.ResetPerpetualTaskRequest parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.harness.delegate.ResetPerpetualTaskRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }
  public static io.harness.delegate.ResetPerpetualTaskRequest parseFrom(java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static io.harness.delegate.ResetPerpetualTaskRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }
  public static io.harness.delegate.ResetPerpetualTaskRequest parseDelimitedFrom(java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static io.harness.delegate.ResetPerpetualTaskRequest parseFrom(com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }
  public static io.harness.delegate.ResetPerpetualTaskRequest parseFrom(com.google.protobuf.CodedInputStream input,
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
  public static Builder newBuilder(io.harness.delegate.ResetPerpetualTaskRequest prototype) {
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
   * Protobuf type {@code io.harness.delegate.ResetPerpetualTaskRequest}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:io.harness.delegate.ResetPerpetualTaskRequest)
      io.harness.delegate.ResetPerpetualTaskRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return io.harness.delegate.DelegateServiceOuterClass
          .internal_static_io_harness_delegate_ResetPerpetualTaskRequest_descriptor;
    }

    @java.
    lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return io.harness.delegate.DelegateServiceOuterClass
          .internal_static_io_harness_delegate_ResetPerpetualTaskRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(io.harness.delegate.ResetPerpetualTaskRequest.class,
              io.harness.delegate.ResetPerpetualTaskRequest.Builder.class);
    }

    // Construct using io.harness.delegate.ResetPerpetualTaskRequest.newBuilder()
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
      if (accountIdBuilder_ == null) {
        accountId_ = null;
      } else {
        accountId_ = null;
        accountIdBuilder_ = null;
      }
      if (perpetualTaskIdBuilder_ == null) {
        perpetualTaskId_ = null;
      } else {
        perpetualTaskId_ = null;
        perpetualTaskIdBuilder_ = null;
      }
      return this;
    }

    @java.
    lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return io.harness.delegate.DelegateServiceOuterClass
          .internal_static_io_harness_delegate_ResetPerpetualTaskRequest_descriptor;
    }

    @java.
    lang.Override
    public io.harness.delegate.ResetPerpetualTaskRequest getDefaultInstanceForType() {
      return io.harness.delegate.ResetPerpetualTaskRequest.getDefaultInstance();
    }

    @java.
    lang.Override
    public io.harness.delegate.ResetPerpetualTaskRequest build() {
      io.harness.delegate.ResetPerpetualTaskRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.
    lang.Override
    public io.harness.delegate.ResetPerpetualTaskRequest buildPartial() {
      io.harness.delegate.ResetPerpetualTaskRequest result = new io.harness.delegate.ResetPerpetualTaskRequest(this);
      if (accountIdBuilder_ == null) {
        result.accountId_ = accountId_;
      } else {
        result.accountId_ = accountIdBuilder_.build();
      }
      if (perpetualTaskIdBuilder_ == null) {
        result.perpetualTaskId_ = perpetualTaskId_;
      } else {
        result.perpetualTaskId_ = perpetualTaskIdBuilder_.build();
      }
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
      if (other instanceof io.harness.delegate.ResetPerpetualTaskRequest) {
        return mergeFrom((io.harness.delegate.ResetPerpetualTaskRequest) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(io.harness.delegate.ResetPerpetualTaskRequest other) {
      if (other == io.harness.delegate.ResetPerpetualTaskRequest.getDefaultInstance())
        return this;
      if (other.hasAccountId()) {
        mergeAccountId(other.getAccountId());
      }
      if (other.hasPerpetualTaskId()) {
        mergePerpetualTaskId(other.getPerpetualTaskId());
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
      io.harness.delegate.ResetPerpetualTaskRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (io.harness.delegate.ResetPerpetualTaskRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private io.harness.delegate.AccountId accountId_;
    private com.google.protobuf.SingleFieldBuilderV3<io.harness.delegate.AccountId,
        io.harness.delegate.AccountId.Builder, io.harness.delegate.AccountIdOrBuilder> accountIdBuilder_;
    /**
     * <code>.io.harness.delegate.AccountId account_id = 1[json_name = "accountId"];</code>
     * @return Whether the accountId field is set.
     */
    public boolean hasAccountId() {
      return accountIdBuilder_ != null || accountId_ != null;
    }
    /**
     * <code>.io.harness.delegate.AccountId account_id = 1[json_name = "accountId"];</code>
     * @return The accountId.
     */
    public io.harness.delegate.AccountId getAccountId() {
      if (accountIdBuilder_ == null) {
        return accountId_ == null ? io.harness.delegate.AccountId.getDefaultInstance() : accountId_;
      } else {
        return accountIdBuilder_.getMessage();
      }
    }
    /**
     * <code>.io.harness.delegate.AccountId account_id = 1[json_name = "accountId"];</code>
     */
    public Builder setAccountId(io.harness.delegate.AccountId value) {
      if (accountIdBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        accountId_ = value;
        onChanged();
      } else {
        accountIdBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.io.harness.delegate.AccountId account_id = 1[json_name = "accountId"];</code>
     */
    public Builder setAccountId(io.harness.delegate.AccountId.Builder builderForValue) {
      if (accountIdBuilder_ == null) {
        accountId_ = builderForValue.build();
        onChanged();
      } else {
        accountIdBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.io.harness.delegate.AccountId account_id = 1[json_name = "accountId"];</code>
     */
    public Builder mergeAccountId(io.harness.delegate.AccountId value) {
      if (accountIdBuilder_ == null) {
        if (accountId_ != null) {
          accountId_ = io.harness.delegate.AccountId.newBuilder(accountId_).mergeFrom(value).buildPartial();
        } else {
          accountId_ = value;
        }
        onChanged();
      } else {
        accountIdBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.io.harness.delegate.AccountId account_id = 1[json_name = "accountId"];</code>
     */
    public Builder clearAccountId() {
      if (accountIdBuilder_ == null) {
        accountId_ = null;
        onChanged();
      } else {
        accountId_ = null;
        accountIdBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.io.harness.delegate.AccountId account_id = 1[json_name = "accountId"];</code>
     */
    public io.harness.delegate.AccountId.Builder getAccountIdBuilder() {
      onChanged();
      return getAccountIdFieldBuilder().getBuilder();
    }
    /**
     * <code>.io.harness.delegate.AccountId account_id = 1[json_name = "accountId"];</code>
     */
    public io.harness.delegate.AccountIdOrBuilder getAccountIdOrBuilder() {
      if (accountIdBuilder_ != null) {
        return accountIdBuilder_.getMessageOrBuilder();
      } else {
        return accountId_ == null ? io.harness.delegate.AccountId.getDefaultInstance() : accountId_;
      }
    }
    /**
     * <code>.io.harness.delegate.AccountId account_id = 1[json_name = "accountId"];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<io.harness.delegate.AccountId,
        io.harness.delegate.AccountId.Builder, io.harness.delegate.AccountIdOrBuilder>
    getAccountIdFieldBuilder() {
      if (accountIdBuilder_ == null) {
        accountIdBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<io.harness.delegate.AccountId,
            io.harness.delegate.AccountId.Builder, io.harness.delegate.AccountIdOrBuilder>(
            getAccountId(), getParentForChildren(), isClean());
        accountId_ = null;
      }
      return accountIdBuilder_;
    }

    private io.harness.perpetualtask.PerpetualTaskId perpetualTaskId_;
    private com.google.protobuf.SingleFieldBuilderV3<io.harness.perpetualtask.PerpetualTaskId,
        io.harness.perpetualtask.PerpetualTaskId.Builder, io.harness.perpetualtask.PerpetualTaskIdOrBuilder>
        perpetualTaskIdBuilder_;
    /**
     * <code>.io.harness.perpetualtask.PerpetualTaskId perpetual_task_id = 2[json_name = "perpetualTaskId"];</code>
     * @return Whether the perpetualTaskId field is set.
     */
    public boolean hasPerpetualTaskId() {
      return perpetualTaskIdBuilder_ != null || perpetualTaskId_ != null;
    }
    /**
     * <code>.io.harness.perpetualtask.PerpetualTaskId perpetual_task_id = 2[json_name = "perpetualTaskId"];</code>
     * @return The perpetualTaskId.
     */
    public io.harness.perpetualtask.PerpetualTaskId getPerpetualTaskId() {
      if (perpetualTaskIdBuilder_ == null) {
        return perpetualTaskId_ == null ? io.harness.perpetualtask.PerpetualTaskId.getDefaultInstance()
                                        : perpetualTaskId_;
      } else {
        return perpetualTaskIdBuilder_.getMessage();
      }
    }
    /**
     * <code>.io.harness.perpetualtask.PerpetualTaskId perpetual_task_id = 2[json_name = "perpetualTaskId"];</code>
     */
    public Builder setPerpetualTaskId(io.harness.perpetualtask.PerpetualTaskId value) {
      if (perpetualTaskIdBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        perpetualTaskId_ = value;
        onChanged();
      } else {
        perpetualTaskIdBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.io.harness.perpetualtask.PerpetualTaskId perpetual_task_id = 2[json_name = "perpetualTaskId"];</code>
     */
    public Builder setPerpetualTaskId(io.harness.perpetualtask.PerpetualTaskId.Builder builderForValue) {
      if (perpetualTaskIdBuilder_ == null) {
        perpetualTaskId_ = builderForValue.build();
        onChanged();
      } else {
        perpetualTaskIdBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.io.harness.perpetualtask.PerpetualTaskId perpetual_task_id = 2[json_name = "perpetualTaskId"];</code>
     */
    public Builder mergePerpetualTaskId(io.harness.perpetualtask.PerpetualTaskId value) {
      if (perpetualTaskIdBuilder_ == null) {
        if (perpetualTaskId_ != null) {
          perpetualTaskId_ =
              io.harness.perpetualtask.PerpetualTaskId.newBuilder(perpetualTaskId_).mergeFrom(value).buildPartial();
        } else {
          perpetualTaskId_ = value;
        }
        onChanged();
      } else {
        perpetualTaskIdBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.io.harness.perpetualtask.PerpetualTaskId perpetual_task_id = 2[json_name = "perpetualTaskId"];</code>
     */
    public Builder clearPerpetualTaskId() {
      if (perpetualTaskIdBuilder_ == null) {
        perpetualTaskId_ = null;
        onChanged();
      } else {
        perpetualTaskId_ = null;
        perpetualTaskIdBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.io.harness.perpetualtask.PerpetualTaskId perpetual_task_id = 2[json_name = "perpetualTaskId"];</code>
     */
    public io.harness.perpetualtask.PerpetualTaskId.Builder getPerpetualTaskIdBuilder() {
      onChanged();
      return getPerpetualTaskIdFieldBuilder().getBuilder();
    }
    /**
     * <code>.io.harness.perpetualtask.PerpetualTaskId perpetual_task_id = 2[json_name = "perpetualTaskId"];</code>
     */
    public io.harness.perpetualtask.PerpetualTaskIdOrBuilder getPerpetualTaskIdOrBuilder() {
      if (perpetualTaskIdBuilder_ != null) {
        return perpetualTaskIdBuilder_.getMessageOrBuilder();
      } else {
        return perpetualTaskId_ == null ? io.harness.perpetualtask.PerpetualTaskId.getDefaultInstance()
                                        : perpetualTaskId_;
      }
    }
    /**
     * <code>.io.harness.perpetualtask.PerpetualTaskId perpetual_task_id = 2[json_name = "perpetualTaskId"];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<io.harness.perpetualtask.PerpetualTaskId,
        io.harness.perpetualtask.PerpetualTaskId.Builder, io.harness.perpetualtask.PerpetualTaskIdOrBuilder>
    getPerpetualTaskIdFieldBuilder() {
      if (perpetualTaskIdBuilder_ == null) {
        perpetualTaskIdBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<io.harness.perpetualtask.PerpetualTaskId,
            io.harness.perpetualtask.PerpetualTaskId.Builder, io.harness.perpetualtask.PerpetualTaskIdOrBuilder>(
            getPerpetualTaskId(), getParentForChildren(), isClean());
        perpetualTaskId_ = null;
      }
      return perpetualTaskIdBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }

    // @@protoc_insertion_point(builder_scope:io.harness.delegate.ResetPerpetualTaskRequest)
  }

  // @@protoc_insertion_point(class_scope:io.harness.delegate.ResetPerpetualTaskRequest)
  private static final io.harness.delegate.ResetPerpetualTaskRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new io.harness.delegate.ResetPerpetualTaskRequest();
  }

  public static io.harness.delegate.ResetPerpetualTaskRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ResetPerpetualTaskRequest> PARSER =
      new com.google.protobuf.AbstractParser<ResetPerpetualTaskRequest>() {
        @java.lang.Override
        public ResetPerpetualTaskRequest parsePartialFrom(
            com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new ResetPerpetualTaskRequest(input, extensionRegistry);
        }
      };

  public static com.google.protobuf.Parser<ResetPerpetualTaskRequest> parser() {
    return PARSER;
  }

  @java.
  lang.Override
  public com.google.protobuf.Parser<ResetPerpetualTaskRequest> getParserForType() {
    return PARSER;
  }

  @java.
  lang.Override
  public io.harness.delegate.ResetPerpetualTaskRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
