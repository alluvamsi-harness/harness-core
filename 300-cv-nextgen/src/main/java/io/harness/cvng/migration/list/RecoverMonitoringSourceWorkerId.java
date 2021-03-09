package io.harness.cvng.migration.list;

import com.google.inject.Inject;
import io.harness.cvng.core.entities.MonitoringSourcePerpetualTask;
import io.harness.cvng.core.entities.MonitoringSourcePerpetualTask.MonitoringSourcePerpetualTaskKeys;
import io.harness.cvng.core.services.api.DataCollectionTaskService;
import io.harness.cvng.migration.CNVGMigration;
import io.harness.persistence.HPersistence;
import lombok.extern.slf4j.Slf4j;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.List;

import static io.harness.data.structure.EmptyPredicate.isNotEmpty;
import static io.harness.persistence.HQuery.excludeAuthority;
@Slf4j
public class RecoverMonitoringSourceWorkerId implements CNVGMigration {
  @Inject private HPersistence hPersistence;
  @Inject private DataCollectionTaskService dataCollectionTaskService;

  @Override
  public void migrate() {
    List<MonitoringSourcePerpetualTask> monitoringSourcePerpetualTasks =
        hPersistence.createQuery(MonitoringSourcePerpetualTask.class, excludeAuthority).asList();
    log.info("Trying to migrate {}", monitoringSourcePerpetualTasks);

    monitoringSourcePerpetualTasks.forEach(monitoringSourcePerpetualTask -> {
      log.info("Starting migration for {}", monitoringSourcePerpetualTask);
      if (isNotEmpty(monitoringSourcePerpetualTask.getPerpetualTaskId())) {
        dataCollectionTaskService.deletePerpetualTasks(
            monitoringSourcePerpetualTask.getAccountId(), monitoringSourcePerpetualTask.getPerpetualTaskId());
      }
      UpdateOperations<MonitoringSourcePerpetualTask> updateOperations =
          hPersistence.createUpdateOperations(MonitoringSourcePerpetualTask.class);
      updateOperations.unset(MonitoringSourcePerpetualTaskKeys.dataCollectionWorkerId);
      updateOperations.unset(MonitoringSourcePerpetualTaskKeys.perpetualTaskId);
      UpdateResults updateResults = hPersistence.update(monitoringSourcePerpetualTask, updateOperations);
      log.info("Updated monitoring source {}, {}", monitoringSourcePerpetualTask, updateResults.getUpdatedCount());
    });
  }
}
