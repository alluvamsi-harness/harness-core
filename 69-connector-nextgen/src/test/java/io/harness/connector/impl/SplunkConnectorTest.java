package io.harness.connector.impl;

import static io.harness.delegate.beans.connector.ConnectorType.SPLUNK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import io.harness.CategoryTest;
import io.harness.category.element.UnitTests;
import io.harness.connector.apis.dto.ConnectorDTO;
import io.harness.connector.apis.dto.ConnectorRequestDTO;
import io.harness.connector.entities.embedded.splunkconnector.SplunkConnector;
import io.harness.connector.mappers.ConnectorMapper;
import io.harness.connector.repositories.base.ConnectorRepository;
import io.harness.delegate.beans.connector.splunkconnector.SplunkConnectorDTO;
import io.harness.rule.Owner;
import io.harness.rule.OwnerRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SplunkConnectorTest extends CategoryTest {
  @Mock ConnectorMapper connectorMapper;
  @Mock ConnectorRepository connectorRepository;
  @InjectMocks DefaultConnectorServiceImpl connectorService;

  String userName = "userName";
  String password = "password";
  String identifier = "identifier";
  String name = "name";
  String splunkUrl = "https://xwz.com";
  ConnectorRequestDTO connectorRequestDTO;
  ConnectorDTO connectorDTO;
  SplunkConnector connector;
  String accountIdentifier = "accountIdentifier";
  @Rule public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    connector = SplunkConnector.builder()
                    .username(userName)
                    .accountId(accountIdentifier)
                    .splunkUrl(splunkUrl)
                    .passwordReference(password)
                    .build();

    connector.setType(SPLUNK);
    connector.setIdentifier(identifier);
    connector.setName(name);

    SplunkConnectorDTO splunkConnectorDTO = SplunkConnectorDTO.builder()
                                                .username(userName)
                                                .accountId(accountIdentifier)
                                                .splunkUrl(splunkUrl)
                                                .passwordReference(password)
                                                .build();

    connectorRequestDTO = ConnectorRequestDTO.builder()
                              .name(name)
                              .identifier(identifier)
                              .connectorType(SPLUNK)
                              .connectorConfig(splunkConnectorDTO)
                              .build();

    connectorDTO = ConnectorDTO.builder()
                       .name(name)
                       .identifier(identifier)
                       .connectorType(SPLUNK)
                       .connectorConfig(splunkConnectorDTO)
                       .build();

    when(connectorRepository.save(connector)).thenReturn(connector);
    when(connectorMapper.writeDTO(connector)).thenReturn(connectorDTO);
    when(connectorMapper.toConnector(connectorRequestDTO, accountIdentifier)).thenReturn(connector);
  }

  private ConnectorDTO createConnector() {
    return connectorService.create(connectorRequestDTO, accountIdentifier);
  }

  @Test
  @Owner(developers = OwnerRule.NEMANJA)
  @Category(UnitTests.class)
  public void testCreateSplunkConnector() {
    ConnectorDTO connectorDTOOutput = createConnector();
    ensureSplunkConnectorFieldsAreCorrect(connectorDTOOutput);
  }
  private void ensureSplunkConnectorFieldsAreCorrect(ConnectorDTO connectorDTOOutput) {
    assertThat(connectorDTOOutput).isNotNull();
    assertThat(connectorDTOOutput.getName()).isEqualTo(name);
    assertThat(connectorDTOOutput.getIdentifier()).isEqualTo(identifier);
    assertThat(connectorDTOOutput.getConnectorType()).isEqualTo(SPLUNK);
    SplunkConnectorDTO splunkConnectorDTO = (SplunkConnectorDTO) connectorDTOOutput.getConnectorConfig();
    assertThat(splunkConnectorDTO).isNotNull();
    assertThat(splunkConnectorDTO.getUsername()).isEqualTo(userName);
    assertThat(splunkConnectorDTO.getPasswordReference()).isEqualTo(password);
    assertThat(splunkConnectorDTO.getSplunkUrl()).isEqualTo(splunkUrl);
    assertThat(splunkConnectorDTO.getAccountId()).isEqualTo(accountIdentifier);
  }

  @Test
  @Owner(developers = OwnerRule.NEMANJA)
  @Category(UnitTests.class)
  public void testGetSplunkConnector() {
    createConnector();
    when(connectorRepository.findByFullyQualifiedIdentifier(anyString())).thenReturn(Optional.of(connector));
    ConnectorDTO connectorDTO = connectorService.get(accountIdentifier, null, null, identifier).get();
    ensureSplunkConnectorFieldsAreCorrect(connectorDTO);
  }
}
