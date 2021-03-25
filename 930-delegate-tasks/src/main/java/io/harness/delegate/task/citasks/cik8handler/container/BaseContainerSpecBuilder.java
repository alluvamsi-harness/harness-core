package io.harness.delegate.task.citasks.cik8handler.container;

/**
 * An abstract class to generate K8 container spec based on container parameters provided to it. It builds minimal
 * container spec essential for creating a container on a pod. It is designed to be a superclass for concrete K8
 * container specification builder.
 */

import static io.harness.data.structure.HasPredicate.hasSome;
import static io.harness.delegate.task.citasks.cik8handler.params.CIConstants.DIND_TAG_REGEX;
import static io.harness.delegate.task.citasks.cik8handler.params.CIConstants.DOCKER_IMAGE_NAME;
import static io.harness.delegate.task.citasks.cik8handler.params.CIConstants.PLUGIN_ACR_IMAGE_NAME;
import static io.harness.delegate.task.citasks.cik8handler.params.CIConstants.PLUGIN_DOCKER_IMAGE_NAME;
import static io.harness.delegate.task.citasks.cik8handler.params.CIConstants.PLUGIN_ECR_IMAGE_NAME;
import static io.harness.delegate.task.citasks.cik8handler.params.CIConstants.PLUGIN_GCR_IMAGE_NAME;
import static io.harness.delegate.task.citasks.cik8handler.params.CIConstants.PLUGIN_HEROKU_IMAGE_NAME;
import static io.harness.validation.Validator.notNullCheck;

import static java.lang.String.format;

import io.harness.delegate.beans.ci.pod.ContainerParams;
import io.harness.delegate.beans.ci.pod.ContainerResourceParams;
import io.harness.delegate.beans.ci.pod.SecretVarParams;
import io.harness.delegate.task.citasks.cik8handler.params.CIConstants;
import io.harness.k8s.model.ImageDetails;

