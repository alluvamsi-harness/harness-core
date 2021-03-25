package software.wings.service.impl;

import static io.harness.annotations.dev.HarnessTeam.CDC;
import static io.harness.data.structure.HasPredicate.hasSome;
import static io.harness.exception.WingsException.USER;
import static io.harness.network.Http.connectableHttpUrl;
import static io.harness.validation.Validator.equalCheck;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

import io.harness.annotations.dev.OwnedBy;
import io.harness.eraro.ErrorCode;
import io.harness.exception.InvalidRequestException;
import io.harness.exception.WingsException;
import io.harness.security.encryption.EncryptedDataDetail;

import software.wings.beans.artifact.ArtifactStreamAttributes;
import software.wings.beans.artifact.ArtifactStreamType;
import software.wings.beans.config.NexusConfig;
import software.wings.helpers.ext.jenkins.BuildDetails;
import software.wings.helpers.ext.jenkins.JobDetails;
import software.wings.helpers.ext.nexus.NexusService;
import software.wings.service.intfc.NexusBuildService;
import software.wings.utils.ArtifactType;
import software.wings.utils.RepositoryFormat;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by srinivas on 3/31/17.
 */
@OwnedBy(CDC)
@Singleton
@Slf4j
public class NexusBuildServiceImpl implements NexusBuildService {
  @Inject private NexusService nexusService;

  @Override
  public Map<String, String> getPlans(NexusConfig config, List<EncryptedDataDetail> encryptionDetails) {
    log.info("[Nexus Delegate Selection] Get Plans with selectors {}", config.getDelegateSelectors());
    return nexusService.getRepositories(config, encryptionDetails);
  }

  @Override
  public Map<String, String> getPlans(NexusConfig config, List<EncryptedDataDetail> encryptionDetails,
      ArtifactType artifactType, String repositoryFormat) {
    log.info("[Nexus Delegate Selection] Get Plans with artifactType {} and repo format {} and delegate selectors - {}",
        artifactType, repositoryFormat, config.getDelegateSelectors());
    if (artifactType != ArtifactType.DOCKER && repositoryFormat != null
        && repositoryFormat.equals(RepositoryFormat.docker.name())) {
      throw new WingsException(format("Not supported for Artifact Type %s", artifactType), USER);
    }
    if (artifactType == ArtifactType.DOCKER) {
      return nexusService.getRepositories(config, encryptionDetails, RepositoryFormat.docker.name());
    }
    return nexusService.getRepositories(config, encryptionDetails, repositoryFormat);
  }

  @Override
  public Map<String, String> getPlans(
      NexusConfig config, List<EncryptedDataDetail> encryptionDetails, RepositoryFormat repositoryFormat) {
    log.info("[Nexus Delegate Selection] Get Plans with repo format {} and delegate selectors - {}", repositoryFormat,
        config.getDelegateSelectors());
    return nexusService.getRepositories(config, encryptionDetails, repositoryFormat.name());
  }

  @Override
  public List<BuildDetails> getBuilds(String appId, ArtifactStreamAttributes artifactStreamAttributes,
      NexusConfig config, List<EncryptedDataDetail> encryptionDetails) {
    log.info("[Nexus Delegate Selection] Get Builds for artifactStreamId {} and delegate selectors - {}",
        artifactStreamAttributes.getArtifactStreamId(), config.getDelegateSelectors());
    return wrapNewBuildsWithLabels(
        getBuildsInternal(artifactStreamAttributes, config, encryptionDetails), artifactStreamAttributes, config);
  }

  @Override
  public BuildDetails getBuild(String appId, ArtifactStreamAttributes artifactStreamAttributes, NexusConfig config,
      List<EncryptedDataDetail> encryptionDetails, String buildNo) {
    log.info("[Nexus Delegate Selection] Get Build for artifactStreamId {} and delegate selectors - {}",
        artifactStreamAttributes.getArtifactStreamId(), config.getDelegateSelectors());
    List<BuildDetails> buildDetails =
        wrapNewBuildsWithLabels(getBuildInternal(artifactStreamAttributes, config, encryptionDetails, buildNo),
            artifactStreamAttributes, config);
    if (hasSome(buildDetails)) {
      return buildDetails.get(0);
    }
    return null;
  }

