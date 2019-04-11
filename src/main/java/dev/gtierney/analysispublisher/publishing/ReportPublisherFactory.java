package dev.gtierney.analysispublisher.publishing;

import dev.gtierney.analysispublisher.AnalysisPublisherEnvironment;
import java.util.List;

public final class ReportPublisherFactory {

  private final List<ReportPublisherType> types;

  public ReportPublisherFactory(List<ReportPublisherType> types) {
    this.types = types;
  }

  /**
   * Create and configure a {@link ReportPublisher} from the publishing environment.
   *
   * @param name The name of the publisher to configure.
   * @param environment The execution environment, containing the properties and environment
   *     variables the application was run with.
   * @return A configured {@link ReportPublisher}.
   * @throws ReportPublisherException if the publisher couldn't be configured from the {@code
   *     environment}, or no sch publisher with the given {@code name} exists.
   */
  public ReportPublisher create(String name, AnalysisPublisherEnvironment environment)
      throws ReportPublisherException {

    for (var publisherType : types) {
      if (publisherType.getName().equals(name)) {
        return publisherType.createPublisher(environment);
      }
    }

    throw new ReportPublisherException("No report publisher found named " + name);
  }
}
