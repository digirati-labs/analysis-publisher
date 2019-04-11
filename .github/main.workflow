workflow "analysis-publisher/ci" {
  on = "push"
  resolves = [
    "analysis-publisher/ci/publish-analysis",
    "analysis-publisher/ci/publish-coverage",
  ]
}

action "analysis-publisher/ci/analyze" {
  uses = "./"
  runs = ["./gradlew", "check"]
}

action "analysis-publisher/ci/publish-analysis" {
  needs = ["analysis-publisher/ci/analyze"]
  secrets = ["GITHUB_TOKEN"]

  uses = "./"
  args = [
    "--report-type=checkstyle", "--path=**/checkstyle/*.xml",
    "--report-type=spotbugs", "--path=**/spotbugs/*.xml",
    "--publisher=github_check"
  ]
}

action "analysis-publisher/ci/publish-coverage" {
  needs = ["analysis-publisher/ci/analyze"]
  uses = "docker://node"
  runs = "npx"
  args = "codecov"
  secrets = ["CODECOV_TOKEN"]
}