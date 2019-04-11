package dev.gtierney.analysispublisher.reporting.collector;

import static dev.gtierney.analysispublisher.reporting.collector.ReportCollectorException.unsupportedReportType;

import dev.gtierney.analysispublisher.reporting.IssueParserFactory;
import edu.hm.hafner.analysis.FileReaderFactory;
import edu.hm.hafner.analysis.IssueParser;
import edu.hm.hafner.analysis.Report;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportCollector {

  private final FileSystem fileSystem;
  private final List<ScanWorkItem> scanWorkItems = new ArrayList<>();
  private final IssueParserFactory issueParserFactory;

  public ReportCollector(IssueParserFactory parserFactory) {
    this(FileSystems.getDefault(), parserFactory);
  }

  private ReportCollector(FileSystem fileSystem, IssueParserFactory parserFactory) {
    this.fileSystem = fileSystem;
    this.issueParserFactory = parserFactory;
  }

  /**
   * Register a report location to be scanned with the given {@code reportType} parser.
   *
   * @param reportType The name of an analysis report parser.
   * @param glob A glob matching paths to reports.
   * @throws ReportCollectorException if there was no parser available for reports of {@code
   *     reportType}.
   */
  public void addReportLocation(String reportType, String glob) throws ReportCollectorException {
    var matcher = fileSystem.getPathMatcher("glob:" + glob);
    var parser =
        issueParserFactory.create(reportType).orElseThrow(() -> unsupportedReportType(reportType));

    scanWorkItems.add(new ScanWorkItem(reportType, parser, matcher));
  }

  /**
   * Visit all files in the {@code workingDirectory}, parsing any reports that match the locations
   * given to the collector by {@link #addReportLocation(String, String)}. Any reports that are
   * parsed will be aggregated into a single report containing a list of every issue across the
   * workspace.
   *
   * @param workingDirectory The directory to scan.
   * @return an aggregate report and listing of all files that contributed to the report.
   * @throws ReportCollectorException if an internal error occurred while collecting reports.
   */
  public ReportCollection collect(String workingDirectory) throws ReportCollectorException {
    var builder = new ReportCollection.Builder();
    var workingDirectoryPath = fileSystem.getPath(workingDirectory);

    try (var candidates = Files.walk(workingDirectoryPath, FileVisitOption.FOLLOW_LINKS)) {
      var workItems =
          candidates
              .map(this::filterCandidate)
              .flatMap(Optional::stream)
              .collect(Collectors.toList());

      for (ParseWorkItem workItem : workItems) {
        builder.addReport(workItem.type, workItem.path, workItem.execute());
      }
    } catch (Exception ex) {
      throw new ReportCollectorException(ex);
    }

    return builder.build();
  }

  private Optional<ParseWorkItem> filterCandidate(Path path) {
    for (var workItem : scanWorkItems) {
      PathMatcher matcher = workItem.matcher;

      if (matcher.matches(path)) {
        return Optional.of(new ParseWorkItem(workItem.type, path, workItem.parser));
      }
    }

    return Optional.empty();
  }

  private static class ParseWorkItem {

    private final String type;
    private final Path path;
    private final IssueParser parser;

    ParseWorkItem(String type, Path path, IssueParser parser) {
      this.type = type;
      this.path = path;
      this.parser = parser;
    }

    Report execute() {
      var reader = new FileReaderFactory(path, StandardCharsets.UTF_8);
      var report = parser.parse(reader);

      return report;
    }
  }

  private static class ScanWorkItem {
    final String type;
    final IssueParser parser;
    final PathMatcher matcher;

    ScanWorkItem(String reportType, IssueParser parser, PathMatcher pathMatcher) {
      this.type = reportType;
      this.parser = parser;
      this.matcher = pathMatcher;
    }
  }
}
