package net.goeller.assimilata;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileVisitorDelegate {
  Path getSource();

  Path getTarget();

  List<String> getIgnoreList();

  void missingTargetDirEntered(Path sourceDir, Path targetDir) throws IOException;

  default void missingTargetDirLeft(Path sourceDir, Path targetDir) throws IOException {}

  void missingTargetFile(Path sourceFile, Path targetFile) throws IOException;

  default void differentTargetFile(Path sourceFile, Path targetFile, String hint)
      throws IOException {}

  default void differentDate(Path sourceFile, Path targetFile) throws IOException {}

  default void equalFileFound(Path sourceFile, Path targetFile) {}

  boolean compareContent();
}