  private List<BuildDetails> getBuildsInternal(ArtifactStreamAttributes artifactStreamAttributes, NexusConfig config,
      List<EncryptedDataDetail> encryptionDetails) {
    equalCheck(artifactStreamAttributes.getArtifactStreamType(), ArtifactStreamType.NEXUS.name());
    if (artifactStreamAttributes.getArtifactType() != null
            && artifactStreamAttributes.getArtifactType() == ArtifactType.DOCKER
        || (artifactStreamAttributes.getRepositoryFormat().equals(RepositoryFormat.docker.name()))) {
      return nexusService.getBuilds(config, encryptionDetails, artifactStreamAttributes, 50);
    } else if (artifactStreamAttributes.getRepositoryFormat().equals(RepositoryFormat.nuget.name())
        || artifactStreamAttributes.getRepositoryFormat().equals(RepositoryFormat.npm.name())) {
      return nexusService.getVersions(artifactStreamAttributes.getRepositoryFormat(), config, encryptionDetails,
          artifactStreamAttributes.getJobName(), artifactStreamAttributes.getNexusPackageName(),
          artifactStreamAttributes.isSupportForNexusGroupReposEnabled());
    } else {
      return nexusService.getVersions(config, encryptionDetails, artifactStreamAttributes.getJobName(),
          artifactStreamAttributes.getGroupId(), artifactStreamAttributes.getArtifactName(),
          artifactStreamAttributes.getExtension(), artifactStreamAttributes.getClassifier(),
          artifactStreamAttributes.isSupportForNexusGroupReposEnabled());
    }
  }

  private List<BuildDetails> getBuildInternal(ArtifactStreamAttributes artifactStreamAttributes, NexusConfig config,
      List<EncryptedDataDetail> encryptionDetails, String buildNo) {
    equalCheck(artifactStreamAttributes.getArtifactStreamType(), ArtifactStreamType.NEXUS.name());
    if (artifactStreamAttributes.getRepositoryFormat().equals(RepositoryFormat.nuget.name())
        || artifactStreamAttributes.getRepositoryFormat().equals(RepositoryFormat.npm.name())) {
      return nexusService.getVersion(artifactStreamAttributes.getRepositoryFormat(), config, encryptionDetails,
          artifactStreamAttributes.getJobName(), artifactStreamAttributes.getNexusPackageName(), buildNo);
    } else {
      return nexusService.getVersion(config, encryptionDetails, artifactStreamAttributes.getJobName(),
          artifactStreamAttributes.getGroupId(), artifactStreamAttributes.getArtifactName(),
          artifactStreamAttributes.getExtension(), artifactStreamAttributes.getClassifier(), buildNo);
    }
  }

  @Override
  public List<JobDetails> getJobs(
      NexusConfig config, List<EncryptedDataDetail> encryptionDetails, Optional<String> parentJobName) {
    log.info("[Nexus Delegate Selection] Get Jobs for delegate selectors - {}", config.getDelegateSelectors());
    List<String> jobNames = Lists.newArrayList(nexusService.getRepositories(config, encryptionDetails).keySet());
    return wrapJobNameWithJobDetails(jobNames);
  }

  @Override
  public List<String> getArtifactPaths(
      String repoId, String groupId, NexusConfig config, List<EncryptedDataDetail> encryptionDetails) {
    log.info("[Nexus Delegate Selection] Get artifact paths for repoId {}, groupId {} and delegate selectors - {}",
        repoId, groupId, config.getDelegateSelectors());
    if (isBlank(groupId)) {
      return nexusService.getArtifactPaths(config, encryptionDetails, repoId);
    }
    return nexusService.getArtifactNames(config, encryptionDetails, repoId, groupId);
  }

  @Override
  public List<String> getArtifactPaths(String repoId, String groupId, NexusConfig config,
      List<EncryptedDataDetail> encryptionDetails, String repositoryFormat) {
    log.info(
        "[Nexus Delegate Selection] Get artifact paths for repoId {}, groupId {}, repository format {} and delegate selectors - {}",
        repoId, groupId, repositoryFormat, config.getDelegateSelectors());
    if (isBlank(groupId)) {
      return nexusService.getArtifactPaths(config, encryptionDetails, repoId);
    }
    return nexusService.getArtifactNames(config, encryptionDetails, repoId, groupId, repositoryFormat);
  }

