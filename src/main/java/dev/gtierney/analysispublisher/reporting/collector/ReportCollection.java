package dev.gtierney.analysispublisher.reporting.collector;

import com.google.common.base.MoreObjects;
import edu.hm.hafner.analysis.Report;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ReportCollection {
  private final Report aggregate;
  private final List<Entry> entries;

  public ReportCollection(Report aggregate, List<Entry> entries) {
    this.aggregate = aggregate;
    this.entries = entries;
  }

  public Report getAggregate() {
    return aggregate;
  }

  public boolean isEmpty() {
    return entries.isEmpty();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("aggregate", aggregate)
        .add("entries", entries)
        .toString();
  }

  static final class Entry {
    private final Path path;
    private final String type;

    Entry(Path path, String type) {
      this.path = path;
      this.type = type;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("path", path).add("type", type).toString();
    }
  }

  public static final class Builder {
    private final Report aggregate = new Report();
    private final List<Entry> entries = new ArrayList<>();

    public void addReport(String type, Path path, Report report) {
      entries.add(new ReportCollection.Entry(path, type));
      aggregate.addAll(report);
    }

    public ReportCollection build() {
      return new ReportCollection(aggregate, entries);
    }
  }
}
