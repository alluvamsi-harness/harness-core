package io.harness.pms.pipeline.service.yamlschema.customstage;

import io.harness.encryption.Scope;
import io.harness.yaml.schema.beans.PartialSchemaDTO;
import io.harness.yaml.schema.beans.YamlSchemaWithDetails;

import java.util.List;

public interface CustomStageYamlSchemaService {
  PartialSchemaDTO getCustomStageYamlSchema(String accountIdentifier, String projectIdentifier, String orgIdentifier,
      Scope scope, List<YamlSchemaWithDetails> yamlSchemaWithDetailsList);
}
