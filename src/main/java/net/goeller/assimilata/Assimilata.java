package net.goeller.assimilata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class Assimilata {
    private final Logger log = LoggerFactory.getLogger(Assimilata.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java " + Assimilata.class.getName() + " <config file>...");
            System.exit(1);
        }

        new Assimilata().start(args);
    }

    public void start(String[] args) {

        for (String configName : args) {
            SyncSet syncSet;
            try {
                syncSet = new SyncSetReader().read(Paths.get(configName));
            } catch (IOException e) {
                log.error("Could not load sync set from " + configName);
                return;
            }
            new Syncer().sync(syncSet);
        }
    }
}
