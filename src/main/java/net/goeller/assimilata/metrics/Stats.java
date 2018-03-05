package net.goeller.assimilata.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stats {
  private final Logger log = LoggerFactory.getLogger(Stats.class);

  private final MetricRegistry metrics = new MetricRegistry();

  private final Counter equalFiles =
      metrics.counter(MetricRegistry.name(Stats.class, "equalFiles"));

  private final Counter copiedFiles =
      metrics.counter(MetricRegistry.name(Stats.class, "copiedFiles"));
  private final Counter overwrittenFiles =
      metrics.counter(MetricRegistry.name(Stats.class, "overwrittenFiles"));

  private final Counter overwrittenSizeFiles =
      metrics.counter(MetricRegistry.name(Stats.class, "overwrittenSizeFiles"));
  private final Counter overwrittenContentFiles =
      metrics.counter(MetricRegistry.name(Stats.class, "overwrittenContentFiles"));

  private final Counter updatedTime =
      metrics.counter(MetricRegistry.name(Stats.class, "updatedTime"));

  private final Counter copiedDirs =
      metrics.counter(MetricRegistry.name(Stats.class, "copiedDirs"));

  private final Counter deletedFiles =
      metrics.counter(MetricRegistry.name(Stats.class, "deletedFiles"));
  private final Counter deletedDirs =
      metrics.counter(MetricRegistry.name(Stats.class, "deletedDirs"));

  public void equalFile() {
    equalFiles.inc();
  }

  public void copiedFile() {
    copiedFiles.inc();
  }

  public void overwriteFile(String hint) {
    overwrittenFiles.inc();
    if ("size".equals(hint)) {
      overwrittenSizeFiles.inc();
    } else if ("content".equals(hint)) {
      overwrittenContentFiles.inc();
    }
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

  public void updatedTime() {
    updatedTime.inc();
  }

  public void report() {
    Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics).outputTo(log).build();
    reporter.report();
  }
}
