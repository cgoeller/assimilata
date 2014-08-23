package net.goeller.assimilata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import net.goeller.assimilata.SynchSet.Option;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceRunDelegate implements FileVisitorDelegate {

	private final Logger log = LoggerFactory.getLogger(SourceRunDelegate.class);

	private final SynchSet synchSet;
	private final Stats stats;

	public SourceRunDelegate(SynchSet synchSet, Stats stats) {
		this.synchSet = synchSet;
		this.stats = stats;
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
	public boolean compareContent() {
		return synchSet.hasOption(Option.COMPARE_CONTENT);
	}
	
	@Override
	public List<String> getIgnoreList() {
		return synchSet.getIgnoreList();
	}

	@Override
	public void missingTargetDirEntered(Path sourceDir, Path targetDir) throws IOException {
		log.info("Creating directory " + targetDir);
		if (!synchSet.isDryRun()) {
			Files.createDirectory(targetDir);
		}
		stats.copiedDir();
	}

	@Override
	public void missingTargetFile(Path sourceFile, Path targetFile) throws IOException {
		log.info("Copying file to " + targetFile);
		if (!synchSet.isDryRun()) {
			Files.copy(sourceFile, targetFile, StandardCopyOption.COPY_ATTRIBUTES);
		}
		stats.copiedFile();
	}

	@Override
	public void differentTargetFile(Path sourceFile, Path targetFile) throws IOException {
		log.info("Overwriting file " + targetFile);
		if (!synchSet.isDryRun()) {
			Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
		}
		stats.overwriteFile();
	}

}
