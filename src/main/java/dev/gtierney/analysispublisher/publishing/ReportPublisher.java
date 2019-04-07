package dev.gtierney.analysispublisher.publishing;

import edu.hm.hafner.analysis.Report;

public interface ReportPublisher {
  void publish(Report report) throws ReportPublisherException;
}
