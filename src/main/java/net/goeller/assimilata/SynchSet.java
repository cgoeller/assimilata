package net.goeller.assimilata;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SynchSet {
	private final Path sourceDir;
	private final Path targetDir;

	private List<String> ignoreList = new ArrayList<>();
	
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
	
	public void ignore(String value) {
		ignoreList.add(value);
	}
	
	public List<String> getIgnoreList() {
		return ignoreList;
	}

}
