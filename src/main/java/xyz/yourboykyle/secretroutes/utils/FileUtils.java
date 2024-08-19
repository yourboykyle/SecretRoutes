package xyz.yourboykyle.secretroutes.utils;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.Constant;
import xyz.yourboykyle.secretroutes.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<String> getFileNames(String path) {
        File[] files = new File(path).listFiles();
        List<String> list = new ArrayList<>();
        for(File f : files){
            list.add(f.getName());
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


}
