package io.harness.ng.serviceaccounts.entities;

import io.harness.annotation.HarnessEntity;
import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.data.validator.EntityIdentifier;
import io.harness.data.validator.NGEntityName;
import io.harness.mongo.index.CompoundMongoIndex;
import io.harness.mongo.index.MongoIndex;
import io.harness.ng.core.NGAccountAccess;
import io.harness.ng.core.NGOrgAccess;
import io.harness.ng.core.NGProjectAccess;
import io.harness.ng.core.common.beans.NGTag;
import io.harness.persistence.PersistentEntity;
import io.harness.persistence.UuidAware;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.Email;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@FieldNameConstants(innerTypeName = "ServiceAccountKeys")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(value = "serviceAccounts", noClassnameStored = true)
@Document("serviceAccounts")
@TypeAlias("serviceAccounts")
@HarnessEntity(exportable = true)
@OwnedBy(HarnessTeam.PL)
public class ServiceAccount implements PersistentEntity, UuidAware, NGAccountAccess, NGOrgAccess, NGProjectAccess {
  public static List<MongoIndex> mongoIndexes() {
    return ImmutableList.<MongoIndex>builder()
        .add(CompoundMongoIndex.builder()
                 .name("unique_idx")
                 .field(ServiceAccountKeys.accountIdentifier)
                 .field(ServiceAccountKeys.orgIdentifier)
                 .field(ServiceAccountKeys.projectIdentifier)
                 .field(ServiceAccountKeys.identifier)
                 .unique(true)
                 .build(),
            CompoundMongoIndex.builder()
                .name("list_accounts_idx")
                .field(ServiceAccountKeys.accountIdentifier)
                .field(ServiceAccountKeys.orgIdentifier)
                .field(ServiceAccountKeys.projectIdentifier)
                .build())
        .build();
  }

  @org.springframework.data.annotation.Id @Id String uuid;
  @CreatedDate Long createdAt;
  @LastModifiedDate Long lastModifiedAt;

  @EntityIdentifier String identifier;
  @NGEntityName String name;
  @Email String email;
  @NotNull @Size(max = 1024) String description;
  @NotNull @Singular @Size(max = 128) List<NGTag> tags;

  @EntityIdentifier String accountIdentifier;
  @EntityIdentifier(allowBlank = true) String orgIdentifier;
  @EntityIdentifier(allowBlank = true) String projectIdentifier;
}
