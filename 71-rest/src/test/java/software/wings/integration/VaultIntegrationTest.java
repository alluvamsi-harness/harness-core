package software.wings.integration;

import static io.harness.data.structure.EmptyPredicate.isEmpty;
import static io.harness.data.structure.EmptyPredicate.isNotEmpty;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import io.harness.security.encryption.EncryptionConfig;
import io.harness.security.encryption.EncryptionType;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.Before;
import org.junit.Test;
import software.wings.beans.Account;
import software.wings.beans.GcpConfig;
import software.wings.beans.KmsConfig;
import software.wings.beans.RestResponse;
import software.wings.beans.SettingAttribute;
import software.wings.beans.VaultConfig;
import software.wings.common.Constants;
import software.wings.security.encryption.EncryptedData;
import software.wings.security.encryption.SecretChangeLog;
import software.wings.service.impl.security.SecretText;
import software.wings.service.intfc.security.SecretManagementDelegateService;
import software.wings.service.intfc.security.SecretManager;
import software.wings.settings.SettingValue;
import software.wings.settings.SettingValue.SettingVariableTypes;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

/**
 * Created by rsingh on 9/21/18.
 */
public class VaultIntegrationTest extends BaseIntegrationTest {
  private static final String VAULT_URL_1 = "http://127.0.0.1:8200";
  private static final String VAULT_URL_2 = "http://127.0.0.1:8300";
  private static final String VAULT_BASE_PATH = "/foo/bar";
  private static final String VAULT_BASE_PATH_2 = "foo2/bar2/ ";
  private static final String VAULT_BASE_PATH_3 = " /";

  @Inject private SecretManagementDelegateService secretManagementDelegateService;

  private String vaultToken;
  private VaultConfig vaultConfig;
  private VaultConfig vaultConfig2;

  private VaultConfig vaultConfigWithBasePath;
  private VaultConfig vaultConfigWithBasePath2;
  private VaultConfig vaultConfigWithBasePath3;

  private KmsConfig kmsConfig;

  @Before
  public void setUp() {
    super.loginAdminUser();
    this.vaultToken = System.getProperty("vault.token", "root");
    Preconditions.checkState(isNotEmpty(vaultToken));

    vaultConfig = VaultConfig.builder()
                      .accountId(accountId)
                      .name("TestVault")
                      .vaultUrl(VAULT_URL_1)
                      .authToken(vaultToken)
                      .isDefault(true)
                      .build();

    vaultConfig2 = VaultConfig.builder()
                       .accountId(accountId)
                       .name("TestVault2")
                       .vaultUrl(VAULT_URL_2)
                       .authToken(vaultToken)
                       .isDefault(true)
                       .build();

    vaultConfigWithBasePath = VaultConfig.builder()
                                  .accountId(accountId)
                                  .name("TestVaultWithBasePath")
                                  .vaultUrl(VAULT_URL_1)
                                  .authToken(vaultToken)
                                  .basePath(VAULT_BASE_PATH)
                                  .isDefault(true)
                                  .build();

    vaultConfigWithBasePath2 = VaultConfig.builder()
                                   .accountId(accountId)
                                   .name("TestVaultWithBasePath")
                                   .vaultUrl(VAULT_URL_1)
                                   .authToken(vaultToken)
                                   .basePath(VAULT_BASE_PATH_2)
                                   .isDefault(true)
                                   .build();

    vaultConfigWithBasePath3 = VaultConfig.builder()
                                   .accountId(accountId)
                                   .name("TestVaultWithRootBasePath")
                                   .vaultUrl(VAULT_URL_1)
                                   .authToken(vaultToken)
                                   .basePath(VAULT_BASE_PATH_3)
                                   .isDefault(true)
                                   .build();

    kmsConfig = KmsConfig.builder()
                    .accountId(accountId)
                    .name("TestAwsKMS")
                    .accessKey("AKIAJRYQHJZ4S6P3RPRQ")
                    .kmsArn("arn:aws:kms:us-east-1:448640225317:key/60b9e9c5-3a1b-43fb-b345-e79ded0d94c3")
                    .secretKey("Gw0XEQ9v6GAdnx4T0EX5HaoYFSHiqQ85h0XYow+i")
                    .region("us-east-1")
                    .isDefault(true)
                    .build();
  }

