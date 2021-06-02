package io.harness.migrations.timescaledb;

import io.harness.annotations.dev.HarnessModule;

public class AddingToCVDeploymentMetrics extends AbstractTimeScaleDBMigration {
  @Override
  public String getFileName() {
    return "timescaledb/add_fields_cv_worfklow_stats.sql";
  }
}
