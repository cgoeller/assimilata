package net.goeller.assimilata.config;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;

public class SyncConfigReaderTest {

  @Rule public ExpectedException exp = ExpectedException.none();

  @Test
  public void readFile() throws Exception {
    URL resource = getClass().getResource("/config.yml");
    SyncConfig config = new SyncConfigReader().readFile(Paths.get(resource.toURI()));
    check(config);
  }

  @Test
  public void readString() throws IOException {
    URL resource = getClass().getResource("/config.yml");
    String content = IOUtils.toString(resource, StandardCharsets.UTF_8);

    SyncConfig config = new SyncConfigReader().readString(content);
    check(config);
  }

  private void check(final SyncConfig config) {
    SyncTask syncTask = config.getTasks().get(0);

    Assert.assertEquals("f:/Media/Photos", syncTask.getSourceDir());
    Assert.assertEquals("m:/Photos", syncTask.getTargetDir());
    Assert.assertTrue(config.isDryRun());
    Assert.assertEquals("Thumbs.db", config.getIgnoreList().get(0));

    System.out.println(config);
  }

  @Test
  public void write() throws IOException {

    SyncTask job = new SyncTask("Photo sync","/from", "/to", true, false, true);

    SyncConfig cfg =
        new SyncConfig(
            false, Collections.singletonList("ignore.txt"), Collections.singletonList(job));
    String out = new SyncConfigReader().write(cfg);

    System.out.println(out);
  }
}
