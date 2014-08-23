package net.goeller.assimilata;

import java.io.IOException;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Syncher {

	private final Logger log = LoggerFactory.getLogger(Syncher.class);

	public void synch(final SynchSet synchSet) {

		log.info("Checking directories before synchronization");
		if (!Files.isDirectory(synchSet.getSourceDir())) {
			log.error("Source directory does not exist: " + synchSet.getSourceDir());
			return;
		}
		if (!Files.isDirectory(synchSet.getTargetDir())) {
			log.error("Target directory does not exist: " + synchSet.getTargetDir());
			return;
		}

		log.info("Beginning synchronization of " + synchSet.getSourceDir() + " to " + synchSet.getTargetDir());

		Visitor visitor = new Visitor(new SourceRunDelegate(synchSet));
		Visitor visitor2 = new Visitor(new TargetRunDelegate(synchSet));

		try {
			Files.walkFileTree(synchSet.getSourceDir(), visitor);
		} catch (IOException e) {
			log.error("Error while walking source tree", e);
		}

		try {
			Files.walkFileTree(synchSet.getSourceDir(), visitor2);
		} catch (IOException e) {
			log.error("Error while walking target tree", e);
		}
	}

}
