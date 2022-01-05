package io.harness.cdng.yaml;

import static io.harness.yaml.schema.beans.SchemaConstants.DEFINITIONS_NODE;
import static io.harness.yaml.schema.beans.SchemaConstants.PROPERTIES_NODE;

import io.harness.EntityType;
import io.harness.ModuleType;
import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.cdng.creator.plan.stage.DeploymentStageConfig;
import io.harness.encryption.Scope;
import io.harness.jackson.JsonNodeUtils;
import io.harness.plancreator.steps.ParallelStepElementConfig;
import io.harness.plancreator.steps.StepElementConfig;
import io.harness.pms.contracts.steps.StepCategory;
import io.harness.yaml.schema.SchemaGeneratorUtils;
import io.harness.yaml.schema.YamlSchemaGenerator;
import io.harness.yaml.schema.YamlSchemaProvider;
import io.harness.yaml.schema.beans.FieldEnumData;
import io.harness.yaml.schema.beans.PartialSchemaDTO;
import io.harness.yaml.schema.beans.SchemaConstants;
import io.harness.yaml.schema.beans.SubtypeClassMap;
import io.harness.yaml.schema.beans.SwaggerDefinitionsMetaInfo;
import io.harness.yaml.schema.beans.YamlSchemaRootClass;
import io.harness.yaml.schema.beans.YamlSchemaWithDetails;
import io.harness.yaml.utils.YamlSchemaUtils;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@OwnedBy(HarnessTeam.CDP)
@Slf4j
public class CdYamlSchemaServiceImpl implements CdYamlSchemaService {
  private static final String DEPLOYMENT_STAGE_CONFIG = YamlSchemaUtils.getSwaggerName(DeploymentStageConfig.class);
  private static final String STEP_ELEMENT_CONFIG = YamlSchemaUtils.getSwaggerName(StepElementConfig.class);
  private static final Class<StepElementConfig> STEP_ELEMENT_CONFIG_CLASS = StepElementConfig.class;

  private static final String CD_NAMESPACE = "cd";

  private final YamlSchemaProvider yamlSchemaProvider;
  private final YamlSchemaGenerator yamlSchemaGenerator;

  private final Map<Class<?>, Set<Class<?>>> yamlSchemaSubtypes;
  private final List<YamlSchemaRootClass> yamlSchemaRootClasses;
  @Inject
  public CdYamlSchemaServiceImpl(YamlSchemaProvider yamlSchemaProvider, YamlSchemaGenerator yamlSchemaGenerator,
      @Named("yaml-schema-subtypes") Map<Class<?>, Set<Class<?>>> yamlSchemaSubtypes,
      List<YamlSchemaRootClass> yamlSchemaRootClasses) {
    this.yamlSchemaProvider = yamlSchemaProvider;
    this.yamlSchemaGenerator = yamlSchemaGenerator;
    this.yamlSchemaSubtypes = yamlSchemaSubtypes;
    this.yamlSchemaRootClasses = yamlSchemaRootClasses;
  }

  @Override
  public PartialSchemaDTO getMergedDeploymentStageYamlSchema(
      String projectIdentifier, String orgIdentifier, Scope scope, List<YamlSchemaWithDetails> stepSchemaWithDetails) {
    return getDeploymentStageYamlSchemaUtil(projectIdentifier, orgIdentifier, scope, stepSchemaWithDetails);
  }

  @Override
  public List<YamlSchemaWithDetails> getDeploymentStageYamlSchemaWithDetails(
      String projectIdentifier, String orgIdentifier, Scope scope) {
    return yamlSchemaProvider.getCrossFunctionalStepsSchemaDetails(projectIdentifier, orgIdentifier, scope,
        YamlSchemaUtils.getNodeEntityTypesByYamlGroup(yamlSchemaRootClasses, StepCategory.STEP.name()), ModuleType.CD);
  }

  @Override
  public JsonNode getStepYamlSchema(EntityType entityType) {
    return yamlSchemaProvider.getYamlSchema(entityType, null, null, null);
  }

  @Override
  public List<PartialSchemaDTO> getDeploymentStageYamlSchema(
      String projectIdentifier, String orgIdentifier, Scope scope) {
    return Collections.singletonList(getDeploymentStageYamlSchemaUtil(projectIdentifier, orgIdentifier, scope, null));
  }