import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.EnvVarSourceBuilder;
import io.fabric8.kubernetes.api.model.LocalObjectReference;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.api.model.SecretKeySelectorBuilder;
import io.fabric8.kubernetes.api.model.SecretVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.SecurityContextBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseContainerSpecBuilder {
  public ContainerSpecBuilderResponse createSpec(ContainerParams containerParams) {
    ContainerSpecBuilderResponse containerSpecBuilderResponse = getBaseSpec(containerParams);
    decorateSpec(containerParams, containerSpecBuilderResponse);
    return containerSpecBuilderResponse;
  }

  /**
   * Builds on minimal container spec generated by getBaseSpec method.
   */
  protected abstract void decorateSpec(
      ContainerParams containerParams, ContainerSpecBuilderResponse containerSpecBuilderResponse);

  private ContainerSpecBuilderResponse getBaseSpec(ContainerParams containerParams) {
    notNullCheck("Container parameters should be specified", containerParams);

    List<EnvVar> envVars = getContainerEnvVars(containerParams.getEnvVars(), containerParams.getSecretEnvVars());
    List<ContainerPort> containerPorts = new ArrayList<>();
    if (containerParams.getPorts() != null) {
      containerParams.getPorts().forEach(
          port -> containerPorts.add(new ContainerPortBuilder().withContainerPort(port).build()));
    }

    List<VolumeMount> volumeMounts = new ArrayList<>();
    List<Volume> volumes = new ArrayList<>();
    if (containerParams.getVolumeToMountPath() != null) {
      containerParams.getVolumeToMountPath().forEach(
          (volumeName, volumeMountPath)
              -> volumeMounts.add(
                  new VolumeMountBuilder().withName(volumeName).withMountPath(volumeMountPath).build()));
    }
    if (containerParams.getSecretVolumes() != null) {
      containerParams.getSecretVolumes().forEach((key, secretVolumeParams) -> {
        volumeMounts.add(new VolumeMountBuilder()
                             .withName(CIConstants.SECRET_VOLUME_NAME)
                             .withMountPath(secretVolumeParams.getMountPath())
                             .build());
        volumes.add(new VolumeBuilder()
                        .withName(CIConstants.SECRET_VOLUME_NAME)
                        .withSecret(new SecretVolumeSourceBuilder()
                                        .withSecretName(secretVolumeParams.getSecretName())
                                        .addNewItem(secretVolumeParams.getSecretKey(), CIConstants.SECRET_FILE_MODE,
                                            secretVolumeParams.getSecretKey())
                                        .build())
                        .build());
      });
    }

    LocalObjectReference imageSecret = null;
    ImageDetails imageDetails = containerParams.getImageDetailsWithConnector().getImageDetails();
    if (containerParams.getImageSecret() != null) {
      imageSecret = new LocalObjectReference(containerParams.getImageSecret());
    }

    ResourceRequirements resourceRequirements = getResourceRequirements(containerParams.getContainerResourceParams());

    String imageName = imageDetails.getName();
    if (hasSome(imageDetails.getTag())) {
      imageName = imageName + ":" + imageDetails.getTag();
    }

    ContainerBuilder containerBuilder = new ContainerBuilder()
                                            .withName(containerParams.getName())
                                            .withImage(imageName)
                                            .withCommand(containerParams.getCommands())
                                            .withArgs(containerParams.getArgs())
                                            .withEnv(envVars)
                                            .withResources(resourceRequirements)
                                            .withPorts(containerPorts)
                                            .withVolumeMounts(volumeMounts);

    if (isPrivilegedImage(imageDetails)) {
      containerBuilder.withSecurityContext(new SecurityContextBuilder().withPrivileged(true).build());
    }

    if (hasSome(containerParams.getWorkingDir())) {
      containerBuilder.withWorkingDir(containerParams.getWorkingDir());
    }

    return ContainerSpecBuilderResponse.builder()
        .containerBuilder(containerBuilder)
        .imageSecret(imageSecret)
        .volumes(volumes)
        .build();
  }

  private boolean isPrivilegedImage(ImageDetails imageDetails) {
    String imageName = imageDetails.getName();
    String tag = imageDetails.getTag();
    if (imageName.equals(PLUGIN_DOCKER_IMAGE_NAME) || imageName.equals(PLUGIN_ECR_IMAGE_NAME)
        || imageName.equals(PLUGIN_ACR_IMAGE_NAME) || imageName.equals(PLUGIN_GCR_IMAGE_NAME)
        || imageName.equals(PLUGIN_HEROKU_IMAGE_NAME)) {
      return true;
    }

    if (imageName.equals(DOCKER_IMAGE_NAME) && hasSome(tag) && tag.matches(DIND_TAG_REGEX)) {
      return true;
    }
    return false;
  }

  private ResourceRequirements getResourceRequirements(ContainerResourceParams containerResourceParams) {
    if (containerResourceParams == null) {
      return null;
    }

    Integer resourceRequestMemoryMiB = containerResourceParams.getResourceRequestMemoryMiB();
    Integer resourceLimitMemoryMiB = containerResourceParams.getResourceLimitMemoryMiB();
    Integer resourceRequestMilliCpu = containerResourceParams.getResourceRequestMilliCpu();
    Integer resourceLimitMilliCpu = containerResourceParams.getResourceLimitMilliCpu();
    ResourceRequirementsBuilder builder = new ResourceRequirementsBuilder();
    if (resourceRequestMemoryMiB != null && resourceRequestMemoryMiB != 0) {
      builder.addToRequests(
          CIConstants.MEMORY, new Quantity(format("%d%s", resourceRequestMemoryMiB, CIConstants.MEMORY_FORMAT)));
    }
    if (resourceLimitMemoryMiB != null && resourceLimitMemoryMiB != 0) {
      builder.addToLimits(
          CIConstants.MEMORY, new Quantity(format("%d%s", resourceLimitMemoryMiB, CIConstants.MEMORY_FORMAT)));
    }

    if (resourceRequestMilliCpu != null && resourceRequestMilliCpu != 0) {
      builder.addToRequests(
          CIConstants.CPU, new Quantity(format("%d%s", resourceRequestMilliCpu, CIConstants.CPU_FORMAT)));
    }
    if (resourceLimitMilliCpu != null && resourceLimitMilliCpu != 0) {
      builder.addToLimits(CIConstants.CPU, new Quantity(format("%d%s", resourceLimitMilliCpu, CIConstants.CPU_FORMAT)));
    }

    return builder.build();
  }

  private List<EnvVar> getContainerEnvVars(Map<String, String> envVars, Map<String, SecretVarParams> secretEnvVars) {
    List<EnvVar> ctrEnvVars = new ArrayList<>();
    if (envVars != null) {
      envVars.forEach((name, val) -> ctrEnvVars.add(new EnvVarBuilder().withName(name).withValue(val).build()));
    }
    if (secretEnvVars != null) {
      secretEnvVars.forEach(
          (name, secretKeyParam)
              -> ctrEnvVars.add(new EnvVarBuilder()
                                    .withName(name)
                                    .withValueFrom(new EnvVarSourceBuilder()
                                                       .withSecretKeyRef(new SecretKeySelectorBuilder()
                                                                             .withKey(secretKeyParam.getSecretKey())
                                                                             .withName(secretKeyParam.getSecretName())
                                                                             .build())
                                                       .build())
                                    .build()));
    }
    return ctrEnvVars;
  }
}
