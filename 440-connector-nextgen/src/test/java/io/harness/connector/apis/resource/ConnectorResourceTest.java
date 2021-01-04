package io.harness.connector.apis.resource;

import static io.harness.delegate.beans.connector.ConnectorType.KUBERNETES_CLUSTER;
import static io.harness.delegate.beans.connector.k8Connector.KubernetesCredentialType.INHERIT_FROM_DELEGATE;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import io.harness.CategoryTest;
import io.harness.category.element.UnitTests;
import io.harness.connector.apis.dto.ConnectorCatalogueItem;
import io.harness.connector.apis.dto.ConnectorCatalogueResponseDTO;
import io.harness.connector.apis.dto.ConnectorDTO;
import io.harness.connector.apis.dto.ConnectorFilterPropertiesDTO;
import io.harness.connector.apis.dto.ConnectorInfoDTO;
import io.harness.connector.apis.dto.ConnectorResponseDTO;
import io.harness.connector.helper.CatalogueHelper;
import io.harness.connector.services.ConnectorService;
import io.harness.delegate.beans.connector.ConnectorCategory;
import io.harness.delegate.beans.connector.ConnectorType;
import io.harness.delegate.beans.connector.ConnectorValidationResult;
import io.harness.delegate.beans.connector.k8Connector.KubernetesClusterConfigDTO;
import io.harness.delegate.beans.connector.k8Connector.KubernetesCredentialDTO;
import io.harness.delegate.beans.connector.k8Connector.KubernetesDelegateDetailsDTO;
import io.harness.ng.beans.PageResponse;
import io.harness.ng.core.dto.ResponseDTO;
import io.harness.rule.Owner;
import io.harness.rule.OwnerRule;
import io.harness.utils.PageTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