  @Test
  public void test_VaultOperations_accountLocalEncryptionEnabled_shouldFail() {
    // 1. Create a new Vault config.
    String vaultConfigId = createVaultConfig(vaultConfig);

    // 2. update account to be 'localEncryptionEnabled'
    wingsPersistence.updateField(Account.class, accountId, "localEncryptionEnabled", true);

    // 3. account encryption type is LOCAL
    EncryptionType encryptionType = secretManager.getEncryptionType(accountId);
    assertEquals(EncryptionType.LOCAL, encryptionType);

    // 4. No secret manager will be returned
    List<EncryptionConfig> secretManagers = secretManager.listEncryptionConfig(accountId);
    assertEquals(0, secretManagers.size());

    // 5. Create new VAULT secret manager should fail.
    try {
      createVaultConfig(vaultConfig2);
      fail("Can't create new Vault secret manager if the account has LOCAL encryption enabled!");
    } catch (Exception e) {
      // Exception is expected.
    } finally {
      // 6. Disable LOCAL encryption for this account
      wingsPersistence.updateField(Account.class, accountId, "localEncryptionEnabled", false);

      // 7. Delete the vault config
      deleteVaultConfig(vaultConfigId);
    }
  }

  @Test
  public void testCreateKmsVaultConfig_shouldSucceed() {
    // 1. Create a new KMS config.
    String kmsConfigId = createKmsConfig(kmsConfig);

    // 2. Create a new Vault config.
    String vaultConfigId = createVaultConfig(vaultConfig);
    VaultConfig savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
    assertNotNull(savedVaultConfig);

    try {
      testUpdateSecretText(savedVaultConfig);
    } finally {
      deleteKmsConfig(kmsConfigId);
    }
  }

  @Test
  public void testCreateUpdateDeleteVaultConfig_shouldSucceed() {
    // 1. Create a new Vault config.
    String vaultConfigId = createVaultConfig(vaultConfig);

    try {
      VaultConfig savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
      assertNotNull(savedVaultConfig);

      // 2. Update the existing vault config to make it default
      savedVaultConfig.setAuthToken(vaultToken);
      updateVaultConfig(savedVaultConfig);

      savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
      assertNotNull(savedVaultConfig);
      assertTrue(savedVaultConfig.isDefault());
    } finally {
      // 3. Delete the vault config
      deleteVaultConfig(vaultConfigId);
    }
  }

  @Test
  public void test_createDuplicateVaultSecretManager_shouldFail() {
    // 1. Create a new Vault config.
    String vaultConfigId = createVaultConfig(vaultConfig);

    // 2. Create the same Vault config with a different name.
    try {
      updateVaultConfig(vaultConfig);
      fail("Exception is expected when creating the same Vault secret manager with a different name");
    } catch (Exception e) {
      // Ignore. Expected.
    } finally {
      // 3. Delete the vault config
      deleteVaultConfig(vaultConfigId);
    }
  }

  @Test
  public void test_updateVaultBasePath_shouldSucceed() {
    // Create the first default vault config
    String vaultConfigId = createVaultConfig(vaultConfig);

    VaultConfig savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
    assertNotNull(savedVaultConfig);

    // Update the vault base path
    savedVaultConfig.setBasePath(VAULT_BASE_PATH);
    savedVaultConfig.setAuthToken(Constants.SECRET_MASK);
    updateVaultConfig(savedVaultConfig);

    savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
    assertEquals(VAULT_BASE_PATH, savedVaultConfig.getBasePath());

    deleteVaultConfig(vaultConfigId);
  }

