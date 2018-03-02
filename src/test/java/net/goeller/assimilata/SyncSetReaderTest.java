package net.goeller.assimilata;

import java.io.IOException;
import java.net.URL;

import net.goeller.assimilata.SyncSet.Option;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SyncSetReaderTest {

    @Rule
    public ExpectedException exp = ExpectedException.none();

    @Test
    public void testWrite() throws IOException {

        SyncSet set = new SyncSet("f:/Media/Photos", "m:/Photos");
        set.ignore("Thumbs.db");
        set.setOptions(Option.COPY_TO_TARGET, Option.DRY_RUN, Option.COMPARE_CONTENT);

        String json = new SyncSetReader().writeJson(set);
        System.out.println(json);
    }

    @Test
    public void testReadJson() throws IOException {
        URL resource = getClass().getResource("/test.json");
        String content = IOUtils.toString(resource);
        SyncSet syncSet = new SyncSetReader().readJson(content);
        check(syncSet);
    }

    @Test
    public void testReadYaml() throws IOException {
        URL resource = getClass().getResource("/test.yml");
        String content = IOUtils.toString(resource);
        SyncSet syncSet = new SyncSetReader().readYaml(content);
        check(syncSet);
    }

    private void check(final SyncSet syncSet) {

        Assert.assertEquals("f:/Media/Photos", syncSet.getSourceDir());
        Assert.assertEquals("m:/Photos", syncSet.getTargetDir());

        System.out.println(syncSet);
    }

}
