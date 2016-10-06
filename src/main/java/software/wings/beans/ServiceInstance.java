package software.wings.beans;

import com.google.common.base.MoreObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Property;
import software.wings.beans.infrastructure.ApplicationHost;
import software.wings.sm.ExecutionStatus;

import java.util.Objects;

/**
 * The Class ServiceInstance.
 */
@Entity(value = "serviceInstance", noClassnameStored = true)
@Indexes(@Index(fields = { @Field("appId")
                           , @Field("envId"), @Field("host"), @Field("serviceTemplate") },
    options = @IndexOptions(unique = true)))
public class ServiceInstance extends Base {
  @Indexed private String envId;

  //@Reference(idOnly = true, ignoreMissing = true) private Host host;
  //@Reference(idOnly = true, ignoreMissing = true) private ServiceTemplate serviceTemplate;

  @Property("serviceTemplate") private String serviceTemplateId;

  private String serviceTemplateName;

  @Indexed private String serviceId;

  @Indexed private String serviceName;

  @Property("host") private String hostId;

  @Indexed private String hostName;

  @Indexed private String tagName;

  private String releaseId;
  @Indexed private String releaseName;
  private String artifactId;
  @Indexed private String artifactName;
  @Indexed private long artifactDeployedOn;
  @Indexed private ExecutionStatus artifactDeploymentStatus;
  private String artifactDeploymentActivityId;

  private String lastActivityId;
  private ExecutionStatus lastActivityStatus;
  private long lastActivityCreatedAt;
  @Indexed private String commandName;
  @Indexed private String commandType;
  private long lastDeployedOn;

  /**
   * Gets env id.
   *
   * @return the env id
   */
  public String getEnvId() {
    return envId;
  }

  /**
   * Sets env id.
   *
   * @param envId the env id
   */
  public void setEnvId(String envId) {
    this.envId = envId;
  }

  /**
   * Gets release id.
   *
   * @return the release id
   */
  public String getReleaseId() {
    return releaseId;
  }

  /**
   * Sets release id.
   *
   * @param releaseId the release id
   */
  public void setReleaseId(String releaseId) {
    this.releaseId = releaseId;
  }

  /**
   * Gets release name.
   *
   * @return the release name
   */
  public String getReleaseName() {
    return releaseName;
  }

  /**
   * Sets release name.
   *
   * @param releaseName the release name
   */
  public void setReleaseName(String releaseName) {
    this.releaseName = releaseName;
  }

  /**
   * Gets artifact id.
   *
   * @return the artifact id
   */
  public String getArtifactId() {
    return artifactId;
  }

  /**
   * Sets artifact id.
   *
   * @param artifactId the artifact id
   */
  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  /**
   * Gets artifact name.
   *
   * @return the artifact name
   */
  public String getArtifactName() {
    return artifactName;
  }

  /**
   * Sets artifact name.
   *
   * @param artifactName the artifact name
   */
  public void setArtifactName(String artifactName) {
    this.artifactName = artifactName;
  }

  /**
   * Gets artifact deployed on.
   *
   * @return the artifact deployed on
   */
  public long getArtifactDeployedOn() {
    return artifactDeployedOn;
  }

  /**
   * Sets artifact deployed on.
   *
   * @param artifactDeployedOn the artifact deployed on
   */
  public void setArtifactDeployedOn(long artifactDeployedOn) {
    this.artifactDeployedOn = artifactDeployedOn;
  }

  /**
   * Gets artifact deployment status.
   *
   * @return the artifact deployment status
   */
  public ExecutionStatus getArtifactDeploymentStatus() {
    return artifactDeploymentStatus;
  }

  /**
   * Sets artifact deployment status.
   *
   * @param artifactDeploymentStatus the artifact deployment status
   */
  public void setArtifactDeploymentStatus(ExecutionStatus artifactDeploymentStatus) {
    this.artifactDeploymentStatus = artifactDeploymentStatus;
  }

