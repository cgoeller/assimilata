package net.goeller.assimilata;

import java.io.IOException;
import java.net.URL;

import net.goeller.assimilata.SynchSet.Option;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SynchSetReaderTest {

	@Rule
	public ExpectedException exp = ExpectedException.none();

	@Test
	public void testWrite() throws IOException {

		SynchSet set = new SynchSet("f:/Media/Photos", "m:/Photos");
		set.ignore("Thumbs.db");
		set.setOptions(Option.COPY_TO_TARGET, Option.DRY_RUN, Option.COMPARE_CONTENT);

		String json = new SynchSetReader().write(set);
		System.out.println(json);
	}

	@Test
	public void testRead() throws IOException {

		URL resource = getClass().getResource("/test.cfg");
		String json = IOUtils.toString(resource);

		SynchSet synchSet = new SynchSetReader().read(json);

		Assert.assertEquals("f:/Media/Photos", synchSet.getSourceDir());
		Assert.assertEquals("m:/Photos", synchSet.getTargetDir());

		System.out.println(synchSet);
	}

}
