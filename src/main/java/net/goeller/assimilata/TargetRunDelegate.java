package net.goeller.assimilata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetRunDelegate implements FileVisitorDelegate {

	private final Logger log = LoggerFactory.getLogger(TargetRunDelegate.class);

	private final SynchSet synchSet;
	private final Stats stats;

	public TargetRunDelegate(SynchSet synchSet, Stats stats) {
		this.synchSet = synchSet;
		this.stats = stats;
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
	public boolean compareContent() {
		return false;
	}
	
	@Override
	public List<String> getIgnoreList() {
		return Collections.emptyList();
	}

	@Override
	public void missingTargetDirEntered(Path sourceDir, Path targetDir) throws IOException {
	}

	@Override
	public void missingTargetDirLeft(Path sourceDir, Path targetDir) throws IOException {
		log.info("Deleting dir: " + sourceDir);
		if (!synchSet.isDryRun()) {
			Files.delete(sourceDir);
		}
		stats.deletedDir();
	}

	@Override
	public void missingTargetFile(Path sourceFile, Path targetFile) throws IOException {
		log.info("Deleting file: " + sourceFile);
		if (!synchSet.isDryRun()) {
			Files.delete(sourceFile);
		}
		stats.deletedFile();
	}

}
