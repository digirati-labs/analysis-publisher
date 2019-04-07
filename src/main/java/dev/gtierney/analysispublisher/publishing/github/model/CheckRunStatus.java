package dev.gtierney.analysispublisher.publishing.github.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CheckRunStatus {
  @JsonProperty("in_progress")
  IN_PROGRESS,

  @JsonProperty("completed")
  COMPLETED
}
