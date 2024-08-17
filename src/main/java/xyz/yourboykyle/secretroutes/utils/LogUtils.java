package xyz.yourboykyle.secretroutes.utils;

import xyz.yourboykyle.secretroutes.Main;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtils {
    public static void info(String msg) {
        appendToFile("====================\n[INFO] " + msg);
    }

    public static void error(Exception error) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        String stackTrace = sw.toString();

        appendToFile("====================\n[ERROR] " + error.getMessage() + "\n" + stackTrace);
    }

    public static void appendToFile(String msg) {
        try {
            FileWriter fw = new FileWriter(Main.outputLogs, true);
            fw.write(msg + "\n");
            fw.close();
        } catch (Exception e) {
            System.out.println("Secret Routes Logging Exception:");
            e.printStackTrace();
        }
    }
}