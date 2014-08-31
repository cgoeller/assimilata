package net.goeller.assimilata;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import net.goeller.assimilata.SyncSet.Option;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceRunDelegate implements FileVisitorDelegate {

	private final Logger log = LoggerFactory.getLogger(SourceRunDelegate.class);

	private final SyncSet syncSet;
	private final SyncJob syncJob;
	private final Stats stats;

	public SourceRunDelegate(final SyncSet syncSet, final SyncJob syncJob, final Stats stats) {
		this.syncSet = syncSet;
		this.syncJob = syncJob;
		this.stats = stats;
	}

	@Override
	public Path getSource() {
		return syncSet.getSourcePath();
	}

	@Override
	public Path getTarget() {
		return syncSet.getTargetPath();
	}

	@Override
	public boolean compareContent() {
		return syncSet.hasOption(Option.COMPARE_CONTENT);
	}

	@Override
	public List<String> getIgnoreList() {
		return syncSet.getIgnoreList();
	}

	@Override
	public void missingTargetDirEntered(Path sourceDir, Path targetDir) throws IOException {
		log.info("Creating directory " + targetDir);
		syncJob.mkdir(targetDir);
		stats.copiedDir();
	}

	@Override
	public void missingTargetFile(Path sourceFile, Path targetFile) throws IOException {
		log.info("Copying file to " + targetFile);
		syncJob.copy(sourceFile, targetFile);
		stats.copiedFile();
	}

	@Override
	public void differentTargetFile(Path sourceFile, Path targetFile) throws IOException {
		log.info("Overwriting file " + targetFile);
		syncJob.copy(sourceFile, targetFile);
		stats.overwriteFile();
	}

	@Override
	public void equalFileFound(Path sourceFile, Path targetFile) {
		stats.equalFile();
	}
	
}
