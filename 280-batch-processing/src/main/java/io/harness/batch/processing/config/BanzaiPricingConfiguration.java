package io.harness.batch.processing.config;

import static io.harness.network.Http.getOkHttpClientBuilder;

import io.harness.batch.processing.pricing.client.BanzaiPricingClient;
import io.harness.network.Http;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Slf4j
@Configuration
public class BanzaiPricingConfiguration {
  private String BASE_PRICING_SERVICE_URL = "http://35.232.8.217:80/";

  @Bean
  public BanzaiPricingClient banzaiPricingClient() {
    log.info("BASE_PRICING_SERVICE_URL: {}", BASE_PRICING_SERVICE_URL);
    OkHttpClient okHttpClient = getOkHttpClientBuilder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .proxy(Http.checkAndGetNonProxyIfApplicable(BASE_PRICING_SERVICE_URL))
                                    .retryOnConnectionFailure(true)
                                    .build();
    Retrofit retrofit = new Retrofit.Builder()
                            .client(okHttpClient)
                            .baseUrl(BASE_PRICING_SERVICE_URL)
                            .addConverterFactory(JacksonConverterFactory.create())
                            .build();
    return retrofit.create(BanzaiPricingClient.class);
  }
}
