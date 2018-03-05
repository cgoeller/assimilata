package net.goeller.assimilata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ShutdownService {

  @Autowired private ApplicationContext appContext;

  public void initiateShutdown() {
    initiateShutdown(0);
  }

  public void initiateShutdown(int returnCode) {
    SpringApplication.exit(appContext, () -> returnCode);
    System.exit(returnCode);
  }
}
