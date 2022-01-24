/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ng.core.service.services;

import static io.harness.data.structure.EmptyPredicate.isNotEmpty;

import static java.lang.Long.parseLong;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.dtos.InstanceDTO;
import io.harness.exception.InvalidRequestException;
import io.harness.ng.core.entities.Organization;
import io.harness.ng.core.entities.Project;
import io.harness.ng.core.services.OrganizationService;
import io.harness.ng.core.services.ProjectService;
import io.harness.service.instance.InstanceService;

import com.google.inject.Inject;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor = @__({ @Inject }))
@OwnedBy(HarnessTeam.PIPELINE)
public class ServiceEntityManagementServiceImpl implements ServiceEntityManagementService {
  private final InstanceService instanceService;
  private final ServiceEntityService serviceEntityService;
  private final ProjectService projectService;
  private final OrganizationService organizationService;

  @Override
  public boolean deleteService(
      String accountId, String orgIdentifier, String projectIdentifier, String serviceIdentifier, String ifMatch) {
    List<InstanceDTO> instanceInfoNGList = instanceService.getActiveInstancesByServiceId(
        accountId, orgIdentifier, projectIdentifier, serviceIdentifier, System.currentTimeMillis());
    if (isNotEmpty(instanceInfoNGList)) {
      throw new InvalidRequestException(String.format(
          "Service [%s] under Project[%s], Organization [%s] couldn't be deleted since there are currently %d active instances for the service",
          serviceIdentifier, projectIdentifier, orgIdentifier, instanceInfoNGList.size()));
    }
    return serviceEntityService.delete(
        accountId, orgIdentifier, projectIdentifier, serviceIdentifier, isNumeric(ifMatch) ? parseLong(ifMatch) : null);
  }

  @Override
  public void checkThatTheOrganizationTProjectExists(
      String orgIdentifier, String projectIdentifier, String accountIdentifier) {
    if (isNotEmpty(orgIdentifier)) {
      final Optional<Organization> organization = organizationService.get(accountIdentifier, orgIdentifier);
      if (!organization.isPresent()) {
        throw new NotFoundException(String.format("org [%s] not found.", orgIdentifier));
      }
    }

    if (isNotEmpty(orgIdentifier) && isNotEmpty(projectIdentifier)) {
      final Optional<Project> project = projectService.get(accountIdentifier, orgIdentifier, projectIdentifier);
      if (!project.isPresent()) {
        throw new NotFoundException(String.format("project [%s] not found.", projectIdentifier));
      }
    }
  }
}
