package software.wings.graphql.datafetcher.artifact;

import io.harness.annotations.dev.Module;
import io.harness.annotations.dev.TargetModule;
import io.harness.exception.InvalidRequestException;
import io.harness.persistence.CreatedAtAware;

import software.wings.beans.artifact.Artifact;
import software.wings.graphql.datafetcher.AbstractConnectionV2DataFetcher;
import software.wings.graphql.schema.query.QLPageQueryParameters;
import software.wings.graphql.schema.type.QLArtifactConnection;
import software.wings.graphql.schema.type.QLArtifactConnection.QLArtifactConnectionBuilder;
import software.wings.graphql.schema.type.aggregation.QLIdFilter;
import software.wings.graphql.schema.type.aggregation.QLIdOperator;
import software.wings.graphql.schema.type.aggregation.QLNoOpSortCriteria;
import software.wings.graphql.schema.type.aggregation.artifact.QLArtifactFilter;
import software.wings.graphql.schema.type.artifact.QLArtifact;
import software.wings.graphql.schema.type.artifact.QLArtifact.QLArtifactBuilder;
import software.wings.graphql.utils.nameservice.NameService;
import software.wings.security.PermissionAttribute;
import software.wings.security.annotations.AuthRule;

import com.google.inject.Inject;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.Sort;

@TargetModule(Module._380_CG_GRAPHQL)
public class ArtifactConnectionDataFetcher
    extends AbstractConnectionV2DataFetcher<QLArtifactFilter, QLNoOpSortCriteria, QLArtifactConnection> {
  @Inject ArtifactQueryHelper artifactQueryHelper;

  @Override
  @AuthRule(permissionType = PermissionAttribute.PermissionType.SERVICE, action = PermissionAttribute.Action.READ)
  protected QLArtifactConnection fetchConnection(List<QLArtifactFilter> filters,
      QLPageQueryParameters pageQueryParameters, List<QLNoOpSortCriteria> sortCriteria) {
    Query<Artifact> query = populateFilters(wingsPersistence, filters, Artifact.class, true)
                                .order(Sort.descending(CreatedAtAware.CREATED_AT_KEY));

    QLArtifactConnectionBuilder connectionBuilder = QLArtifactConnection.builder();
    connectionBuilder.pageInfo(utils.populate(pageQueryParameters, query, artifact -> {
      QLArtifactBuilder builder = QLArtifact.builder();
      ArtifactController.populateArtifact(artifact, builder);
      connectionBuilder.node(builder.build());
    }));
    return connectionBuilder.build();
  }

  @Override
  protected void populateFilters(List<QLArtifactFilter> filters, Query query) {
    artifactQueryHelper.setQuery(filters, query);
  }

  @Override
  protected QLArtifactFilter generateFilter(DataFetchingEnvironment environment, String key, String value) {
    QLIdFilter idFilter = QLIdFilter.builder()
                              .operator(QLIdOperator.EQUALS)
                              .values(new String[] {(String) utils.getFieldValue(environment.getSource(), value)})
                              .build();
    if (NameService.artifactSource.equals(key)) {
      return QLArtifactFilter.builder().artifactSource(idFilter).build();
    }
    throw new InvalidRequestException("Unsupported field " + key + " while generating filter");
  }
}
