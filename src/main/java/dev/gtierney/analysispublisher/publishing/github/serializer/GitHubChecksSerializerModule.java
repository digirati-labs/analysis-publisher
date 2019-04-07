package dev.gtierney.analysispublisher.publishing.github.serializer;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class GitHubChecksSerializerModule extends SimpleModule {
  public GitHubChecksSerializerModule() {
    addSerializer(new AnnotationSerializer());
  }
}