  @Test
  public void test_createNewDefaultVault_shouldUnsetPreviousDefaultVaultConfig() {
    // Create the first default vault config
    String vaultConfigId = createVaultConfig(vaultConfig);
    // Create 2nd default vault config. The 1 vault will be set to be non-default.
    String vaultConfig2Id = createVaultConfig(vaultConfig2);

    VaultConfig savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
    assertNotNull(savedVaultConfig);

    VaultConfig savedVaultConfig2 = wingsPersistence.get(VaultConfig.class, vaultConfig2Id);
    assertNotNull(savedVaultConfig2);

    try {
      assertFalse(savedVaultConfig.isDefault());
      assertTrue(savedVaultConfig2.isDefault());

      // Update 1st vault config to be default again. 2nd vault config will be set to be non-default.
      savedVaultConfig.setDefault(true);
      savedVaultConfig.setAuthToken(vaultToken);
      updateVaultConfig(savedVaultConfig);

      savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
      assertTrue(savedVaultConfig.isDefault());

      savedVaultConfig2 = wingsPersistence.get(VaultConfig.class, vaultConfig2Id);
      assertFalse(savedVaultConfig2.isDefault());
    } finally {
      // Delete both vault configs.
      deleteVaultConfig(vaultConfigId);
      deleteVaultConfig(vaultConfig2Id);
    }
  }

  @Test
  public void test_unsetOnlyDefaultVault_shouldFail() {
    // Create the first default vault config
    String vaultConfigId = createVaultConfig(vaultConfig);
    VaultConfig savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
    assertNotNull(savedVaultConfig);

    try {
      savedVaultConfig.setDefault(false);
      savedVaultConfig.setAuthToken(vaultToken);
      updateVaultConfig(savedVaultConfig);
      fail("Unset the only default vault config manager will fail!");
    } catch (Exception e) {
      // Exception is expected.
    } finally {
      // Clean up.
      deleteVaultConfig(vaultConfigId);
    }
  }

  @Test
  public void test_UpdateSecretTextWithValue_VaultWithBasePath_shouldSucceed() {
    // Create the first default vault config
    String vaultConfigId = createVaultConfig(vaultConfigWithBasePath);
    VaultConfig savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
    assertNotNull(savedVaultConfig);

    testUpdateSecretText(savedVaultConfig);
  }

  @Test
  public void test_UpdateSecretTextWithValue_VaultWithBasePath2_shouldSucceed() {
    // Create the first default vault config
    String vaultConfigId = createVaultConfig(vaultConfigWithBasePath2);
    VaultConfig savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
    assertNotNull(savedVaultConfig);

    testUpdateSecretText(savedVaultConfig);
  }

  @Test
  public void test_UpdateSecretTextWithValue_shouldSucceed() {
    // Create the first default vault config
    String vaultConfigId = createVaultConfig(vaultConfig);
    VaultConfig savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
    assertNotNull(savedVaultConfig);

    testUpdateSecretText(savedVaultConfig);
  }

  private void testUpdateSecretText(VaultConfig savedVaultConfig) {
    String secretUuid = null;
    try {
      secretUuid = createSecretText("FooBarSecret", "MySecretValue", null);
      updateSecretText(secretUuid, "FooBarSecret_Modified", "MySecretValue_Modified", null);
      verifySecretValue(secretUuid, "MySecretValue_Modified", savedVaultConfig);
    } finally {
      // Clean up.
      if (secretUuid != null) {
        deleteSecretText(secretUuid);
      }
      deleteVaultConfig(savedVaultConfig.getUuid());
    }
  }

  @Test
  public void test_CreateSecretText_WithInvalidPath_shouldFail() {
    // Create the first default vault config
    String vaultConfigId = createVaultConfig(vaultConfig);
    VaultConfig savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
    assertNotNull(savedVaultConfig);

    try {
      createSecretText("FooBarSecret", null, "foo/bar/InvalidSecretPath");
      fail("Expected to fail when creating a secret text pointing to an invalid Vault path.");
    } catch (Exception e) {
      // Exception expected.
    } finally {
      // Clean up.
      deleteVaultConfig(vaultConfigId);
    }
  }

