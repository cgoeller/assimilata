package net.goeller.assimilata;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SynchSet {

	public enum Option {
		COPY_TO_TARGET, DELETE_FROM_TARGET, DRY_RUN, COMPARE_CONTENT;
	}

	private final Path sourceDir;
	private final Path targetDir;
	private Option[] options = { Option.COPY_TO_TARGET, Option.DELETE_FROM_TARGET };

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

	public boolean isDryRun() {
		return hasOption(Option.DRY_RUN);
	}

	public void ignore(String value) {
		ignoreList.add(value);
	}

	public List<String> getIgnoreList() {
		return ignoreList;
	}

}
