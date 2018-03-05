package net.goeller.assimilata.engine;

import net.goeller.assimilata.metrics.Stats;
import net.goeller.assimilata.config.SyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SourceRunDelegate implements FileVisitorDelegate {

  private final Logger log = LoggerFactory.getLogger(SourceRunDelegate.class);

  private final SyncTask syncTask;
  private final SyncJob syncJob;
  private final Stats stats;
  private final List<String> ignoreList;

  public SourceRunDelegate(
      final SyncTask syncTask,
      final SyncJob syncJob,
      final Stats stats,
      final List<String> ignoreList) {
    this.syncTask = syncTask;
    this.syncJob = syncJob;
    this.stats = stats;
    this.ignoreList = ignoreList;
  }

  @Override
  public Path getSource() {
    return syncTask.getSourcePath();
  }

  @Override
  public Path getTarget() {
    return syncTask.getTargetPath();
  }

  @Override
  public boolean compareContent() {
    return syncTask.isCompareContent();
  }

  @Override
  public List<String> getIgnoreList() {
    return ignoreList;
  }

  @Override
  public void missingTargetDirEntered(Path sourceDir, Path targetDir) throws IOException {
    log.debug("Creating directory " + targetDir);
    syncJob.mkdir(targetDir, Files.getLastModifiedTime(sourceDir));
    stats.copiedDir();
  }

  @Override
  public void missingTargetFile(Path sourceFile, Path targetFile) throws IOException {
    log.debug("Copying file to " + targetFile);
    syncJob.copy(sourceFile, targetFile);
    stats.copiedFile();
  }

  @Override
  public void differentTargetFile(final Path sourceFile, final Path targetFile, final String hint)
      throws IOException {
    log.debug("Overwriting file " + targetFile);
    if ("date".equals(hint)) {
      syncJob.updateTime(targetFile, Files.getLastModifiedTime(sourceFile));
    } else {
    }
    syncJob.copy(sourceFile, targetFile);
    stats.overwriteFile(hint);
  }

  @Override
  public void differentDate(Path sourceFile, Path targetFile) throws IOException {
    log.debug("Updating time of file " + targetFile);
    syncJob.updateTime(targetFile, Files.getLastModifiedTime(sourceFile));
    stats.updatedTime();
  }

  @Override
  public void equalFileFound(final Path sourceFile, final Path targetFile) {
    stats.equalFile();
  }
}
