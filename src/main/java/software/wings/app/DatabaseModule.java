package software.wings.app;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import com.deftlabs.lock.mongo.DistributedLockSvc;
import com.deftlabs.lock.mongo.DistributedLockSvcFactory;
import com.deftlabs.lock.mongo.DistributedLockSvcOptions;
import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import software.wings.beans.ReadPref;
import software.wings.dl.MongoConfig;
import software.wings.lock.ManagedDistributedLockSvc;
import software.wings.utils.NoDefaultConstructorMorphiaObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc

/**
 * Created by peeyushaggarwal on 5/25/16.
 */
public class DatabaseModule extends AbstractModule {
  private Datastore primaryDatastore;

  private Datastore secondaryDatastore;

  private DistributedLockSvc distributedLockSvc;

  private Map<ReadPref, Datastore> datastoreMap = Maps.newHashMap();

  /**
   * Creates a guice module for portal app.
   *
   * @param configuration Dropwizard configuration
   */
  public DatabaseModule(MainConfiguration configuration) {
    MongoConfig mongoConfig = configuration.getMongoConnectionFactory();
    List<String> hosts = Splitter.on(",").splitToList(mongoConfig.getHost());
    List<ServerAddress> serverAddresses = new ArrayList<>();

    for (String host : hosts) {
      serverAddresses.add(new ServerAddress(host, mongoConfig.getPort()));
    }
    Morphia morphia = new Morphia();
    morphia.getMapper().getOptions().setObjectFactory(new NoDefaultConstructorMorphiaObjectFactory());
    MongoClient mongoClient = new MongoClient(serverAddresses);
    this.primaryDatastore = morphia.createDatastore(mongoClient, mongoConfig.getDb());
    distributedLockSvc = new ManagedDistributedLockSvc(
        new DistributedLockSvcFactory(new DistributedLockSvcOptions(mongoClient, mongoConfig.getDb(), "locks"))
            .getLockSvc());

    if (hosts.size() > 1) {
      mongoClient = new MongoClient(serverAddresses);
      mongoClient.setReadPreference(ReadPreference.secondaryPreferred());
      this.secondaryDatastore = morphia.createDatastore(mongoClient, mongoConfig.getDb());
    } else {
      this.secondaryDatastore = primaryDatastore;
    }

    morphia.mapPackage("software.wings.beans");
    this.primaryDatastore.ensureIndexes();
    if (hosts.size() > 1) {
      this.secondaryDatastore.ensureIndexes();
    }

    datastoreMap.put(ReadPref.CRITICAL, primaryDatastore);
    datastoreMap.put(ReadPref.NORMAL, secondaryDatastore);
  }

  /**
   * Instantiates a new database module.
   *
   * @param primaryDatastore   the primary datastore
   * @param secondaryDatastore the secondary datastore
   * @param distributedLockSvc the distributed lock svc
   */
  public DatabaseModule(
      Datastore primaryDatastore, Datastore secondaryDatastore, DistributedLockSvc distributedLockSvc) {
    this.primaryDatastore = primaryDatastore;
    this.secondaryDatastore = secondaryDatastore;

    datastoreMap.put(ReadPref.CRITICAL, primaryDatastore);
    datastoreMap.put(ReadPref.NORMAL, secondaryDatastore);
    this.distributedLockSvc = distributedLockSvc;
  }

  /* (non-Javadoc)
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    bind(Datastore.class).annotatedWith(Names.named("primaryDatastore")).toInstance(primaryDatastore);
    bind(Datastore.class).annotatedWith(Names.named("secondaryDatastore")).toInstance(secondaryDatastore);
    bind(new TypeLiteral<Map<ReadPref, Datastore>>() {})
        .annotatedWith(Names.named("datastoreMap"))
        .toInstance(datastoreMap);
    bind(DistributedLockSvc.class).toInstance(distributedLockSvc);
  }

  /**
   * Gets primary datastore.
   *
   * @return the primary datastore
   */
  public Datastore getPrimaryDatastore() {
    return primaryDatastore;
  }
}
