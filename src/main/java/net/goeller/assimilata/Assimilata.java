package net.goeller.assimilata;

import net.goeller.assimilata.config.SyncConfig;
import net.goeller.assimilata.config.SyncConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

@SpringBootApplication
@EnableScheduling
public class Assimilata {
  private static final Logger LOG = LoggerFactory.getLogger(Assimilata.class);

  public static void main(String[] args) {

    if (args.length == 0) {
      SpringApplicationBuilder builder = new SpringApplicationBuilder(Assimilata.class);
      builder.bannerMode(Banner.Mode.OFF);
      builder.headless(false);
      builder.run(args);
    } else {
      String configName = args[0];
      new Assimilata().start(configName);
    }
  }

  @Bean
  public SyncConfig loadConfig(@Value("${assimilata.config}") String configName) {
    try {
      LOG.info("Loading config from file: {}", configName);
      return new SyncConfigReader().readFile(Paths.get(configName));
    } catch (IOException e) {
      throw new RuntimeException("Could not load config from " + configName, e);
    }
  }

  @Bean
  public ThreadPoolTaskScheduler syncScheduler(@Autowired SyncConfig config) {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(1);
    scheduler.setErrorHandler(t -> LOG.error("Error during execution", t));
    scheduler.setDaemon(true);
    scheduler.setThreadNamePrefix("Scheduler");
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    return scheduler;
  }

  private void start(String configName) {
    SyncConfig syncConfig = loadConfig(configName);

    ThreadPoolTaskScheduler scheduler = syncScheduler(syncConfig);
    scheduler.afterPropertiesSet();

    SyncExecutor syncExecutor = new SyncExecutor();
    syncExecutor.setConfig(syncConfig);
    syncExecutor.setScheduler(scheduler);

    ScheduledFuture<?> future = syncExecutor.execSync();

    try {
      future.get();
    } catch (InterruptedException e) {
      LOG.error(e.getMessage(), e);
    } catch (ExecutionException e) {
      LOG.error(e.getMessage(), e);
    }
  }
}
