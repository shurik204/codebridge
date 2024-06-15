package me.shurik.codebridge.data;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.suggestion.Suggestion;

import java.io.IOException;

public class SuggestionTypeAdapter extends TypeAdapter<Suggestion> {
    @Override
    public void write(JsonWriter out, Suggestion value) throws IOException {
        out.beginObject();
        out.name("text").value(value.getText());
        if (value.getTooltip() != null)
            out.name("tooltip").value(value.getTooltip().getString());
        out.endObject();
    }

    @Override
    public Suggestion read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }
}