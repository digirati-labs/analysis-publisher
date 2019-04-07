package dev.gtierney.analysispublisher.reporting;

import edu.hm.hafner.analysis.IssueParser;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface IssueParserFactory {

  /**
   * Create a new compose capable of constructing any {@link IssueParser} contributed to it by
   * parser suppliers registered with the {@link IssueParserFactoryBuilder}.
   *
   * <pre>
   *     var parserFactory = IssueParserFactory.compose(builder -> {
   *         builder.add("javac", JavacParser::new);
   *     });
   * </pre>
   *
   * @param consumers A collection of {@link IssueParserFactoryBuilder} consumers that contribtue
   *     parsers to the factory.
   * @return A new {@link IssueParserFactory}.
   */
  @SafeVarargs
  static IssueParserFactory compose(Consumer<IssueParserFactoryBuilder>... consumers) {
    Map<String, Supplier<IssueParser>> map = new HashMap<>();
    IssueParserFactoryBuilder builder = map::put;

    for (Consumer<IssueParserFactoryBuilder> consumer : consumers) {
      consumer.accept(builder);
    }

    return name -> Optional.ofNullable(map.get(name)).map(Supplier::get);
  }

  /**
   * Create a new {@link IssueParserFactory} supporting the standard parsers from the Jenkins
   * Analysis-Model library.
   */
  static IssueParserFactory createDefaultFactory() {
    return compose(IssueParserFactoryBuilder::contributeDefaultParsers);
  }

  /**
   * Create a new {@link IssueParser} capable of parsing issues emitted by the given analyzer {@code
   * name}.
   *
   * @param name The name of the analysis tool to create an {@code IssueParser} for.
   */
  Optional<IssueParser> create(String name);
}
