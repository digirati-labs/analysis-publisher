# GitHub Check (github_check)

The GitHub Check publisher works by creating a new check run on the target commit and attaching the found issues as line annotations.

## Options

|Property name      | Required  | Description |
|:-----------------:|:---------:|-------------|
| github.repository | X         | The qualified name of the GitHub repository the analysis targets. |
| github.token      | X         | The oauth token used to authenticate against the GitHub API. |
| github.sha        | X         | The hash of the commit being analyzed. |
| github.api        |           | The URL of the GitHub API (defaults to https://api.github.com/). |
| github.check.name |           | The name of the check run to create (defaults to "analysis-publisher"). |
| github.check.title|           | The title to associate with the check run (defaults to "Analysis"). |
| github.workspace  |           | Absolute path to the workspace being analyzed (defaults to the current working directory). |
