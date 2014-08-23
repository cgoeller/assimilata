package net.goeller.assimilata;

import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceRunDelegate implements FileVisitorDelegate {

	private final Logger log = LoggerFactory.getLogger(SourceRunDelegate.class);

	private final SynchSet synchSet;

	public SourceRunDelegate(SynchSet synchSet) {
		this.synchSet = synchSet;
	}

	@Override
	public Path getSource() {
		return synchSet.getSourceDir();
	}

	@Override
	public Path getTarget() {
		return synchSet.getTargetDir();
	}

	@Override
	public List<String> getIgnoreList() {
		return synchSet.getIgnoreList();
	}

	@Override
	public void missingTargetDir(Path sourceDir, Path targetDir) {
		log.info("Creating directory " + targetDir);

	}

	@Override
	public void missingTargetFile(Path sourceFile, Path targetFile) {
		log.info("Copying file to " + targetFile);

	}

}
