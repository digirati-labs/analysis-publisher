package dev.gtierney.analysispublisher;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnalysisPublisherEnvironment {
  private Map<String, String> configVariables;
  private Map<String, String> environmentVariables;

  public AnalysisPublisherEnvironment(Map<String, String> config, Map<String, String> env) {
    this.configVariables = config;
    this.environmentVariables = env;
  }

  public static VarSpec env(String name) {
    return new VarSpec(name, env -> Optional.ofNullable(env.environmentVariables.get(name)));
  }

  public static VarSpec property(String name) {
    return new VarSpec(name, env -> Optional.ofNullable(env.configVariables.get(name)));
  }

  private static AnalysisPublisherEnvironmentException missingRequiredVar(VarSpec... specs) {
    var keys = Arrays.stream(specs).map(spec -> spec.key).collect(Collectors.joining(", "));
    var message = String.format("Missing a value for %s", keys);

    return new AnalysisPublisherEnvironmentException(message);
  }

  public Optional<String> lookup(VarSpec... specs) {
    return Arrays.stream(specs)
        .map(spec -> spec.lookup.apply(this))
        .flatMap(Optional::stream)
        .findFirst();
  }

  public Optional<String> lookup(String name) {
    String propName = name.replaceAll("_", ".").toLowerCase();
    String envName = name.toUpperCase();

    return lookup(property(propName), env(envName));
  }

  public String lookupRequired(String name) throws AnalysisPublisherEnvironmentException {
    String propName = name.replaceAll("_", ".").toLowerCase();
    String envName = name.toUpperCase();

    return lookupRequired(property(propName), env(envName));
  }

  public String lookupRequired(VarSpec... specs) throws AnalysisPublisherEnvironmentException {
    return lookup(specs).orElseThrow(() -> missingRequiredVar(specs));
  }

  public static class VarSpec {
    final String key;
    final Function<AnalysisPublisherEnvironment, Optional<String>> lookup;

    VarSpec(String key, Function<AnalysisPublisherEnvironment, Optional<String>> lookup) {
      this.key = key;
      this.lookup = lookup;
    }
  }
}