public class ConnectorResourceTest extends CategoryTest {
  @Mock private ConnectorService connectorService;
  @InjectMocks private ConnectorResource connectorResource;
  ConnectorResponseDTO connectorResponse;
  ConnectorInfoDTO connectorInfo;
  ConnectorDTO connectorRequest;
  String accountIdentifier = "accountIdentifier";
  ConnectorCatalogueResponseDTO catalogueResponseDTO;
  private final CatalogueHelper catalogueHelper = new CatalogueHelper();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    connectorInfo =
        ConnectorInfoDTO.builder()
            .name("connector")
            .identifier("identifier")
            .connectorType(KUBERNETES_CLUSTER)
            .connectorConfig(
                KubernetesClusterConfigDTO.builder()
                    .credential(KubernetesCredentialDTO.builder()
                                    .kubernetesCredentialType(INHERIT_FROM_DELEGATE)
                                    .config(KubernetesDelegateDetailsDTO.builder().delegateName("delegateName").build())
                                    .build())
                    .build())
            .build();
    connectorRequest = ConnectorDTO.builder().connectorInfo(connectorInfo).build();
    connectorResponse = ConnectorResponseDTO.builder().connector(connectorInfo).build();
    catalogueResponseDTO = setUpCatalogueResponse();
  }

  private ConnectorCatalogueResponseDTO setUpCatalogueResponse() {
    return ConnectorCatalogueResponseDTO.builder()
        .catalogue(catalogueHelper.getConnectorTypeToCategoryMapping())
        .build();
  }

  @Test
  @Owner(developers = OwnerRule.DEEPAK)
  @Category(UnitTests.class)
  public void create() {
    doReturn(connectorResponse).when(connectorService).create(any(), any());
    ResponseDTO<ConnectorResponseDTO> connectorResponseDTO =
        connectorResource.create(connectorRequest, accountIdentifier);
    Mockito.verify(connectorService, times(1)).create(any(), any());
    assertThat(connectorResponseDTO.getData()).isNotNull();
  }

  @Test
  @Owner(developers = OwnerRule.DEEPAK)
  @Category(UnitTests.class)
  public void update() {
    when(connectorService.update(any(), any())).thenReturn(connectorResponse);
    ResponseDTO<ConnectorResponseDTO> connectorResponseDTO =
        connectorResource.update(connectorRequest, accountIdentifier);
    Mockito.verify(connectorService, times(1)).update(any(), any());
    assertThat(connectorResponseDTO.getData()).isNotNull();
  }

  @Test
  @Owner(developers = OwnerRule.DEEPAK)
  @Category(UnitTests.class)
  public void get() {
    when(connectorService.get(any(), any(), any(), any())).thenReturn(Optional.of(connectorResponse));
    ConnectorResponseDTO connectorRequestDTO =
        connectorResource.get("accountIdentifier", "orgIdentifier", "projectIdentifier", "connectorIdentifier")
            .getData();
    Mockito.verify(connectorService, times(1)).get(any(), any(), any(), any());
    assertThat(connectorRequestDTO).isNotNull();
  }

  @Test
  @Owner(developers = OwnerRule.DEEPAK)
  @Category(UnitTests.class)
  public void list() {
    String orgIdentifier = "orgIdentifier";
    String projectIdentifier = "projectIdentifier";
    String filterIdentifier = "filterIdentifier";
    String searchTerm = "searchTerm";
    ConnectorFilterPropertiesDTO connectorListFilter = ConnectorFilterPropertiesDTO.builder().build();
    final Page<ConnectorResponseDTO> page =
        PageTestUtils.getPage(Arrays.asList(ConnectorResponseDTO.builder().build()), 1);
    when(connectorService.list(
             0, 100, accountIdentifier, null, orgIdentifier, projectIdentifier, filterIdentifier, searchTerm, false))
        .thenReturn(page);
    ResponseDTO<PageResponse<ConnectorResponseDTO>> connectorSummaryListResponse = connectorResource.list(
        0, 100, accountIdentifier, searchTerm, orgIdentifier, projectIdentifier, filterIdentifier, false, null);
    Mockito.verify(connectorService, times(1))
        .list(eq(0), eq(100), eq(accountIdentifier), eq(null), eq(orgIdentifier), eq(projectIdentifier),
            eq(filterIdentifier), eq(searchTerm), eq(false));
    assertThat(connectorSummaryListResponse.getData()).isNotNull();
  }

  @Test
  @Owner(developers = OwnerRule.DEEPAK)
  @Category(UnitTests.class)
  public void delete() {
    when(connectorService.delete(any(), any(), any(), any())).thenReturn(true);
    ResponseDTO<Boolean> result =
        connectorResource.delete("accountIdentifier", "orgIdentifier", "projectIdentifier", "connectorIdentifier");
    Mockito.verify(connectorService, times(1)).delete(any(), any(), any(), any());
    assertThat(result.getData()).isTrue();
  }

  @Test
  @Owner(developers = OwnerRule.DEEPAK)
  @Category(UnitTests.class)
  public void validateTheIdentifierIsUniqueTest() {
    when(connectorService.validateTheIdentifierIsUnique(any(), any(), any(), any())).thenReturn(true);
    ResponseDTO<Boolean> result = connectorResource.validateTheIdentifierIsUnique(
        "accountIdentifier", "orgIdentifier", "projectIdentifier", "connectorIdentifier");
    Mockito.verify(connectorService, times(1)).validateTheIdentifierIsUnique(any(), any(), any(), any());
    assertThat(result.getData()).isTrue();
  }

  @Test
  @Owner(developers = OwnerRule.DEEPAK)
  @Category(UnitTests.class)
  public void validateTest() {
    ResponseDTO<ConnectorValidationResult> result = connectorResource.validate(connectorRequest, accountIdentifier);
    Mockito.verify(connectorService, times(1)).validate(eq(connectorRequest), eq(accountIdentifier));
  }

  @Test
  @Owner(developers = OwnerRule.DEEPAK)
  @Category(UnitTests.class)
  public void testConnectionResourceTest() {
    ResponseDTO<ConnectorValidationResult> validationResult = connectorResource.testConnection(
        "accountIdentifier", "orgIdentifier", "projectIdentifier", "connectorIdentifier");
    Mockito.verify(connectorService, times(1)).testConnection(any(), any(), any(), any());
  }

  @Test
  @Owner(developers = OwnerRule.HARSH)
  @Category(UnitTests.class)
  public void testGitRepoConnectionResourceTest() {
    ResponseDTO<ConnectorValidationResult> validationResult =
        connectorResource.testGitRepoConnection("accountIdentifier", "orgIdentifier", "projectIdentifier",
            "connectorIdentifier", "https://github.com/wings-software/portal.git");
    Mockito.verify(connectorService, times(1)).testGitRepoConnection(any(), any(), any(), any(), any());
  }

  @Test
  @Owner(developers = OwnerRule.VARDAN_BANSAL)
  @Category(UnitTests.class)
  public void getConnectorCatalogueTest() {
    when(connectorService.getConnectorCatalogue()).thenReturn(catalogueResponseDTO);
    final ResponseDTO<ConnectorCatalogueResponseDTO> response =
        connectorResource.getConnectorCatalogue("accountIdentifier");
    assertThat(response).isNotNull();
    final List<ConnectorCatalogueItem> catalogue = response.getData().getCatalogue();
    assertThat(catalogue.size()).isEqualTo(ConnectorCategory.values().length);
    final int totalConnectorsWithinAllCategories =
        catalogue.stream().map(item -> item.getConnectors().size()).mapToInt(Integer::intValue).sum();
    // Handling it specially for git.
    assertThat(totalConnectorsWithinAllCategories).isEqualTo(ConnectorType.values().length - 1);
    Mockito.verify(connectorService, times(1)).getConnectorCatalogue();
  }
}
