package net.goeller.assimilata;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SynchSet {

	public enum Option {
		COPY_TO_TARGET, DELETE_FROM_TARGET;
	}

	private final Path sourceDir;
	private final Path targetDir;
	private boolean dryRun;
	private Option[] options = Option.values();

	private List<String> ignoreList = new ArrayList<>();

	public SynchSet(Path sourceDir, Path targetDir) {
		this.sourceDir = sourceDir;
		this.targetDir = targetDir;
	}

	public void setOptions(Option... options) {
		this.options = options;
	}

	public boolean hasOption(Option toTest) {
		if (options != null) {
			for (Option option : options) {
				if (option == toTest) {
					return true;
				}
			}
		}
		return false;
	}

	public Path getSourceDir() {
		return sourceDir;
	}

	public Path getTargetDir() {
		return targetDir;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	public boolean isDryRun() {
		return dryRun;
	}

	public void ignore(String value) {
		ignoreList.add(value);
	}

	public List<String> getIgnoreList() {
		return ignoreList;
	}

}
