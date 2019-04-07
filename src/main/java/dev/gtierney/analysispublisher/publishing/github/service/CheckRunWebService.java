package dev.gtierney.analysispublisher.publishing.github.service;

import dev.gtierney.analysispublisher.publishing.github.model.CheckRun;
import dev.gtierney.analysispublisher.publishing.github.model.CheckRunCreationResult;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * A web service that interacts with the GitHub API to manipulate <a
 * href="https://developer.github.com/v3/checks/runs/">check runs</a>.
 */
@Path("/repos/{repository}/check-runs")
public interface CheckRunWebService {
  String APPLICATION_JSON_GHPREVIEW = "application/vnd.github.antiope-preview+json";

  @POST
  @Consumes({APPLICATION_JSON_GHPREVIEW})
  @Produces({APPLICATION_JSON_GHPREVIEW})
  CheckRunCreationResult create(@PathParam("repository") String repository, CheckRun run);

  /**
   * Submit an update to an already created check run.
   *
   * @param repository The GitHub repository the check run belongs to (including owner, e.g.,
   *     "garyttierney/analysis-publisher").
   * @param id The unique identifier of the check run.
   * @param run The payload to update the check run with. Any annotations attached to this update
   *     will be appended to the existing list rather than replacing them.
   */
  @PATCH
  @Path("/{id}")
  @Consumes({APPLICATION_JSON_GHPREVIEW})
  @Produces({APPLICATION_JSON_GHPREVIEW})
  void update(@PathParam("repository") String repository, @PathParam("id") int id, CheckRun run);
}
