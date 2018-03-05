package net.goeller.assimilata.engine;

import net.goeller.assimilata.metrics.Stats;
import net.goeller.assimilata.config.SyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class TargetRunDelegate implements FileVisitorDelegate {

  private final Logger log = LoggerFactory.getLogger(TargetRunDelegate.class);

  private final SyncTask syncTask;
  private final SyncJob syncJob;
  private final Stats stats;

  public TargetRunDelegate(final SyncTask syncTask, final SyncJob syncJob, final Stats stats) {
    this.syncTask = syncTask;
    this.syncJob = syncJob;
    this.stats = stats;
  }

  @Override
  public Path getSource() {
    return syncTask.getTargetPath();
  }

  @Override
  public Path getTarget() {
    return syncTask.getSourcePath();
  }

  @Override
  public boolean compareContent() {
    return false;
  }

  @Override
  public List<String> getIgnoreList() {
    return Collections.emptyList();
  }

  @Override
  public void missingTargetDirEntered(Path sourceDir, Path targetDir) throws IOException {}

  @Override
  public void missingTargetDirLeft(Path sourceDir, Path targetDir) throws IOException {
    log.debug("Deleting dir: " + sourceDir);
    syncJob.delete(sourceDir);
    stats.deletedDir();
  }

  @Override
  public void missingTargetFile(Path sourceFile, Path targetFile) throws IOException {
    log.debug("Deleting file: " + sourceFile);
    syncJob.delete(sourceFile);
    stats.deletedFile();
  }
}
