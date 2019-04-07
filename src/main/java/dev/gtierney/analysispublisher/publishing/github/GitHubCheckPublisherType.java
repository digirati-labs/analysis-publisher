package dev.gtierney.analysispublisher.publishing.github;

import dev.gtierney.analysispublisher.AnalysisPublisherEnvironment;
import dev.gtierney.analysispublisher.AnalysisPublisherEnvironmentException;
import dev.gtierney.analysispublisher.publishing.ReportPublisherException;
import dev.gtierney.analysispublisher.publishing.ReportPublisherType;
import java.net.URI;
import java.net.URISyntaxException;

public class GitHubCheckPublisherType implements ReportPublisherType<GitHubCheckPublisher> {

  @Override
  public String getName() {
    return "github_check";
  }

  @Override
  public GitHubCheckPublisher createPublisher(AnalysisPublisherEnvironment environment)
      throws ReportPublisherException {
    try {
      var api = new URI(environment.lookup("GITHUB_API").orElse("https://api.github.com/"));
      var token = environment.lookupRequired("GITHUB_TOKEN");
      var repository = environment.lookupRequired("GITHUB_REPOSITORY");
      var sha = environment.lookupRequired("GITHUB_SHA");
      var checkName = environment.lookup("GITHUB_CHECK_NAME").orElse("analysis-publisher");
      var checkTitle = environment.lookup("GITHUB_CHECK_TITLE").orElse("Analysis");
      var workspacePath = environment.lookup("GITHUB_WORKSPACE").orElse(null);

      var config =
          new GitHubCheckPublisherConfig(
              api, sha, repository, token, checkName, checkTitle, workspacePath);

      return new GitHubCheckPublisher(config);
    } catch (AnalysisPublisherEnvironmentException e) {
      throw new ReportPublisherException(e);
    } catch (URISyntaxException e) {
      throw new ReportPublisherException("Invalid URI supplied for GitHub API", e);
    }
  }
}
