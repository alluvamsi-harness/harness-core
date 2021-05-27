package io.harness.pms.sdk.core;

import io.harness.eventsframework.EventsFrameworkConfiguration;
import io.harness.grpc.client.GrpcClientConfig;
import io.harness.grpc.server.GrpcServerConfig;
import io.harness.redis.RedisConfig;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;

@Value
@Builder
public class PmsSdkCoreConfig {
  String serviceName;
  SdkDeployMode sdkDeployMode;
  GrpcServerConfig grpcServerConfig;
  GrpcClientConfig grpcClientConfig;
  @Default
  EventsFrameworkConfiguration eventsFrameworkConfiguration =
      EventsFrameworkConfiguration.builder()
          .redisConfig(RedisConfig.builder().redisUrl("dummyRedisUrl").build())
          .build();
  ;
}
