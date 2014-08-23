package net.goeller.assimilata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;

public class Stats {
	private final Logger log = LoggerFactory.getLogger(Stats.class);

	private final MetricRegistry metrics = new MetricRegistry();

	private final Counter copiedFiles = metrics.counter(MetricRegistry.name(Stats.class, "copiedFiles"));
	private final Counter copiedDirs = metrics.counter(MetricRegistry.name(Stats.class, "copiedDirs"));
	private final Counter deletedFiles = metrics.counter(MetricRegistry.name(Stats.class, "deletedFiles"));
	private final Counter deletedDirs = metrics.counter(MetricRegistry.name(Stats.class, "deletedDirs"));

	public void copiedFile() {
		copiedFiles.inc();
	}

	public void copiedDir() {
		copiedDirs.inc();
	}

	public void deletedFile() {
		deletedFiles.inc();
	}

	public void deletedDir() {
		deletedDirs.inc();
	}

	public void report() {
		Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics).outputTo(log).build();
		reporter.report();
	}
}
