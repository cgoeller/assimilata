package net.goeller.assimilata;

import java.io.IOException;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Syncher {

	private final Logger log = LoggerFactory.getLogger(Syncher.class);

	public void synch(final SynchSet synchSet) {

		log.info("Beginning synchronization of " + synchSet.getSourceDir() + " to " + synchSet.getTargetDir());

		Visitor visitor = new Visitor(synchSet);

		try {
			Files.walkFileTree(synchSet.getSourceDir(), visitor);
		} catch (IOException e) {
			log.error("Error", e);
		}
	}

}