  public PartialSchemaDTO getDeploymentStageYamlSchemaUtil(
      String projectIdentifier, String orgIdentifier, Scope scope, List<YamlSchemaWithDetails> stepSchemaWithDetails) {
    JsonNode deploymentStageSchema =
        yamlSchemaProvider.getYamlSchema(EntityType.DEPLOYMENT_STAGE, orgIdentifier, projectIdentifier, scope);

    JsonNode deploymentStepsSchema =
        yamlSchemaProvider.getYamlSchema(EntityType.DEPLOYMENT_STEPS, orgIdentifier, projectIdentifier, scope);

    JsonNode definitions = deploymentStageSchema.get(DEFINITIONS_NODE);
    JsonNode deploymentStepDefinitions = deploymentStepsSchema.get(DEFINITIONS_NODE);

    JsonNodeUtils.merge(definitions, deploymentStepDefinitions);
    yamlSchemaProvider.mergeAllV2StepsDefinitions(projectIdentifier, orgIdentifier, scope, (ObjectNode) definitions,
        YamlSchemaUtils.getNodeEntityTypesByYamlGroup(yamlSchemaRootClasses, StepCategory.STEP.name()));
    JsonNode jsonNode = definitions.get(ParallelStepElementConfig.class.getSimpleName());
    if (jsonNode.isObject()) {
      flattenParallelStepElementConfig((ObjectNode) jsonNode);
    }

    JsonNode stepElementConfigNode = definitions.get(StepElementConfig.class.getSimpleName());
    if (stepElementConfigNode != null && stepElementConfigNode.isObject()) {
      modifyStepElementSchema((ObjectNode) stepElementConfigNode);
    }

    yamlSchemaGenerator.modifyRefsNamespace(deploymentStageSchema, CD_NAMESPACE);

    // Should be after this modifyRefsNamespace call.
    YamlSchemaUtils.addOneOfInExecutionWrapperConfig(deploymentStageSchema.get(DEFINITIONS_NODE),
        YamlSchemaUtils.getNodeClassesByYamlGroup(yamlSchemaRootClasses, StepCategory.STEP.name()), CD_NAMESPACE);
    if (stepSchemaWithDetails != null) {
      YamlSchemaUtils.addOneOfInExecutionWrapperConfig(
          deploymentStageSchema.get(DEFINITIONS_NODE), stepSchemaWithDetails, ModuleType.CD);
    }
    ObjectMapper mapper = SchemaGeneratorUtils.getObjectMapperForSchemaGeneration();
    JsonNode node = mapper.createObjectNode().set(CD_NAMESPACE, definitions);

    JsonNode partialCdSchema = ((ObjectNode) deploymentStageSchema).set(DEFINITIONS_NODE, node);

    return PartialSchemaDTO.builder()
        .namespace(CD_NAMESPACE)
        .nodeName(DEPLOYMENT_STAGE_CONFIG)
        .schema(partialCdSchema)
        .nodeType(getDeploymentStageTypeName())
        .moduleType(ModuleType.CD)
        .build();
  }

  private void modifyStepElementSchema(ObjectNode jsonNode) {
    ObjectMapper mapper = SchemaGeneratorUtils.getObjectMapperForSchemaGeneration();
    Map<String, SwaggerDefinitionsMetaInfo> swaggerDefinitionsMetaInfoMap = new HashMap<>();

    Set<FieldEnumData> fieldEnumData = getFieldEnumData(STEP_ELEMENT_CONFIG_CLASS);

    swaggerDefinitionsMetaInfoMap.put(
        STEP_ELEMENT_CONFIG, SwaggerDefinitionsMetaInfo.builder().fieldEnumData(fieldEnumData).build());

    yamlSchemaGenerator.convertSwaggerToJsonSchema(
        swaggerDefinitionsMetaInfoMap, mapper, STEP_ELEMENT_CONFIG, jsonNode);
  }

  private Set<FieldEnumData> getFieldEnumData(Class<?> clazz) {
    Field typedField = YamlSchemaUtils.getTypedField(clazz);
    String fieldName = YamlSchemaUtils.getJsonTypeInfo(typedField).property();
    Set<Class<?>> cachedSubtypes = yamlSchemaSubtypes.get(typedField.getType());
    Set<SubtypeClassMap> mapOfSubtypes = YamlSchemaUtils.toSetOfSubtypeClassMap(cachedSubtypes);
    return ImmutableSet.of(
        FieldEnumData.builder()
            .fieldName(fieldName)
            .enumValues(ImmutableSortedSet.copyOf(
                mapOfSubtypes.stream().map(SubtypeClassMap::getSubtypeEnum).collect(Collectors.toList())))
            .build());
  }

  private void flattenParallelStepElementConfig(ObjectNode objectNode) {
    JsonNode sections = objectNode.get(PROPERTIES_NODE).get("sections");
    if (sections.isObject()) {
      objectNode.removeAll();
      objectNode.setAll((ObjectNode) sections);
      objectNode.put(SchemaConstants.SCHEMA_NODE, SchemaConstants.JSON_SCHEMA_7);
    }
  }

  private String getDeploymentStageTypeName() {
    JsonTypeName annotation = DeploymentStageConfig.class.getAnnotation(JsonTypeName.class);
    return annotation.value();
  }
}
