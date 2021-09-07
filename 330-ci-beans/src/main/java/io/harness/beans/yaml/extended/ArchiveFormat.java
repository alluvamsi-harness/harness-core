package io.harness.beans.yaml.extended;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ArchiveFormat {
  @JsonProperty("Tar") TAR("tar"),
  @JsonProperty("Gzip") GZIP("gzip");
  private final String yamlName;

  @JsonCreator
  public static ArchiveFormat getArchiveFormat(@JsonProperty("archiveFormat") String yamlName) {
    for (ArchiveFormat archiveFormat : ArchiveFormat.values()) {
      if (archiveFormat.yamlName.equalsIgnoreCase(yamlName)) {
        return archiveFormat;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + yamlName);
  }

  ArchiveFormat(String yamlName) {
    this.yamlName = yamlName;
  }

  @JsonValue
  public String getYamlName() {
    return yamlName;
  }

  @Override
  public String toString() {
    return yamlName;
  }

  public static ArchiveFormat fromString(final String s) {
    return ArchiveFormat.getArchiveFormat(s);
  }
}
