package net.goeller.assimilata.engine;

import net.goeller.assimilata.config.SyncConfig;
import net.goeller.assimilata.config.SyncTask;
import net.goeller.assimilata.metrics.Stats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;

public class Syncer {

  private final Logger log = LoggerFactory.getLogger(Syncer.class);
  private SyncConfig config;

  private SyncJob prepare(final SyncTask syncTask) {

    log.info("Starting prepare phase for task: {}", syncTask.getName());
    log.info("Checking directories");
    if (!Files.isDirectory(syncTask.getSourcePath())) {
      throw new IllegalStateException(
          "Source directory does not exist: " + syncTask.getSourceDir());
    }
    if (!Files.isDirectory(syncTask.getTargetPath())) {
      throw new IllegalStateException(
          "Target directory does not exist: " + syncTask.getTargetDir());
    }

    log.info(
        "Preparing synchronization of "
            + syncTask.getSourceDir()
            + " to "
            + syncTask.getTargetDir());
    if (config.isDryRun()) {
      log.info("Dry run is active");
    }

    Stats stats = new Stats();
    SyncJob syncJob = new SyncJob(syncTask);

    if (syncTask.isCopyToTarget()) {
      Visitor visitor =
          new Visitor(new SourceRunDelegate(syncTask, syncJob, stats, config.getIgnoreList()));
      try {
        log.info("Scanning source tree for files to copy");
        Files.walkFileTree(syncTask.getSourcePath(), visitor);
        log.info("Finished scanning source tree");
      } catch (IOException e) {
        log.error("Error while walking source tree", e);
      }
    }

    if (syncTask.isDeleteOrphans()) {
      Visitor visitor = new Visitor(new TargetRunDelegate(syncTask, syncJob, stats));
      try {
        log.info("Scanning target tree for files to delete");
        Files.walkFileTree(syncTask.getTargetPath(), visitor);
        log.info("Finished scanning target tree");
      } catch (IOException e) {
        log.error("Error while walking target tree", e);
      }
    }

    log.info("Finished prepare phase for task: {}", syncTask.getName());
    stats.report();

    return syncJob;
  }

  private void sync(final SyncJob syncJob) {
    log.info("Starting sync phase for task: {}", syncJob.getSyncTask().getName());
    if (config.isDryRun()) {
      log.info("Dry run is active");
    }
    log.info(
        "Beginning synchronization of "
            + syncJob.getSyncTask().getSourceDir()
            + " to "
            + syncJob.getSyncTask().getTargetDir());

    try {
      syncJob.doExecute(config.isDryRun());
    } catch (IOException e) {
      log.error("Error while synchronizing directories", e);
    }

    log.info("Finished sync phase for task: {}", syncJob.getSyncTask().getName());
  }

  public void sync(final SyncConfig config) {
    this.config = config;
    config.getTasks().forEach(syncTask -> sync(prepare(syncTask)));
  }
}
