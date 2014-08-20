package net.goeller.assimilata;

import java.nio.file.Path;

public class SynchSet {
	private final Path sourceDir;
	private final Path targetDir;

	public SynchSet(Path sourceDir, Path targetDir) {
		this.sourceDir = sourceDir;
		this.targetDir = targetDir;
	}

	public Path getSourceDir() {
		return sourceDir;
	}

	public Path getTargetDir() {
		return targetDir;
	}

}
