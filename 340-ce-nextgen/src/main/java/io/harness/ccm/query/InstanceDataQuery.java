package io.harness.ccm.query;

import static io.harness.annotations.dev.HarnessTeam.CE;

import io.harness.annotations.dev.OwnedBy;
import io.harness.ccm.dto.BillingDataDemo;
import io.harness.ccm.dto.InstanceDataDemo;
import io.harness.ccm.utils.GraphQLUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;

/**
 * For documentation/examples refer https://github.com/leangen/graphql-spqr-samples
 */
@Slf4j
@Singleton
@OwnedBy(CE)
public class InstanceDataQuery {
  @Inject private GraphQLUtils graphQLUtils;
  @Getter private final String dataLoaderName = "instancedata";
  // GraphQL Query Schema and Service Class
  @GraphQLQuery
  public CompletableFuture<InstanceDataDemo> instancedata(
      @GraphQLContext BillingDataDemo billingDataDemo, @GraphQLEnvironment final ResolutionEnvironment env) {
    final String accountId = graphQLUtils.getAccountIdentifier(env);
    final DataLoader<CacheKey, InstanceDataDemo> dataLoader = env.dataFetchingEnvironment.getDataLoader("instancedata");

    log.debug("INSIDE: getInstanceDataById Query In BillingData Context");
    return dataLoader.load(CacheKey.of(accountId, billingDataDemo.getInstanceid()));
  }

  @GraphQLQuery
  public CompletableFuture<InstanceDataDemo> instancedata(
      @GraphQLNonNull String instanceid, @GraphQLEnvironment final ResolutionEnvironment env) {
    final String accountId = graphQLUtils.getAccountIdentifier(env);
    // example on how to use dataLoader shared across multiple queries.
    final DataLoader<CacheKey, InstanceDataDemo> dataLoader = env.dataFetchingEnvironment.getDataLoader(dataLoaderName);

    log.debug("INSIDE: getInstanceDataById Query");
    // can use 'instanceDataLoader' directly instead of 'dataLoader' as well.
    return dataLoader.load(CacheKey.of(accountId, instanceid));
  }

  @Value(staticConstructor = "of")
  private static class CacheKey {
    String accountId;
    String clusterId;
  }

  // DataLoader and DAO
  @Getter
  private final DataLoader<CacheKey, InstanceDataDemo> instanceDataLoader =
      DataLoader.newDataLoader(instanceIds -> CompletableFuture.supplyAsync(() -> getInstanceDataByIds(instanceIds)));

  private List<InstanceDataDemo> getInstanceDataByIds(final List<CacheKey> keys) {
    log.debug("INSIDE: getInstanceDataByIds DAO");
    List<InstanceDataDemo> result = new ArrayList<>();
    for (CacheKey id : keys) {
      result.add(InstanceDataDemo.builder()
                     .cloudprovider("cloudprovider_" + id.getAccountId() + "/" + id.getClusterId())
                     .instancetype("instance_type_" + id.getAccountId() + "/" + id.getClusterId())
                     .region("region_" + id.getAccountId() + "/" + id.getClusterId())
                     .build());
    }
    return result;
  }
}
