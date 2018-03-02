package net.goeller.assimilata.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.FileSystems;
import java.nio.file.Path;

@AllArgsConstructor
@Data
public final class SyncTask {

  private final String name;
  private final String sourceDir;
  private final String targetDir;
  private final boolean copyToTarget;
  private final boolean deleteOrphans;
  private final boolean compareContent;

  // ---- convenience methods

  @JsonIgnore
  public Path getTargetPath() {
    return FileSystems.getDefault().getPath(targetDir);
  }

  @JsonIgnore
  public Path getSourcePath() {
    return FileSystems.getDefault().getPath(sourceDir);
  }
}
