package xyz.yourboykyle.secretroutes.utils;

import cc.polyfrost.oneconfig.config.core.OneColor;
import com.google.gson.*;

import java.lang.reflect.Type;

public class OneColorDeserializer implements JsonDeserializer<OneColor> {
    @Override
    public OneColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray hsba = jsonObject.getAsJsonArray("hsba");
        float hue = hsba.get(0).getAsFloat();
        float saturation = hsba.get(1).getAsFloat();
        float brightness = hsba.get(2).getAsFloat();
        float alpha = hsba.get(3).getAsFloat();
        return new OneColor(hue, saturation, brightness, alpha);
    }
}