  /**
   * Gets artifact deployment activity id.
   *
   * @return the artifact deployment activity id
   */
  public String getArtifactDeploymentActivityId() {
    return artifactDeploymentActivityId;
  }

  /**
   * Sets artifact deployment activity id.
   *
   * @param artifactDeploymentActivityId the artifact deployment activity id
   */
  public void setArtifactDeploymentActivityId(String artifactDeploymentActivityId) {
    this.artifactDeploymentActivityId = artifactDeploymentActivityId;
  }

  /**
   * Gets last activity id.
   *
   * @return the last activity id
   */
  public String getLastActivityId() {
    return lastActivityId;
  }

  /**
   * Sets last activity id.
   *
   * @param lastActivityId the last activity id
   */
  public void setLastActivityId(String lastActivityId) {
    this.lastActivityId = lastActivityId;
  }

  /**
   * Gets last activity status.
   *
   * @return the last activity status
   */
  public ExecutionStatus getLastActivityStatus() {
    return lastActivityStatus;
  }

  /**
   * Sets last activity status.
   *
   * @param lastActivityStatus the last activity status
   */
  public void setLastActivityStatus(ExecutionStatus lastActivityStatus) {
    this.lastActivityStatus = lastActivityStatus;
  }

  /**
   * Gets command name.
   *
   * @return the command name
   */
  public String getCommandName() {
    return commandName;
  }

  /**
   * Sets command name.
   *
   * @param commandName the command name
   */
  public void setCommandName(String commandName) {
    this.commandName = commandName;
  }

  /**
   * Gets command type.
   *
   * @return the command type
   */
  public String getCommandType() {
    return commandType;
  }

  /**
   * Sets command type.
   *
   * @param commandType the command type
   */
  public void setCommandType(String commandType) {
    this.commandType = commandType;
  }

  /**
   * Gets last deployed on.
   *
   * @return the last deployed on
   */
  public long getLastDeployedOn() {
    return lastDeployedOn;
  }

  /**
   * Sets last deployed on.
   *
   * @param lastDeployedOn the last deployed on
   */
  public void setLastDeployedOn(long lastDeployedOn) {
    this.lastDeployedOn = lastDeployedOn;
  }

  /**
   * Gets last activity created on.
   *
   * @return the last activity created on
   */
  public long getLastActivityCreatedAt() {
    return lastActivityCreatedAt;
  }

  /**
   * Sets last activity created on.
   *
   * @param lastActivityCreatedAt the last activity created on
   */
  public void setLastActivityCreatedAt(long lastActivityCreatedAt) {
    this.lastActivityCreatedAt = lastActivityCreatedAt;
  }

  /**
   * Getter for property 'serviceTemplateId'.
   *
   * @return Value for property 'serviceTemplateId'.
   */
  public String getServiceTemplateId() {
    return serviceTemplateId;
  }

  /**
   * Setter for property 'serviceTemplateId'.
   *
   * @param serviceTemplateId Value to set for property 'serviceTemplateId'.
   */
  public void setServiceTemplateId(String serviceTemplateId) {
    this.serviceTemplateId = serviceTemplateId;
  }

  /**
   * Getter for property 'serviceName'.
   *
   * @return Value for property 'serviceName'.
   */
  public String getServiceName() {
    return serviceName;
  }

  /**
   * Setter for property 'serviceName'.
   *
   * @param serviceName Value to set for property 'serviceName'.
   */
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  /**
   * Getter for property 'hostId'.
   *
   * @return Value for property 'hostId'.
   */
  public String getHostId() {
    return hostId;
  }

  /**
   * Setter for property 'hostId'.
   *
   * @param hostId Value to set for property 'hostId'.
   */
  public void setHostId(String hostId) {
    this.hostId = hostId;
  }

  /**
   * Getter for property 'hostName'.
   *
   * @return Value for property 'hostName'.
   */
  public String getHostName() {
    return hostName;
  }

