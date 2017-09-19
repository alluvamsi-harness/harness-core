package software.wings.service.intfc.yaml;

import org.hibernate.validator.constraints.NotEmpty;
import software.wings.yaml.gitSync.YamlGitSync;

public interface YamlGitSyncService {
  /**
   * Gets the yaml git sync info by uuid
   *
   * @param uuid the uuid
   * @return the rest response
   */
  public YamlGitSync get(String uuid);

  /**
   * Gets the yaml git sync info by object type and entitytId (uuid)
   *
   * @param type the object type
   * @param entityId the uuid of the entity
   * @param accountId the account id
   * @return the rest response
   */
  public YamlGitSync get(String type, String entityId, String accountId);

  public boolean exist(@NotEmpty String type, @NotEmpty String entityId, @NotEmpty String accountId);

  /**
   * Creates a new yaml git sync info by object type and entitytId (uuid)
   *
   * @param type the object type
   * @param accountId the account id
   * @param yamlGitSync the yamlGitSync info
   * @return the rest response
   */
  public YamlGitSync save(String type, String accountId, YamlGitSync yamlGitSync);

  /**
   * Updates the yaml git sync info by object type and entitytId (uuid)
   *
   * @param type the object type
   *@param entityId the uuid of the entity
   * @param accountId the account id
   * @param yamlGitSync the yamlGitSync info
   * @return the rest response
   */
  public YamlGitSync update(String type, String entityId, String accountId, YamlGitSync yamlGitSync);
}
