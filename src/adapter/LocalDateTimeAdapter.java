package adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.nullValue(); // Записываем null, если дата отсутствует
        } else {
            jsonWriter.value(localDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == com.google.gson.stream.JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        return LocalDateTime.parse(jsonReader.nextString(), DateTimeFormatter.ISO_DATE_TIME);
    }
}