  /**
   * Setter for property 'hostName'.
   *
   * @param hostName Value to set for property 'hostName'.
   */
  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  /**
   * Getter for property 'tagName'.
   *
   * @return Value for property 'tagName'.
   */
  public String getTagName() {
    return tagName;
  }

  /**
   * Setter for property 'tagName'.
   *
   * @param tagName Value to set for property 'tagName'.
   */
  public void setTagName(String tagName) {
    this.tagName = tagName;
  }

  /**
   * Getter for property 'serviceId'.
   *
   * @return Value for property 'serviceId'.
   */
  public String getServiceId() {
    return serviceId;
  }

  /**
   * Setter for property 'serviceId'.
   *
   * @param serviceId Value to set for property 'serviceId'.
   */
  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  /**
   * Getter for property 'serviceTemplateName'.
   *
   * @return Value for property 'serviceTemplateName'.
   */
  public String getServiceTemplateName() {
    return serviceTemplateName;
  }

  /**
   * Setter for property 'serviceTemplateName'.
   *
   * @param serviceTemplateName Value to set for property 'serviceTemplateName'.
   */
  public void setServiceTemplateName(String serviceTemplateName) {
    this.serviceTemplateName = serviceTemplateName;
  }

  /**
   * Gets display name.
   *
   * @return the display name
   */
  @JsonProperty
  public String getDisplayName() {
    return hostName + ":" + serviceTemplateName;
  }

  @Override
  public int hashCode() {
    return 31 * super.hashCode()
        + Objects.hash(envId, serviceTemplateId, serviceTemplateName, serviceId, serviceName, hostId, hostName, tagName,
              releaseId, releaseName, artifactId, artifactName, artifactDeployedOn, artifactDeploymentStatus,
              artifactDeploymentActivityId, lastActivityId, lastActivityStatus, lastActivityCreatedAt, commandName,
              commandType, lastDeployedOn);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    final ServiceInstance other = (ServiceInstance) obj;
    return Objects.equals(this.envId, other.envId) && Objects.equals(this.serviceTemplateId, other.serviceTemplateId)
        && Objects.equals(this.serviceTemplateName, other.serviceTemplateName)
        && Objects.equals(this.serviceId, other.serviceId) && Objects.equals(this.serviceName, other.serviceName)
        && Objects.equals(this.hostId, other.hostId) && Objects.equals(this.hostName, other.hostName)
        && Objects.equals(this.tagName, other.tagName) && Objects.equals(this.releaseId, other.releaseId)
        && Objects.equals(this.releaseName, other.releaseName) && Objects.equals(this.artifactId, other.artifactId)
        && Objects.equals(this.artifactName, other.artifactName)
        && Objects.equals(this.artifactDeployedOn, other.artifactDeployedOn)
        && Objects.equals(this.artifactDeploymentStatus, other.artifactDeploymentStatus)
        && Objects.equals(this.artifactDeploymentActivityId, other.artifactDeploymentActivityId)
        && Objects.equals(this.lastActivityId, other.lastActivityId)
        && Objects.equals(this.lastActivityStatus, other.lastActivityStatus)
        && Objects.equals(this.lastActivityCreatedAt, other.lastActivityCreatedAt)
        && Objects.equals(this.commandName, other.commandName) && Objects.equals(this.commandType, other.commandType)
        && Objects.equals(this.lastDeployedOn, other.lastDeployedOn);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("envId", envId)
        .add("serviceTemplateId", serviceTemplateId)
        .add("serviceTemplateName", serviceTemplateName)
        .add("serviceId", serviceId)
        .add("serviceName", serviceName)
        .add("hostId", hostId)
        .add("hostName", hostName)
        .add("tagName", tagName)
        .add("releaseId", releaseId)
        .add("releaseName", releaseName)
        .add("artifactId", artifactId)
        .add("artifactName", artifactName)
        .add("artifactDeployedOn", artifactDeployedOn)
        .add("artifactDeploymentStatus", artifactDeploymentStatus)
        .add("artifactDeploymentActivityId", artifactDeploymentActivityId)
        .add("lastActivityId", lastActivityId)
        .add("lastActivityStatus", lastActivityStatus)
        .add("lastActivityCreatedAt", lastActivityCreatedAt)
        .add("commandName", commandName)
        .add("commandType", commandType)
        .add("lastDeployedOn", lastDeployedOn)
        .add("displayName", getDisplayName())
        .toString();
  }

