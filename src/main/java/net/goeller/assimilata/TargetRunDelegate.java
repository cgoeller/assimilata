package net.goeller.assimilata;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetRunDelegate implements FileVisitorDelegate {

	private final Logger log = LoggerFactory.getLogger(TargetRunDelegate.class);

	private final SynchSet synchSet;

	public TargetRunDelegate(SynchSet synchSet) {
		this.synchSet = synchSet;
	}

	@Override
	public Path getSource() {
		return synchSet.getTargetDir();
	}

	@Override
	public Path getTarget() {
		return synchSet.getSourceDir();
	}

	@Override
	public List<String> getIgnoreList() {
		return Collections.emptyList();
	}

	@Override
	public void missingTargetDir(Path sourceDir, Path targetDir) {
		// delete source Dir
		log.info("Deleting dir: " + sourceDir);
	}

	@Override
	public void missingTargetFile(Path sourceFile, Path targetFile) {
		// delete source file
		log.info("Deleting file: " + sourceFile);
	}

}
