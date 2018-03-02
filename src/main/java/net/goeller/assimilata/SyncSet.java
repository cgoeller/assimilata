package net.goeller.assimilata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyncSet {

    private final String sourceDir;
    private final String targetDir;
    private final List<String> ignoreList = new ArrayList<>();
    private Option[] options = {Option.COPY_TO_TARGET, Option.DELETE_FROM_TARGET};
    @JsonCreator
    public SyncSet(@JsonProperty("sourceDir") final String sourceDir, @JsonProperty("targetDir") final String targetDir) {
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
    }

    public Option[] getOptions() {
        return options;
    }

    public void setOptions(Option... options) {
        this.options = options;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public List<String> getIgnoreList() {
        return ignoreList;
    }

    public void ignore(String value) {
        ignoreList.add(value);
    }

    // ---- convenience methods

    @JsonIgnore
    public Path getTargetPath() {
        return FileSystems.getDefault().getPath(targetDir);
    }

    @JsonIgnore
    public Path getSourcePath() {
        return FileSystems.getDefault().getPath(sourceDir);
    }

    public boolean hasOption(Option toTest) {
        if (options != null) {
            for (Option option : options) {
                if (option == toTest) {
                    return true;
                }
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean isDryRun() {
        return hasOption(Option.DRY_RUN);
    }

    @Override
    public String toString() {
        return "SynchSet [sourceDir=" + sourceDir + ", targetDir=" + targetDir + ", ignoreList=" + ignoreList
                + ", options=" + Arrays.toString(options) + "]";
    }

    public enum Option {
        COPY_TO_TARGET, DELETE_FROM_TARGET, DRY_RUN, COMPARE_CONTENT;
    }

}