  /**
   * The type Builder.
   */
  public static final class Builder {
    private String envId;
    private String serviceTemplateId;
    private String serviceTemplateName;
    private String serviceId;
    private String serviceName;
    private String hostId;
    private String hostName;
    private String tagName;
    private String releaseId;
    private String releaseName;
    private String artifactId;
    private String artifactName;
    private long artifactDeployedOn;
    private ExecutionStatus artifactDeploymentStatus;
    private String uuid;
    private String artifactDeploymentActivityId;
    private String appId;
    private String lastActivityId;
    private ExecutionStatus lastActivityStatus;
    private User createdBy;
    private long lastActivityCreatedAt;
    private long createdAt;
    private String commandName;
    private String commandType;
    private User lastUpdatedBy;
    private long lastDeployedOn;
    private long lastUpdatedAt;
    private boolean active = true;

    /**
     * Do not instantiate Builder.
     */
    private Builder() {}

    /**
     * A service instance builder.
     *
     * @return the builder
     */
    public static Builder aServiceInstance() {
      return new Builder();
    }

    /**
     * With env id builder.
     *
     * @param envId the env id
     * @return the builder
     */
    public Builder withEnvId(String envId) {
      this.envId = envId;
      return this;
    }

    /**
     * With service template id builder.
     *
     * @param serviceTemplateId the service template id
     * @return the builder
     */
    public Builder withServiceTemplateId(String serviceTemplateId) {
      this.serviceTemplateId = serviceTemplateId;
      return this;
    }

    /**
     * With service name builder.
     *
     * @param serviceName the service name
     * @return the builder
     */
    public Builder withServiceName(String serviceName) {
      this.serviceName = serviceName;
      return this;
    }

    /**
     * With host builder.
     *
     * @param host the host
     * @return the builder
     */
    public Builder withHost(ApplicationHost host) {
      this.hostId = host.getUuid();
      this.hostName = host.getHostName();
      this.tagName = host.getConfigTag() != null ? host.getConfigTag().getName() : null;
      return this;
    }

    /**
     * With service template builder.
     *
     * @param serviceTemplate the service template
     * @return the builder
     */
    public Builder withServiceTemplate(ServiceTemplate serviceTemplate) {
      this.serviceName = serviceTemplate.getService() != null ? serviceTemplate.getService().getName() : "";
      this.serviceId = serviceTemplate.getService() != null ? serviceTemplate.getService().getUuid() : "";
      this.serviceTemplateName = serviceTemplate.getName();

      this.serviceTemplateId = serviceTemplate.getUuid();
      return this;
    }

    /**
     * With host id builder.
     *
     * @param hostId the host id
     * @return the builder
     */
    public Builder withHostId(String hostId) {
      this.hostId = hostId;
      return this;
    }

    /**
     * With host name builder.
     *
     * @param hostName the host name
     * @return the builder
     */
    public Builder withHostName(String hostName) {
      this.hostName = hostName;
      return this;
    }

    /**
     * With tag name builder.
     *
     * @param tagName the tag name
     * @return the builder
     */
    public Builder withTagName(String tagName) {
      this.tagName = tagName;
      return this;
    }

    /**
     * With release id builder.
     *
     * @param releaseId the release id
     * @return the builder
     */
    public Builder withReleaseId(String releaseId) {
      this.releaseId = releaseId;
      return this;
    }

    /**
     * With release name builder.
     *
     * @param releaseName the release name
     * @return the builder
     */
    public Builder withReleaseName(String releaseName) {
      this.releaseName = releaseName;
      return this;
    }

