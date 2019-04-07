package dev.gtierney.analysispublisher.publishing.github.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckRunCreationResult {

  private int id;

  @JsonCreator
  public CheckRunCreationResult(@JsonProperty("id") int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }
}
