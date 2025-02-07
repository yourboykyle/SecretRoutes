package xyz.yourboykyle.secretroutes.utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import xyz.yourboykyle.secretroutes.Main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class APIUtils {
    private static final String API_URL = "https://srm.yourboykyle.xyz/аpi";
    static CloseableHttpClient client = HttpClients.custom().setUserAgent("SRM").setSslcontext(SSLUtils.context).build();

    public static byte addMember(){
        try{
            HttpPost request = new HttpPost(new URL(API_URL+"/users").toURI());
            request.setProtocolVersion(HttpVersion.HTTP_1_1);
            request.setHeader("x-uuid", Minecraft.getMinecraft().thePlayer.getUniqueID().toString());
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
                    LogUtils.error(e);
                }

            }catch (Exception e){
                LogUtils.error(e);
            }

        }catch (Exception e){
            LogUtils.error(e);
        }

        return -1;
    }
    public static byte offline() {
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
