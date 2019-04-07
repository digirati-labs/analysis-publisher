package dev.gtierney.analysispublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "publish")
public class AnalysisPublisherOptions {

  @Option(names = {"-d", "--directory"})
  String workingDirectory = ".";

  @Option(names = {"--timeout"})
  int timeout = 10;

  @Option(names = {"--ignore-errors"})
  boolean ignoreErrors = false;

  @ArgGroup(heading = "Report Locations\n", exclusive = false, multiplicity = "1..*")
  List<ReportSpec> reportSpecs = new ArrayList<>();

  @ArgGroup(heading = "Publishers\n", exclusive = false, multiplicity = "1..*")
  List<PublisherSpec> publisherSpecs = new ArrayList<>();

  @ArgGroup(heading = "Publisher properties\n", exclusive = false, multiplicity = "0..*")
  List<Property> properties = new ArrayList<>();

  public Map<String, String> getProperties() {
    return properties.stream().collect(Collectors.toMap(p -> p.key, p -> p.value));
  }

  public int getTimeout() {
    return timeout;
  }

  public boolean shouldIgnoreErrors() {
    return ignoreErrors;
  }

  public static class Property {
    @Option(
        description = "Name of the property to be set.",
        names = {"-D", "--property"},
        required = true)
    String key;

    @Parameters(index = "0", arity = "1")
    String value;
  }

  public static class ReportSpec {
    @Option(
        description = "The type of analyzer that emitted the report.",
        names = {"-t", "--report-type"},
        required = true)
    String type;

    @Option(
        description = "A glob matching paths containing analysis reports.",
        names = {"-p", "--path"},
        required = true)
    String path;
  }

  public static class PublisherSpec {
    @Option(
        description = "The name of a supported analysis publisher type.",
        names = {"--publisher"},
        required = true)
    String type;
  }
}
