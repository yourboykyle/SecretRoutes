package xyz.yourboykyle.secretroutes.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {
    public static JsonObject toJsonObject(String string){
        JsonParser parser = new JsonParser();
        return parser.parse(string).getAsJsonObject();
    }
}
