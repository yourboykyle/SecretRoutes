//#if FABRIC
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
import net.fabricmc.loader.api.FabricLoader;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.config.SRMConfig.TextColor;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtils {

    public static final File COLOR_PROFILE_DIR = FabricLoader.getInstance().getConfigDir().resolve("SecretRoutes/colorprofiles").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static String[] keys = {
            // Text Colors
            "startWaypointColor", "exitWaypointColor",
            "interactWaypointColor",
            "itemWaypointColor", "batWaypointColor",
            "etherwarpsWaypointColor", "minesWaypointColor",
            "interactsWaypointColor",
            "superboomsWaypointColor", "enderpearlWaypointColor",

            // Sizes
            "startTextSize", "exitTextSize",
            "interactTextSize",
            "interactsTextSize",
            "itemTextSize", "batTextSize", "etherwarpsTextSize",
            "minesTextSize", "superboomsTextSize", "enderpearlTextSize",
            "pearlLineWidth",

            // Toggles
            "startTextToggle", "exitTextToggle",
            "interactTextToggle",
            "interactsTextToggle",
            "itemTextToggle", "batTextToggle", "etherwarpsTextToggle", "minesTextToggle",
            "superboomsTextToggle", "enderpearlTextToggle",

            "interactsEnumToggle", "etherwarpsEnumToggle", "minesEnumToggle", "superboomsEnumToggle", "enderpearlEnumToggle",

            "etherwarpFullBlock", "mineFullBlock", "superboomsFullBlock", "enderpearlFullBlock",
            "secretsItemFullBlock", "secretsInteractFullBlock", "secretsBatFullBlock", "interactsFullBlock",

            "renderEtherwarps", "renderMines", "renderInteracts", "renderSuperboom",
            "renderEnderpearls", "renderSecretsItem", "renderSecretIteract", "renderSecretBat",

            // Colors
            "lineColor", "etherWarp", "mine", "interacts", "superbooms", "enderpearls",
            "secretsItem", "secretsInteract", "secretsBat", "pearlLineColor",
            "secondStepEtherWarp", "secondStepMine", "secondStepInteracts", "secondStepSuperbooms",
            "secondStepEnderpearls", "secondStepSecretsItem", "secondStepSecretsInteract", "secondStepSecretsBat"
    };

    public static void init() {
        if (!COLOR_PROFILE_DIR.exists()) COLOR_PROFILE_DIR.mkdirs();
    }

    public static void writeColorConfig(String path) {
        if (!path.endsWith(".json")) path += ".json";

        SRMConfig config = SRMConfig.get();
        Map<String, Object> profileData = new HashMap<>();

        for (String key : keys) {
            try {
                Field field = SRMConfig.class.getField(key);
                Object value = field.get(config);

                if (value instanceof Color c) {
                    profileData.put(key, c.getRGB());
                } else if (value instanceof Enum) {
                    profileData.put(key, value.toString());
                } else {
                    profileData.put(key, value);
                }
            } catch (Exception e) {
                LogUtils.error(new IOException("Failed to save config field: " + key));
            }
        }

        try (FileWriter writer = new FileWriter(new File(COLOR_PROFILE_DIR, path))) {
            GSON.toJson(profileData, writer);
            ChatUtils.sendChatMessage("§aColor profile '" + path + "' saved successfully.");
        } catch (Exception e) {
            LogUtils.error(e);
            ChatUtils.sendChatMessage("§cError saving profile to disk.");
        }
    }

    public static boolean loadColorConfig(String path) {
        if (!path.endsWith(".json")) path += ".json";
        File file = new File(COLOR_PROFILE_DIR, path);

        if (!file.exists()) {
            ChatUtils.sendChatMessage("§cColor profile not found: " + path);
            return false;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject data = GSON.fromJson(reader, JsonObject.class);
            SRMConfig config = SRMConfig.get();
            boolean changed = false;

            for (String key : keys) {
                try {
                    Field field = SRMConfig.class.getField(key);
                    Class<?> type = field.getType();
                    JsonElement element = data.get(key);

                    if (element == null && type == TextColor.class) {
                        JsonElement indexElement = data.get(key + "Index");

                        if (indexElement == null && key.equals("interactsWaypointColor")) {
                            indexElement = data.get("interactWaypointColorIndex");
                        }

                        if (indexElement != null) {
                            field.set(config, getTextColorFromLegacyIndex(indexElement.getAsInt()));
                            changed = true;
                            continue;
                        }
                    }

                    if (element == null) continue;

                    if (type == boolean.class) {
                        field.setBoolean(config, element.getAsBoolean());
                    } else if (type == float.class) {
                        field.setFloat(config, element.getAsFloat());
                    } else if (type == int.class) {
                        field.setInt(config, element.getAsInt());
                    } else if (type == String.class) {
                        field.set(config, element.getAsString());
                    } else if (type == Color.class) {
                        field.set(config, parseColor(element));
                    } else if (type == TextColor.class) {
                        try {
                            field.set(config, TextColor.valueOf(element.getAsString()));
                        } catch (Exception e) {
                            field.set(config, TextColor.RED);
                        }
                    }
                    changed = true;

                } catch (NoSuchFieldException e) {
                } catch (Exception e) {
                    LogUtils.error(new IOException("Error loading field " + key + ": " + e.getMessage()));
                }
            }

            if (changed) {
                SRMConfig.HANDLER.save();
                ChatUtils.sendChatMessage("§aLoaded color profile: " + path);
                return true;
            } else {
                ChatUtils.sendChatMessage("§eProfile loaded, but no matching settings found.");
            }

        } catch (Exception e) {
            LogUtils.error(e);
            ChatUtils.sendChatMessage("§cError reading profile file.");
        }
        return false;
    }

    private static Color parseColor(JsonElement json) {
        try {
            if (json.isJsonPrimitive()) {
                return new Color(json.getAsInt(), true);
            }
            if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                if (obj.has("hsba")) {
                    JsonArray hsba = obj.getAsJsonArray("hsba");
                    float h = hsba.get(0).getAsInt() / 360f;
                    float s = hsba.get(1).getAsInt() / 100f;
                    float b = hsba.get(2).getAsInt() / 100f;
                    int a = hsba.get(3).getAsInt();

                    Color c = Color.getHSBColor(h, s, b);
                    return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
                }
                if (obj.has("value")) {
                    return new Color(obj.get("value").getAsInt(), true);
                }
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
        return Color.WHITE;
    }

    private static TextColor getTextColorFromLegacyIndex(int index) {
        return switch (index) {
            case 0 -> TextColor.BLACK;
            case 1 -> TextColor.DARK_BLUE;
            case 2 -> TextColor.DARK_GREEN;
            case 3 -> TextColor.DARK_AQUA;
            case 4 -> TextColor.DARK_RED;
            case 5 -> TextColor.DARK_PURPLE;
            case 6 -> TextColor.GOLD;
            case 7 -> TextColor.GRAY;
            case 8 -> TextColor.DARK_GRAY;
            case 9 -> TextColor.BLUE;
            case 10 -> TextColor.GREEN;
            case 11 -> TextColor.AQUA;
            case 13 -> TextColor.LIGHT_PURPLE;
            case 14 -> TextColor.YELLOW;
            case 15 -> TextColor.WHITE;
            default -> TextColor.RED;
        };
    }
}
//#endif
