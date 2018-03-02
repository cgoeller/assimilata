package net.goeller.assimilata;

import net.goeller.assimilata.config.SyncConfig;
import net.goeller.assimilata.config.SyncConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;

public class Assimilata {
  private static final Logger log = LoggerFactory.getLogger(Assimilata.class);
  private SyncConfig config;
  private TrayIcon trayIcon;
  private ThreadPoolTaskScheduler scheduler;

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("Usage: java " + Assimilata.class.getName() + " <config file> [service]");
      System.exit(1);
    }

    String configName = args[0];

    boolean service = false;
    if (args.length == 2) {
      if ("service".equals(args[1])) {
        service = true;
      }
    }

    new Assimilata().start(configName, service);
  }

  private void start(String configName, boolean service) {
    try {
      config = new SyncConfigReader().readFile(Paths.get(configName));
    } catch (IOException e) {
      log.error("Could not load config from " + configName);
      log.error(e.getMessage());
      System.exit(1);
    }

    if (service) {
      startService();
    } else {
      execSync();
    }
  }

  private void execSync() {
    new Syncer().sync(config);
  }

  private void startService() {

    log.info("Starting Assimilata as service");

    scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(1);
    scheduler.setErrorHandler(throwable -> handleError(throwable));
    scheduler.setDaemon(true);
    scheduler.setThreadNamePrefix("Scheduler");
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    scheduler.afterPropertiesSet();

    log.info("Scheduling service: {}", config.getSchedule());
    scheduler.schedule(this::execSync, new CronTrigger(config.getSchedule()));

    if (SystemTray.isSupported()) {
      try {
        final MenuItem syncItem = new MenuItem("Sync");
        syncItem.addActionListener(e -> scheduler.execute(this::execSync));

        final MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> stopService());

        final PopupMenu popup = new PopupMenu();
        popup.add(syncItem);
        popup.addSeparator();
        popup.add(exitItem);

        Image image = ImageIO.read(getClass().getResource("/cyborg_16.png"));
        trayIcon = new TrayIcon(image, "Assimilata", popup);
        trayIcon.setImageAutoSize(true);
        SystemTray.getSystemTray().add(trayIcon);
      } catch (Exception e) {
        log.error("Could not setup tray icon", e);
        System.exit(1);
      }
    }
  }

  private void handleError(Throwable e) {
    log.error("Error during execution", e);
  }

  private void stopService() {
    log.info("Stopping Assimilata service");
    scheduler.shutdown();
    if (SystemTray.isSupported()) {
      SystemTray.getSystemTray().remove(trayIcon);
    }
    System.exit(0);
  }
}
