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
	private final FileVisitorDelegate delegate;

	private Path currentTargetPath;

	public Visitor(FileVisitorDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		log.debug("Entered source directory: " + dir);

		if (dir.equals(delegate.getSource())) {
			currentTargetPath = delegate.getTarget();
		} else {
			Path fileName = dir.getFileName();
			currentTargetPath = currentTargetPath.resolve(fileName);
		}

		if (!Files.isDirectory(currentTargetPath)) {
			delegate.missingTargetDirEntered(dir, currentTargetPath);
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		
		if (!Files.isDirectory(currentTargetPath)) {
			delegate.missingTargetDirLeft(dir, currentTargetPath);
		}
		
		log.debug("Left source directory: " + dir);
		currentTargetPath = currentTargetPath.getParent();
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		log.debug("Visited file: " + file);

		if (delegate.getIgnoreList().contains(file.getFileName().toString())) {
			return FileVisitResult.CONTINUE;
		}

		Path targetFile = currentTargetPath.resolve(file.getFileName());

		if (!Files.isRegularFile(targetFile)) {
			delegate.missingTargetFile(file, targetFile);
		}

		return FileVisitResult.CONTINUE;
	}

}
