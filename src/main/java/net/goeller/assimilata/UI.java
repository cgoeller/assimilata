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
  private JMenuItem cancelMenuItem;

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
    cancelMenuItem.setEnabled(true);
  }

  public void syncFinished() {
    syncMenuItem.setEnabled(true);
    cancelMenuItem.setEnabled(false);
    notifyUser("Finished synchronisation");
  }

  private ImageIcon loadIcon(String name) {
    return new ImageIcon(getClass().getResource(name));
  }

  private void cancelSync() {
    LOG.info("Cancelling synchronisation");
    syncExecutor.cancel();
  }

  @PostConstruct
  public void installTrayIcon() {

    if (SystemTray.isSupported()) {
      LOG.info("Installing system tray icon");
      try {
        syncMenuItem = new JMenuItem("Run synchronisation", loadIcon("/sync.png"));
        syncMenuItem.setToolTipText("Starts the synchronisation");
        syncMenuItem.addActionListener(e -> syncExecutor.execSync());

        cancelMenuItem = new JMenuItem("Cancel synchronisation", loadIcon("/forbidden.png"));
        cancelMenuItem.setToolTipText("Cancels the synchronisation");
        cancelMenuItem.addActionListener(e -> cancelSync());
        cancelMenuItem.setEnabled(false);

        final JMenuItem exitItem = new JMenuItem("Exit Assimilata", loadIcon("/logout.png"));
        exitItem.setToolTipText("Exits the application");
        exitItem.addActionListener(e -> shutdownService.initiateShutdown());

        final JPopupMenu popup = new JPopupMenu();
        popup.add(syncMenuItem);
        popup.add(cancelMenuItem);
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
