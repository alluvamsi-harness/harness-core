/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.serializer.kryo;

import io.harness.cdng.artifact.outcome.AcrArtifactOutcome;
import io.harness.cdng.artifact.outcome.ArtifactoryGenericArtifactOutcome;
import io.harness.cdng.artifact.outcome.DockerArtifactOutcome;
import io.harness.cdng.artifact.outcome.EcrArtifactOutcome;
import io.harness.cdng.artifact.outcome.GcrArtifactOutcome;
import io.harness.cdng.infra.beans.K8sDirectInfrastructureOutcome;
import io.harness.cdng.infra.beans.K8sGcpInfrastructureOutcome;
import io.harness.cdng.infra.beans.ServerlessAwsLambdaInfrastructureOutcome;
import io.harness.cdng.instance.outcome.DeploymentInfoOutcome;
import io.harness.cdng.manifest.yaml.HelmChartManifestOutcome;
import io.harness.cdng.manifest.yaml.HelmCommandFlagType;
import io.harness.cdng.manifest.yaml.HelmManifestCommandFlag;
import io.harness.cdng.manifest.yaml.K8sManifestOutcome;
import io.harness.cdng.manifest.yaml.KustomizeManifestOutcome;
import io.harness.cdng.manifest.yaml.KustomizePatchesManifestOutcome;
import io.harness.cdng.manifest.yaml.OpenshiftManifestOutcome;
import io.harness.cdng.manifest.yaml.OpenshiftParamManifestOutcome;
import io.harness.cdng.manifest.yaml.ServerlessAwsLambdaManifestOutcome;
import io.harness.cdng.manifest.yaml.ValuesManifestOutcome;
import io.harness.cdng.manifest.yaml.storeConfig.StoreConfig;
import io.harness.cdng.service.beans.ServiceConfigOutcome;
import io.harness.cdng.service.beans.ServiceOutcome;
import io.harness.ng.core.service.entity.ServiceEntity;
import io.harness.serializer.KryoRegistrar;

import com.esotericsoftware.kryo.Kryo;

public class NGEntitiesKryoRegistrar implements KryoRegistrar {
  @Override
  public void register(Kryo kryo) {
    kryo.register(ServiceEntity.class, 22002);
    kryo.register(K8sDirectInfrastructureOutcome.class, 8105);
    kryo.register(K8sGcpInfrastructureOutcome.class, 8300);
    kryo.register(DeploymentInfoOutcome.class, 12547);
    kryo.register(HelmChartManifestOutcome.class, 12524);
    kryo.register(HelmCommandFlagType.class, 12526);
    kryo.register(HelmManifestCommandFlag.class, 12525);
    kryo.register(K8sManifestOutcome.class, 12502);
    kryo.register(KustomizeManifestOutcome.class, 12532);
    kryo.register(OpenshiftManifestOutcome.class, 12535);
    kryo.register(OpenshiftParamManifestOutcome.class, 12537);
    kryo.register(ValuesManifestOutcome.class, 12503);
    kryo.register(ServiceOutcome.class, 8018);
    kryo.register(ServiceOutcome.ArtifactsOutcome.class, 8019);
    kryo.register(ServiceOutcome.StageOverridesOutcome.class, 12504);
    kryo.register(ServiceOutcome.ArtifactsWrapperOutcome.class, 12505);
    kryo.register(ServiceOutcome.ManifestsWrapperOutcome.class, 12506);
    kryo.register(ServiceOutcome.VariablesWrapperOutcome.class, 12507);
    kryo.register(StoreConfig.class, 8022);
    kryo.register(KustomizePatchesManifestOutcome.class, 12548);
    kryo.register(DockerArtifactOutcome.class, 8007);
    kryo.register(GcrArtifactOutcome.class, 390006);
    kryo.register(EcrArtifactOutcome.class, 390007);
    kryo.register(ServerlessAwsLambdaManifestOutcome.class, 390008);
    kryo.register(ServerlessAwsLambdaInfrastructureOutcome.class, 390009);
    kryo.register(ArtifactoryGenericArtifactOutcome.class, 390010);
    kryo.register(AcrArtifactOutcome.class, 390011);
    kryo.register(ServiceConfigOutcome.class, 12508);
  }
}
