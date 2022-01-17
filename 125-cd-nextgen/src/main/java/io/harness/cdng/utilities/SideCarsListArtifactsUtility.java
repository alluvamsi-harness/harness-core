/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cdng.utilities;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.cdng.visitor.YamlTypes;
import io.harness.data.structure.EmptyPredicate;
import io.harness.exception.InvalidRequestException;
import io.harness.exception.YamlException;
import io.harness.pms.contracts.plan.YamlUpdates;
import io.harness.pms.yaml.YamlField;
import io.harness.pms.yaml.YamlNode;
import io.harness.pms.yaml.YamlUtils;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Map;
import lombok.experimental.UtilityClass;

@OwnedBy(HarnessTeam.PIPELINE)
@UtilityClass
public class SideCarsListArtifactsUtility {
  public JsonNode getSideCarsListJsonNode() {
    String yamlField = "---\n"
        + "- sidecar:\n"
        + "      spec:\n"
        + "      type: DockerRegistry\n"
        + "      identifier: \""
        + "abc"
        + "\"\n";
    YamlField sideCarsYamlField;
    try {
      String yamlFieldWithUuid = YamlUtils.injectUuid(yamlField);
      sideCarsYamlField = YamlUtils.readTree(yamlFieldWithUuid);
    } catch (IOException e) {
      throw new InvalidRequestException("Exception while creating primary field");
    }
    return sideCarsYamlField.getNode().getCurrJsonNode();
  }

  public JsonNode getIndividualSideCarsListJsonNode(String identifier) {
    String yamlField = "---\n"
        + "- sidecar:\n"
        + "      spec:\n"
        + "      type: DockerRegistry\n"
        + "      identifier: \"" + identifier + "\"\n";
    YamlField sideCarsYamlField;
    try {
      String yamlFieldWithUuid = YamlUtils.injectUuid(yamlField);
      sideCarsYamlField = YamlUtils.readTree(yamlFieldWithUuid);
    } catch (IOException e) {
      throw new InvalidRequestException("Exception while creating primary field");
    }
    return sideCarsYamlField.getNode().getCurrJsonNode();
  }

  public YamlField createIndividualSideCarsArtifactYamlFieldAndSetYamlUpdate(YamlField sideCarsYamlField,
      String sideCarIdentifier, Map<String, YamlNode> mapIdentifierWithYamlNode, YamlUpdates.Builder yamlUpdates) {
    if (mapIdentifierWithYamlNode.containsKey(sideCarIdentifier)) {
      return new YamlField(mapIdentifierWithYamlNode.get(sideCarIdentifier))
          .getNode()
          .getField(YamlTypes.SIDECAR_ARTIFACT_CONFIG);
    }

    return sideCarsYamlField.getNode().asArray().get(0).getField(YamlTypes.SIDECAR_ARTIFACT_CONFIG);
  }

  public YamlField createSideCarsArtifactYamlFieldAndSetYamlUpdate(
      YamlField artifactField, YamlUpdates.Builder yamlUpdates) {
    YamlField sideCarsYamlField = artifactField.getNode().getField(YamlTypes.SIDECARS_ARTIFACT_CONFIG);

    if (sideCarsYamlField != null && EmptyPredicate.isNotEmpty(sideCarsYamlField.getNode().asArray())) {
      return sideCarsYamlField;
    }

    sideCarsYamlField = createSideCarsYamlFieldUnderArtifacts(artifactField);
    setYamlUpdate(sideCarsYamlField, yamlUpdates);
    return sideCarsYamlField;
  }

  private static YamlUpdates.Builder setYamlUpdate(YamlField yamlField, YamlUpdates.Builder yamlUpdates) {
    try {
      return yamlUpdates.putFqnToYaml(yamlField.getYamlPath(), YamlUtils.writeYamlString(yamlField));
    } catch (IOException e) {
      throw new YamlException(
          "Yaml created for yamlField at " + yamlField.getYamlPath() + " could not be converted into a yaml string");
    }
  }

  private YamlField createSideCarsYamlFieldUnderArtifacts(YamlField artifacts) {
    return new YamlField(YamlTypes.SIDECARS_ARTIFACT_CONFIG,
        new YamlNode(YamlTypes.SIDECARS_ARTIFACT_CONFIG, SideCarsListArtifactsUtility.getSideCarsListJsonNode(),
            artifacts.getNode()));
  }
}