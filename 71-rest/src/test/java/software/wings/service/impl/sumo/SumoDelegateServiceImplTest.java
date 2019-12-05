package software.wings.service.impl.sumo;

import static io.harness.rule.OwnerRule.BRETT;
import static io.harness.rule.OwnerRule.SRIRAM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.sumologic.client.SumoClientException;
import com.sumologic.client.SumoLogicClient;
import com.sumologic.client.SumoServerException;
import io.harness.category.element.UnitTests;
import io.harness.exception.VerificationOperationException;
import io.harness.exception.WingsException;
import io.harness.rule.OwnerRule.Owner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import software.wings.WingsBaseTest;
import software.wings.beans.SumoConfig;
import software.wings.service.impl.security.EncryptionServiceImpl;
import software.wings.service.intfc.security.EncryptionService;

import java.io.IOException;
import java.util.Collections;

/**
 * Created by sriram_parthasarathy on 9/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class SumoDelegateServiceImplTest extends WingsBaseTest {
  @Mock SumoConfig sumoConfig;

  @Mock SumoLogicClient sumoLogicClient;

  @Mock EncryptionService encryptionService;

  @Before
  public void setUp() {
    when(sumoConfig.getAccessId()).thenReturn("1234".toCharArray());
    when(sumoConfig.getAccessKey()).thenReturn("3456".toCharArray());
    when(sumoConfig.getSumoUrl()).thenReturn("https://localhost:9000/");
  }

  @Test
  @Owner(developers = BRETT)
  @Category(UnitTests.class)
  public void testValidateConfigBadUrl() throws IOException, IllegalAccessException {
    when(sumoConfig.getSumoUrl()).thenReturn("htt//localhost:9000/");
    SumoDelegateServiceImpl sumoDelegateService = new SumoDelegateServiceImpl();
    FieldUtils.writeField(sumoDelegateService, "encryptionService", new EncryptionServiceImpl(null, null), true);
    String exceptionMsg = "";
    try {
      sumoDelegateService.validateConfig(sumoConfig, Collections.emptyList());
    } catch (VerificationOperationException e) {
      exceptionMsg = (String) e.getParams().get("reason");
    }
    assertThat(exceptionMsg).contains("Unable to create SumoLogic Client");
  }

  @Test(expected = SumoClientException.class)
  @Owner(developers = SRIRAM)
  @Category(UnitTests.class)
  public void testSumoException() throws IOException {
    doThrow(new SumoServerException("https://localhost:9000/", "{\"message\": \"This is broken\"}"))
        .when(sumoConfig)
        .getSumoUrl();
    SumoDelegateServiceImpl sumoDelegateService = new SumoDelegateServiceImpl();
    String msg = "This is broken";
    String exceptionMsg = "";
    try {
      sumoDelegateService.validateConfig(sumoConfig, Collections.emptyList());
    } catch (WingsException ex) {
      exceptionMsg = ex.getMessage();
    }
    assertThat(exceptionMsg).isEqualTo(msg);
  }
}
