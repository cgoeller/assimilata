package net.goeller.assimilata;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Visitor extends SimpleFileVisitor<Path> {

	private final Logger log = LoggerFactory.getLogger(Visitor.class);
	private final SynchSet synchSet;

	private Path currentTargetPath;
	
	public Visitor(SynchSet synchSet) {
		this.synchSet = synchSet;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

		if (dir.equals(synchSet.getSourceDir())) {
			currentTargetPath = synchSet.getTargetDir();
		}
		else {
			currentTargetPath = currentTargetPath.resolve(dir.getFileName());
		}
		
		//log.info("Entered directory: " + dir + "  target is : " + currentTargetPath);
		
		if (!Files.isDirectory(currentTargetPath)) {
			System.out.println("target dir not found: " + currentTargetPath);
		}
		
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		
		currentTargetPath = currentTargetPath.getParent();
		
		//log.info("Left directory: " + dir);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

		if (synchSet.getIgnoreList().contains(file.getFileName().toString())) {
			return FileVisitResult.CONTINUE;
		}
		
		Path targetFile = currentTargetPath.resolve(file.getFileName());
		
		
		if (!Files.isRegularFile(targetFile)) {
			System.out.println("target file not found: " + targetFile);
		}
		
		//log.info("Found file: " + file);
		return FileVisitResult.CONTINUE;
	}

}
