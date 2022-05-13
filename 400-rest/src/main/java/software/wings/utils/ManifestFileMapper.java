package software.wings.utils;

import static io.harness.data.structure.EmptyPredicate.isEmpty;

import software.wings.beans.appmanifest.ManifestFile;
import software.wings.beans.appmanifest.ManifestFileDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ManifestFileMapper {
  public List<ManifestFileDTO> manifestFileDTOList(List<ManifestFile> manifestFiles) {
    if (isEmpty(manifestFiles)) {
      return Collections.emptyList();
    }
    return manifestFiles.stream()
        .map(manifestFile
            -> ManifestFileDTO.builder()
                   .accountId(manifestFile.getAccountId())
                   .fileName(manifestFile.getFileName())
                   .fileContent(manifestFile.getFileContent())
                   .applicationManifestId(manifestFile.getApplicationManifestId())
                   .build())
        .collect(Collectors.toList());
  }
}
