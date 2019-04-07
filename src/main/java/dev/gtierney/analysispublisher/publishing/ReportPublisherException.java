package dev.gtierney.analysispublisher.publishing;

public class ReportPublisherException extends Exception {

  public ReportPublisherException(String message) {
    super(message);
  }

  public ReportPublisherException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReportPublisherException(Throwable cause) {
    super(cause);
  }
}
