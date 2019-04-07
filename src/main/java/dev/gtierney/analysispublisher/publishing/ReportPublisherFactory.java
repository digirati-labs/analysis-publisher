package dev.gtierney.analysispublisher.publishing;

import dev.gtierney.analysispublisher.AnalysisPublisherEnvironment;
import java.util.List;

public final class ReportPublisherFactory {

  private final List<ReportPublisherType> types;

  public ReportPublisherFactory(List<ReportPublisherType> types) {
    this.types = types;
  }

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
