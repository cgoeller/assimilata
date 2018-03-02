package net.goeller.assimilata;

import net.goeller.assimilata.SyncJob.Entry;
import net.goeller.assimilata.SyncSet.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;

public class Syncer {

    private final Logger log = LoggerFactory.getLogger(Syncer.class);

    public SyncJob prepare(final SyncSet syncSet) {

        log.info("Checking directories");
        if (!Files.isDirectory(syncSet.getSourcePath())) {
            throw new IllegalStateException("Source directory does not exist: " + syncSet.getSourceDir());
        }
        if (!Files.isDirectory(syncSet.getTargetPath())) {
            throw new IllegalStateException("Target directory does not exist: " + syncSet.getTargetDir());
        }

        log.info("Preparing synchronization of " + syncSet.getSourceDir() + " to " + syncSet.getTargetDir());
        if (syncSet.isDryRun()) {
            log.info("Dry run is active");
        }

        Stats stats = new Stats();
        SyncJob syncJob = new SyncJob(syncSet);

        if (syncSet.hasOption(Option.COPY_TO_TARGET)) {
            Visitor visitor = new Visitor(new SourceRunDelegate(syncSet, syncJob, stats));
            try {
                log.info("Scanning source tree for files to copy");
                Files.walkFileTree(syncSet.getSourcePath(), visitor);
                log.info("Finished scanning source tree");
            } catch (IOException e) {
                log.error("Error while walking source tree", e);
            }
        }

        if (syncSet.hasOption(Option.DELETE_FROM_TARGET)) {
            Visitor visitor = new Visitor(new TargetRunDelegate(syncSet, syncJob, stats));
            try {
                log.info("Scanning target tree for files to delete");
                Files.walkFileTree(syncSet.getTargetPath(), visitor);
                log.info("Finished scanning target tree");
            } catch (IOException e) {
                log.error("Error while walking target tree", e);
            }
        }

        log.info("Finished preparation");
        stats.report();

        return syncJob;
    }

    public void sync(final SyncJob syncJob) {
        if (syncJob.getSyncSet().isDryRun()) {
            log.info("Dry run: Skipping sync phase");
            return;
        }

        log.info("Beginning synchronization of " + syncJob.getSyncSet().getSourceDir() + " to "
                + syncJob.getSyncSet().getTargetDir());

        try {
            for (Entry entry : syncJob.getSyncEntries()) {
                entry.execute();
            }
        } catch (IOException e) {
            log.error("Error while synchronizing directories", e);
        }

        log.info("Finished synchronization");
    }

    public void sync(final SyncSet syncSet) {
        sync(prepare(syncSet));
    }

}