    /**
     * With artifact id builder.
     *
     * @param artifactId the artifact id
     * @return the builder
     */
    public Builder withArtifactId(String artifactId) {
      this.artifactId = artifactId;
      return this;
    }

    /**
     * With artifact name builder.
     *
     * @param artifactName the artifact name
     * @return the builder
     */
    public Builder withArtifactName(String artifactName) {
      this.artifactName = artifactName;
      return this;
    }

    /**
     * With artifact deployed on builder.
     *
     * @param artifactDeployedOn the artifact deployed on
     * @return the builder
     */
    public Builder withArtifactDeployedOn(long artifactDeployedOn) {
      this.artifactDeployedOn = artifactDeployedOn;
      return this;
    }

    /**
     * With artifact deployment status builder.
     *
     * @param artifactDeploymentStatus the artifact deployment status
     * @return the builder
     */
    public Builder withArtifactDeploymentStatus(ExecutionStatus artifactDeploymentStatus) {
      this.artifactDeploymentStatus = artifactDeploymentStatus;
      return this;
    }

    /**
     * With uuid builder.
     *
     * @param uuid the uuid
     * @return the builder
     */
    public Builder withUuid(String uuid) {
      this.uuid = uuid;
      return this;
    }

    /**
     * With artifact deployment activity id builder.
     *
     * @param artifactDeploymentActivityId the artifact deployment activity id
     * @return the builder
     */
    public Builder withArtifactDeploymentActivityId(String artifactDeploymentActivityId) {
      this.artifactDeploymentActivityId = artifactDeploymentActivityId;
      return this;
    }

    /**
     * With app id builder.
     *
     * @param appId the app id
     * @return the builder
     */
    public Builder withAppId(String appId) {
      this.appId = appId;
      return this;
    }

    /**
     * With last activity id builder.
     *
     * @param lastActivityId the last activity id
     * @return the builder
     */
    public Builder withLastActivityId(String lastActivityId) {
      this.lastActivityId = lastActivityId;
      return this;
    }

    /**
     * With last activity status builder.
     *
     * @param lastActivityStatus the last activity status
     * @return the builder
     */
    public Builder withLastActivityStatus(ExecutionStatus lastActivityStatus) {
      this.lastActivityStatus = lastActivityStatus;
      return this;
    }

