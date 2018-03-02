package net.goeller.assimilata;

import net.goeller.assimilata.config.SyncConfig;
import net.goeller.assimilata.config.SyncConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;

public class Assimilata {
  private static final Logger log = LoggerFactory.getLogger(Assimilata.class);
  private TrayIcon trayIcon;

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

    SyncConfig config = null;
    try {
      config = new SyncConfigReader().readFile(Paths.get(configName));
    } catch (IOException e) {
      log.error("Could not load config from " + configName);
      log.error(e.getMessage());
      System.exit(1);
    }

    if (service) {
      startService(config);
    } else {
      new Syncer().sync(config);
    }
  }

  private void startService(SyncConfig config) {

    log.info("Starting Assimilata as service");

    if (SystemTray.isSupported()) {
      try {
        Image image = ImageIO.read(getClass().getResource("/cyborg_16.png"));

        final PopupMenu popup = new PopupMenu();
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> stopService());
        popup.add(exitItem);

        trayIcon = new TrayIcon(image, "Assimilata", popup);
        trayIcon.setImageAutoSize(true);
        SystemTray.getSystemTray().add(trayIcon);

        // trayIcon.displayMessage("Assimilata", "Started", TrayIcon.MessageType.INFO);

      } catch (Exception e) {
        log.error("Could not setup tray icon", e);
        System.exit(1);
      }
    }
  }

  private void stopService() {
    log.info("Stopping Assimilata service");
    if (SystemTray.isSupported()) {
      SystemTray.getSystemTray().remove(trayIcon);
    }
    System.exit(0);
  }
}
