package net.goeller.assimilata;

import net.goeller.assimilata.config.SyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

public class SyncJob {
  private final Logger log = LoggerFactory.getLogger(SyncJob.class);

  private final List<Entry> syncEntries = new ArrayList<>();
  private final SyncTask syncTask;

  public SyncJob(final SyncTask syncTask) {
    this.syncTask = syncTask;
  }

  public SyncTask getSyncTask() {
    return syncTask;
  }

  public void copy(Path from, Path to) {
    syncEntries.add(new Entry(from, to, FileOption.copy));
  }

  public void updateTime(Path fileToUpdate, FileTime fileTime) {
    syncEntries.add(new Entry(fileToUpdate, fileTime, FileOption.updateTime));
  }

  public void delete(Path fileToDelete) {
    syncEntries.add(new Entry(fileToDelete, FileOption.delete));
  }

  public void mkdir(Path dir, FileTime fileTime) {
    syncEntries.add(new Entry(dir, fileTime, FileOption.mkdir));
  }

  public List<Entry> getSyncEntries() {
    return syncEntries;
  }

  enum FileOption {
    copy,
    delete,
    mkdir,
    updateTime
  }

  class Entry {
    private Path path1;
    private Path path2;
    private FileTime fileTime;
    private FileOption fileOption;

    public Entry(Path path1, Path path2, FileOption fileOption) {
      this.path1 = path1;
      this.path2 = path2;
      this.fileOption = fileOption;
    }

    public Entry(Path path1, FileTime fileTime, FileOption fileOption) {
      this.path1 = path1;
      this.fileTime = fileTime;
      this.fileOption = fileOption;
    }

    public Entry(Path path1, FileOption fileOption) {
      this.path1 = path1;
      this.fileOption = fileOption;
    }

    public void log() {
      switch (fileOption) {
        case copy:
          log.info("Copying file from {} to {}", path1, path2);
          break;
        case delete:
          log.info("Deleting path {}", path1);
          break;
        case mkdir:
          log.info("Creating directory {}", path1);
          break;
        case updateTime:
          log.info("Updating last-modified time of {}", path1);
          break;
      }
    }

    public void execute() throws IOException {
      log();

      switch (fileOption) {
        case copy:
          Files.copy(
              path1,
              path2,
              StandardCopyOption.REPLACE_EXISTING,
              StandardCopyOption.COPY_ATTRIBUTES);
          Files.setLastModifiedTime(path2, Files.getLastModifiedTime(path1));
          break;
        case delete:
          Files.delete(path1);
          break;
        case mkdir:
          Files.createDirectory(path1);
          if (fileTime != null) {
            Files.setLastModifiedTime(path1, fileTime);
          }
          break;
        case updateTime:
          Files.setLastModifiedTime(path1, fileTime);
          break;
        default:
          throw new IllegalStateException("Invalid file option: " + fileOption);
      }
    }
  }
}
