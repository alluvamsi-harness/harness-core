package software.wings.integration;

import static io.harness.data.structure.EmptyPredicate.isNotEmpty;
import static javax.ws.rs.client.Entity.entity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.annotations.Entity;
import software.wings.beans.Account;
import software.wings.beans.AccountStatus;
import software.wings.beans.AccountType;
import software.wings.beans.Application;
import software.wings.beans.Environment;
import software.wings.beans.LicenseInfo;
import software.wings.beans.Pipeline;
import software.wings.beans.RestResponse;
import software.wings.beans.Workflow;
import software.wings.beans.trigger.Trigger;
import software.wings.common.Constants;
import software.wings.dl.exportimport.ExportMode;
import software.wings.dl.exportimport.ImportStatusReport;
import software.wings.verification.CVConfiguration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

/**
 * @author marklu on 10/25/18
 */
public class AccountExportImportIntegrationTest extends BaseIntegrationTest {
  private String accountId;

  @Before
  public void setUp() {
    super.loginAdminUser();

    Account account = accountService.getByName("Harness");
    assertNotNull(account);

    accountId = account.getUuid();
  }

  @Test
  public void testAccountExportImport() throws Exception {
    byte[] exportedAccountData = exportAccountData(accountId);
    assertNotNull(exportedAccountData);

    ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(exportedAccountData));

