package software.wings.beans.instance.dashboard;

/**
 * Service info with parent app info
 * @author rktummala on 08/13/17
 */
public class ServiceSummary extends EntitySummary {
  private EntitySummary appSummary;

  public EntitySummary getAppSummary() {
    return appSummary;
  }

  private void setAppSummary(EntitySummary appSummary) {
    this.appSummary = appSummary;
  }

  public static final class Builder extends EntitySummary.Builder {
    private EntitySummary appSummary;

    private Builder() {}

    public static Builder aServiceSummary() {
      return new Builder();
    }

    public Builder withAppSummary(EntitySummary appSummary) {
      this.appSummary = appSummary;
      return this;
    }

    public Builder but() {
      return (Builder) aServiceSummary().withAppSummary(appSummary).withId(id).withName(name).withType(type);
    }

    public ServiceSummary build() {
      ServiceSummary serviceSummary = new ServiceSummary();
      serviceSummary.setId(id);
      serviceSummary.setName(name);
      serviceSummary.setType(type);
      serviceSummary.setAppSummary(appSummary);
      return serviceSummary;
    }
  }
}
