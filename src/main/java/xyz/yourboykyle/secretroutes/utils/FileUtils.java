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

import xyz.yourboykyle.secretroutes.Main;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
}