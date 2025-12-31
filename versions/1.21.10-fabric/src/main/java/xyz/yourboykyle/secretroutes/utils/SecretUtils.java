//#if FABRIC && MC == 1.21.10
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.polyfrost.polyui.color.ColorUtils;
import org.polyfrost.polyui.color.PolyColor;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.events.OnSkyblockerRender;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;
import xyz.yourboykyle.secretroutes.utils.multistorage.Tuple;

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

    public static void renderingCallback(JsonObject currentSecretWaypoints, int index2) {
        ArrayList<BlockPos> etherwarpPositions = new ArrayList<>();
        ArrayList<BlockPos> minesPositions = new ArrayList<>();
        ArrayList<BlockPos> interactsPositions = new ArrayList<>();
        ArrayList<BlockPos> superboomsPositions = new ArrayList<>();
        ArrayList<Triple<Double, Double, Double>> enderpearlPositons = new ArrayList<>();
        ArrayList<Tuple<Float, Float>> enderpearlAngles = new ArrayList<>();

        if(SRMConfig.playerWaypointLine){
            BlockPos nextSecret = Main.currentRoom.getSecretLocation();
            if(nextSecret!=null){
                Vec3d point = new Vec3d(nextSecret.getX() + 0.5, nextSecret.getY() + 0.5, nextSecret.getZ() + 0.5);
                OnSkyblockerRender.addLineFromCursor(new RenderTypes.LineFromCursor(point, SRMConfig.lineColor, SRMConfig.width));
            }
        }

        // Render the etherwarps
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("etherwarps") != null && (!SRMConfig.wholeRoute || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.renderEtherwarps) {
            JsonArray etherwarpLocations = currentSecretWaypoints.get("etherwarps").getAsJsonArray();
            for (JsonElement etherwarpLocationElement : etherwarpLocations) {

                JsonArray etherwarpLocation = etherwarpLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(etherwarpLocation.get(0).getAsInt(), etherwarpLocation.get(1).getAsInt(), etherwarpLocation.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());
                if(!SRMConfig.wholeRoute && etherwarpLocations.get(0) == etherwarpLocationElement && SRMConfig.playerToEtherwarp){
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    Vec3d point = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    OnSkyblockerRender.addLineFromCursor(new RenderTypes.LineFromCursor(point, ColorUtils.rgba(0, 255, 255), SRMConfig.width));
                }
                etherwarpPositions.add(pos);
                if(SRMConfig.etherwarpFullBlock){
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    OnSkyblockerRender.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.etherWarp, 1, 1, true));
                }else{
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    OnSkyblockerRender.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.etherWarp, 1, 1, true));
                }
            }
        }

        // Render the mines
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("mines") != null && (!SRMConfig.wholeRoute || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.renderMines) {
            JsonArray mineLocations = currentSecretWaypoints.get("mines").getAsJsonArray();
            for (JsonElement mineLocationElement : mineLocations) {
                JsonArray mineLocation = mineLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils. relativeToActual(new BlockPos(mineLocation.get(0).getAsInt(), mineLocation.get(1).getAsInt(), mineLocation.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());
                minesPositions.add(pos);
                if(SRMConfig.mineFullBlock){
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    OnSkyblockerRender.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.mine, 1, 1, true));
                }else {
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    OnSkyblockerRender.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.mine, 1, 1, true));
                }
            }
        }

        // Render the interacts
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("interacts") != null && (!SRMConfig.wholeRoute || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.renderInteracts) {
            JsonArray interactLocations = currentSecretWaypoints.get("interacts").getAsJsonArray();
            for (JsonElement interactLocationElement : interactLocations) {
                JsonArray interactLocation = interactLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(interactLocation.get(0).getAsInt(), interactLocation.get(1).getAsInt(), interactLocation.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());
                interactsPositions.add(pos);
                if(SRMConfig.interactsFullBlock) {
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    OnSkyblockerRender.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.interacts, 1, 1, true));
                }else {
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    OnSkyblockerRender.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.interacts, 1, 1, true));
                }
            }
        }

        // Render the tnts
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("tnts") != null && (!SRMConfig.wholeRoute || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.renderSuperboom) {
            JsonArray tntLocations = currentSecretWaypoints.get("tnts").getAsJsonArray();
            for (JsonElement tntLocationElement : tntLocations) {
                JsonArray tntLocation = tntLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(tntLocation.get(0).getAsInt(), tntLocation.get(1).getAsInt(), tntLocation.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());
                superboomsPositions.add(pos);
                if(SRMConfig.superboomsFullBlock) {
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    OnSkyblockerRender.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.superbooms, 1, 1, true));
                }else{
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    OnSkyblockerRender.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.superbooms, 1, 1, true));
                }
            }
        }
        // Render normal lines if config says so
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("locations") != null && SRMConfig.lineType == 1  && (!SRMConfig.wholeRoute || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex)) {
            List<Triple<Double, Double, Double>> lines = new LinkedList<>();

            JsonArray lineLocations = currentSecretWaypoints.get("locations").getAsJsonArray();
            for (JsonElement lineLocationElement : lineLocations) {
                JsonArray lineLocation = lineLocationElement.getAsJsonArray();

                Main.checkRoomData();
                Triple<Double, Double, Double> linePos = MapUtils.relativeToActual(lineLocation.get(0).getAsDouble(), lineLocation.get(1).getAsDouble(), lineLocation.get(2).getAsDouble(), RoomDetection.roomDirection(), RoomDetection.roomCorner());
                linePos.setOne(linePos.getOne() + 0.5);
                linePos.setTwo(linePos.getTwo() + 0.5);
                linePos.setThree(linePos.getThree() + 0.5);
                lines.add(linePos);
            }

            if (SRMConfig.modEnabled) {
                Vec3d[] linePoints = new Vec3d[lines.size()];
                for (int i = 0; i < lines.size(); i++) {
                    Triple<Double, Double, Double> line = lines.get(i);
                    linePoints[i] = new Vec3d(line.getOne(), line.getTwo(), line.getThree());
                }

                // Each addLines only handles 2 points,
                OnSkyblockerRender.addLinesFromPoints(linePoints, SRMConfig.lineColor, SRMConfig.width, true);
            }
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

                Triple<Double, Double, Double> positions = MapUtils.relativeToActual(posX, posY, posZ, RoomDetection.roomDirection(), RoomDetection.roomCorner());
                posX = positions.getOne() - 0.25;
                posY = positions.getTwo();
                posZ = positions.getThree() - 0.25;
                if(SRMConfig.enderpearlFullBlock){
                    Vec3d position = new Vec3d(posX, posY, posZ);
                    OnSkyblockerRender.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.enderpearls, 1, 1, true));
                }else {
                    Vec3d position = new Vec3d(posX, posY, posZ);
                    OnSkyblockerRender.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.enderpearls, 1, 1, true));
                }
                double yaw = RotationUtils.relativeToActualYaw(enderpearlAngles.get(index).getTwo(), RoomDetection.roomDirection());
                double pitch = enderpearlAngles.get(index).getOne();

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
                Vec3d start = new Vec3d(posX + 0.25F, posY + 1.62F, posZ + 0.25F);
                Vec3d end = new Vec3d(newX, newY, newZ);
                OnSkyblockerRender.addLine(new RenderTypes.Line(start, end, SRMConfig.pearlLineColor, SRMConfig.pearlLineWidth, true));
                enderpearlPositons.add(new Triple<>(posX, posY, posZ));
                index++;
            }
        }
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("secret") != null) {
            JsonObject secret = currentSecretWaypoints.get("secret").getAsJsonObject();
            String type = secret.get("type").getAsString();
            JsonArray location = secret.get("location").getAsJsonArray();

            Main.checkRoomData();
            BlockPos pos = MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());

            switch (type) {
                case "interact":
                    if (SRMConfig.renderSecretIteract) {
                        if (SRMConfig.secretsInteractFullBlock) {
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            OnSkyblockerRender.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.secretsInteract, 1, 1, true));
                        } else {
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            OnSkyblockerRender.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.secretsInteract, 1, 1, true));
                        }
                        if (SRMConfig.interactTextToggle) {
                            Text text = Text.literal(SecretRoutesRenderUtils.getTextColor(SRMConfig.interactsWaypointColorIndex) + "Interact");
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.interactTextSize));
                        }
                    }
                    break;
                case "item":
                    if (SRMConfig.renderSecretsItem) {
                        if (SRMConfig.secretsItemFullBlock) {
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            OnSkyblockerRender.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.secretsItem, 1, 1, true));
                        } else {
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            OnSkyblockerRender.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.secretsItem, 1, 1, true));
                        }

                        if (SRMConfig.itemTextToggle) {
                            Text text = Text.literal(SecretRoutesRenderUtils.getTextColor(SRMConfig.itemWaypointColorIndex) + "Item");
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.itemTextSize));
                        }
                    }
                    break;
                case "bat":
                    if (SRMConfig.renderSecretBat) {
                        if (SRMConfig.secretsBatFullBlock) {
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            OnSkyblockerRender.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.secretsBat, 1, 1, true));
                        } else {
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            OnSkyblockerRender.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.secretsBat, 1, 1, true));
                        }
                        if (SRMConfig.batTextToggle) {
                            Text text = Text.literal(SecretRoutesRenderUtils.getTextColor(SRMConfig.batWaypointColorIndex) + "Bat");
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.batTextSize));
                        }
                    }
                    break;
            }
            if (SRMConfig.etherwarpsTextToggle) {
                int iEtherwarp = 1;
                for (BlockPos etherwarpPos : etherwarpPositions) {
                    String colorFormatter = SecretRoutesRenderUtils.getTextColor(SRMConfig.etherwarpsWaypointColorIndex);
                    Text text = Text.literal(colorFormatter + (SRMConfig.etherwarpsEnumToggle ? "etherwarp" : "etherwarp " + iEtherwarp++));
                    Vec3d position = new Vec3d(etherwarpPos.getX(), etherwarpPos.getY(), etherwarpPos.getZ());
                    OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.etherwarpsTextSize));
                }
            }
            if (SRMConfig.minesTextToggle) {
                int iMine = 1;
                for (BlockPos minePos : minesPositions) {
                    String colorFormatter = SecretRoutesRenderUtils.getTextColor(SRMConfig.minesWaypointColorIndex);
                    Text text = Text.literal(colorFormatter + (SRMConfig.minesEnumToggle ? "mine" : "mine " + iMine++));
                    Vec3d position = new Vec3d(minePos.getX(), minePos.getY(), minePos.getZ());
                    OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.minesTextSize));
                }
            }
            if (SRMConfig.interactsTextToggle) {
                int iInteract = 1;
                for (BlockPos interactPos : interactsPositions) {
                    String colorFormatter = SecretRoutesRenderUtils.getTextColor(SRMConfig.interactsWaypointColorIndex);
                    Text text = Text.literal(colorFormatter + (SRMConfig.interactsEnumToggle ? "interact" : "interact " + iInteract));
                    Vec3d position = new Vec3d(interactPos.getX(), interactPos.getY(), interactPos.getZ());
                    OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.interactsTextSize));
                }
            }
            if (SRMConfig.superboomsTextToggle) {
                int iSuperboom = 1;
                for (BlockPos superboomPos : superboomsPositions) {
                    String colorFormatter = SecretRoutesRenderUtils.getTextColor(SRMConfig.superboomsWaypointColorIndex);
                    Text text = Text.literal(colorFormatter + (SRMConfig.superboomsEnumToggle ? "superboom" : "superboom " + iSuperboom++));
                    Vec3d position = new Vec3d(superboomPos.getX(), superboomPos.getY(), superboomPos.getZ());
                    OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.superboomsTextSize));
                }
            }
            if (SRMConfig.enderpearlTextToggle) {
                int iEnderpearl = 1;
                for (Triple<Double, Double, Double> enderpearlPos : enderpearlPositons) {
                    String colorFormatter = SecretRoutesRenderUtils.getTextColor(SRMConfig.enderpearlWaypointColorIndex);
                    Text text = Text.literal(colorFormatter + (SRMConfig.enderpearlEnumToggle ? "ender pearl" : "ender pearl " + iEnderpearl++));
                    Vec3d position = new Vec3d(enderpearlPos.getOne(), enderpearlPos.getTwo(), enderpearlPos.getThree());
                    OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.enderpearlTextSize));
                }
            }
        }

        // Render the secret

        // Render the start / end waypoint text
        JsonObject waypoints = currentSecretWaypoints;
        if (waypoints != null && waypoints.get("locations") != null && waypoints.get("locations").getAsJsonArray().size() >0 && waypoints.get("locations").getAsJsonArray().get(0) != null) {
            if (index2 == 0) {
                // First secret in the route (the start)
                JsonArray startCoords = currentSecretWaypoints.get("locations").getAsJsonArray().get(0).getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(startCoords.get(0).getAsInt(), startCoords.get(1).getAsInt(), startCoords.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());

                // Render the text
                if (SRMConfig.startTextToggle) {
                    Text text = Text.literal(SecretRoutesRenderUtils.getTextColor(SRMConfig.startWaypointColorIndex) + "Start");
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.startTextSize));
                }
            }
            if (index2 == Main.currentRoom.currentSecretRoute.getAsJsonArray().size() - 1) {
                JsonObject secret = currentSecretWaypoints.get("secret").getAsJsonObject();
                String type = secret.get("type").getAsString();
                JsonArray location = secret.get("location").getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());

                // Render the text
                if (SRMConfig.exitTextToggle && type.equals("exitroute")) {
                    Text text = Text.literal(SecretRoutesRenderUtils.getTextColor(SRMConfig.exitWaypointColorIndex) + "Exit");
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.exitTextSize));
                }
            }
        }
    }

    public static void renderSecrets(){
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
                    Triple<Double, Double, Double> abs = MapUtils.relativeToActual(xPos, yPos, zPos, RoomDetection.roomDirection(), RoomDetection.roomCorner());
                    System.out.println("Relative position: " + xPos + ", " + yPos + ", " + zPos + " (Direction: " + RoomDetection.roomDirection() + ", Corner: " + RoomDetection.roomCorner() + ")");
                    System.out.println("Actual position: " + abs.getOne() + ", " + abs.getTwo() + ", " + abs.getThree());
                    PolyColor color = ColorUtils.rgba(255, 255, 255);
                    if(secretLocations.contains(BlockUtils.blockPos(new BlockPos(xPos, yPos, zPos)))){continue;}
                    if(name.contains("Chest") || name.contains("Wither Essence")) {
                        color = SRMConfig.secretsInteract;
                        if(SRMConfig.interactTextToggle){
                            // Draw the text
                            Text text = Text.literal(SecretRoutesRenderUtils.getTextColor(SRMConfig.interactWaypointColorIndex) + "Interact");
                            Vec3d position = new Vec3d(abs.getOne() + 0.5, abs.getTwo() + 0.5, abs.getThree() + 0.5);
                            System.out.println("Adding world text at " + position);
                            OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.interactTextSize));
                        }
                    }else if(name.contains("Bat")){
                        color = SRMConfig.secretsBat;
                        if(SRMConfig.batTextToggle) {
                            // Draw the text
                            Text text = Text.literal(SecretRoutesRenderUtils.getTextColor(SRMConfig.batWaypointColorIndex) + "Bat");
                            Vec3d position = new Vec3d(abs.getOne() + 0.5, abs.getTwo() + 0.5, abs.getThree() + 0.5);
                            System.out.println("Adding world text at " + position);
                            OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.batTextSize));
                        }
                    }else if (name.contains("Lever")){
                        color = SRMConfig.interacts;
                        if(SRMConfig.interactsTextToggle){
                            // Draw the text
                            Text text = Text.literal(SecretRoutesRenderUtils.getTextColor(SRMConfig.interactsWaypointColorIndex) + "Interact");
                            Vec3d position = new Vec3d(abs.getOne() + 0.5, abs.getTwo() + 0.5, abs.getThree() + 0.5);
                            System.out.println("Adding world text at " + position);
                            OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.interactsTextSize));
                        }
                    }else if (name.contains("Item")){
                        color = SRMConfig.secretsItem;
                        if(SRMConfig.itemTextToggle) {
                            // Draw the text
                            Text text = Text.literal(SecretRoutesRenderUtils.getTextColor(SRMConfig.itemWaypointColorIndex) + "Item");
                            Vec3d position = new Vec3d(abs.getOne() + 0.5, abs.getTwo() + 0.5, abs.getThree() + 0.5);
                            System.out.println("Adding world text at " + position);
                            OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.itemTextSize));
                        }
                    }
                    Vec3d position = new Vec3d(abs.getOne(), abs.getTwo(), abs.getThree());
                    System.out.println("Adding box at at " + position);
                    OnSkyblockerRender.addOutlinedBox(new RenderTypes.OutlinedBox(position, color, 1f, 1f, true));
                }
            }

        }
    }

    public static void renderLever() {
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

                    Triple<Double, Double, Double> abs = MapUtils.relativeToActual(x, y, z, RoomDetection.roomDirection(), RoomDetection.roomCorner());
                    BlockPos pos = new BlockPos(abs.getOne().intValue(), abs.getTwo().intValue(), abs.getThree().intValue());
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
                Triple<Double, Double, Double> abs = MapUtils.relativeToActual(currentLeverPos.getX(), currentLeverPos.getY(), currentLeverPos.getZ(), RoomDetection.roomDirection(), RoomDetection.roomCorner());
                if(SRMConfig.secretsInteractFullBlock){
                    Vec3d position = new Vec3d(abs.getOne(), abs.getTwo(), abs.getThree());
                    OnSkyblockerRender.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.secretsInteract, 1f, 1f, true));
                }else{
                    Vec3d position = new Vec3d(abs.getOne(), abs.getTwo(), abs.getThree());
                    OnSkyblockerRender.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.secretsInteract, 1f, 1f, true));
                }
                Text text = Text.literal(SecretRoutesRenderUtils.getTextColor(SRMConfig.interactsWaypointColorIndex) + "Locked chest lever");
                Vec3d position = new Vec3d(abs.getOne(), abs.getTwo(), abs.getThree());
                OnSkyblockerRender.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.interactsTextSize));
                if(first){
                    removeBannerTime = System.currentTimeMillis() + 5000;
                    new Thread(()-> {
                        try{
                            Thread.sleep(5000);
                            removeBannerTime = null;
                        } catch (InterruptedException ignored){
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
        if(Main.currentRoom == null || Main.currentRoom.name == null) return null;

        String roomName = Main.currentRoom.name.toLowerCase();
        if(secrets == null){
            try{
                try (Reader reader  = new InputStreamReader(Main.class.getResourceAsStream("/assets/roomdetection/secretlocations.json"))) {
                    JsonParser parser = new JsonParser();
                    JsonObject object = parser.parse(reader).getAsJsonObject();
                    for (String key : object.keySet()) {
                        if (key.equalsIgnoreCase(roomName)) {
                            secrets = object.getAsJsonArray(key);
                            break;
                        }
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
//#endif
