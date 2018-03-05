package net.goeller.assimilata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Component
public final class UI {
  private static final Logger LOG = LoggerFactory.getLogger(UI.class);

  private SyncExecutor syncExecutor;
  private ShutdownService shutdownService;

  private TrayIcon trayIcon;
  private JMenuItem syncMenuItem;

  public UI() {
    try {
      LOG.info("Setting Look&Feel");
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      LOG.error("Could not set L&F", e);
    }
  }

  @Autowired
  public void setSyncExecutor(SyncExecutor syncExecutor) {
    this.syncExecutor = syncExecutor;
  }

  @Autowired
  public void setShutdownService(ShutdownService shutdownService) {
    this.shutdownService = shutdownService;
  }

  public void syncStarted() {
    notifyUser("Starting synchronisation");
    syncMenuItem.setEnabled(false);
  }

  public void syncFinished() {
    syncMenuItem.setEnabled(true);
    notifyUser("Finished synchronisation");
  }

  @PostConstruct
  public void installTrayIcon() {

    if (SystemTray.isSupported()) {
      LOG.info("Installing system tray icon");
      try {
        ImageIcon icon = new ImageIcon(getClass().getResource("/sync.png"));

        syncMenuItem = new JMenuItem("Start synchronisation", icon);
        syncMenuItem.setToolTipText("Starts the synchronisation");
        syncMenuItem.addActionListener(e -> syncExecutor.execSync());

        icon = new ImageIcon(getClass().getResource("/logout.png"));
        final JMenuItem exitItem = new JMenuItem("Exit Assimilata", icon);
        exitItem.setToolTipText("Exits the application");
        exitItem.addActionListener(e -> shutdownService.initiateShutdown());

        final JPopupMenu popup = new JPopupMenu();
        popup.add(syncMenuItem);
        popup.addSeparator();
        popup.add(exitItem);

        Image image = ImageIO.read(getClass().getResource("/cyborg_16.png"));
        trayIcon = new TrayIcon(image, "Assimilata");
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(
            new MouseAdapter() {

              @Override
              public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
              }

              @Override
              public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
              }

              private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {

                  JDialog popupDummy = new JDialog();
                  popupDummy.setSize(0, 0);
                  popupDummy.setUndecorated(true);
                  popupDummy.setVisible(true);
                  popup.show(popupDummy, e.getX(), e.getY());
                }
              }
            });
        SystemTray.getSystemTray().add(trayIcon);
      } catch (Exception e) {
        LOG.error("Could not setup tray icon", e);
        System.exit(1);
      }
    } else {
      LOG.warn("System tray not supported");
    }
  }

  @PreDestroy
  public void removeTrayIcon() {
    if (SystemTray.isSupported()) {
      LOG.info("Removing system tray icon");
      SystemTray.getSystemTray().remove(trayIcon);
    }
  }

  public void notifyUser(String message) {
    if (trayIcon != null) {
      trayIcon.displayMessage("Assimilata", message, TrayIcon.MessageType.INFO);
    }
  }
}
