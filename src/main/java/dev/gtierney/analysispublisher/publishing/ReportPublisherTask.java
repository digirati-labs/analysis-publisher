package dev.gtierney.analysispublisher.publishing;

import edu.hm.hafner.analysis.Report;
import java.util.concurrent.Callable;

public class ReportPublisherTask implements Callable<Void> {

  private final ReportPublisher publisher;
  private final Report report;

  public ReportPublisherTask(ReportPublisher publisher, Report report) {
    this.publisher = publisher;
    this.report = report;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Void call() throws Exception {
    publisher.publish(report);
    return null;
  }
}