  @Test
  public void test_UpdateSecretText_WithInvalidPath_shouldFail() {
    String vaultConfigId = createVaultConfig(vaultConfig);
    VaultConfig savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
    assertNotNull(savedVaultConfig);

    String secretValue = "MySecretValue";
    String secretUuid1 = null;
    String secretUuid2 = null;
    try {
      // This will create one secret at path 'harness/SECRET_TEXT/FooSecret".
      secretUuid1 = createSecretText("FooSecret", secretValue, null);
      // Second secret will refer the first secret by relative path, as the default root is "/harness".
      secretUuid2 = createSecretText("BarSecret", null, "SECRET_TEXT/FooSecret");
      updateSecretText(secretUuid2, "BarSecret", null, "foo/bar/InvalidSecretPath");
      fail("Expected to fail when updating a secret text pointing to an invalid Vault path.");
    } catch (Exception e) {
      // Exception is expected here.
    } finally {
      if (secretUuid1 != null) {
        deleteSecretText(secretUuid1);
      }
      if (secretUuid2 != null) {
        deleteSecretText(secretUuid2);
      }
      // Clean up.
      deleteVaultConfig(vaultConfigId);
    }
  }

  @Test
  public void test_CreateSecretText_withInvalidPathReference_shouldFail() {
    String vaultConfigId = createVaultConfig(vaultConfig);
    VaultConfig savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
    assertNotNull(savedVaultConfig);

    String secretName = "MySecret";
    String secretName2 = "AbsolutePathSecret";
    String pathPrefix = isEmpty(savedVaultConfig.getBasePath()) ? "/harness" : savedVaultConfig.getBasePath();
    String absoluteSecretPathWithNoPound = pathPrefix + "/SECRET_TEXT/" + secretName + "/FooSecret";

    try {
      testCreateSecretText(savedVaultConfig, secretName, secretName2, absoluteSecretPathWithNoPound);
      fail("Saved with secret path doesn't contain # should fail");
    } catch (Exception e) {
      // Exception is expected.
    }
  }

  @Test
  public void test_CreateSecretText_WithValidPath_shouldSucceed() {
    testCreateSecretText(vaultConfig);
  }

  @Test
  public void test_importSecrets_fromCSV_shouldSucceed() {
    importSecretTextsFromCsv("./encryption/secrets.csv");
    verifySecretTextExists("secret1");
    verifySecretTextExists("secret3");
  }

  @Test
  public void test_listSettingAttributes_shoudlSucceed() throws IllegalAccessException {
    List<SettingAttribute> settingAttributes = listSettingAttributes();
    for (SettingAttribute settingAttribute : settingAttributes) {
      SettingValue settingValue = settingAttribute.getValue();
      if (settingValue instanceof GcpConfig) {
        // For some reason GcpConfig is not seting the full encrypted value back. Skip it for now.
        continue;
      }
      List<Field> encryptedFields = settingValue.getEncryptedFields();
      for (Field field : encryptedFields) {
        field.setAccessible(true);
        char[] secrets = (char[]) field.get(settingValue);
        assertArrayEquals(SecretManager.ENCRYPTED_FIELD_MASK.toCharArray(), secrets);
      }
    }
  }

  @Test
  public void test_CreateSecretText_vaultWithBasePath_validPath_shouldSucceed() {
    testCreateSecretText(vaultConfigWithBasePath);
  }

  @Test
  public void test_CreateSecretText_vaultWithBasePath2_validPath_shouldSucceed() {
    testCreateSecretText(vaultConfigWithBasePath2);
  }

  @Test
  public void test_CreateSecretText_vaultWithBasePath3_validPath_shouldSucceed() {
    testCreateSecretText(vaultConfigWithBasePath3);
  }

  private void testCreateSecretText(VaultConfig vaultconfig) {
    String vaultConfigId = createVaultConfig(vaultconfig);
    VaultConfig savedVaultConfig = wingsPersistence.get(VaultConfig.class, vaultConfigId);
    assertNotNull(savedVaultConfig);

    String secretName = "FooSecret";
    String secretName2 = "AbsolutePathSecret";
    String pathPrefix = isEmpty(savedVaultConfig.getBasePath()) ? "/harness" : savedVaultConfig.getBasePath();
    String absoluteSecretPath = pathPrefix + "/SECRET_TEXT/" + secretName + "#value";
    testCreateSecretText(savedVaultConfig, secretName, secretName2, absoluteSecretPath);
  }

