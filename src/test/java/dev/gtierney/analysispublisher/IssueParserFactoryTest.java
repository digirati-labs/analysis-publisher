package dev.gtierney.analysispublisher;

import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.gtierney.analysispublisher.reporting.IssueParserFactory;
import edu.hm.hafner.analysis.parser.JavacParser;
import org.junit.jupiter.api.Test;

public class IssueParserFactoryTest {

  @Test
  public void factory_contributesBuilderExtensions() {
    IssueParserFactory factory =
        IssueParserFactory.compose(
            builder -> {
              builder.add("test", JavacParser::new);
            });

    assertTrue(factory.create("test").isPresent());
  }
}
