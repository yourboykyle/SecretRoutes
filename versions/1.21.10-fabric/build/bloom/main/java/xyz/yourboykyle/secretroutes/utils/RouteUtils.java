//#if FORGE && MC == 1.8.9
//$$ // TODO: update this file for multi versioning (1.8.9 -> 1.21.10)
//$$ package xyz.yourboykyle.secretroutes.utils;
//$$
//$$ import com.google.gson.Gson;
//$$ import com.google.gson.GsonBuilder;
//$$ import com.google.gson.JsonObject;
//$$ import xyz.yourboykyle.secretroutes.Main;
//$$
//$$ import java.io.File;
//$$ import java.io.FileNotFoundException;
//$$ import java.io.FileReader;
//$$ import java.net.URL;
//$$
//$$ public class RouteUtils {
//$$
//$$
//$$     public static void checkRoutesFiles(){
//$$         LogUtils.info("Checking routes files...");
//$$         String routesDirectory = Main.CONFIG_FOLDER_PATH+ File.separator + "routes";
//$$         checkFile(routesDirectory+File.separator+"routes.json", "routes");
//$$         checkFile(routesDirectory+File.separator+"pearlroutes.json", "pearlroutes");
//$$     }
//$$     public static void checkFile(String path, String name){
//$$         File file = new File(path);
//$$         if(file.exists()){
//$$             Gson gson = new GsonBuilder().create();
//$$             JsonObject data = new JsonObject();
//$$             try {
//$$                 FileReader reader = new FileReader(file);
//$$
//$$                 data = gson.fromJson(reader, JsonObject.class);
//$$
//$$             }catch (FileNotFoundException ignored){
//$$             }catch (Exception e){
//$$                 LogUtils.error(e);
//$$             }
//$$             if(data.has("Version")){
//$$                 try{
//$$                     String version = data.get("Version").getAsString();
//$$                     Boolean tmp = VersionUtils.isLower(version);
//$$                     if(tmp != null && tmp) {
//$$                         LogUtils.info("Updating file: " + file.getName());
//$$                         if(name.equals("routes")){
//$$                             updateRoutes();
//$$                         }else if(name.equals("pearlroutes")){
//$$                             updatePearlRoutes();
//$$                         }
//$$                     }
//$$                 }catch (Exception e){
//$$                     LogUtils.error(e);
//$$                 }
//$$             }else{
//$$                 LogUtils.info("File does not contain version: " + file.getName());
//$$                 if(name.equals("routes")){
//$$                     updateRoutes();
//$$                 }else if(name.equals("pearlroutes")){
//$$                     updatePearlRoutes();
//$$                 }
//$$             }
//$$         }else{
//$$             LogUtils.info("File does not exist: " + file.getName());
//$$             if(name.equals("routes")){
//$$                 updateRoutes();
//$$             }else if(name.equals("pearlroutes")){
//$$                 updatePearlRoutes();
//$$             }
//$$         }
//$$     }
//$$     public static void updateRoutes(File configFile) {
//$$         try {
//$$             LogUtils.info("Downloading routes.json...");
//$$             URL url = new URL("https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/routes.json");
//$$             FileUtils.downloadFile(configFile, url);
//$$
//$$         }catch(Exception e){
//$$             LogUtils.error(e);
//$$         }
//$$     }
//$$
//$$     public static void updateRoutes() {
//$$         File configFile = new File(Main.ROUTES_PATH + File.separator + "routes.json");
//$$         try {
//$$             LogUtils.info("Downloading routes.json...");
//$$             URL url = new URL("https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/routes.json");
//$$             FileUtils.downloadFile(configFile, url);
//$$
//$$         } catch (Exception e){
//$$             LogUtils.error(e);
//$$         }
//$$     }
//$$     public static void updatePearlRoutes() {
//$$         File configFile = new File(Main.ROUTES_PATH + File.separator + "pearlroutes.json");
//$$         try {
//$$             LogUtils.info("Downloading pearlroutes.json...");
//$$             URL url = new URL("https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/pearlroutes.json");
//$$             FileUtils.downloadFile(configFile, url);
//$$         } catch (Exception e){
//$$             LogUtils.error(e);
//$$         }
//$$     }
//$$ }
//#endif
