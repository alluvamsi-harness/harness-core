/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cdng.gitops.service;

import static io.harness.annotations.dev.HarnessTeam.GITOPS;

import io.harness.annotations.dev.OwnedBy;
import io.harness.cdng.gitops.entity.Cluster;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.domain.Page;

@OwnedBy(GITOPS)
public interface ClusterService {
  /**
   * @param orgIdentifier
   * @param projectIdentifier
   * @param accountId
   * @param envIdentifier
   * @param identifier
   * @param deleted
   * @return
   */
  Optional<Cluster> get(@NotEmpty String orgIdentifier, @NotEmpty String projectIdentifier, @NotEmpty String accountId,
      String envIdentifier, @NotEmpty String identifier, boolean deleted);

  /**
   * @param Cluster
   * @return Cluster
   */
  Cluster create(@NotNull Cluster Cluster);

  /**
   * @param Cluster
   * @return Cluster
   */
  Cluster update(@NotNull Cluster Cluster);

  /**
   * @param accountId
   * @param entities
   * @return
   */
  Page<Cluster> bulkCreate(@NotEmpty String accountId, @NotNull List<Cluster> entities);

  /**
   * @param accountId
   * @param orgIdentifier
   * @param projectIdentifier
   * @param envIdentifier
   * @param identifier
   * @return
   */
  boolean delete(@NotEmpty String accountId, @NotEmpty String orgIdentifier, @NotEmpty String projectIdentifier,
      @NotEmpty String envIdentifier, @NotEmpty String identifier);

  /**
   * @param page
   * @param size
   * @param accountIdentifier
   * @param orgIdentifier
   * @param projectIdentifier
   * @param envRef
   * @param searchTerm
   * @param identifiers
   * @return
   */
  Page<Cluster> list(int page, int size, @NotEmpty String accountIdentifier, @NotEmpty String orgIdentifier,
      @NotEmpty String projectIdentifier, @NotEmpty String envRef, String searchTerm, List<String> identifiers,
      List<String> sort);
}