  @Override
  public List<String> getArtifactPathsUsingPrivateApis(String repoId, String groupId, NexusConfig config,
      List<EncryptedDataDetail> encryptionDetails, String repositoryFormat) {
    return nexusService.getArtifactNamesUsingPrivateApis(config, encryptionDetails, repoId, groupId, repositoryFormat);
  }

  @Override
  public List<String> getGroupIds(
      String repositoryName, NexusConfig config, List<EncryptedDataDetail> encryptionDetails) {
    log.info("[Nexus Delegate Selection] Get GroupIds for repo name {} and delegate selectors - {}", repositoryName,
        config.getDelegateSelectors());
    return nexusService.getGroupIdPaths(config, encryptionDetails, repositoryName, null);
  }

  @Override
  public List<String> getGroupIds(
      String repositoryName, String repositoryFormat, NexusConfig config, List<EncryptedDataDetail> encryptionDetails) {
    log.info("[Nexus Delegate Selection] Get GroupIds for repo name {} and delegate selectors - {}", repositoryName,
        config.getDelegateSelectors());
    return nexusService.getGroupIdPaths(config, encryptionDetails, repositoryName, repositoryFormat);
  }

  @Override
  public List<String> getGroupIdsUsingPrivateApis(
      String repositoryName, String repositoryFormat, NexusConfig config, List<EncryptedDataDetail> encryptionDetails) {
    return nexusService.getGroupIdPathsUsingPrivateApis(config, encryptionDetails, repositoryName, repositoryFormat);
  }

  @Override
  public BuildDetails getLastSuccessfulBuild(String appId, ArtifactStreamAttributes artifactStreamAttributes,
      NexusConfig config, List<EncryptedDataDetail> encryptionDetails) {
    equalCheck(artifactStreamAttributes.getArtifactStreamType(), ArtifactStreamType.NEXUS.name());
    return wrapLastSuccessfulBuildWithLabels(
        nexusService.getLatestVersion(config, encryptionDetails, artifactStreamAttributes.getJobName(),
            artifactStreamAttributes.getGroupId(), artifactStreamAttributes.getArtifactName()),
        artifactStreamAttributes, config);
  }

  @Override
  public boolean validateArtifactServer(NexusConfig nexusConfig, List<EncryptedDataDetail> encryptedDataDetails) {
    if (!connectableHttpUrl(nexusConfig.getNexusUrl())) {
      throw new WingsException(ErrorCode.INVALID_ARTIFACT_SERVER, USER)
          .addParam("message", "Could not reach Nexus Server at : " + nexusConfig.getNexusUrl());
    }
    return nexusService.isRunning(nexusConfig, encryptedDataDetails);
  }

  @Override
  public boolean validateArtifactSource(NexusConfig config, List<EncryptedDataDetail> encryptionDetails,
      ArtifactStreamAttributes artifactStreamAttributes) {
    if (hasSome(artifactStreamAttributes.getExtension()) || hasSome(artifactStreamAttributes.getClassifier())) {
      return nexusService.existsVersion(config, encryptionDetails, artifactStreamAttributes.getJobName(),
          artifactStreamAttributes.getGroupId(), artifactStreamAttributes.getArtifactName(),
          artifactStreamAttributes.getExtension(), artifactStreamAttributes.getClassifier());
    }
    return true;
  }

  @Override
  public Map<String, String> getBuckets(
      NexusConfig config, String projectId, List<EncryptedDataDetail> encryptionDetails) {
    throw new InvalidRequestException("Operation not supported by Nexus Artifact Stream");
  }

  @Override
  public List<String> getSmbPaths(NexusConfig config, List<EncryptedDataDetail> encryptionDetails) {
    throw new InvalidRequestException("Operation not supported by Nexus Build Service", WingsException.USER);
  }

  @Override
  public List<String> getArtifactPathsByStreamType(
      NexusConfig config, List<EncryptedDataDetail> encryptionDetails, String streamType) {
    throw new InvalidRequestException("Operation not supported by Nexus Build Service", WingsException.USER);
  }
}