  private void testCreateSecretText(
      VaultConfig savedVaultConfig, String secretName, String secretName2, String absoluteSecretPath) {
    String secretValue = "MySecretValue";
    String secretUuid1 = null;
    String secretUuid2 = null;
    try {
      // This will create one secret at path 'harness/SECRET_TEXT/FooSecret".
      secretUuid1 = createSecretText(secretName, secretValue, null);
      verifySecretValue(secretUuid1, secretValue, savedVaultConfig);

      // Second secret will refer the first secret by absolute path of format "/foo/bar/FooSecret#value'.
      secretUuid2 = createSecretText(secretName2, null, absoluteSecretPath);
      verifySecretValue(secretUuid2, secretValue, savedVaultConfig);
      verifyVaultChangeLog(secretUuid2);

    } finally {
      if (secretUuid1 != null) {
        deleteSecretText(secretUuid1);
      }
      if (secretUuid2 != null) {
        deleteSecretText(secretUuid2);
      }
      // Clean up.
      deleteVaultConfig(savedVaultConfig.getUuid());
    }
  }

  private String createSecretText(String name, String value, String path) {
    WebTarget target = client.target(API_BASE + "/secrets/add-secret?accountId=" + accountId);
    SecretText secretText = SecretText.builder().name(name).value(value).path(path).build();
    RestResponse<String> restResponse = getRequestBuilderWithAuthHeader(target).post(
        entity(secretText, APPLICATION_JSON), new GenericType<RestResponse<String>>() {});
    // Verify vault config was successfully created.
    assertEquals(0, restResponse.getResponseMessages().size());
    String encryptedDataId = restResponse.getResource();
    assertTrue(isNotEmpty(encryptedDataId));

    return encryptedDataId;
  }

  private void updateSecretText(String uuid, String name, String value, String path) {
    WebTarget target = client.target(API_BASE + "/secrets/update-secret?accountId=" + accountId + "&uuid=" + uuid);
    SecretText secretText = SecretText.builder().name(name).value(value).path(path).build();
    RestResponse<Boolean> restResponse = getRequestBuilderWithAuthHeader(target).post(
        entity(secretText, APPLICATION_JSON), new GenericType<RestResponse<Boolean>>() {});
    // Verify vault config was successfully created.
    assertEquals(0, restResponse.getResponseMessages().size());
    Boolean updated = restResponse.getResource();
    assertTrue(updated);
  }

