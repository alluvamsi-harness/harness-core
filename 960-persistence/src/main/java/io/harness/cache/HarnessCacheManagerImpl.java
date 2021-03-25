package io.harness.cache;

import static io.harness.annotations.dev.HarnessTeam.PL;
import static io.harness.cache.CacheBackend.CAFFEINE;
import static io.harness.data.structure.HasPredicate.hasNone;

import io.harness.annotations.dev.OwnedBy;

import java.util.Optional;
import java.util.stream.Stream;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.ExpiryPolicy;

@OwnedBy(PL)
public class HarnessCacheManagerImpl implements HarnessCacheManager {
  private final CacheManager cacheManager;
  private final CacheConfig cacheConfig;
  static final String CACHE_PREFIX = "hCache";

  HarnessCacheManagerImpl(CacheManager cacheManager, CacheConfig cacheConfig) {
    this.cacheManager = cacheManager;
    this.cacheConfig = cacheConfig;
  }

  @Override
  public <K, V> Cache<K, V> getCache(
      String cacheName, Class<K> keyType, Class<V> valueType, Factory<ExpiryPolicy> expiryPolicy) {
    return getCacheInternal(cacheName, keyType, valueType, expiryPolicy);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <K, V> Cache<K, V> getCache(
      String cacheName, Class<K> keyType, Class<V> valueType, Factory<ExpiryPolicy> expiryPolicy, String keyVersion) {
    Cache<VersionedKey<K>, V> jCache = getCacheInternal(cacheName, (Class) VersionedKey.class, valueType, expiryPolicy);
    return new VersionedCache<>(jCache, keyVersion);
  }

  private <K, V> Cache<K, V> getCacheInternal(
      String cacheName, Class<K> keyType, Class<V> valueType, Factory<ExpiryPolicy> expiryPolicy) {
    if (isCacheDisabled(cacheName)) {
      return new NoOpCache<>();
    }
    String cacheNamespace = hasNone(cacheConfig.getCacheNamespace())
        ? CACHE_PREFIX
        : cacheConfig.getCacheNamespace().concat("/").concat(CACHE_PREFIX);
    String internalCacheName = String.format("%s/%s", cacheNamespace, cacheName);
    MutableConfiguration<K, V> jCacheConfiguration = new MutableConfiguration<>();
    jCacheConfiguration.setTypes(keyType, valueType);
    jCacheConfiguration.setStoreByValue(cacheConfig.getCacheBackend() != CAFFEINE);
    jCacheConfiguration.setExpiryPolicyFactory(expiryPolicy);
    jCacheConfiguration.setStatisticsEnabled(true);
    jCacheConfiguration.setManagementEnabled(true);
    try {
      return Optional.ofNullable(cacheManager.getCache(internalCacheName, keyType, valueType))
          .orElseGet(() -> cacheManager.createCache(internalCacheName, jCacheConfiguration));
    } catch (CacheException ce) {
      if (isCacheExistsError(ce, internalCacheName)) {
        return cacheManager.getCache(internalCacheName, keyType, valueType);
      }
      throw ce;
    }
  }

  private boolean isCacheExistsError(CacheException ce, String cacheName) {
    return ce.getMessage().equalsIgnoreCase("Cache " + cacheName + " already exists")
        || ce.getMessage().equalsIgnoreCase("A cache named " + cacheName + " already exists.");
  }

  private boolean isCacheDisabled(String cacheName) {
    if (hasNone(cacheConfig.getDisabledCaches())) {
      return false;
    }
    Optional<String> disabledCacheName =
        Stream.of(cacheName.split(":")).filter(value -> cacheConfig.getDisabledCaches().contains(value)).findFirst();
    return disabledCacheName.isPresent();
  }
}
