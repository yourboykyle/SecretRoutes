//#if FABRIC
package xyz.yourboykyle.secretroutes.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import xyz.yourboykyle.secretroutes.Main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RouteUtils {

    private static final String BASE_URL = "https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/";
    private static final Gson GSON = new Gson();

    public static void checkRoutesFiles() {
        LogUtils.info("Checking routes files...");
        String routesDirectory = Main.CONFIG_FOLDER_PATH + File.separator + "routes";

        File dir = new File(routesDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        checkAndUpdateFile(routesDirectory + File.separator + "fowroutes.json", "fowroutes.json", "FlameOfWar Routes");
        checkAndUpdateFile(routesDirectory + File.separator + "3ppopkaroutes.json", "3ppopkaroutes.json", "3ppopka Routes");
    }

    private static void checkAndUpdateFile(String localPath, String fileName, String displayName) {
        File localFile = new File(localPath);
        String localVersion = getLocalVersion(localFile);

        try {
            URL url = new URL(BASE_URL + fileName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == 200) {
                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonObject remoteData = GSON.fromJson(reader, JsonObject.class);

                    if (remoteData != null && remoteData.has("Version")) {
                        String remoteVersion = remoteData.get("Version").getAsString();

                        if (localVersion == null || isRemoteNewer(localVersion, remoteVersion)) {
                            LogUtils.info("Updating " + displayName + " from version " +
                                    (localVersion == null ? "None" : localVersion) + " to " + remoteVersion);

                            try (Writer writer = new FileWriter(localFile)) {
                                GSON.toJson(remoteData, writer);
                            }
                        } else {
                            LogUtils.info(displayName + " is up to date (Version: " + localVersion + ").");
                        }
                    } else {
                        LogUtils.info("Remote file " + fileName + " is missing a 'Version' key.");
                    }
                }
            } else {
                LogUtils.info("Failed to fetch remote routes for " + displayName + ". HTTP Code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            LogUtils.info("Error while checking for " + displayName + " updates: " + e.getMessage());
        }
    }

    private static String getLocalVersion(File file) {
        if (!file.exists()) return null;

        try (FileReader reader = new FileReader(file)) {
            JsonObject data = GSON.fromJson(reader, JsonObject.class);
            if (data != null && data.has("Version")) {
                return data.get("Version").getAsString();
            }
        } catch (Exception e) {
            LogUtils.info("Failed to read local version from " + file.getName() + ": " + e.getMessage());
        }
        return null;
    }

    private static boolean isRemoteNewer(String localVersion, String remoteVersion) {
        try {
            String[] localParts = localVersion.split("\\.");
            String[] remoteParts = remoteVersion.split("\\.");
            int length = Math.max(localParts.length, remoteParts.length);

            for (int i = 0; i < length; i++) {
                int localPart = i < localParts.length ? Integer.parseInt(localParts[i]) : 0;
                int remotePart = i < remoteParts.length ? Integer.parseInt(remoteParts[i]) : 0;

                if (remotePart > localPart) return true;
                if (remotePart < localPart) return false;
            }
        } catch (NumberFormatException e) {
            LogUtils.info("Error parsing version numbers: local=" + localVersion + ", remote=" + remoteVersion);
            return !localVersion.equals(remoteVersion);
        }
        return false;
    }
}
//#endif