package xyz.yourboykyle.secretroutes.utils;

import cc.polyfrost.oneconfig.config.core.OneColor;
import com.google.gson.*;

import java.lang.reflect.Type;

public class OneColorDeserializer implements JsonDeserializer<OneColor> {
    @Override
    public OneColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray hsba = jsonObject.getAsJsonArray("hsba");
        int hue = hsba.get(0).getAsInt();
        int saturation = hsba.get(1).getAsInt();
        int brightness = hsba.get(2).getAsInt();
        int alpha = hsba.get(3).getAsInt();
        int chromaSpeed = jsonObject.get("dataBit").getAsInt();
        return new OneColor(hue, saturation, brightness, alpha, chromaSpeed);
    }
}