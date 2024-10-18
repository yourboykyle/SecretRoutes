package xyz.yourboykyle.secretroutes.utils;

import cc.polyfrost.oneconfig.config.core.OneColor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.commands.SRM;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.MapUtils;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class SecretUtils {
    public static JsonArray secrets = null;
    public static PrintingUtils xPrinter = new PrintingUtils();
    public static PrintingUtils yPrinter = new PrintingUtils();
    public static PrintingUtils zPrinter = new PrintingUtils();


    public static void renderSecrets(RenderWorldLastEvent event){
        Main.checkRoomData();
        String roomName = Main.currentRoom.name;
        try{
            try (Reader reader  = new InputStreamReader(Main.class.getResourceAsStream("/assets/roomdetection/secretlocations.json"))) {
                JsonParser parser = new JsonParser();
                JsonObject object = parser.parse(reader).getAsJsonObject();
                if(object.has(roomName)){
                    secrets = object.getAsJsonArray(roomName);

                }
            }
        }catch (Exception e){
            LogUtils.error(e);
        }

        if(secrets != null && SRMConfig.allSecrets){
            for(JsonElement secret : secrets){

                JsonObject secretInfos = secret.getAsJsonObject();
                String name = secretInfos.get("secretName").getAsString();
                if(name.contains("Chest") || name.contains("Bat") || name.contains("Wither Essence") || name.contains("Lever") || name.contains("Item")){
                    int xPos = secretInfos.get("x").getAsInt();
                    int yPos = secretInfos.get("y").getAsInt();
                    int zPos = secretInfos.get("z").getAsInt();
                    Main.checkRoomData();
                    Triple<Double, Double, Double> abs = MapUtils.relativeToActual(xPos, yPos, zPos, RoomDetection.roomDirection, RoomDetection.roomCorner);
                    OneColor color = new OneColor(255, 255, 255);
                    if(name.contains("Chest") || name.contains("Wither Essence")) {
                        color = SRMConfig.secretsInteract;
                        if(SRMConfig.interactTextToggle){
                        SecretRoutesRenderUtils.drawText(abs.getOne(), abs.getTwo(), abs.getThree(), SecretRoutesRenderUtils.getTextColor(SRMConfig.interactWaypointColorIndex) + "Interact", SRMConfig.interactTextSize, event.partialTicks);
                        }
                    }else if(name.contains("Bat")){
                        color = SRMConfig.secretsBat;
                        if(SRMConfig.batTextToggle) {
                            SecretRoutesRenderUtils.drawText(abs.getOne(), abs.getTwo(), abs.getThree(), SecretRoutesRenderUtils.getTextColor(SRMConfig.batWaypointColorIndex) + "Bat", SRMConfig.batTextSize, event.partialTicks);
                        }
                    }else if (name.contains("Lever")){
                        color = SRMConfig.interacts;
                        if(SRMConfig.interactsTextToggle){
                            SecretRoutesRenderUtils.drawText(abs.getOne(), abs.getTwo(), abs.getThree(), SecretRoutesRenderUtils.getTextColor(SRMConfig.interactsWaypointColorIndex)+"Interact", SRMConfig.interactsTextSize, event.partialTicks);
                        }
                    }else if (name.contains("Item")){
                        color = SRMConfig.secretsItem;
                        if(SRMConfig.itemTextToggle) {
                            SecretRoutesRenderUtils.drawText(abs.getOne(), abs.getTwo(), abs.getThree(), SecretRoutesRenderUtils.getTextColor(SRMConfig.itemWaypointColorIndex) + "Item", SRMConfig.itemTextSize, event.partialTicks);
                        }
                    }
                    SecretRoutesRenderUtils.drawBoxAtBlock(abs.getOne(), abs.getTwo(), abs.getThree(), color, 1, 1, 1);
                }




            }

        }
    }

    public static void renderLever(RenderWorldLastEvent event){}
        







}
