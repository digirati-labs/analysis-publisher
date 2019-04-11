package dev.gtierney.analysispublisher;

import dev.gtierney.analysispublisher.publishing.ReportPublisherException;
import dev.gtierney.analysispublisher.publishing.ReportPublisherFactory;
import dev.gtierney.analysispublisher.publishing.ReportPublisherTask;
import dev.gtierney.analysispublisher.publishing.github.GitHubCheckPublisherType;
import dev.gtierney.analysispublisher.reporting.IssueParserFactory;
import dev.gtierney.analysispublisher.reporting.collector.ReportCollection;
import dev.gtierney.analysispublisher.reporting.collector.ReportCollector;
import dev.gtierney.analysispublisher.reporting.collector.ReportCollectorException;
import edu.hm.hafner.analysis.Report;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParameterException;

@Command(name = "analysis-publisher")
public class AnalysisPublisherApplication {

  private static final Logger logger = LogManager.getLogger(AnalysisPublisherApplication.class);

  private final IssueParserFactory parserFactory;
  private final ReportPublisherFactory publisherFactory;

  public AnalysisPublisherApplication(
      IssueParserFactory parserFactory, ReportPublisherFactory publisherFactory) {
    this.parserFactory = parserFactory;
    this.publisherFactory = publisherFactory;
  }

  /**
   * Start the application to collect and publish analysis results with the given {@link
   * AnalysisPublisherOptions} represented by the command line {@code args}.
   */
  public static void main(String[] args) {
    var parserFactory = IssueParserFactory.createDefaultFactory();
    var publisherFactory = new ReportPublisherFactory(List.of(new GitHubCheckPublisherType()));

    var app = new AnalysisPublisherApplication(parserFactory, publisherFactory);
    var options = new AnalysisPublisherOptions();

    try {
      CommandLine.populateCommand(options, args);
    } catch (ParameterException e) {
      System.out.println(e.getMessage());
      CommandLine.usage(options, System.out);
      return;
    }

    app.run(options);
  }

  private void run(AnalysisPublisherOptions options) {
    ReportCollection reportCollection;
    try {
      reportCollection = collectReports(options);
    } catch (ReportCollectorException ex) {
      logger.error(ex);
      return;
    }

    if (reportCollection.isEmpty()) {
      logger.error("No report files were found, refusing to publish");
      return;
    }

    publishReports(options, reportCollection.getAggregate());
  }

  private ReportCollection collectReports(AnalysisPublisherOptions options)
      throws ReportCollectorException {
    var collector = new ReportCollector(parserFactory);

    for (var report : options.reportSpecs) {
      try {
        collector.addReportLocation(report.type, report.path);
      } catch (ReportCollectorException e) {
        logger.error("Unable to add report location", e);
        if (!options.shouldIgnoreErrors()) {
          throw e;
        }
      }
    }

    return collector.collect(options.workingDirectory);
  }

  void publishReports(AnalysisPublisherOptions options, Report aggregate) {
    var env = new AnalysisPublisherEnvironment(options.getProperties(), System.getenv());
    var publisherTasks = new ArrayList<Callable<Void>>();

    for (var publisherSpec : options.publisherSpecs) {
      try {
        var publisher = publisherFactory.create(publisherSpec.type, env);
        publisherTasks.add(new ReportPublisherTask(publisher, aggregate));
      } catch (ReportPublisherException e) {
        logger.error("Unable to configure publisher", e);
        if (!options.shouldIgnoreErrors()) {
          return;
        }
      }
    }

    var taskPool = Executors.newCachedThreadPool();
    try {
      var tasks = taskPool.invokeAll(publisherTasks, options.getTimeout(), TimeUnit.SECONDS);
      for (var task : tasks) {
        task.get();
      }
    } catch (InterruptedException e) {
      logger.error("Interrupted while submitting report", e);
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      logger.error("Publisher failed to submit report", e);
    }
  }
}
