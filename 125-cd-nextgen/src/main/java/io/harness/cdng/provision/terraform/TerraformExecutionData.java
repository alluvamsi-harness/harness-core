package io.harness.cdng.provision.terraform;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.common.SwaggerConstants;
import io.harness.cdng.provision.terraform.TerraformExecutionDataParameters.TerraformExecutionDataParametersBuilder;
import io.harness.data.structure.EmptyPredicate;
import io.harness.exception.InvalidRequestException;
import io.harness.exception.WingsException;
import io.harness.pms.yaml.ParameterField;
import io.harness.validation.Validator;
import io.harness.yaml.core.variables.NGVariable;
import io.harness.yaml.utils.NGVariablesUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@OwnedBy(HarnessTeam.CDP)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerraformExecutionData {
  @ApiModelProperty(dataType = SwaggerConstants.STRING_CLASSPATH) ParameterField<String> workspace;
  @JsonProperty("configFiles") TerraformConfigFilesWrapper terraformConfigFilesWrapper;
  @JsonProperty("varFiles") List<TerraformVarFileWrapper> terraformVarFiles;
  @JsonProperty("backendConfig") TerraformBackendConfig terraformBackendConfig;
  @ApiModelProperty(dataType = SwaggerConstants.STRING_LIST_CLASSPATH) ParameterField<List<String>> targets;
  List<NGVariable> environmentVariables;

  public TerraformExecutionDataParameters toStepParameters() {
    Validator.notNullCheck("Config files are null", terraformConfigFilesWrapper);
    TerraformExecutionDataParametersBuilder builder =
        TerraformExecutionDataParameters.builder()
            .workspace(workspace)
            .configFiles(terraformConfigFilesWrapper)
            .backendConfig(terraformBackendConfig)
            .targets(targets)
            .environmentVariables(NGVariablesUtils.getMapOfVariables(environmentVariables, 0L));
    LinkedHashMap<String, TerraformVarFile> varFiles = new LinkedHashMap<>();
    if (EmptyPredicate.isNotEmpty(terraformVarFiles)) {
      terraformVarFiles.forEach(terraformVarFile -> {
        if (terraformVarFile != null) {
          TerraformVarFile varFile = terraformVarFile.getVarFile();
          if (varFile != null) {
            if (StringUtils.isEmpty(varFile.getIdentifier())) {
              throw new InvalidRequestException("Identifier in Var File can't be empty", WingsException.USER);
            }
            varFiles.put(varFile.getIdentifier(), varFile);
          }
        }
      });
    }
    builder.varFiles(varFiles);
    return builder.build();
  }
}
