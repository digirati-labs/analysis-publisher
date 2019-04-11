package dev.gtierney.analysispublisher.publishing;

import com.github.tomakehurst.wiremock.WireMockServer;
import dev.gtierney.analysispublisher.AnalysisPublisherApplication;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class EndToEndHttpPublisherTest {
  protected WireMockServer server;

  protected abstract void configure(WireMockServer server);

  protected abstract List<String> getArguments(WireMockServer server);

  /**
   * Bootstraps each test by setting up a web server that can be stubbed and running the application
   * with the arguments that were returned by the test implementation.
   */
  @BeforeEach
  public void setup() {
    server = new WireMockServer();
    server.start();

    configure(server);

    List<String> argList = new ArrayList<>(getArguments(server));
    argList.add("-d");
    argList.add("testData/e2e");

    String[] args = argList.toArray(String[]::new);
    AnalysisPublisherApplication.main(args);
  }

  @AfterEach
  public void teardown() {
    server.stop();
  }
}
