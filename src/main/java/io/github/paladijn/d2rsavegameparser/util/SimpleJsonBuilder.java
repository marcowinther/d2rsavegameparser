package io.github.paladijn.d2rsavegameparser.util;

public class SimpleJsonBuilder {
    private final StringBuilder sb = new StringBuilder();
    private boolean hasElements = false;

    public SimpleJsonBuilder beginObject() {
        sb.append("{");
        hasElements = false;
        return this;
    }

    public SimpleJsonBuilder endObject() {
        sb.append("}");
        hasElements = true;
        return this;
    }

    public SimpleJsonBuilder beginArray(String name) {
        addCommaIfNeeded();
        sb.append('"').append(name).append('"').append(": [");
        hasElements = false;
        return this;
    }

    public SimpleJsonBuilder endArray() {
        sb.append("]");
        hasElements = true;
        return this;
    }

    public SimpleJsonBuilder addField(String name, String value) {
        addCommaIfNeeded();
        sb.append('"').append(name).append('"').append(": ")
          .append('"').append(escape(value)).append('"');
        hasElements = true;
        return this;
    }

    public SimpleJsonBuilder addField(String name, int value) {
        addCommaIfNeeded();
        sb.append('"').append(name).append('"').append(": ")
          .append(value);
        hasElements = true;
        return this;
    }

    public SimpleJsonBuilder addField(String name, boolean value) {
        addCommaIfNeeded();
        sb.append('"').append(name).append('"').append(": ")
          .append(value);
        hasElements = true;
        return this;
    }

    public SimpleJsonBuilder addRawField(String name, String rawJson) {
        addCommaIfNeeded();
        sb.append('"').append(name).append('"').append(": ")
          .append(rawJson);
        hasElements = true;
        return this;
    }

    public SimpleJsonBuilder addRawValue(String rawJson) {
        addCommaIfNeeded();
        sb.append(rawJson);
        hasElements = true;
        return this;
    }

    private void addCommaIfNeeded() {
        if (hasElements) {
            sb.append(", ");
        }
    }

    private String escape(String value) {
        return value.replace("\"", "\\\"");
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
