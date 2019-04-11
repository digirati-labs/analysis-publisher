package dev.gtierney.analysispublisher.publishing.github.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.gtierney.analysispublisher.publishing.github.serializer.AnnotationSerializer;
import edu.hm.hafner.analysis.Issue;
import java.util.List;

public class CheckRunOutput {
  private final String title;
  private final String summary;
  private final List<Issue> issues;

  CheckRunOutput(String title, String summary, List<Issue> issues) {
    this.title = title;
    this.summary = summary;
    this.issues = issues;
  }

  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  @JsonProperty("summary")
  public String getSummary() {
    return summary;
  }

  @JsonProperty("annotations")
  @JsonSerialize(contentUsing = AnnotationSerializer.class)
  public List<Issue> getIssues() {
    return issues;
  }
}
