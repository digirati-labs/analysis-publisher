package dev.gtierney.analysispublisher.reporting;

import edu.hm.hafner.analysis.IssueParser;
import edu.hm.hafner.analysis.parser.FindBugsParser;
import edu.hm.hafner.analysis.parser.FindBugsParser.PriorityProperty;
import edu.hm.hafner.analysis.parser.JavaDocParser;
import edu.hm.hafner.analysis.parser.JavacParser;
import java.util.function.Supplier;

@FunctionalInterface
public interface IssueParserFactoryBuilder {
  static void contributeDefaultParsers(IssueParserFactoryBuilder builder) {
    builder.add("javac", JavacParser::new);
    builder.add("javadoc", JavaDocParser::new);

    Supplier<IssueParser> spotbugs = () -> new FindBugsParser(PriorityProperty.CONFIDENCE);
    builder.add("spotbugs", spotbugs);
    builder.add("findbugs", spotbugs);
  }

  void add(String name, Supplier<IssueParser> parser);
}
