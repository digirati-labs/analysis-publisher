package dev.gtierney.analysispublisher.publishing.github.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimestampSerializer extends StdSerializer<Instant> {

  protected TimestampSerializer() {
    super(Instant.class);
  }

  @Override
  public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    ZonedDateTime zonedValue = value.atZone(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES);
    gen.writeString(zonedValue.format(DateTimeFormatter.ISO_INSTANT));
  }
}
