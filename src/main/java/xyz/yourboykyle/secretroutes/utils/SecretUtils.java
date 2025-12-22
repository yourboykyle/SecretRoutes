// TODO: update this file for multi versioning (1.8.9 -> 1.21.8)
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.polyfrost.polyui.color.ColorUtils;
import org.polyfrost.polyui.color.PolyColor;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.MapUtils;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SecretUtils {
    public static JsonArray secrets = null;
    public static boolean renderLever = false;
    public static PrintingUtils xPrinter = new PrintingUtils();
    public static PrintingUtils yPrinter = new PrintingUtils();
    public static PrintingUtils zPrinter = new PrintingUtils();
    public static BlockPos currentLeverPos = null;
    public static BlockPos lastInteract;
    public static Long removeBannerTime = null;
    public static boolean first = true;
    public static String chestName = null;
    public static String leverName = null;
    public static String leverNumber = null;
    public static ArrayList<String> secretLocations = new ArrayList<>();

    public static void renderingCallback(JsonObject currentSecretWaypoints, RenderWorldLastEvent event, int index2){
        ArrayList<BlockPos> etherwarpPositions = new ArrayList<>();
        ArrayList<BlockPos> minesPositions = new ArrayList<>();
        ArrayList<BlockPos> interactsPositions = new ArrayList<>();
        ArrayList<BlockPos> superboomsPositions = new ArrayList<>();
        ArrayList<Triple<Double, Double, Double>> enderpearlPositons = new ArrayList<>();
        ArrayList<Tuple<Float, Float>> enderpearlAngles = new ArrayList<>();

        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        ///GlStateManager.disableBlend();
        if(SRMConfig.playerWaypointLine){
            BlockPos nextSecret = Main.currentRoom.getSecretLocation();
            if(nextSecret!=null){
                RenderUtils.drawFromPlayer(Minecraft.getMinecraft().thePlayer, nextSecret.getX(), nextSecret.getY(), nextSecret.getZ(), SRMConfig.lineColor, event.partialTicks, SRMConfig.width );
            }
        }

        // Render the etherwarps
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("etherwarps") != null && (!SRMConfig.wholeRoute || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.renderEtherwarps) {
            JsonArray etherwarpLocations = currentSecretWaypoints.get("etherwarps").getAsJsonArray();
            for (JsonElement etherwarpLocationElement : etherwarpLocations) {

                JsonArray etherwarpLocation = etherwarpLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(etherwarpLocation.get(0).getAsInt(), etherwarpLocation.get(1).getAsInt(), etherwarpLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
                if(!SRMConfig.wholeRoute && etherwarpLocations.get(0) == etherwarpLocationElement && SRMConfig.playerToEtherwarp){
                    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                    RenderUtils.drawFromPlayer(player, pos, ColorUtils.rgba(0, 255, 255), event.partialTicks, 1);
                }
                etherwarpPositions.add(pos);
                if(SRMConfig.etherwarpFullBlock){
                    SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.etherWarp, 1, 1);
                }else{
                    SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.etherWarp, 1, 1);
                }
            }
        }

        // Render the mines
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("mines") != null && (!SRMConfig.wholeRoute || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.renderMines) {
            JsonArray mineLocations = currentSecretWaypoints.get("mines").getAsJsonArray();
            for (JsonElement mineLocationElement : mineLocations) {
                JsonArray mineLocation = mineLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils. relativeToActual(new BlockPos(mineLocation.get(0).getAsInt(), mineLocation.get(1).getAsInt(), mineLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
                minesPositions.add(pos);
                if(SRMConfig.mineFullBlock){
                    SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.mine, 1, 1);
                }else {
                    SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.mine, 1, 1);
                }
            }
        }

        // Render the interacts
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("interacts") != null && (!SRMConfig.wholeRoute || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.renderInteracts) {
            JsonArray interactLocations = currentSecretWaypoints.get("interacts").getAsJsonArray();
            for (JsonElement interactLocationElement : interactLocations) {
                JsonArray interactLocation = interactLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(interactLocation.get(0).getAsInt(), interactLocation.get(1).getAsInt(), interactLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
                interactsPositions.add(pos);
                if(SRMConfig.interactsFullBlock) {
                    SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.interacts, 1, 1);
                }else {
                    SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.interacts, 1, 1);
                }
            }
        }

        // Render the tnts
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("tnts") != null && (!SRMConfig.wholeRoute || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.renderSuperboom) {
            JsonArray tntLocations = currentSecretWaypoints.get("tnts").getAsJsonArray();
            for (JsonElement tntLocationElement : tntLocations) {
                JsonArray tntLocation = tntLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(tntLocation.get(0).getAsInt(), tntLocation.get(1).getAsInt(), tntLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
                superboomsPositions.add(pos);
                if(SRMConfig.superboomsFullBlock) {
                    SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.superbooms, 1, 1);
                }else{
                    SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.superbooms, 1, 1);
                }
            }
        }
        // Render normal lines if config says so
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("locations") != null && SRMConfig.lineType == 1  && (!SRMConfig.wholeRoute || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex)) {
            GlStateManager.enableDepth();
            List<Triple<Double, Double, Double>> lines = new LinkedList<>();

            JsonArray lineLocations = currentSecretWaypoints.get("locations").getAsJsonArray();
            for (JsonElement lineLocationElement : lineLocations) {
                JsonArray lineLocation = lineLocationElement.getAsJsonArray();

                Main.checkRoomData();
                Triple<Double, Double, Double> linePos = MapUtils.relativeToActual(lineLocation.get(0).getAsDouble(), lineLocation.get(1).getAsDouble(), lineLocation.get(2).getAsDouble(), RoomDetection.roomDirection, RoomDetection.roomCorner);
                linePos.setOne(linePos.getOne() + 0.5);
                linePos.setTwo(linePos.getTwo() + 0.5);
                linePos.setThree(linePos.getThree() + 0.5);
                lines.add(linePos);
            }

            if (SRMConfig.modEnabled) {
                RenderUtils.drawMultipleNormalLines(lines, event.partialTicks, SRMConfig.lineColor, SRMConfig.width);
            }
            GlStateManager.disableDepth();
        }

        // Render the ender pearls
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("enderpearls") != null && (!SRMConfig.wholeRoute || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.renderEnderpearls) {
            JsonArray enderpearlAnglesArray = currentSecretWaypoints.get("enderpearlangles").getAsJsonArray();
            for (JsonElement pearlAngleElement : enderpearlAnglesArray) {
                JsonArray pearlAngle = pearlAngleElement.getAsJsonArray();

                Main.checkRoomData();
                enderpearlAngles.add(new Tuple<>(pearlAngle.get(0).getAsFloat(), pearlAngle.get(1).getAsFloat()));
            }

            JsonArray pearlLocations = currentSecretWaypoints.get("enderpearls").getAsJsonArray();
            int index = 0;
            for (JsonElement pearlLocationElement : pearlLocations) {

                JsonArray pearlLocation = pearlLocationElement.getAsJsonArray();

                Main.checkRoomData();
                double posX = pearlLocation.get(0).getAsDouble();
                double posY = pearlLocation.get(1).getAsDouble();
                double posZ = pearlLocation.get(2).getAsDouble();

                Triple<Double, Double, Double> positions = MapUtils.relativeToActual(posX, posY, posZ, RoomDetection.roomDirection, RoomDetection.roomCorner);
                posX = positions.getOne() - 0.25;
                posY = positions.getTwo();
                posZ = positions.getThree() - 0.25;
                if(SRMConfig.enderpearlFullBlock){
                    SecretRoutesRenderUtils.drawFilledBoxAtBlock(posX, posY, posZ, SRMConfig.enderpearls, 0.5f, 0);

                }else {
                    SecretRoutesRenderUtils.drawBoxAtBlock(posX, posY, posZ, SRMConfig.enderpearls, 0.5, 0);
                }
                double yaw = RotationUtils.relativeToActualYaw(enderpearlAngles.get(index).getSecond(), RoomDetection.roomDirection);
                double pitch = enderpearlAngles.get(index).getFirst();

                double yawRadians = Math.toRadians(yaw);
                double pitchRadians = Math.toRadians(pitch);


                double length = 10.0D;
                double x = -Math.sin(yawRadians) * Math.cos(pitchRadians); // (z)
                double y = -Math.sin(pitchRadians); // z
                double z = Math.cos(yawRadians) * Math.cos(pitchRadians); // (x)

                double sideLength = Math.sqrt(x * x + y * y + z * z);
                x /= sideLength;
                y /= sideLength;
                z /= sideLength;

                double newX = posX + x * length + 0.25;
                double newY = posY + y * length + 1.62;
                double newZ = posZ + z * length + 0.25;
                //sendVerboseMessage("Origin: (" + (posX +0.25f)+ ", " + (posY +1.62f) + ", " + posZ +(0.25)+") to End: (" + newX + ", " + newY + ", " + newZ + ") with a angles of ("+yaw+", "+pitch+") -> ("+yawRadians+", "+pitchRadians+")", verboseTAG);
                //SecretRoutesRenderUtils.drawBoxAtBlock(newX, newY, newZ, SRMConfig.enderpearls, 0.03125, 0.03125);
                RenderUtils.drawNormalLine(posX + 0.25F, posY + 1.62F, posZ + 0.25F, newX, newY, newZ, SRMConfig.pearlLineColor, event.partialTicks, !SRMConfig.renderLinesThroughWalls, SRMConfig.pearlLineWidth);
                enderpearlPositons.add(new Triple<>(posX, posY, posZ));
                index++;
            }
        }
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("secret") != null) {
            JsonObject secret = currentSecretWaypoints.get("secret").getAsJsonObject();
            String type = secret.get("type").getAsString();
            JsonArray location = secret.get("location").getAsJsonArray();

            Main.checkRoomData();
            BlockPos pos = MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

            GlStateManager.disableTexture2D();

            switch (type) {
                case "interact":
                    if (SRMConfig.renderSecretIteract) {
                        if (SRMConfig.secretsInteractFullBlock) {
                            SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.secretsInteract, 1, 1);
                        } else {
                            SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.secretsInteract, 1, 1);
                        }
                        if (SRMConfig.interactTextToggle) {
                            SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.interactWaypointColorIndex) + "Interact", SRMConfig.interactTextSize, event.partialTicks);
                        }
                    }
                    break;
                case "item":
                    if (SRMConfig.renderSecretsItem) {
                        if (SRMConfig.secretsItemFullBlock) {
                            SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.secretsItem, 1, 1);
                        } else {
                            SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.secretsItem, 1, 1);
                        }

                        if (SRMConfig.itemTextToggle) {
                            SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.itemWaypointColorIndex) + "Item", SRMConfig.itemTextSize, event.partialTicks);
                        }
                    }
                    break;
                case "bat":
                    if (SRMConfig.renderSecretBat) {
                        if (SRMConfig.secretsBatFullBlock) {
                            SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.secretsBat, 1, 1);
                        } else {
                            SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.secretsBat, 1, 1);
                        }
                        if (SRMConfig.batTextToggle) {
                            SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.batWaypointColorIndex) + "Bat", SRMConfig.batTextSize, event.partialTicks);
                        }
                    }
                    break;
            }
            if (SRMConfig.etherwarpsTextToggle) {
                int iEtherwarp = 1;
                for (BlockPos etherwarpPos : etherwarpPositions) {
                    String text = SRMConfig.etherwarpsEnumToggle ? "etherwarp" : "etherwarp " + iEtherwarp++;
                    SecretRoutesRenderUtils.drawText(etherwarpPos.getX(), etherwarpPos.getY(), etherwarpPos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.etherwarpsWaypointColorIndex) + text, SRMConfig.etherwarpsTextSize, event.partialTicks);
                }
            }
            if (SRMConfig.minesTextToggle) {
                int iMine = 1;
                for (BlockPos minePos : minesPositions) {
                    String text = SRMConfig.minesEnumToggle ? "mine" : "mine " + iMine++;
                    SecretRoutesRenderUtils.drawText(minePos.getX(), minePos.getY(), minePos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.minesWaypointColorIndex) + text, SRMConfig.minesTextSize, event.partialTicks);
                }
            }
            if (SRMConfig.interactsTextToggle) {
                int iInteract = 1;
                for (BlockPos interactPos : interactsPositions) {
                    String text = SRMConfig.interactsEnumToggle ? "interact" : "interact " + iInteract;
                    SecretRoutesRenderUtils.drawText(interactPos.getX(), interactPos.getY(), interactPos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.interactsWaypointColorIndex) + text, SRMConfig.interactsTextSize, event.partialTicks);
                }
            }
            if (SRMConfig.superboomsTextToggle) {
                int iSuperboom = 1;
                for (BlockPos superboomPos : superboomsPositions) {
                    String text = SRMConfig.superboomsEnumToggle ? "superboom" : "superboom " + iSuperboom++;
                    SecretRoutesRenderUtils.drawText(superboomPos.getX(), superboomPos.getY(), superboomPos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.superboomsWaypointColorIndex) + text, SRMConfig.superboomsTextSize, event.partialTicks);
                }
            }
            if (SRMConfig.enderpearlTextToggle) {
                int iEnderpearl = 1;
                for (Triple<Double, Double, Double> enderpearlPos : enderpearlPositons) {
                    String text = SRMConfig.enderpearlEnumToggle ? "ender pearl" : "ender pearl " + iEnderpearl++;
                    SecretRoutesRenderUtils.drawText(enderpearlPos.getOne(), enderpearlPos.getTwo(), enderpearlPos.getThree(), SecretRoutesRenderUtils.getTextColor(SRMConfig.enderpearlWaypointColorIndex) + text, SRMConfig.enderpearlTextSize, event.partialTicks);
                }
            }
            GlStateManager.enableTexture2D();
        }

        // Render the secret

        // Render the start / end waypoint text
        JsonObject waypoints = currentSecretWaypoints;
        if (waypoints != null && waypoints.get("locations") != null && waypoints.get("locations").getAsJsonArray().size() >0 && waypoints.get("locations").getAsJsonArray().get(0) != null) {
            if (index2 == 0) {
                // First secret in the route (the start)
                JsonArray startCoords = currentSecretWaypoints.get("locations").getAsJsonArray().get(0).getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(startCoords.get(0).getAsInt(), startCoords.get(1).getAsInt(), startCoords.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

                // Render the text
                if (SRMConfig.startTextToggle) {
                    SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.startWaypointColorIndex) + "Start", SRMConfig.startTextSize, event.partialTicks);
                }
            }
            if (index2 == Main.currentRoom.currentSecretRoute.getAsJsonArray().size() - 1) {
                JsonObject secret = currentSecretWaypoints.get("secret").getAsJsonObject();
                String type = secret.get("type").getAsString();
                JsonArray location = secret.get("location").getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

                // Render the text
                if (SRMConfig.exitTextToggle && type.equals("exitroute")) {
                    SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.exitWaypointColorIndex) + "Exit", SRMConfig.exitTextSize, event.partialTicks);
                }
            }
        }
        //GlStateManager.enableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    public static void renderSecrets(RenderWorldLastEvent event){

        secrets = getSecrets();


        if(secrets != null){
            for(JsonElement secret : secrets){
                JsonObject secretInfos = secret.getAsJsonObject();
                String name = secretInfos.get("secretName").getAsString();
                String type = secretInfos.get("category").getAsString();
                if(name.contains("Chest") || name.contains("Bat") || name.contains("Wither Essence") || name.contains("Lever") || name.contains("Item")){

                    int xPos = secretInfos.get("x").getAsInt();
                    int yPos = secretInfos.get("y").getAsInt();
                    int zPos = secretInfos.get("z").getAsInt();
                    Main.checkRoomData();
                    Triple<Double, Double, Double> abs = MapUtils.relativeToActual(xPos, yPos, zPos, RoomDetection.roomDirection, RoomDetection.roomCorner);
                    PolyColor color = ColorUtils.rgba(255, 255, 255);
                    if(secretLocations.contains(BlockUtils.blockPos(new BlockPos(xPos, yPos, zPos)))){continue;}
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

    public static void renderLever(RenderWorldLastEvent e){
        ArrayList<JsonElement> levers = new ArrayList<>();
        JsonArray csr = getSecrets();
        String leverNum = null;
        if(csr == null){
            SecretUtils.renderLever = false;
        }
        if(currentLeverPos == null && csr != null){
            for(JsonElement secret : csr){
                JsonObject secretInfos = secret.getAsJsonObject();
                String name = secretInfos.get("secretName").getAsString();
                String category = secretInfos.get("category").getAsString();
                if(category.equals("chest") && leverNum == null){
                    int x = secretInfos.get("x").getAsInt();
                    int y = secretInfos.get("y").getAsInt();
                    int z = secretInfos.get("z").getAsInt();

                    Triple<Double, Double, Double> abs = MapUtils.relativeToActual(x, y, z, RoomDetection.roomDirection, RoomDetection.roomCorner);
                    BlockPos pos = new BlockPos(abs.getOne(), abs.getTwo(), abs.getThree());
                    //ChatUtils.sendChatMessage(Utils.blockPos(pos));
                    //ChatUtils.sendChatMessage(Utils.blockPos(lastInteract));
                    if(BlockUtils.blockPos(pos).equals(BlockUtils.blockPos(lastInteract))){
                        leverNum = name.split(" ")[0];
                        leverNumber = leverNum;
                        chestName = name;
                        //ChatUtils.sendChatMessage("Found lever, defined num");
                    }
                }
                if(category.equals("lever")){
                    if(leverNum == null){
                        levers.add(secret);
                        //ChatUtils.sendChatMessage("Adding lever to lists");
                    }else{
                        String[] nums = leverNum.split("/");
                        for(String num : nums){
                            if(name.contains(num)){
                                //ChatUtils.sendChatMessage("Found right lever. Setting pos");
                                currentLeverPos = new BlockPos(secretInfos.get("x").getAsInt(), secretInfos.get("y").getAsInt(), secretInfos.get("z").getAsInt());
                                leverName = name;
                            }
                        }

                    }
                }

            }
        }


        if(currentLeverPos != null || !levers.isEmpty()){
            if(currentLeverPos == null && leverNum != null){
                for(JsonElement secret : levers){
                    JsonObject secretInfos = secret.getAsJsonObject();
                    String name = secretInfos.get("secretName").getAsString();
                    String[] nums = leverNum.split("/");
                    for(String num : nums){
                        if(name.contains(num)){
                            //ChatUtils.sendChatMessage("Found right lever (iteration). Setting pos");
                            currentLeverPos = new BlockPos(secretInfos.get("x").getAsInt(), secretInfos.get("y").getAsInt(), secretInfos.get("z").getAsInt());
                            leverName = name;
                        }
                    }
                }
            }
            if(currentLeverPos == null){ChatUtils.sendChatMessage("§cLever not found :(");}else{
                Triple<Double, Double, Double> abs = MapUtils.relativeToActual(currentLeverPos.getX(), currentLeverPos.getY(), currentLeverPos.getZ(), RoomDetection.roomDirection, RoomDetection.roomCorner);
                if(SRMConfig.secretsInteractFullBlock){
                    SecretRoutesRenderUtils.drawFilledBoxAtBlock(abs.getOne(), abs.getTwo(), abs.getThree(), SRMConfig.secretsInteract, 1, 1, 1);

                }else{
                    SecretRoutesRenderUtils.drawBoxAtBlock(abs.getOne(), abs.getTwo(), abs.getThree(), SRMConfig.secretsInteract, 1, 1, 1);
                }
                SecretRoutesRenderUtils.drawText(abs.getOne(), abs.getTwo(), abs.getThree(), SecretRoutesRenderUtils.getTextColor(SRMConfig.interactsWaypointColorIndex) + "Locked chest lever", SRMConfig.interactsTextSize, e.partialTicks);
                if(first){
                    removeBannerTime = System.currentTimeMillis() + 5000;
                    new Thread(()-> {
                        try{
                            Thread.sleep(5000);
                            removeBannerTime = null;
                        }catch (InterruptedException ignored){

                        }


                    }).start();
                    first = false;
                }



            }

        }else{
            ChatUtils.sendChatMessage("§cLever not found :(");
        }



    }



    public static JsonArray getSecrets(){
        Main.checkRoomData();
        String roomName = Main.currentRoom.name;
        if(secrets == null){
            try{
                try (Reader reader  = new InputStreamReader(Main.class.getResourceAsStream("/assets/roomdetection/secretlocations.json"))) {
                    JsonParser parser = new JsonParser();
                    JsonObject object = parser.parse(reader).getAsJsonObject();
                    if(object.has(roomName)){
                        secrets = object.getAsJsonArray(roomName);

                    }
                    return secrets;
                }
            }catch (Exception e){
                LogUtils.error(e);
            }
            return null;
        }else{
            return secrets;
        }

    }
    public static void resetValues(){
        renderLever = false;
        currentLeverPos = null;
        removeBannerTime = null;
        first = true;
        chestName = null;
        leverName = null;
        leverNumber = null;
    }

}
