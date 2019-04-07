package dev.gtierney.analysispublisher.publishing.github.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CheckRunConclusion {
  @JsonProperty("success")
  SUCCESS,

  @JsonProperty("neutral")
  NEUTRAL,

  @JsonProperty("failure")
  FAILURE
}
