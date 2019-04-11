package dev.gtierney.analysispublisher.publishing.github;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import dev.gtierney.analysispublisher.publishing.EndToEndHttpPublisherTest;
import java.util.List;
import javax.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("e2e")
public class GitHubCheckPublisherTest extends EndToEndHttpPublisherTest {

  private static final String TEST_HEAD_SHA = "master";
  private static final String TEST_TOKEN = "fake-token";
  private static final String TEST_REPO = "test-repo";
  private static final UrlPattern CREATION_URL = urlEqualTo("/repos/" + TEST_REPO + "/check-runs");
  private static final UrlPattern UPDATE_URL = urlEqualTo("/repos/" + TEST_REPO + "/check-runs/1");

  @Override
  protected void configure(WireMockServer server) {
    var createRunResponse =
        aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .withStatus(200)
            .withBody("{ \"id\": 1, \"extra_data\": 2 }");

    var createRunStub =
        post(urlEqualTo("/repos/test-repo/check-runs")).willReturn(createRunResponse);

    server.stubFor(createRunStub);

    var updateRunResponse =
        aResponse().withStatus(201).withHeader(HttpHeaders.CONTENT_TYPE, "application/json");

    var updateRunStub =
        patch(urlPathMatching("/repos/test-repo/check-runs/\\d+")).willReturn(updateRunResponse);

    server.stubFor(updateRunStub);
  }

  @Override
  protected List<String> getArguments(WireMockServer server) {
    return List.of(
        "--publisher",
        "github_check",
        "-t",
        "javac",
        "-p",
        "**/javac.txt",
        "--property=github.token",
        TEST_TOKEN,
        "--property=github.repository",
        TEST_REPO,
        "--property=github.sha",
        TEST_HEAD_SHA,
        "--property=github.api",
        server.baseUrl());
  }

  @Test
  public void submit_testdata_to_github() {
    // All requests have the correct `Accept` header, and are authenticated.
    server.verify(
        anyRequestedFor(anyUrl())
            .withHeader(HttpHeaders.ACCEPT, equalTo("application/vnd.github.antiope-preview+json"))
            .withHeader(HttpHeaders.AUTHORIZATION, containing(TEST_TOKEN)));

    // A `status` value was provided when we created the check run, and it targets a specific
    // `head_sha`.
    server.verify(
        postRequestedFor(CREATION_URL)
            .withRequestBody(matchingJsonPath("status", equalTo("in_progress")))
            .withRequestBody(matchingJsonPath("head_sha", equalTo(TEST_HEAD_SHA))));

    // We received a payload of issues from the report scan.
    server.verify(
        patchRequestedFor(UPDATE_URL)
            .withRequestBody(
                matchingJsonPath(
                    "output.annotations[0].message", containing("has been deprecated"))));

    // The check run was marked as completed after submitting all reports.
    server.verify(
        patchRequestedFor(UPDATE_URL)
            .withRequestBody(matchingJsonPath("status", equalTo("completed")))
            .withRequestBody(matchingJsonPath("conclusion", equalTo("neutral"))));
  }
}
