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

import org.apache.commons.io.IOUtils;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<String> getFileNames(String path) {
        File[] files = new File(path).listFiles();
        List<String> list = new ArrayList<>();
        for(File f : files){
            if(f.getName().contains(".json")){ list.add(f.getName());}
        }
        return list;
    }
    public static List<String> getRouteFileNames() {
        return getFileNames(Main.ROUTES_PATH);
    }

    public static String[] getRouteFileNamesArray() {
        List<String> routeFileNames = getRouteFileNames();
        return routeFileNames.toArray(new String[0]);
    }

    public static boolean doesFileExist(String path){
        return new File(path).exists();
    }

    public static File promptUserForFile() {
        Frame frame = new Frame();
        frame.setVisible(false);
        frame.setAlwaysOnTop(true);

        FileDialog fileDialog = new FileDialog(frame, "Select a file", FileDialog.LOAD);

        fileDialog.setVisible(true);

        String filePath = fileDialog.getFile();
        String directoryPath = fileDialog.getDirectory();

        frame.dispose();

        if (filePath != null) {
            return new File(directoryPath, filePath);
        } else {
            return null;
        }
    }

    public static void copyFileToDirectory(File sourceFile, String targetDirectoryPath) {
        Path targetDirectory = new File(targetDirectoryPath).toPath();

        try {
            if (!Files.exists(targetDirectory)) {
                Files.createDirectories(targetDirectory);
            }

            Path targetFilePath = targetDirectory.resolve(sourceFile.getName());

            Files.copy(sourceFile.toPath(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LogUtils.error(e);
        }
    }

    public static void copyFileToRoutesDirectory(){
        try{
            String filename = SRMConfig.copyFileName;
            copyAndRename(System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "routes.json", Main.ROUTES_PATH, filename);
        }catch (Exception e){
            LogUtils.error(e);
        }
    }

    public static void copyAndRename(String sourceFile, String targetDirectoryPath, String targetFileName){
        Path sourcePath = Paths.get(sourceFile);
        Path destinationPath = Paths.get(targetDirectoryPath, targetFileName);

        try {
            // Create the destination directory if it doesn't exist
            Files.createDirectories(destinationPath.getParent());

            // Copy the target file to the new location with the given name
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully to: " + destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(File outputFile, URL url) throws IOException {
        HttpsURLConnection connection =  (HttpsURLConnection) url.openConnection();

        connection.setSSLSocketFactory(SSLUtils.getSSLSocketFactory());

        InputStream inputStream = connection.getInputStream();
        OutputStream outputStream = Files.newOutputStream(outputFile.toPath());
        IOUtils.copy(inputStream, outputStream);
        outputStream.close();
        inputStream.close();
    }
}
//#endif
