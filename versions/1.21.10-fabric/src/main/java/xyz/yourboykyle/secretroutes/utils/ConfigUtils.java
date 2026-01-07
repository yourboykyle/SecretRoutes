/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC
 *
 * <DO NOT REMOVE THIS COPYRIGHT NOTICE>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.yourboykyle.secretroutes.utils;

import com.google.gson.*;
import net.minecraft.util.Formatting;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.config.SRMConfig.TextColor;
import xyz.yourboykyle.secretroutes.utils.LogUtils;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static xyz.yourboykyle.secretroutes.Main.COLOR_PROFILE_PATH;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class ConfigUtils {
    static String[] keys = {
            "startTextToggle", "startWaypointColor", "startTextSize",
            "exitTextToggle", "exitWaypointColor", "exitTextSize",
            "interactTextToggle", "interactWaypointColor", "interactTextSize",
            "itemTextToggle", "itemWaypointColor", "itemTextSize",
            "batTextToggle", "batWaypointColor", "batTextSize",
            "etherwarpsTextToggle", "etherwarpsEnumToggle", "etherwarpsWaypointColor", "etherwarpsTextSize",
            "minesTextToggle", "minesEnumToggle", "minesWaypointColor", "minesTextSize",
            "interactTextToggle", "interactsEnumToggle", "interactWaypointColor", "interactsTextSize",
            "superboomsTextToggle", "superboomsEnumToggle", "superboomsWaypointColor", "superboomsTextSize",
            "etherwarpFullBlock", "mineFullBlock", "superboomsFullBlock", "enderpearlFullBlock",
            "secretsItemFullBlock", "secretsInteractFullBlock", "secretsBatFullBlock", "interactsFullBlock",
            "lineColor", "etherWarp", "mine", "interacts", "superbooms", "enderpearls",
            "secretsItem", "secretsInteract", "secretsBat", "pearlLineColor",
            "renderEtherwarps", "renderMines", "renderInteracts", "renderSuperboom", "renderEnderpearls",
            "renderSecretsItem", "renderSecretIteract", "renderSecretBat"
    };

    public static void writeColorConfig(String path) {
        if (!path.endsWith(".json")) {
            path += ".json";
        }

        SRMConfig config = SRMConfig.get();
        Map<String, Object> profileData = new HashMap<>();

        for (String key : keys) {
            try {
                Field field = SRMConfig.class.getDeclaredField(key);
                field.setAccessible(true);
                Object value = field.get(config);

                if (value instanceof Color) {
                    profileData.put(key, ((Color) value).getRGB());
                } else {
                    profileData.put(key, value);
                }
            } catch (NoSuchFieldException e) {
            } catch (Exception e) {
                LogUtils.error(e);
            }
        }

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(profileData);

            File profileFile = new File(COLOR_PROFILE_PATH + File.separator + path);
            profileFile.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(profileFile)) {
                writer.write(json);
                sendChatMessage(Formatting.GREEN + path + Formatting.DARK_GREEN + " color profile created successfully.");
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public static boolean loadColorConfig(String path) {
        if (!path.endsWith(".json")) {
            path += ".json";
        }
        String finalPath = COLOR_PROFILE_PATH + File.separator + path;

        if (!new File(finalPath).exists()) {
            sendChatMessage(Formatting.RED + "Color profile not found, please select different one or create it.");
            return false;
        }

        Gson gson = new GsonBuilder().create();
        FileReader reader;
        try {
            reader = new FileReader(finalPath);
        } catch (FileNotFoundException e) {
            LogUtils.error(e);
            return false;
        }

        JsonObject data = gson.fromJson(reader, JsonObject.class);
        SRMConfig config = SRMConfig.get();

        boolean changed = false;

        for (String key : keys) {
            if (!data.has(key)) continue;

            try {
                Field field = SRMConfig.class.getDeclaredField(key);
                field.setAccessible(true);
                Class<?> type = field.getType();

                if (type == boolean.class) {
                    field.setBoolean(config, data.get(key).getAsBoolean());
                } else if (type == int.class) {
                    field.setInt(config, data.get(key).getAsInt());
                } else if (type == float.class) {
                    field.setFloat(config, data.get(key).getAsFloat());
                } else if (type == String.class) {
                    field.set(config, data.get(key).getAsString());
                } else if (type == Color.class) {
                    field.set(config, parseColor(data.get(key)));
                } else if (type == TextColor.class) {
                    try {
                        String enumName = data.get(key).getAsString();
                        field.set(config, TextColor.valueOf(enumName));
                    } catch (Exception ex) {
                        field.set(config, TextColor.RED);
                    }
                }
                changed = true;
            } catch (NoSuchFieldException e) {
            } catch (Exception e) {
                LogUtils.error(e);
            }
        }

        if (changed) {
            SRMConfig.HANDLER.save();
        }

        return true;
    }

    private static Color parseColor(JsonElement json) {
        try {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
                return new Color(json.getAsInt(), true);
            }

            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                if (jsonObject.has("hsba")) {
                    JsonArray hsba = jsonObject.getAsJsonArray("hsba");
                    float h = hsba.get(0).getAsInt() / 360f;
                    float s = hsba.get(1).getAsInt() / 100f;
                    float b = hsba.get(2).getAsInt() / 100f;
                    int a = hsba.get(3).getAsInt();

                    Color c = Color.getHSBColor(h, s, b);
                    return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
                }
                if (jsonObject.has("value")) {
                    return new Color(jsonObject.get("value").getAsInt(), true);
                }
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
        return Color.WHITE;
    }
}