  private void importSecretTextsFromCsv(String secretCsvFilePath) {
    File fileToImport = new File(getClass().getClassLoader().getResource(secretCsvFilePath).getFile());

    MultiPart multiPart = new MultiPart();
    FormDataBodyPart formDataBodyPart = new FormDataBodyPart("file", fileToImport, MediaType.MULTIPART_FORM_DATA_TYPE);
    multiPart.bodyPart(formDataBodyPart);

    WebTarget target = client.target(API_BASE + "/secrets/import-secrets?accountId=" + accountId);
    RestResponse<List<String>> restResponse = getRequestBuilderWithAuthHeader(target).post(
        entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE), new GenericType<RestResponse<List<String>>>() {});
    // Verify vault config was successfully created.
    assertEquals(0, restResponse.getResponseMessages().size());
    assertNotNull(restResponse.getResource());
  }

  private void deleteSecretText(String uuid) {
    WebTarget target = client.target(API_BASE + "/secrets/delete-secret?accountId=" + accountId + "&uuid=" + uuid);
    RestResponse<Boolean> restResponse =
        getRequestBuilderWithAuthHeader(target).delete(new GenericType<RestResponse<Boolean>>() {});
    // Verify vault config was successfully created.
    assertEquals(0, restResponse.getResponseMessages().size());
    Boolean deleted = restResponse.getResource();
    assertTrue(deleted);
  }

  private void verifyVaultChangeLog(String uuid) {
    WebTarget target = client.target(API_BASE + "/secrets/change-logs?accountId=" + accountId + "&entityId=" + uuid
        + "&type=" + SettingVariableTypes.SECRET_TEXT);
    RestResponse<List<SecretChangeLog>> restResponse =
        getRequestBuilderWithAuthHeader(target).get(new GenericType<RestResponse<List<SecretChangeLog>>>() {});
    // Verify vault config was successfully created.
    assertEquals(0, restResponse.getResponseMessages().size());
    assertTrue(restResponse.getResource().size() > 0);
  }

  private String createVaultConfig(VaultConfig vaultConfig) {
    WebTarget target = client.target(API_BASE + "/vault?accountId=" + accountId);
    RestResponse<String> restResponse = getRequestBuilderWithAuthHeader(target).post(
        entity(vaultConfig, APPLICATION_JSON), new GenericType<RestResponse<String>>() {});
    // Verify vault config was successfully created.
    assertEquals(0, restResponse.getResponseMessages().size());
    String vaultConfigId = restResponse.getResource();
    assertTrue(isNotEmpty(vaultConfigId));

    return vaultConfigId;
  }

  private String createKmsConfig(KmsConfig kmsConfig) {
    WebTarget target = client.target(API_BASE + "/kms/save-kms?accountId=" + accountId);
    RestResponse<String> restResponse = getRequestBuilderWithAuthHeader(target).post(
        entity(kmsConfig, APPLICATION_JSON), new GenericType<RestResponse<String>>() {});
    // Verify vault config was successfully created.
    assertEquals(0, restResponse.getResponseMessages().size());
    String kmsConfigId = restResponse.getResource();
    assertTrue(isNotEmpty(kmsConfigId));

    return kmsConfigId;
  }

  private void updateVaultConfig(VaultConfig vaultConfig) {
    WebTarget target = client.target(API_BASE + "/vault?accountId=" + accountId);
    vaultConfig.setName("TestVault_Different_Name");
    getRequestBuilderWithAuthHeader(target).post(
        entity(vaultConfig, APPLICATION_JSON), new GenericType<RestResponse<String>>() {});
  }

  private void deleteVaultConfig(String vaultConfigId) {
    WebTarget target = client.target(API_BASE + "/vault?accountId=" + accountId + "&vaultConfigId=" + vaultConfigId);
    RestResponse<Boolean> deleteRestResponse =
        getRequestBuilderWithAuthHeader(target).delete(new GenericType<RestResponse<Boolean>>() {});
    // Verify the vault config was deleted successfully
    assertEquals(0, deleteRestResponse.getResponseMessages().size());
    assertTrue(Boolean.valueOf(deleteRestResponse.getResource()));
    assertNull(wingsPersistence.get(VaultConfig.class, vaultConfigId));
  }

  private void deleteKmsConfig(String kmsConfigId) {
    WebTarget target =
        client.target(API_BASE + "/kms/delete-kms?accountId=" + accountId + "&kmsConfigId=" + kmsConfigId);
    RestResponse<Boolean> deleteRestResponse =
        getRequestBuilderWithAuthHeader(target).get(new GenericType<RestResponse<Boolean>>() {});
    // Verify the vault config was deleted successfully
    assertEquals(0, deleteRestResponse.getResponseMessages().size());
    assertTrue(Boolean.valueOf(deleteRestResponse.getResource()));
    assertNull(wingsPersistence.get(VaultConfig.class, kmsConfigId));
  }

  private void verifySecretValue(String secretUuid, String expectedValue, VaultConfig vaultConfig) {
    vaultConfig.setAuthToken(this.vaultToken);
    EncryptedData encryptedData = wingsPersistence.get(EncryptedData.class, secretUuid);
    assertNotNull(encryptedData);

    char[] decrypted = secretManagementDelegateService.decrypt(encryptedData, vaultConfig);
    assertTrue(isNotEmpty(decrypted));
    assertEquals(expectedValue, new String(decrypted));
  }

  private void verifySecretTextExists(String secretName) {
    EncryptedData encryptedData = secretManager.getSecretByName(accountId, secretName, false);
    assertNotNull(encryptedData);
    assertNull(encryptedData.getPath());
    assertEquals(SettingVariableTypes.SECRET_TEXT, encryptedData.getType());
  }

  private List<SettingAttribute> listSettingAttributes() {
    WebTarget target = client.target(API_BASE + "/secrets/list-values?accountId=" + accountId);
    RestResponse<List<SettingAttribute>> response =
        getRequestBuilderWithAuthHeader(target).get(new GenericType<RestResponse<List<SettingAttribute>>>() {});
    // Verify the vault config was deleted successfully
    assertEquals(0, response.getResponseMessages().size());
    assertTrue(response.getResource().size() > 0);
    return response.getResource();
  }
}
