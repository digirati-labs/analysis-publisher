Analysis Publisher
------------------

analysis-publisher is a tool that searches your project for code quality reports and publishes an aggregate to external services.
A large variety of report formats are supported, and can currently be published to GitHub.

![Codecov](https://img.shields.io/codecov/c/github/garyttierney/analysis-publisher.svg)
 
Dual-licensed under ISC or ASL 2.0.

## What publishers are supported?

Each publisher implementation may differ slightly in how issues are published, but ideally reports are published as line annotations on sections of code.
All supported publishers are listed below:

- [GitHub Checks](doc/publishers/github_check.md) (id: `github_check`)

## What report formats are supported?

The `analysis-model` library from Jenkins is used to parse issue reports, and as such `analysis-publisher` supports all formats from the [upstream Jenkins plugin](https://github.com/jenkinsci/warnings-ng-plugin/blob/master/SUPPORTED-FORMATS.md).

**Note**: only a few of these publishers are currently registered, see [IssueParserFactoryBuilder.java](src/main/java/dev/gtierney/analysispublisher/reporting/IssueParserFactoryBuilder.java) for a list of supported report parsers.

## Usage

A Docker image is available containing a compiled binary of the analysis publisher application, which can be used to upload analysis reports in a GitHub action.

### Command-Line

To get detailed help on the options available on the command-line see the output of:
```
analysis-publisher --help
```

At least one report specification must be given using the repeatable --report-type and --path options, as well as one publisher specification:
```
analysis-publisher --report-type=spotbugs --path=**/spotbugs.xml --publisher=github_check
```

By default publishers will be configured by environment variables set in the execution environment, but can be overriden using properties set on the command-line:

```
analysis-publisher --publisher=github_check \
  --property=github.repository garyttierney/analysis-publisher \
  --property=github.token TEST_TOKEN
```

Documentation on available publisher properties can be found in the documentation for the specific publisher listed above.

### GitHub Actions

```hcl
workflow "analysis-publisher/ci" {
  on = "push"
  resolves = [
    "analysis-publisher/ci/publish-analysis",
  ]
}

action "analysis-publisher/ci/analyze" {
  uses = "openjdk:11"
  runs = ["./gradlew", "check"]
}

action "analysis-publisher/ci/publish-analysis" {
  needs = ["analysis-publisher/ci/analyze"]
  secrets = ["GITHUB_TOKEN"]

  uses = "garyttierney/analysis-publisher@master"
  args = [
    "--report-type=checkstyle", "--path=**/checkstyle/*.xml",
    "--report-type=spotbugs", "--path=**/spotbugs/*.xml",
    "--publisher=github_check"
  ]
}
```

## Examples

This repository publishes its own code quality reports with every commit, and can be seen in the [check runs](https://github.com/garyttierney/analysis-publisher/runs/99421229) for each commit.