package software.wings.beans;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ConfigFileDto {
  private final String uuid;
  private final String relativeFilePath;
  private final Map<String, Integer> envVersionMap;
  private final long size;
  private final boolean encrypted;
  private final int defaultVersion;

  public int getVersionForEnv(String envId) {
    if (envVersionMap.containsKey(envId)) {
      return envVersionMap.get(envId);
    }

    return defaultVersion;
  }
}
