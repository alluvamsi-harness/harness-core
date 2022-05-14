package io.harness.variable;

import static io.harness.annotations.dev.HarnessTeam.PL;

import io.harness.annotations.dev.OwnedBy;
import io.harness.remote.client.ClientMode;
import io.harness.remote.client.ServiceHttpClientConfig;
import io.harness.security.ServiceTokenGenerator;
import io.harness.serializer.kryo.KryoConverterFactory;
import io.harness.variable.remote.VariableClient;
import io.harness.variable.remote.VariableHttpClientFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

@OwnedBy(PL)
public class VariableClientModule extends AbstractModule {
  private static VariableClientModule instance;
  private final ServiceHttpClientConfig variableManagerClientConfig;
  private final String serviceSecret;
  private final String clientId;

  public VariableClientModule(
      ServiceHttpClientConfig variableManagerClientConfig, String serviceSecret, String clientId) {
    this.variableManagerClientConfig = variableManagerClientConfig;
    this.serviceSecret = serviceSecret;
    this.clientId = clientId;
  }

  public static VariableClientModule getInstance(
      ServiceHttpClientConfig serviceHttpClientConfig, String serviceSecret, String clientId) {
    if (instance == null) {
      instance = new VariableClientModule(serviceHttpClientConfig, serviceSecret, clientId);
    }

    return instance;
  }

  @Provides
  @Named("PRIVILEGED")
  private VariableHttpClientFactory privilegedVariableHttpClientFactory(KryoConverterFactory kryoConverterFactory) {
    return new VariableHttpClientFactory(variableManagerClientConfig, serviceSecret, new ServiceTokenGenerator(),
        kryoConverterFactory, clientId, ClientMode.PRIVILEGED);
  }

  @Provides
  @Named("NON_PRIVILEGED")
  private VariableHttpClientFactory nonPrivilegedVariableHttpClientFactory(KryoConverterFactory kryoConverterFactory) {
    return new VariableHttpClientFactory(variableManagerClientConfig, serviceSecret, new ServiceTokenGenerator(),
        kryoConverterFactory, clientId, ClientMode.NON_PRIVILEGED);
  }

  @Override
  protected void configure() {
    bind(VariableClient.class)
        .annotatedWith(Names.named(ClientMode.PRIVILEGED.name()))
        .toProvider(Key.get(VariableHttpClientFactory.class, Names.named(ClientMode.PRIVILEGED.name())))
        .in(Scopes.SINGLETON);
    bind(VariableClient.class)
        .toProvider(Key.get(VariableHttpClientFactory.class, Names.named(ClientMode.NON_PRIVILEGED.name())))
        .in(Scopes.SINGLETON);
  }
}
