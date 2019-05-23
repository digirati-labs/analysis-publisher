package dev.gtierney.analysispublisher.reporting;

import edu.hm.hafner.analysis.IssueParser;
import edu.hm.hafner.analysis.parser.CMakeParser;
import edu.hm.hafner.analysis.parser.CargoCheckParser;
import edu.hm.hafner.analysis.parser.ClangParser;
import edu.hm.hafner.analysis.parser.ClangTidyParser;
import edu.hm.hafner.analysis.parser.FindBugsParser;
import edu.hm.hafner.analysis.parser.FindBugsParser.PriorityProperty;
import edu.hm.hafner.analysis.parser.GccParser;
import edu.hm.hafner.analysis.parser.GoLintParser;
import edu.hm.hafner.analysis.parser.JSLintXmlSaxParser;
import edu.hm.hafner.analysis.parser.JavaDocParser;
import edu.hm.hafner.analysis.parser.JavacParser;
import edu.hm.hafner.analysis.parser.LintParser;
import edu.hm.hafner.analysis.parser.PhpParser;
import edu.hm.hafner.analysis.parser.PyLintParser;
import edu.hm.hafner.analysis.parser.checkstyle.CheckStyleParser;
import java.util.function.Supplier;

@FunctionalInterface
public interface IssueParserFactoryBuilder {

  /**
   * Contributes all known default issue parsers to the given {@link IssueParserFactoryBuilder}.
   *
   * @param builder the builder to register parsers with.
   */
  static void contributeDefaultParsers(IssueParserFactoryBuilder builder) {
    // Java related parsers.
    builder.add("javac", JavacParser::new);
    builder.add("javadoc", JavaDocParser::new);

    Supplier<IssueParser> spotbugs = () -> new FindBugsParser(PriorityProperty.CONFIDENCE);
    builder.add("spotbugs", spotbugs);
    builder.add("findbugs", spotbugs);
    builder.add("checkstyle", CheckStyleParser::new);

    // Rust related parsers.
    builder.add("rustc", CargoCheckParser::new);
    builder.add("cargo", CargoCheckParser::new);

    // C/C++ related parsers
    builder.add("cmake", CMakeParser::new);
    builder.add("clang", ClangParser::new);
    builder.add("clang-tidy", ClangTidyParser::new);
    builder.add("gcc", GccParser::new);

    // Misc parsers.
    builder.add("lint", LintParser::new);

    // Python related parsers.
    builder.add("pylint", PyLintParser::new);

    // PHP related parsers.
    builder.add("php", PhpParser::new);

    // Go related parsers
    builder.add("golint", GoLintParser::new);
  }

  void add(String name, Supplier<IssueParser> parser);
}
