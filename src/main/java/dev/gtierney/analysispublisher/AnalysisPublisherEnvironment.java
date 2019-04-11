package dev.gtierney.analysispublisher;

import java.util.Map;
import java.util.Optional;

public class AnalysisPublisherEnvironment {
  private final Map<String, String> configVariables;
  private final Map<String, String> environmentVariables;

  AnalysisPublisherEnvironment(Map<String, String> config, Map<String, String> env) {
    this.configVariables = config;
    this.environmentVariables = env;
  }

  private static AnalysisPublisherEnvironmentException missingRequiredVar(String name) {
    return new AnalysisPublisherEnvironmentException(
        "Missing value for config variable '" + name + "'");
  }

  /**
   * Lookup a configuration property defined by {@code name}.
   *
   * @param name The variable specifications to search for in CONSTANT_CASE. The name will be
   *     normalized to a property key by swapping underscores (_) for periods (.) and lower-casing
   *     the name. If a configuration value was given on the command line for the given key, this
   *     will be returned, if not, any environment variable matching the constant case name will be
   *     returned.
   * @return The value of the config variable that was matched, or empty if none was found.
   */
  public Optional<String> lookup(String name) {
    var propName = name.replaceAll("_", ".").toLowerCase();
    var envName = name.toUpperCase();
    var value = configVariables.getOrDefault(propName, environmentVariables.get(envName));

    return Optional.ofNullable(value);
  }

  public String lookupRequired(String name) throws AnalysisPublisherEnvironmentException {
    return lookup(name).orElseThrow(() -> missingRequiredVar(name));
  }
}
