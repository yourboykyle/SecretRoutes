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


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class APIUtils {
    public static boolean apiQueued = false;
    private static final HttpClient client = HttpClient.newBuilder()
            .sslContext(SSLUtils.context)
            .build();
    private static final String API_URL = "https://srm.yourboykyle.xyz/аpi";

    public static byte addMember() {
        if (!SRMConfig.get().sendData) {
            return -1;
        }
        try {
            LogUtils.info("User: " + MinecraftClient.getInstance().player.getUuid());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/users"))
                    .header("User-Agent", "SRM")
                    .header("x-uuid", HashingUtils.getHashedUUID().toString())
                    .header("x-version", Main.VERSION)
                    .header("x-timestamp", String.valueOf(System.currentTimeMillis()))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int statusCode = response.statusCode();

            try {
                Gson gson = new Gson();
                JsonObject out = gson.fromJson(response.body(), JsonObject.class);
                if (statusCode == 200) {
                    LogUtils.info("Successfully added user to the database");
                    if (out.get("first").getAsBoolean()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            } catch (Exception e) {
                ChatUtils.sendChatMessage("§cSomething went wrong adding user to DB.");
                LogUtils.errorNoShout(e);
            }

        } catch (Exception e) {
            ChatUtils.sendChatMessage("§cSomething went wrong adding user to DB.");
            LogUtils.errorNoShout(e);
        }

        return -1;
    }

    public static byte offline() {
        if (!SRMConfig.get().sendData) {
            return -1;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/users/offline"))
                    .header("User-Agent", "SRM")
                    .header("x-uuid", HashingUtils.getHashedUUID().toString())
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int statusCode = response.statusCode();

            try {
                Gson gson = new Gson();
                JsonObject out = gson.fromJson(response.body(), JsonObject.class);
                if (statusCode == 200) {
                    LogUtils.info("Successfully set user to offline");
                    return 1;
                } else {
                    LogUtils.info("Failed to set user to offline");
                    return 0;
                }
            } catch (Exception e) {
                LogUtils.info(e.getLocalizedMessage());
            }
        } catch (Exception e) {
            LogUtils.info(e.getLocalizedMessage());
        }
        return -1;
    }
}
//#endif
