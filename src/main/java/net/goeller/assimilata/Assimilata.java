package net.goeller.assimilata;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Assimilata {

	public void start() {
		// load config from somewhere
		Path sourceDir = FileSystems.getDefault().getPath("f:/Media/Photos");
		Path targetDir = FileSystems.getDefault().getPath("m:/Photos");
		
		SynchSet set = new SynchSet(sourceDir, targetDir);
		set.ignore("Thumbs.db");

		// do some action
		new Syncher().synch(set);
	}

	public static void main(String[] args) {
		new Assimilata().start();
	}
}
