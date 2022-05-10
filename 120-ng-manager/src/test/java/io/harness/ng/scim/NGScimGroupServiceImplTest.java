package io.harness.ng.scim;

import static io.harness.annotations.dev.HarnessTeam.PL;
import static io.harness.rule.OwnerRule.UJJAWAL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.harness.NgManagerTestBase;
import io.harness.annotations.dev.OwnedBy;
import io.harness.category.element.UnitTests;
import io.harness.ng.core.api.UserGroupService;
import io.harness.ng.core.user.entities.UserGroup;
import io.harness.ng.core.user.service.NgUserService;
import io.harness.rule.Owner;
import io.harness.scim.ScimGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@OwnedBy(PL)
public class NGScimGroupServiceImplTest extends NgManagerTestBase {
  private UserGroupService userGroupService;
  private NgUserService ngUserService;

  private NGScimGroupServiceImpl scimGroupService;

  @Before
  public void setup() throws IllegalAccessException {
    ngUserService = mock(NgUserService.class);
    userGroupService = mock(UserGroupService.class);

    scimGroupService = new NGScimGroupServiceImpl(userGroupService, ngUserService);
  }

  @Test
  @Owner(developers = UJJAWAL)
  @Category(UnitTests.class)
  public void testCreateGroup() {
    String accountId = "accountId";

    ScimGroup scimGroup = new ScimGroup();
    scimGroup.setDisplayName("displayname");
    scimGroup.setId("id");

    UserGroup userGroup = UserGroup.builder()
                              .name(scimGroup.getDisplayName())
                              .identifier(scimGroup.getDisplayName().replaceAll("\\.", "_"))
                              .build();
    when(userGroupService.create(any())).thenReturn(userGroup);
    ScimGroup userGroupCreated = scimGroupService.createGroup(scimGroup, accountId);

    assertThat(userGroupCreated.getDisplayName()).isNotNull();
    assertThat(userGroupCreated.getDisplayName()).isEqualTo(scimGroup.getDisplayName());
    assertThat(userGroupCreated.getId()).isEqualTo(scimGroup.getDisplayName());
  }

  @Test
  @Owner(developers = UJJAWAL)
  @Category(UnitTests.class)
  public void testCreateGroup2() {
    String accountId = "accountId";

    ScimGroup scimGroup = new ScimGroup();
    scimGroup.setDisplayName("display.name");
    scimGroup.setId("id");

    UserGroup userGroup = UserGroup.builder()
                              .name(scimGroup.getDisplayName())
                              .identifier(scimGroup.getDisplayName().replaceAll("\\.", "_"))
                              .build();
    when(userGroupService.create(any())).thenReturn(userGroup);
    ScimGroup userGroupCreated = scimGroupService.createGroup(scimGroup, accountId);

    assertThat(userGroupCreated.getDisplayName()).isNotNull();
    assertThat(userGroupCreated.getDisplayName()).isEqualTo(scimGroup.getDisplayName());
    assertThat(userGroupCreated.getId()).isEqualTo(scimGroup.getDisplayName().replaceAll("\\.", "_"));
    assertThat(userGroupCreated.getId()).isEqualTo("display_name");
  }

  @Test
  @Owner(developers = UJJAWAL)
  @Category(UnitTests.class)
  public void testCreateGroup4() {
    String accountId = "accountId";
    ScimGroup scimGroup = new ScimGroup();
    scimGroup.setDisplayName("display_name");
    scimGroup.setId("id");

    UserGroup userGroup = UserGroup.builder()
                              .name(scimGroup.getDisplayName())
                              .identifier(scimGroup.getDisplayName().replaceAll("\\.", "_"))
                              .build();
    when(userGroupService.create(any())).thenReturn(userGroup);
    ScimGroup userGroupCreated = scimGroupService.createGroup(scimGroup, accountId);

    assertThat(userGroupCreated.getDisplayName()).isNotNull();
    assertThat(userGroupCreated.getDisplayName()).isEqualTo(scimGroup.getDisplayName());
    assertThat(userGroupCreated.getId()).isEqualTo(scimGroup.getDisplayName().replaceAll("\\.", "_"));
    assertThat(userGroupCreated.getId()).isEqualTo("display_name");
  }

  @Test
  @Owner(developers = UJJAWAL)
  @Category(UnitTests.class)
  public void testCreateGroup3() {
    String accountId = "accountId";

    ScimGroup scimGroup = new ScimGroup();
    scimGroup.setDisplayName("display.name");
    scimGroup.setId("id");

    when(userGroupService.create(any())).thenReturn(null);
    ScimGroup userGroupCreated = scimGroupService.createGroup(scimGroup, accountId);

    assertThat(userGroupCreated.getDisplayName()).isNull();
    assertThat(userGroupCreated.getId()).isNull();
    assertThat(userGroupCreated.getMembers()).isNull();
  }
}
