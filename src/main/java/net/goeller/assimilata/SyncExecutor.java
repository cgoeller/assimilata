package net.goeller.assimilata;

import net.goeller.assimilata.config.SyncConfig;
import net.goeller.assimilata.engine.Syncer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Component
public class SyncExecutor {

  private final Syncer syncer = new Syncer();
  private UI ui;
  private SyncConfig config;
  private ThreadPoolTaskScheduler scheduler;

  @Autowired
  public void setConfig(SyncConfig config) {
    this.config = config;
  }

  @Autowired
  public void setUi(UI ui) {
    this.ui = ui;
  }

  @Autowired
  public void setScheduler(ThreadPoolTaskScheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Scheduled(cron = "${assimilata.schedule}")
  public void scheduledExecution() {
    execSyncInternal();
  }

  public ScheduledFuture<?> execSync() {
    return scheduler.schedule(this::execSyncInternal, Instant.now());
  }

  private void execSyncInternal() {
    try {
      if (ui != null) {
        ui.syncStarted();
      }
      syncer.sync(config);
    } finally {
      if (ui != null) {
        ui.syncFinished();
      }
    }
  }

  public void cancel() {
    syncer.cancel();
  }
}
