package net.goeller.assimilata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class TargetRunDelegate implements FileVisitorDelegate {

    private final Logger log = LoggerFactory.getLogger(TargetRunDelegate.class);

    private final SyncSet syncSet;
    private final SyncJob syncJob;
    private final Stats stats;

    public TargetRunDelegate(final SyncSet syncSet, final SyncJob syncJob, final Stats stats) {
        this.syncSet = syncSet;
        this.syncJob = syncJob;
        this.stats = stats;
    }

    @Override
    public Path getSource() {
        return syncSet.getTargetPath();
    }

    @Override
    public Path getTarget() {
        return syncSet.getSourcePath();
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
        syncJob.delete(sourceDir);
        stats.deletedDir();
    }

    @Override
    public void missingTargetFile(Path sourceFile, Path targetFile) throws IOException {
        log.info("Deleting file: " + sourceFile);
        syncJob.delete(sourceFile);
        stats.deletedFile();
    }

}
