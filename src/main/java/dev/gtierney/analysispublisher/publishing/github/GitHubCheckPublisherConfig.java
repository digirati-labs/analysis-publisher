package dev.gtierney.analysispublisher.publishing.github;

import java.net.URI;
import java.util.Optional;

class GitHubCheckPublisherConfig {

  private final URI apiUri;
  private final String headSha;
  private final String repository;
  private final String token;
  private final String checkName;
  private final String checkTitle;
  private final String workspacePath;

  GitHubCheckPublisherConfig(
      URI apiUri,
      String headSha,
      String repository,
      String token,
      String checkName,
      String checkTitle,
      String workspacePath) {
    this.apiUri = apiUri;
    this.headSha = headSha;
    this.repository = repository;
    this.token = token;
    this.checkName = checkName;
    this.checkTitle = checkTitle;
    this.workspacePath = workspacePath;
  }

  URI getApiUri() {
    return apiUri;
  }

  String getRepository() {
    return repository;
  }

  String getHeadSha() {
    return headSha;
  }

  String getToken() {
    return token;
  }

  String getCheckName() {
    return checkName;
  }

  String getCheckTitle() {
    return checkTitle;
  }

  Optional<String> getWorkspacePath() {
    return Optional.ofNullable(workspacePath);
  }
}
