package me.shurik.codebridge;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import me.shurik.codebridge.data.WebSocketException;

import java.io.IOException;
import java.io.StringReader;

public class Utils {
    private static final TypeAdapter<JsonObject> strictGsonObjectAdapter =
            new Gson().getAdapter(JsonObject.class);

    public static JsonObject parseStrict(String json) {
        // https://stackoverflow.com/questions/43233898/how-to-check-if-json-is-valid-in-java-using-gson/47890960#47890960
        try {
            //return strictGsonObjectAdapter.fromJson(json); // this still allows multiple top level values (
            try (JsonReader reader = new JsonReader(new StringReader(json))) {
                JsonObject result = strictGsonObjectAdapter.read(reader);
                reader.hasNext(); // throws on multiple top level values
                return result;
            }
        } catch (IOException e) {
            throw new JsonSyntaxException(e);
        }
    }

    public static <T> T notNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }
}