    boolean hasAccounts = false;
    boolean hasApplications = false;
    ZipEntry zipEntry;
    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
      if (zipEntry.getName().equals("accounts.json")) {
        hasAccounts = true;
      } else if (zipEntry.getName().equals("applications.json")) {
        hasApplications = true;
      }
    }
    assertTrue(hasAccounts);
    assertTrue(hasApplications);

    importAccountData(accountId, exportedAccountData);
  }

  @Test
  public void testImportBrandNewAccountDataFromZipFile() {
    String qaHarnessAccountId = "zEaak-FLS425IEO7OLzMUg";
    String qaHarnessAccountName = "Harness-QA";

    if (accountService.exists(qaHarnessAccountName)) {
      deleteAccount(qaHarnessAccountId);
    }
    createAccount(qaHarnessAccountId, qaHarnessAccountName);

    try {
      String qaHarnessAccountDataZipFile = "./exportimport/account_zEaak-FLS425IEO7OLzMUg.zip";
      importAccountDataFromFile(qaHarnessAccountId, qaHarnessAccountDataZipFile);

      // Verify relevant data has been imported successfully.
      Application application = appService.getAppByName(qaHarnessAccountId, "Harness Verification");
      assertNotNull(application);
      Pipeline pipeline =
          pipelineService.getPipelineByName(application.getAppId(), "Continuous Verification NewRelic Splunk Elk");
      assertNotNull(pipeline);
      Environment environment = environmentService.getEnvironmentByName(application.getUuid(), "Production");
      Workflow workflow = workflowService.readWorkflowByName(application.getAppId(), "CV Containers Canary AppD");
      assertNotNull(workflow);
      Trigger trigger = triggerService.getTriggerByWebhookToken("q1JbgjsCqeggY3Y6z3QPCvXpvQtBzZRPcSCm0Rqm");
      assertNotNull(trigger);
      CVConfiguration cvConfiguration =
          cvConfigurationService.getConfiguration("Manager Prod", application.getUuid(), environment.getUuid());
      assertNotNull(cvConfiguration);
    } finally {
      // Delete the imported account after done.
      deleteAccount(qaHarnessAccountId);
    }
  }

  @Test
  public void testSpecificExport() throws Exception {
    byte[] exportedAccountData =
        exportSpecificAccountData(accountId, Application.class.getAnnotation(Entity.class).value());
    assertNotNull(exportedAccountData);

    ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(exportedAccountData));

    boolean hasUsers = false;
    boolean hasApplications = false;
    ZipEntry zipEntry;
    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
      if (zipEntry.getName().equals("users.json")) {
        hasUsers = true;
      } else if (zipEntry.getName().equals("applications.json")) {
        hasApplications = true;
      }
    }
    assertFalse(hasUsers);
    assertTrue(hasApplications);
  }

  @Test
  public void testSpecificExport_noEntityTypes_shouldFail() {
    try {
      WebTarget target =
          client.target(API_BASE + "/account/export?accountId=" + accountId + "&mode=" + ExportMode.SPECIFIC);
      getRequestBuilderWithAuthHeader(target).get(new GenericType<RestResponse<String>>() {});
      fail("Should not reach here, exception is expected");
    } catch (Exception e) {
      // Exception is expected
    }
  }

  private byte[] exportAccountData(String accountId) {
    WebTarget target = client.target(API_BASE + "/account/export?accountId=" + accountId);
    byte[] responseZip = getRequestBuilderWithAuthHeader(target).get(new GenericType<byte[]>() {});
    assertTrue(isNotEmpty(responseZip));

    return responseZip;
  }

  private byte[] exportSpecificAccountData(String accountId, String entityType) {
    WebTarget target = client.target(API_BASE + "/account/export?accountId=" + accountId
        + "&mode=" + ExportMode.SPECIFIC + "&entityTypes=" + entityType);
    byte[] responseZip = getRequestBuilderWithAuthHeader(target).get(new GenericType<byte[]>() {});
    assertTrue(isNotEmpty(responseZip));

    return responseZip;
  }

  private void importAccountData(String accountId, byte[] accountDataJson) {
    MultiPart multiPart = new MultiPart();
    FormDataBodyPart formDataBodyPart =
        new FormDataBodyPart("file", accountDataJson, MediaType.MULTIPART_FORM_DATA_TYPE);
    multiPart.bodyPart(formDataBodyPart);

    WebTarget target = client.target(API_BASE + "/account/import?accountId=" + accountId);
    RestResponse<Void> restResponse = getRequestBuilderWithAuthHeader(target).post(
        entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE), new GenericType<RestResponse<Void>>() {});
    assertEquals(0, restResponse.getResponseMessages().size());
  }

  private void importAccountDataFromFile(String accountId, String accountDataZipFile) {
    File fileToImport = new File(getClass().getClassLoader().getResource(accountDataZipFile).getFile());

    MultiPart multiPart = new MultiPart();
    FormDataBodyPart formDataBodyPart = new FormDataBodyPart("file", fileToImport, MediaType.MULTIPART_FORM_DATA_TYPE);
    multiPart.bodyPart(formDataBodyPart);

    WebTarget target = client.target(
        API_BASE + "/account/import?accountId=" + accountId + "&disableSchemaCheck=true&disableNaturalKeyCheck=false");
    RestResponse<ImportStatusReport> restResponse = getRequestBuilderWithAuthHeader(target).post(
        entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE), new GenericType<RestResponse<ImportStatusReport>>() {});
    // Verify vault config was successfully created.
    assertEquals(0, restResponse.getResponseMessages().size());
    assertNotNull(restResponse.getResource());
    assertTrue(restResponse.getResource().getStatuses().size() > 0);
  }

  private void createAccount(String accountId, String accountName) {
    Account account = Account.Builder.anAccount()
                          .withUuid(accountId)
                          .withAccountName(accountName)
                          .withCompanyName(accountName)
                          .withLicenseInfo(LicenseInfo.builder()
                                               .accountType(AccountType.PAID)
                                               .accountStatus(AccountStatus.ACTIVE)
                                               .licenseUnits(Constants.DEFAULT_PAID_LICENSE_UNITS)
                                               .build())
                          .build();

    WebTarget target = client.target(API_BASE + "/users/account?addUser=true");
    RestResponse<Account> response = getRequestBuilderWithAuthHeader(target).post(
        entity(account, MediaType.APPLICATION_JSON), new GenericType<RestResponse<Account>>() {});
    assertNotNull(response.getResource());
    assertTrue(accountService.exists(account.getAccountName()));
    assertNotNull(accountService.getByName(account.getCompanyName()));
  }

  private void deleteAccount(String accountId) {
    WebTarget target = client.target(API_BASE + "/account/" + accountId);
    RestResponse<Boolean> response =
        getRequestBuilderWithAuthHeader(target).delete(new GenericType<RestResponse<Boolean>>() {});
    assertNotNull(response.getResource());
    assertTrue(response.getResource());
  }
}
