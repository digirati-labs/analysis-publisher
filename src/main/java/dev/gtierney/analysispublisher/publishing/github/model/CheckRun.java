package dev.gtierney.analysispublisher.publishing.github.model;

import dev.gtierney.analysispublisher.publishing.github.serializer.TimestampSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.hm.hafner.analysis.Issue;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class CheckRun {

  /**
   * The maximum number of annotations that can be associated with a {@code CheckRun} in a single
   * request.
   */
  public static final int MAX_ANNOTATIONS = 50;

  private final String name;
  private final String headSha;
  private final CheckRunOutput output;
  private final CheckRunConclusion conclusion;
  private final Instant startedAt;
  private final Instant completedAt;
  private final CheckRunStatus status;

  /**
   * Create a new in-progress check run with the given {@code name}, targeting the commit referenced
   * by {@code headSha}.
   *
   * @param name The name given to this check run, used for status checks.
   * @param headSha The commit ref this run targets.
   */
  public CheckRun(String name, String headSha, String title, String summary) {
    this.name = name;
    this.headSha = headSha;
    this.output = new CheckRunOutput(title, summary, Collections.emptyList());
    this.completedAt = null;
    this.conclusion = null;
    this.startedAt = Instant.now();
    this.status = CheckRunStatus.IN_PROGRESS;
  }

  /**
   * Internal constructor used to create an update to an existing {@code CheckRun}.
   *
   * @see #completed(CheckRunConclusion)
   * @see #withIssues(List)
   */
  private CheckRun(
      String name,
      String headSha,
      CheckRunOutput output,
      CheckRunConclusion conclusion,
      CheckRunStatus status,
      Instant completedAt) {
    this.name = name;
    this.headSha = headSha;
    this.output = output;
    this.completedAt = completedAt;
    this.status = status;
    this.conclusion = conclusion;
    this.startedAt = null;
  }

  public CheckRun completed(CheckRunConclusion conclusion) {
    return new CheckRun(name, headSha, output, conclusion, CheckRunStatus.COMPLETED, Instant.now());
  }

  public CheckRun withIssues(List<Issue> issues) {
    if (issues.size() > MAX_ANNOTATIONS) {
      throw new IllegalArgumentException("A check-run can only accept 50 issues at a time");
    }

    return new CheckRun(
        name,
        headSha,
        new CheckRunOutput(output.title, output.summary, issues),
        null,
        status,
        null);
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("head_sha")
  public String getHeadSha() {
    return headSha;
  }

  @JsonProperty("output")
  public CheckRunOutput getOutput() {
    return output;
  }

  @JsonProperty("conclusion")
  public CheckRunConclusion getConclusion() {
    return conclusion;
  }

  @JsonSerialize(using = TimestampSerializer.class)
  @JsonProperty("started_at")
  public Instant getStartedAt() {
    return startedAt;
  }

  @JsonSerialize(using = TimestampSerializer.class)
  @JsonProperty("completed_at")
  public Instant getCompletedAt() {
    return completedAt;
  }

  @JsonProperty("status")
  public CheckRunStatus getStatus() {
    return status;
  }
}
