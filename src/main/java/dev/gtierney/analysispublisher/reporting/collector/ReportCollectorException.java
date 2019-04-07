package dev.gtierney.analysispublisher.reporting.collector;

public class ReportCollectorException extends Exception {

  ReportCollectorException(Exception ex) {
    super(ex);
  }

  ReportCollectorException(String message) {
    super(message);
  }

  static ReportCollectorException unsupportedReportType(String type) {
    return new ReportCollectorException("Can't find a parser for '" + type + "' reports");
  }
}
