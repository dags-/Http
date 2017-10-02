package me.dags.http.json;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author dags <dags@dags.me>
 */
public class JsonParser {

    private final Reader reader;
    private char current = (char) -1;
    private int line = 1;
    private int character = 0;

    public JsonParser(Reader reader) {
        this.reader = reader;
    }

    private void next() throws IOException {
        current = (char) reader.read();
        if (current == '\n') {
            line++;
            character = 0;
        } else {
            character++;
        }
    }

    public JsonNode parse() {
        try {
            next();
            return nextElement();
        } catch (Exception e) {
            System.out.printf("Line: %s, Char: %s\n", line, character);
            e.printStackTrace();
            return JsonNode.NULL;
        }
    }

    private JsonNode nextElement() throws IOException {
        while (Character.isWhitespace(current)) {
            next();
        }

        switch (current) {
            case '{':
                return nextObject();
            case '[':
                return nextArray();
            case '"':
                return new JsonNode(nextString());
            case 't':
                reader.skip(4);
                return new JsonNode(true);
            case 'f':
                reader.skip(5);
                return new JsonNode(false);
            case 'n':
                reader.skip(3);
                return JsonNode.NULL;
            default:
                return new JsonNode(nextNumber(current));
        }
    }

    private JsonNode nextObject() throws IOException {
        Map<String, JsonNode> map = new LinkedHashMap<>();
        while (true) {
            next();

            if (current == ',' || Character.isWhitespace(current)) {
                continue;
            }

            if (current == '"') {
                String key = nextString();
                readUntil(':');
                next();
                map.put(key, nextElement());
            }

            if (current == '}') {
                break;
            }
        }
        return new JsonNode(map);
    }

    private JsonNode nextArray() throws IOException {
        List<JsonNode> list = new LinkedList<>();
        while (true) {
            next();
            if (current == ',' || Character.isWhitespace(current)) {
                continue;
            }

            if (current != ']') {
                list.add(nextElement());
            }

            if (current == ']') {
                break;
            }
        }
        return new JsonNode(list);
    }

    private String nextString() throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        while (true) {
            next();

            if (!escaped && current == '"') {
                break;
            }

            escaped = current == '\\';

            if (!escaped) {
                sb.append(current);
            }
        }
        return sb.toString();
    }

    private Number nextNumber(char start) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(start);

        boolean decimal = false;

        while (true) {
            next();
            if (!isDigit(current)) {
                break;
            }
            if (current == '.') {
                decimal = true;
            }
            sb.append(current);
        }

        if (decimal) {
            return Double.parseDouble(sb.toString());
        }

        return Long.parseLong(sb.toString());
    }

    private void readUntil(char end) throws IOException {
        while (true) {
            next();
            if (current == end) {
                return;
            }
        }
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c) || c == '.' || c == '-' || c == '+' || c == 'e' ||  c == 'E';
    }
}
