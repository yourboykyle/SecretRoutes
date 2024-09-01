package xyz.yourboykyle.secretroutes.utils.autoupdate;

import moe.nea.libautoupdate.UpdateUtils;
import net.minecraft.client.Minecraft;
import xyz.yourboykyle.secretroutes.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static java.lang.Thread.sleep;

public class Updater {
    public static void setUpdateAtShutdown(String jarPath, String currentVersion, String newVersion) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            File logFile = new File(Main.logDir + File.separator + "update.log");
            try {
                String modPath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath()+ File.separator+"mods";
                String currentJarUNFIXED = Updater.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("/", File.separator);
                String currentJar = UpdateUtils.getJarFileContainingClass(Updater.class).getAbsolutePath();

                String newJar = modPath + File.separator + "SecretRoutes-" + newVersion.replaceAll("\"", "").replace("v", "") + ".jar";
                String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                String decodedPath = URLDecoder.decode(path, "UTF-8");
                // Ensure the new jar file is created or cleared if it already exists
                File destinationFile = new File(newJar);
                if (destinationFile.exists()) {
                    destinationFile.delete();
                }
                destinationFile.createNewFile();

                // Ensure the log file is created or cleared if it already exists
                if (logFile.exists()) {
                    logFile.delete();
                }
                logFile.createNewFile();



                writeInfo("Current jar: " + currentJar, logFile);


                writeInfo("Decoded path: " + decodedPath, logFile);
                try {
                    File sourceFile = new File(jarPath);

                    // Copy the data from the specified path to the new jar file
                    try {
                        Files.move(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("File copied successfully from " + jarPath + " to " + newJar);
                        writeInfo("File copied successfully from " + jarPath + " to " + newJar, logFile);
                    } catch (IOException e) {
                        System.err.println("Failed to copy file from " + jarPath + " to " + newJar);
                        writeInfo("Failed to copy file from " + jarPath + " to " + newJar, logFile);
                        e.printStackTrace();
                    }
                    File updateDir = new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + ".autoupdates"+File.separator+"secretroutesmod");
                    if (updateDir.exists()) {
                        if(updateDir.delete()){
                            writeInfo("Update directory deleted successfully", logFile);
                        }else {
                            writeInfo("Failed to delete update directory", logFile);
                        }
                    }else{
                        writeInfo("Update directory does not exist - Something went horribly wrong", logFile);
                    }
                    String osName = System.getProperty("os.name");
                    writeInfo("OS Name: " + osName, logFile);
                    if(osName.toLowerCase().contains("windows")){

                        File batchFile = new File(Main.tmpDir +File.separator+ "update.bat");
                        try (FileWriter batchWriter = new FileWriter(batchFile)) {
                            batchWriter.write("@echo off\n");
                            batchWriter.write("ping localhost -n 5\n");
                            batchWriter.write("del "+currentJar+"\n");
                            batchWriter.write("exit \n");
                        }


                        Runtime.getRuntime().exec("cmd /c start /MIN /B " + batchFile.getAbsolutePath());
                    }else{
                        File shellFile = new File(Main.tmpDir +File.separator+ "update.sh");
                        try (FileWriter shellWriter = new FileWriter(shellFile)) {
                            shellWriter.write("#!/bin/bash\n");
                            shellWriter.write("sleep 5\n");
                            shellWriter.write("rm "+currentJar+"\n");
                            shellWriter.write("exit \n");
                        }

                    }

                    writeInfo(String.valueOf(System.currentTimeMillis()), logFile);
                }catch (Exception e){
                    writeInfo("Error occured during update: " + e.getMessage(), logFile);
                }

            } catch (Exception e) {
                writeInfo("Error occured during update: " + e.getMessage(), logFile);
                e.printStackTrace();
            }
        }));
    }

    private static String getCurrentJarPath() throws URISyntaxException {
        URL jarUrl = Updater.class.getProtectionDomain().getCodeSource().getLocation();
        if (jarUrl != null) {
            return new File(jarUrl.toURI()).getPath();
        } else {
            throw new URISyntaxException("null", "Unable to get JAR file path");
        }
    }


    public static void writeInfo(String msg, File logFile){
        try{
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(msg + "\n");
            fw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
