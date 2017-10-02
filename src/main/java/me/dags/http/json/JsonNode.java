package me.dags.http.json;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author dags <dags@dags.me>
 */
public class JsonNode {

    public static JsonNode NULL = new JsonNode(null);

    private final Object value;

    public JsonNode(Object value) {
        this.value = value;
    }

    public boolean isPresent() {
        return this != NULL;
    }

    public boolean isArray() {
        return value != null && List.class.isInstance(value);
    }

    public boolean isObject() {
        return value != null && Map.class.isInstance(value);
    }

    public boolean isString() {
        return String.class.isInstance(value);
    }

    public Number number() {
        return cast(value, Number.class, Double.NaN);
    }

    public String string() {
        return value == null ? "null" : value.toString();
    }

    public boolean bool() {
        return cast(value, Boolean.class, false);
    }

    public List<JsonNode> list() {
        return cast(value, List.class, Collections.emptyList());
    }

    public Map<String, JsonNode> map() {
        return cast(value, Map.class, Collections.emptyMap());
    }

    public JsonNode get(String key) {
        return map().getOrDefault(key, NULL);
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        JsonWriter writer = new JsonWriter(sb);
        try {
            writer.write(this);
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object o, Class<?> type, T def) {
        if (o != null && type.isInstance(o)) {
            return (T) o;
        }
        return def;
    }

    public static JsonNode parse(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            return parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
            return JsonNode.NULL;
        }
    }

    public static JsonNode parse(Reader reader) {
        return new JsonParser(reader).parse();
    }
}
