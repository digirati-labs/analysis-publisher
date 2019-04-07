workflow "analysis-publisher/ci" {
  on = "push"
  resolves = [
    "analysis-publisher/ci/publish-analysis"
  ]
}

action "analysis-publisher/ci/analyze" {
  uses = "./"
  runs = ["./gradlew", "check"]
}

action "analysis-publisher/ci/publish-analysis" {
  uses = "./"
  needs = ["analysis-publisher/ci/analyze"]
  secrets = ["GITHUB_TOKEN"]
  args = ["--report-type", "spotbugs", "--path", "**/spotbugs/*.xml", "--publisher=github_check"]
}