package xyz.yourboykyle.secretroutes.utils;

import xyz.yourboykyle.secretroutes.Main;

public class VersionUtils {
    public static Boolean isLower(String version) {
        String[] current = Main.VERSION.split("\\.");
        String[] check = version.split("\\.");
        if (check.length != 3) {
            return null;
        }
        for (int i = 0; i < current.length && i < check.length; i++) {
            if (Integer.parseInt(current[i]) < Integer.parseInt(check[i])) {
                return false;
            } else if (Integer.parseInt(current[i]) > Integer.parseInt(check[i])) {
                return true;
            }
        }
        return true;
    }
}
