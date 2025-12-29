//#if FORGE && MC == 1.8.9
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
import net.minecraft.client.Minecraft;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class APIUtils {
    private static String API_URL = "https://srm.yourboykyle.xyz/аpi";
    static CloseableHttpClient client = HttpClients.custom().setUserAgent("SRM").setSslcontext(SSLUtils.context).build();
    public static boolean apiQueued = false;

    public static byte addMember(){
        if(!SRMConfig.sendData){
            return -1;
        }
        try{
            LogUtils.info("User: "+Minecraft.getMinecraft().thePlayer.getUniqueID());
            HttpPost request = new HttpPost(new URL(API_URL+"/users").toURI());
            request.setProtocolVersion(HttpVersion.HTTP_1_1);
            request.setHeader("x-uuid", HashingUtils.getHashedUUID().toString());
            request.setHeader("x-version", Main.VERSION);
            request.setHeader("x-timestamp", String.valueOf(System.currentTimeMillis()));

            try(CloseableHttpResponse response = client.execute(request)){
                HttpEntity entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8))) {
                    Gson gson = new Gson();
                    JsonObject out = gson.fromJson(in, JsonObject.class);
                    if(statusCode == 200){
                        LogUtils.info("Successfully added user to the database");
                        if(out.get("first").getAsBoolean()){
                            return 1;
                        }else{
                            return 0;
                        }
                    }
                }catch (Exception e){
                    ChatUtils.sendChatMessage("§cSomething went wrong adding user to DB.");
                    LogUtils.errorNoShout(e);
                }

            }catch (Exception e){
                ChatUtils.sendChatMessage("§cSomething went wrong adding user to DB.");
                LogUtils.errorNoShout(e);
            }

        }catch (Exception e){
            ChatUtils.sendChatMessage("§cSomething went wrong adding user to DB.");
            LogUtils.errorNoShout(e);
        }

        return -1;
    }
    public static byte offline() {
        if(!SRMConfig.sendData){
            return -1;
        }
        try {
            HttpPatch request = new HttpPatch(new URL(API_URL + "/users/offline").toURI());
            request.setProtocolVersion(HttpVersion.HTTP_1_1);
            request.setHeader("x-uuid", HashingUtils.getHashedUUID().toString());
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8))) {
                    Gson gson = new Gson();
                    JsonObject out = gson.fromJson(in, JsonObject.class);
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
        } catch (Exception e) {
            LogUtils.info(e.getLocalizedMessage());
        }
        return -1;
    }
}
//#endif