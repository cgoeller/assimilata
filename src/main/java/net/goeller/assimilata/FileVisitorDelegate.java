package net.goeller.assimilata;

import java.nio.file.Path;
import java.util.List;

public interface FileVisitorDelegate {
	Path getSource();

	Path getTarget();

	List<String> getIgnoreList();

	void missingTargetDir(Path sourceDir, Path targetDir);

	void missingTargetFile(Path sourceFile, Path targetFile);

}
