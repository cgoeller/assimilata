package net.goeller.assimilata;

import java.io.IOException;
import java.nio.file.Files;

import net.goeller.assimilata.SynchSet.Option;

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
		if (synchSet.isDryRun()) {
			log.info("Dry run is active");
		}

		Stats stats = new Stats();
		Visitor visitor = new Visitor(new SourceRunDelegate(synchSet, stats));
		Visitor visitor2 = new Visitor(new TargetRunDelegate(synchSet, stats));

		if (synchSet.hasOption(Option.COPY_TO_TARGET)) {
			try {
				log.info("Scanning source tree for files to copy");
				Files.walkFileTree(synchSet.getSourceDir(), visitor);
				log.info("Finished scanning source tree");
			} catch (IOException e) {
				log.error("Error while walking source tree", e);
			}
		}

		if (synchSet.hasOption(Option.DELETE_FROM_TARGET)) {
			try {
				log.info("Scanning target tree for files to delete");
				Files.walkFileTree(synchSet.getTargetDir(), visitor2);
				log.info("Finished scanning target tree");
			} catch (IOException e) {
				log.error("Error while walking target tree", e);
			}
		}

		log.info("Finished synchronization");
		stats.report();
	}

}
