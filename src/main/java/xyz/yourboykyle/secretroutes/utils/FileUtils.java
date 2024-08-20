package xyz.yourboykyle.secretroutes.utils;

import xyz.yourboykyle.secretroutes.Main;

import java.awt.*;
import java.io.File;
import java.io.IOException;
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