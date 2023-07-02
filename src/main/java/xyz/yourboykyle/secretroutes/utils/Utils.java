package xyz.yourboykyle.secretroutes.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class Utils {
    public static Map.Entry<String, JsonElement> getLastEntry(JsonObject jsonObject) {
        Map.Entry<String, JsonElement> lastEntry = null;
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            lastEntry = entry;
        }
        return lastEntry;
    }
}
