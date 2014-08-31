package net.goeller.assimilata;

import net.goeller.assimilata.SynchSet.Option;

public class Assimilata {

	public void start() {

		SynchSet set = new SynchSet("f:/Media/Photos", "m:/Photos");
		set.ignore("Thumbs.db");
		set.setOptions(Option.COPY_TO_TARGET, Option.DRY_RUN, Option.COMPARE_CONTENT);

		// do some action
		new Syncher().synch(set);
	}

	public static void main(String[] args) {
		new Assimilata().start();
	}
}
