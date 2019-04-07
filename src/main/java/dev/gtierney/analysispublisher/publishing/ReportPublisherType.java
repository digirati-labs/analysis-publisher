package dev.gtierney.analysispublisher.publishing;

import dev.gtierney.analysispublisher.AnalysisPublisherEnvironment;

public interface ReportPublisherType<PublisherT extends ReportPublisher> {

  /**
   * Get the unique name of this type of {@link ReportPublisher}.
   *
   * @return This issue publishers unique name.
   */
  String getName();

  /**
   * Create a new {@link ReportPublisher}, capable of publishing an issue report for this publisher
   * type.
   *
   * @param environment The environment to configure the {@link ReportPublisher} with.
   */
  PublisherT createPublisher(AnalysisPublisherEnvironment environment)
      throws ReportPublisherException;
}
