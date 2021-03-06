package net.goeller.assimilata.engine;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Visitor extends SimpleFileVisitor<Path> {

  private final Logger log = LoggerFactory.getLogger(Visitor.class);
  private final Syncer syncer;
  private final FileVisitorDelegate delegate;

  private Path currentTargetPath;

  public Visitor(Syncer syncer, FileVisitorDelegate delegate) {
    this.syncer = syncer;
    this.delegate = delegate;
  }

  private FileVisitResult check(final FileVisitResult result) {
    if (syncer.isCanceled()) {
      return FileVisitResult.TERMINATE;
    } else {
      return result;
    }
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    log.info("Entered source directory: " + dir);

    if (dir.equals(delegate.getSource())) {
      currentTargetPath = delegate.getTarget();
    } else {
      Path fileName = dir.getFileName();
      currentTargetPath = currentTargetPath.resolve(fileName);
    }

    if (!Files.isDirectory(currentTargetPath)) {
      delegate.missingTargetDirEntered(dir, currentTargetPath);
    } else if (!Files.getLastModifiedTime(dir)
        .equals(Files.getLastModifiedTime(currentTargetPath))) {
      delegate.differentDate(dir, currentTargetPath);
    }

    return check(FileVisitResult.CONTINUE);
  }

  @Override
  public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

    if (!Files.isDirectory(currentTargetPath)) {
      delegate.missingTargetDirLeft(dir, currentTargetPath);
    }

    log.debug("Left source directory: " + dir);
    currentTargetPath = currentTargetPath.getParent();
    return check(FileVisitResult.CONTINUE);
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    log.debug("Visited file: " + file);

    if (delegate.getIgnoreList().contains(file.getFileName().toString())) {
      return check(FileVisitResult.CONTINUE);
    }

    Path targetFile = currentTargetPath.resolve(file.getFileName());

    if (!Files.isRegularFile(targetFile)) {
      delegate.missingTargetFile(file, targetFile);
    } else if (Files.size(file) != Files.size(targetFile)) { // different size
      delegate.differentTargetFile(file, targetFile, "size");
    } else if (delegate.compareContent()
        && !FileUtils.contentEquals(
            file.toFile(), targetFile.toFile())) { // optional different content
      delegate.differentTargetFile(file, targetFile, "content");
    } else if (!Files.getLastModifiedTime(file)
        .equals(Files.getLastModifiedTime(targetFile))) { // different date
      delegate.differentDate(file, targetFile);
    } else {
      delegate.equalFileFound(file, targetFile);
    }

    return check(FileVisitResult.CONTINUE);
  }
}
