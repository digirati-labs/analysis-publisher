package dev.gtierney.analysispublisher.publishing.github.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.Severity;
import java.io.IOException;

public class AnnotationSerializer extends StdSerializer<Issue> {

  private static final String ANNOTATION_LEVEL = "annotation_level";
  private static final String ANNOTATION_MESSAGE = "message";
  private static final String ANNOTATION_PATH = "path";
  private static final String ANNOTATION_START_LINE = "start_line";
  private static final String ANNOTATION_END_LINE = "end_line";
  private static final String ANNOTATION_START_COLUMN = "start_column";
  private static final String ANNOTATION_END_COLUMN = "end_column";

  AnnotationSerializer() {
    super(Issue.class);
  }

  @Override
  public void serialize(Issue value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeStartObject();
    gen.writeStringField(ANNOTATION_LEVEL, mapSeverity(value.getSeverity()));
    gen.writeStringField(ANNOTATION_MESSAGE, value.getMessage());
    gen.writeStringField(ANNOTATION_PATH, value.getFileName());

    var lineStart = value.getLineStart();
    var lineEnd = value.getLineEnd();

    gen.writeNumberField(ANNOTATION_START_LINE, lineStart);
    gen.writeNumberField(ANNOTATION_END_LINE, lineEnd);

    // GitHub only accepts annotations with column numbers where the annotaion only
    // spans a single line.
    if (lineStart == lineEnd) {
      gen.writeNumberField(ANNOTATION_START_COLUMN, value.getColumnStart());
      gen.writeNumberField(ANNOTATION_END_COLUMN, value.getColumnEnd());
    }

    gen.writeEndObject();
  }

  private String mapSeverity(Severity severity) {
    if (severity.equals(Severity.ERROR)) {
      return "failure";
    } else if (severity.equals(Severity.WARNING_HIGH) || severity.equals(Severity.WARNING_NORMAL)) {
      return "warning";
    } else {
      return "notice";
    }
  }
}
