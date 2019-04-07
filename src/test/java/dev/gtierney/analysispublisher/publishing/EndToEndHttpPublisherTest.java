package dev.gtierney.analysispublisher.publishing;

import dev.gtierney.analysispublisher.AnalysisPublisherApplication;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class EndToEndHttpPublisherTest {
  protected WireMockServer server;

  protected abstract void configure(WireMockServer server);

  protected abstract List<String> getArguments(WireMockServer server);

  @BeforeEach
  public void setup() {
    server = new WireMockServer(8889);
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
