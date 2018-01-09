package me.dags.http.json;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author dags <dags@dags.me>
 */
public class JsonWriter {

    private final Appendable appendable;
    private final int spaces = 2;
    private int indent = 0;

    public JsonWriter(Appendable appendable) {
        this.appendable = appendable;
    }

    public void write(JsonNode node) throws IOException {
        if (node.isObject()) {
            writeObject(node);
            return;
        }

        if (node.isArray()) {
            writeArray(node);
            return;
        }

        if (node.isString()) {
            writeString(node.string());
            return;
        }

        appendable.append(node.string());
    }

    private void writeObject(JsonNode node) throws IOException {
        Iterator<Map.Entry<String, JsonNode>> iterator = node.map().entrySet().iterator();
        appendable.append('{');
        incIndent();

        if (iterator.hasNext()) {
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> next = iterator.next();
                newLine();
                indent();
                writeString(next.getKey());
                appendable.append(':').append(' ');
                write(next.getValue());
                if (iterator.hasNext()) {
                    appendable.append(',');
                }
            }
            newLine();
            decIndent();
            indent();
        } else {
            decIndent();
        }
        appendable.append('}');
    }

    private void writeArray(JsonNode node) throws IOException {
        Iterator<JsonNode> iterator = node.list().iterator();
        appendable.append('[');
        incIndent();

        if (iterator.hasNext()) {
            while (iterator.hasNext()) {
                newLine();
                indent();
                write(iterator.next());
                if (iterator.hasNext()) {
                    appendable.append(',');
                }
            }
            newLine();
            decIndent();
            indent();
        } else {
            decIndent();
        }
        appendable.append(']');
    }

    private void writeString(String in) throws IOException {
        appendable.append('"').append(in).append('"');
    }

    private void incIndent() {
        indent++;
    }

    private void decIndent() {
        indent--;
    }

    private void indent() throws IOException {
        for (int i = indent * spaces; i > 0; i--) {
            appendable.append(' ');
        }
    }

    private void newLine() throws IOException {
        appendable.append('\n');
    }
}
