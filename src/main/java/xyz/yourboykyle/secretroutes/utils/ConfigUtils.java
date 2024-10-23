/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2024 yourboykyle & R-aMcC
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

import cc.polyfrost.oneconfig.config.core.OneColor;
import com.google.gson.*;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static xyz.yourboykyle.secretroutes.Main.COLOR_PROFILE_PATH;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class ConfigUtils {
    static String[] keys = {
            "startTextToggle", "startWaypointColorIndex", "startTextSize",
            "exitTextToggle", "exitWaypointColorIndex", "exitTextSize",
            "interactTextToggle", "interactWaypointColorIndex", "interactTextSize",
            "itemTextToggle", "itemWaypointColorIndex", "itemTextSize",
            "batTextToggle", "batWaypointColorIndex", "batTextSize",
            "etherwarpsTextToggle", "etherwarpsEnumToggle", "etherwarpsWaypointColorIndex", "etherwarpsTextSize",
            "minesTextToggle", "minesEnumToggle", "minesWaypointColorIndex", "minesTextSize",
            "interactsTextToggle", "interactsEnumToggle", "interactsWaypointColorIndex", "interactsTextSize",
            "superboomsTextToggle", "superboomsEnumToggle", "superboomsWaypointColorIndex", "superboomsTextSize",
            "enderpearlTextToggle", "enderpearlEnumToggle", "enderpearlWaypointColorIndex", "enderpearlTextSize",
            "etherwarpFullBlock", "mineFullBlock", "superboomsFullBlock", "enderpearlFullBlock",
            "secretsItemFullBlock", "secretsInteractFullBlock", "secretsBatFullBlock", "interactsFullBlock",
            "lineColor", "etherWarp", "mine", "interacts", "superbooms", "enderpearls",
            "secretsItem", "secretsInteract", "secretsBat", "pearlLineColor",
            "renderEtherwarps ", "renderMines", "renderInteracts", "renderSuperboom ", "renderEnderpearls",
            "renderSecretsItem", "renderSecretIteract", "renderSecretBat"
};





    public static void writeColorConfig(String path) {
        if(!path.endsWith(".json")){
            path += ".json";
        }
        Map<String, Object> defaultColors = new HashMap<>();
        for (String key : keys) {
            try {
                defaultColors.put(key,SRMConfig.class.getDeclaredField(key).get(null));
            }catch(Exception e){
                LogUtils.error(e);
            }
        }
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(defaultColors);
            try (FileWriter writer = new FileWriter(COLOR_PROFILE_PATH+ File.separator+path)) {
                writer.write(json);
                sendChatMessage(EnumChatFormatting.GREEN+path+EnumChatFormatting.DARK_GREEN+" color profile created successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            LogUtils.error(e);
        }

    }

    public static boolean loadColorConfig(String path) {
        if (!path.endsWith(".json")) {
            path += ".json";
        }
        String finalPath = COLOR_PROFILE_PATH + File.separator + path;
        if(!new File(finalPath).exists()){
            sendChatMessage(EnumChatFormatting.RED+"Color profile not found, please select different one or create it.");
            return false;
        }
        Gson gson = new GsonBuilder().create();
        FileReader reader = null;
        try{
            reader = new FileReader(finalPath);
        }catch(FileNotFoundException e){
            //This should never happen...
            LogUtils.error(e);
            sendChatMessage("§4 THIS SHOULD NEVER HAVE HAPPENED... (ConfigUtils 123)");
            return false;
        }
        JsonObject data = gson.fromJson(reader, JsonObject.class);

        for (String key : keys) {

            try {
                switch (key) {
                    case "startTextToggle":
                    case "exitTextToggle":
                    case "interactTextToggle":
                    case "itemTextToggle":
                    case "batTextToggle":
                    case "etherwarpsTextToggle":
                    case "minesTextToggle":
                    case "interactsTextToggle":
                    case "superboomsTextToggle":
                    case "enderpearlTextToggle":
                    case "etherwarpFullBlock":
                    case "mineFullBlock":
                    case "superboomsFullBlock":
                    case "enderpearlFullBlock":
                    case "interactsFullBlock":
                    case "secretsItemFullBlock":
                    case "secretsInteractFullBlock":
                    case "secretsBatFullBlock":
                    case "renderEtherwarps ":
                    case "renderMines":
                    case "renderInteracts":
                    case "renderSuperboom":
                    case "renderEnderpearls":
                    case "renderSecretsItem":
                    case "renderSecretIteract":
                    case "renderSecretBat":
                        SRMConfig.class.getDeclaredField(key).setBoolean(null, data.has(key) && data.get(key).getAsBoolean());
                        continue;
                    case "startWaypointColorIndex":
                    case "exitWaypointColorIndex":
                    case "interactWaypointColorIndex":
                    case "itemWaypointColorIndex":
                    case "batWaypointColorIndex":
                    case "etherwarpsWaypointColorIndex":
                    case "minesWaypointColorIndex":
                    case "interactsWaypointColorIndex":
                    case "superboomsWaypointColorIndex":
                    case "enderpearlWaypointColorIndex":
                        SRMConfig.class.getDeclaredField(key).setInt(null, data.has(key) ? data.get(key).getAsInt() : 0);
                        continue;
                    case "startTextSize":
                    case "exitTextSize":
                    case "interactTextSize":
                    case "itemTextSize":
                    case "batTextSize":
                    case "etherwarpsTextSize":
                    case "minesTextSize":
                    case "interactsTextSize":
                    case "superboomsTextSize":
                    case "enderpearlTextSize":
                        SRMConfig.class.getDeclaredField(key).setFloat(null, data.has(key) ? data.get(key).getAsFloat() : 3f);
                        continue;
                    case "lineColor":
                    case "etherWarp":
                    case "mine":
                    case "interacts":
                    case "superbooms":
                    case "enderpearls":
                    case "secretsItem":
                    case "secretsInteract":
                    case "secretsBat":
                    case "pearlLineColor":
                        SRMConfig.class.getDeclaredField(key).set(null, data.has(key) ? parseOneColor(data.get(key)) : new OneColor(255, 255, 255));
                    default:
                        continue;
                }
            }catch (Exception e) {
                LogUtils.error(e);
            }
        }
        return true;
    }

    public static OneColor parseOneColor(JsonElement json){
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