    /**
     * With created by builder.
     *
     * @param createdBy the created by
     * @return the builder
     */
    public Builder withCreatedBy(User createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    /**
     * With last activity created at builder.
     *
     * @param lastActivityCreatedAt the last activity created at
     * @return the builder
     */
    public Builder withLastActivityCreatedAt(long lastActivityCreatedAt) {
      this.lastActivityCreatedAt = lastActivityCreatedAt;
      return this;
    }

    /**
     * With created at builder.
     *
     * @param createdAt the created at
     * @return the builder
     */
    public Builder withCreatedAt(long createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    /**
     * With command name builder.
     *
     * @param commandName the command name
     * @return the builder
     */
    public Builder withCommandName(String commandName) {
      this.commandName = commandName;
      return this;
    }

    /**
     * With command type builder.
     *
     * @param commandType the command type
     * @return the builder
     */
    public Builder withCommandType(String commandType) {
      this.commandType = commandType;
      return this;
    }

    /**
     * With last updated by builder.
     *
     * @param lastUpdatedBy the last updated by
     * @return the builder
     */
    public Builder withLastUpdatedBy(User lastUpdatedBy) {
      this.lastUpdatedBy = lastUpdatedBy;
      return this;
    }

    /**
     * With last deployed on builder.
     *
     * @param lastDeployedOn the last deployed on
     * @return the builder
     */
    public Builder withLastDeployedOn(long lastDeployedOn) {
      this.lastDeployedOn = lastDeployedOn;
      return this;
    }

    /**
     * With last updated at builder.
     *
     * @param lastUpdatedAt the last updated at
     * @return the builder
     */
    public Builder withLastUpdatedAt(long lastUpdatedAt) {
      this.lastUpdatedAt = lastUpdatedAt;
      return this;
    }

    /**
     * With active builder.
     *
     * @param active the active
     * @return the builder
     */
    public Builder withActive(boolean active) {
      this.active = active;
      return this;
    }

    /**
     * But builder.
     *
     * @return the builder
     */
    public Builder but() {
      return aServiceInstance()
          .withEnvId(envId)
          .withServiceTemplateId(serviceTemplateId)
          .withServiceTemplateName(serviceTemplateName)
          .withServiceId(serviceId)
          .withServiceName(serviceName)
          .withHostId(hostId)
          .withHostName(hostName)
          .withTagName(tagName)
          .withReleaseId(releaseId)
          .withReleaseName(releaseName)
          .withArtifactId(artifactId)
          .withArtifactName(artifactName)
          .withArtifactDeployedOn(artifactDeployedOn)
          .withArtifactDeploymentStatus(artifactDeploymentStatus)
          .withUuid(uuid)
          .withArtifactDeploymentActivityId(artifactDeploymentActivityId)
          .withAppId(appId)
          .withLastActivityId(lastActivityId)
          .withLastActivityStatus(lastActivityStatus)
          .withCreatedBy(createdBy)
          .withLastActivityCreatedAt(lastActivityCreatedAt)
          .withCreatedAt(createdAt)
          .withCommandName(commandName)
          .withCommandType(commandType)
          .withLastUpdatedBy(lastUpdatedBy)
          .withLastDeployedOn(lastDeployedOn)
          .withLastUpdatedAt(lastUpdatedAt)
          .withActive(active);
    }

    /**
     * With service template name builder.
     *
     * @param serviceTemplateName the service template name
     * @return the builder
     */
    public Builder withServiceTemplateName(String serviceTemplateName) {
      this.serviceTemplateName = serviceTemplateName;
      return this;
    }

    /**
     * With service id builder.
     *
     * @param serviceId the service id
     * @return the builder
     */
    public Builder withServiceId(String serviceId) {
      this.serviceId = serviceId;
      return this;
    }

    /**
     * Build service instance.
     *
     * @return the service instance
     */
    public ServiceInstance build() {
      ServiceInstance serviceInstance = new ServiceInstance();
      serviceInstance.setEnvId(envId);
      serviceInstance.setServiceTemplateId(serviceTemplateId);
      serviceInstance.setServiceName(serviceName);
      serviceInstance.setHostId(hostId);
      serviceInstance.setHostName(hostName);
      serviceInstance.setTagName(tagName);
      serviceInstance.setReleaseId(releaseId);
      serviceInstance.setReleaseName(releaseName);
      serviceInstance.setArtifactId(artifactId);
      serviceInstance.setArtifactName(artifactName);
      serviceInstance.setArtifactDeployedOn(artifactDeployedOn);
      serviceInstance.setArtifactDeploymentStatus(artifactDeploymentStatus);
      serviceInstance.setUuid(uuid);
      serviceInstance.setArtifactDeploymentActivityId(artifactDeploymentActivityId);
      serviceInstance.setAppId(appId);
      serviceInstance.setLastActivityId(lastActivityId);
      serviceInstance.setLastActivityStatus(lastActivityStatus);
      serviceInstance.setCreatedBy(createdBy);
      serviceInstance.setLastActivityCreatedAt(lastActivityCreatedAt);
      serviceInstance.setCreatedAt(createdAt);
      serviceInstance.setCommandName(commandName);
      serviceInstance.setCommandType(commandType);
      serviceInstance.setLastUpdatedBy(lastUpdatedBy);
      serviceInstance.setLastDeployedOn(lastDeployedOn);
      serviceInstance.setLastUpdatedAt(lastUpdatedAt);
      serviceInstance.setActive(active);
      serviceInstance.setServiceId(serviceId);
      serviceInstance.setServiceTemplateName(serviceTemplateName);
      return serviceInstance;
    }
  }
}
