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
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;
import xyz.yourboykyle.secretroutes.utils.multistorage.Tuple;
import xyz.yourboykyle.secretroutes.utils.skyblocker.DungeonScanner;

import java.awt.*;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SecretUtils {
    public static List<DungeonScanner.SecretWaypoint> secrets = null;
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

    // Helper to get color code from Enum
    private static String getColorCode(SRMConfig.TextColor color) {
        return color.formatting.toString();
    }

    public static void renderingCallback(JsonObject currentSecretWaypoints, int index2) {
        ArrayList<BlockPos> etherwarpPositions = new ArrayList<>();
        ArrayList<BlockPos> minesPositions = new ArrayList<>();
        ArrayList<BlockPos> interactsPositions = new ArrayList<>();
        ArrayList<BlockPos> superboomsPositions = new ArrayList<>();
        ArrayList<Triple<Double, Double, Double>> enderpearlPositons = new ArrayList<>();
        ArrayList<Tuple<Float, Float>> enderpearlAngles = new ArrayList<>();

        if (SRMConfig.get().playerWaypointLine) {
            BlockPos nextSecret = Main.currentRoom.getSecretLocation();
            if (nextSecret != null) {
                Vec3d point = new Vec3d(nextSecret.getX() + 0.5, nextSecret.getY() + 0.5, nextSecret.getZ() + 0.5);
                AnotherRenderingUtil.addLineFromCursor(new RenderTypes.LineFromCursor(point, SRMConfig.get().lineColor, SRMConfig.get().width));
            }
        }

        // Render the etherwarps
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("etherwarps") != null && (!SRMConfig.get().wholeRoute || SRMConfig.get().allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.get().renderEtherwarps) {
            JsonArray etherwarpLocations = currentSecretWaypoints.get("etherwarps").getAsJsonArray();
            for (JsonElement etherwarpLocationElement : etherwarpLocations) {

                JsonArray etherwarpLocation = etherwarpLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(etherwarpLocation.get(0).getAsInt(), etherwarpLocation.get(1).getAsInt(), etherwarpLocation.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());
                if (!SRMConfig.get().wholeRoute && etherwarpLocations.get(0) == etherwarpLocationElement && SRMConfig.get().playerToEtherwarp) {
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    if (player != null) {
                        Vec3d point = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                        AnotherRenderingUtil.addLineFromCursor(new RenderTypes.LineFromCursor(point, new Color(0, 255, 255), SRMConfig.get().width));
                    }
                }
                etherwarpPositions.add(pos);
                if (SRMConfig.get().etherwarpFullBlock) {
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    AnotherRenderingUtil.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.get().etherWarp, 1, 1, true));
                } else {
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    AnotherRenderingUtil.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.get().etherWarp, 1, 1, true));
                }
            }
        }

        // Render the mines
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("mines") != null && (!SRMConfig.get().wholeRoute || SRMConfig.get().allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.get().renderMines) {
            JsonArray mineLocations = currentSecretWaypoints.get("mines").getAsJsonArray();
            for (JsonElement mineLocationElement : mineLocations) {
                JsonArray mineLocation = mineLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(mineLocation.get(0).getAsInt(), mineLocation.get(1).getAsInt(), mineLocation.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());
                minesPositions.add(pos);
                if (SRMConfig.get().mineFullBlock) {
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    AnotherRenderingUtil.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.get().mine, 1, 1, true));
                } else {
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    AnotherRenderingUtil.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.get().mine, 1, 1, true));
                }
            }
        }

        // Render the interacts
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("interacts") != null && (!SRMConfig.get().wholeRoute || SRMConfig.get().allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.get().renderInteracts) {
            JsonArray interactLocations = currentSecretWaypoints.get("interacts").getAsJsonArray();
            for (JsonElement interactLocationElement : interactLocations) {
                JsonArray interactLocation = interactLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(interactLocation.get(0).getAsInt(), interactLocation.get(1).getAsInt(), interactLocation.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());
                interactsPositions.add(pos);
                if (SRMConfig.get().interactsFullBlock) {
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    AnotherRenderingUtil.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.get().interacts, 1, 1, true));
                } else {
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    AnotherRenderingUtil.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.get().interacts, 1, 1, true));
                }
            }
        }

        // Render the tnts (Superbooms)
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("tnts") != null && (!SRMConfig.get().wholeRoute || SRMConfig.get().allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.get().renderSuperboom) {
            JsonArray tntLocations = currentSecretWaypoints.get("tnts").getAsJsonArray();
            for (JsonElement tntLocationElement : tntLocations) {
                JsonArray tntLocation = tntLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(tntLocation.get(0).getAsInt(), tntLocation.get(1).getAsInt(), tntLocation.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());
                superboomsPositions.add(pos);
                if (SRMConfig.get().superboomsFullBlock) {
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    AnotherRenderingUtil.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.get().superbooms, 1, 1, true));
                } else {
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    AnotherRenderingUtil.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.get().superbooms, 1, 1, true));
                }
            }
        }

        // Render normal lines
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("locations") != null && SRMConfig.get().lineType == SRMConfig.LineType.LINES && (!SRMConfig.get().wholeRoute || SRMConfig.get().allSteps || index2 == Main.currentRoom.currentSecretIndex)) {
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

            if (SRMConfig.get().modEnabled) {
                Vec3d[] linePoints = new Vec3d[lines.size()];
                for (int i = 0; i < lines.size(); i++) {
                    Triple<Double, Double, Double> line = lines.get(i);
                    linePoints[i] = new Vec3d(line.getOne(), line.getTwo(), line.getThree());
                }

                AnotherRenderingUtil.addLinesFromPoints(linePoints, SRMConfig.get().lineColor, SRMConfig.get().width, true);
            }
        }

        // Render the ender pearls
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("enderpearls") != null && (!SRMConfig.get().wholeRoute || SRMConfig.get().allSteps || index2 == Main.currentRoom.currentSecretIndex) && SRMConfig.get().renderEnderpearls) {
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

                if (SRMConfig.get().enderpearlFullBlock) {
                    Vec3d position = new Vec3d(posX, posY, posZ);
                    AnotherRenderingUtil.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.get().enderpearls, 1, 1, true));
                } else {
                    Vec3d position = new Vec3d(posX, posY, posZ);
                    AnotherRenderingUtil.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.get().enderpearls, 1, 1, true));
                }

                double yaw = RotationUtils.relativeToActualYaw(enderpearlAngles.get(index).getTwo(), RoomDetection.roomDirection());
                double pitch = enderpearlAngles.get(index).getOne();
                double yawRadians = Math.toRadians(yaw);
                double pitchRadians = Math.toRadians(pitch);

                double length = 10.0D;
                double x = -Math.sin(yawRadians) * Math.cos(pitchRadians);
                double y = -Math.sin(pitchRadians);
                double z = Math.cos(yawRadians) * Math.cos(pitchRadians);

                double sideLength = Math.sqrt(x * x + y * y + z * z);
                x /= sideLength;
                y /= sideLength;
                z /= sideLength;

                double newX = posX + x * length + 0.25;
                double newY = posY + y * length + 1.62;
                double newZ = posZ + z * length + 0.25;

                Vec3d start = new Vec3d(posX + 0.25F, posY + 1.62F, posZ + 0.25F);
                Vec3d end = new Vec3d(newX, newY, newZ);
                AnotherRenderingUtil.addLine(new RenderTypes.Line(start, end, SRMConfig.get().pearlLineColor, SRMConfig.get().pearlLineWidth, true));
                enderpearlPositons.add(new Triple<>(posX, posY, posZ));
                index++;
            }
        }

        // Render Secrets (Interact, Item, Bat)
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("secret") != null) {
            JsonObject secret = currentSecretWaypoints.get("secret").getAsJsonObject();
            String type = secret.get("type").getAsString();
            JsonArray location = secret.get("location").getAsJsonArray();

            Main.checkRoomData();
            BlockPos pos = MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());

            switch (type) {
                case "interact":
                    if (SRMConfig.get().renderSecretIteract) {
                        if (SRMConfig.get().secretsInteractFullBlock) {
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            AnotherRenderingUtil.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.get().secretsInteract, 1, 1, true));
                        } else {
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            AnotherRenderingUtil.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.get().secretsInteract, 1, 1, true));
                        }
                        if (SRMConfig.get().interactTextToggle) {
                            Text text = Text.literal(getColorCode(SRMConfig.get().interactWaypointColor) + "Interact");
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().interactTextSize));
                        }
                    }
                    break;
                case "item":
                    if (SRMConfig.get().renderSecretsItem) {
                        if (SRMConfig.get().secretsItemFullBlock) {
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            AnotherRenderingUtil.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.get().secretsItem, 1, 1, true));
                        } else {
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            AnotherRenderingUtil.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.get().secretsItem, 1, 1, true));
                        }

                        if (SRMConfig.get().itemTextToggle) {
                            Text text = Text.literal(getColorCode(SRMConfig.get().itemWaypointColor) + "Item");
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().itemTextSize));
                        }
                    }
                    break;
                case "bat":
                    if (SRMConfig.get().renderSecretBat) {
                        if (SRMConfig.get().secretsBatFullBlock) {
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            AnotherRenderingUtil.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.get().secretsBat, 1, 1, true));
                        } else {
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            AnotherRenderingUtil.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.get().secretsBat, 1, 1, true));
                        }
                        if (SRMConfig.get().batTextToggle) {
                            Text text = Text.literal(getColorCode(SRMConfig.get().batWaypointColor) + "Bat");
                            Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                            AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().batTextSize));
                        }
                    }
                    break;
            }
        }

        // Render Labels for Etherwarps
        if (SRMConfig.get().etherwarpsTextToggle) {
            int iEtherwarp = 1;
            for (BlockPos etherwarpPos : etherwarpPositions) {
                String colorFormatter = getColorCode(SRMConfig.get().etherwarpsWaypointColor);
                Text text = Text.literal(colorFormatter + (SRMConfig.get().etherwarpsEnumToggle ? "etherwarp" : "etherwarp " + iEtherwarp++));
                Vec3d position = new Vec3d(etherwarpPos.getX(), etherwarpPos.getY(), etherwarpPos.getZ());
                AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().etherwarpsTextSize));
            }
        }
        // Render Labels for Mines
        if (SRMConfig.get().minesTextToggle) {
            int iMine = 1;
            for (BlockPos minePos : minesPositions) {
                String colorFormatter = getColorCode(SRMConfig.get().minesWaypointColor);
                Text text = Text.literal(colorFormatter + (SRMConfig.get().minesEnumToggle ? "mine" : "mine " + iMine++));
                Vec3d position = new Vec3d(minePos.getX(), minePos.getY(), minePos.getZ());
                AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().minesTextSize));
            }
        }

        if (SRMConfig.get().interactsTextToggle) {
            int iInteract = 1;
            for (BlockPos interactPos : interactsPositions) {
                String colorFormatter = getColorCode(SRMConfig.get().interactWaypointColor); // Reused singular color
                Text text = Text.literal(colorFormatter + (SRMConfig.get().interactsEnumToggle ? "interact" : "interact " + iInteract++));
                Vec3d position = new Vec3d(interactPos.getX(), interactPos.getY(), interactPos.getZ());
                AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().interactsTextSize));
            }
        }
        // Render Labels for Superbooms
        if (SRMConfig.get().superboomsTextToggle) {
            int iSuperboom = 1;
            for (BlockPos superboomPos : superboomsPositions) {
                String colorFormatter = getColorCode(SRMConfig.get().superboomsWaypointColor);
                Text text = Text.literal(colorFormatter + (SRMConfig.get().superboomsEnumToggle ? "superboom" : "superboom " + iSuperboom++));
                Vec3d position = new Vec3d(superboomPos.getX(), superboomPos.getY(), superboomPos.getZ());
                AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().superboomsTextSize));
            }
        }
        // Render Labels for Enderpearls
        if (SRMConfig.get().enderpearlTextToggle) {
            int iEnderpearl = 1;
            for (Triple<Double, Double, Double> enderpearlPos : enderpearlPositons) {
                String colorFormatter = getColorCode(SRMConfig.get().enderpearlWaypointColor); // Reused singular color field name if applicable, or add new one
                Text text = Text.literal(colorFormatter + (SRMConfig.get().enderpearlEnumToggle ? "ender pearl" : "ender pearl " + iEnderpearl++));
                Vec3d position = new Vec3d(enderpearlPos.getOne(), enderpearlPos.getTwo(), enderpearlPos.getThree());
                AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().enderpearlTextSize));
            }
        }

        // Render the start / end waypoint text
        JsonObject waypoints = currentSecretWaypoints;
        if (waypoints != null && waypoints.get("locations") != null && waypoints.get("locations").getAsJsonArray().size() > 0 && waypoints.get("locations").getAsJsonArray().get(0) != null) {
            if (index2 == 0) {
                // First secret in the route
                JsonArray startCoords = currentSecretWaypoints.get("locations").getAsJsonArray().get(0).getAsJsonArray();
                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(startCoords.get(0).getAsInt(), startCoords.get(1).getAsInt(), startCoords.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());

                if (SRMConfig.get().startTextToggle) {
                    Text text = Text.literal(getColorCode(SRMConfig.get().startWaypointColor) + "Start");
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().startTextSize));
                }
            }
            if (index2 == Main.currentRoom.currentSecretRoute.getAsJsonArray().size() - 1) {
                JsonObject secret = currentSecretWaypoints.get("secret").getAsJsonObject();
                String type = secret.get("type").getAsString();
                JsonArray location = secret.get("location").getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection(), RoomDetection.roomCorner());

                if (SRMConfig.get().exitTextToggle && type.equals("exitroute")) {
                    Text text = Text.literal(getColorCode(SRMConfig.get().exitWaypointColor) + "Exit");
                    Vec3d position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().exitTextSize));
                }
            }
        }
    }

    public static void renderSecrets() {
        secrets = getSecrets();

        if (secrets != null) {
            for (DungeonScanner.SecretWaypoint secret : secrets) {
                String name = secret.secretName();

                if (name.contains("Chest") || name.contains("Bat") || name.contains("Wither Essence") || name.contains("Lever") || name.contains("Item")) {
                    int xPos = secret.x();
                    int yPos = secret.y();
                    int zPos = secret.z();
                    Main.checkRoomData();
                    Triple<Double, Double, Double> abs = MapUtils.relativeToActual(xPos, yPos, zPos, RoomDetection.roomDirection(), RoomDetection.roomCorner());

                    Color color = new Color(255, 255, 255);
                    if (secretLocations.contains(BlockUtils.blockPos(new BlockPos(xPos, yPos, zPos)))) {
                        continue;
                    }

                    if (name.contains("Chest") || name.contains("Wither Essence")) {
                        color = SRMConfig.get().secretsInteract;
                        if (SRMConfig.get().interactTextToggle) {
                            Text text = Text.literal(getColorCode(SRMConfig.get().interactWaypointColor) + "Interact");
                            Vec3d position = new Vec3d(abs.getOne() + 0.5, abs.getTwo() + 0.5, abs.getThree() + 0.5);
                            AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().interactTextSize));
                        }
                    } else if (name.contains("Bat")) {
                        color = SRMConfig.get().secretsBat;
                        if (SRMConfig.get().batTextToggle) {
                            Text text = Text.literal(getColorCode(SRMConfig.get().batWaypointColor) + "Bat");
                            Vec3d position = new Vec3d(abs.getOne() + 0.5, abs.getTwo() + 0.5, abs.getThree() + 0.5);
                            AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().batTextSize));
                        }
                    } else if (name.contains("Lever")) {
                        color = SRMConfig.get().interacts;
                        if (SRMConfig.get().interactsTextToggle) {
                            Text text = Text.literal(getColorCode(SRMConfig.get().interactWaypointColor) + "Interact");
                            Vec3d position = new Vec3d(abs.getOne() + 0.5, abs.getTwo() + 0.5, abs.getThree() + 0.5);
                            AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().interactsTextSize));
                        }
                    } else if (name.contains("Item")) {
                        color = SRMConfig.get().secretsItem;
                        if (SRMConfig.get().itemTextToggle) {
                            Text text = Text.literal(getColorCode(SRMConfig.get().itemWaypointColor) + "Item");
                            Vec3d position = new Vec3d(abs.getOne() + 0.5, abs.getTwo() + 0.5, abs.getThree() + 0.5);
                            AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().itemTextSize));
                        }
                    }
                    Vec3d position = new Vec3d(abs.getOne(), abs.getTwo(), abs.getThree());
                    AnotherRenderingUtil.addOutlinedBox(new RenderTypes.OutlinedBox(position, color, 1f, 1f, true));
                }
            }
        }
    }

    public static void renderLever() {
        ArrayList<DungeonScanner.SecretWaypoint> levers = new ArrayList<>();
        List<DungeonScanner.SecretWaypoint> csr = getSecrets();
        String leverNum = null;
        if (csr == null) {
            SecretUtils.renderLever = false;
        }
        if (currentLeverPos == null && csr != null) {
            for (DungeonScanner.SecretWaypoint secret : csr) {
                String name = secret.secretName();
                String category = secret.category();

                if (category.equals("chest") && leverNum == null) {
                    int x = secret.x();
                    int y = secret.y();
                    int z = secret.z();

                    Triple<Double, Double, Double> abs = MapUtils.relativeToActual(x, y, z, RoomDetection.roomDirection(), RoomDetection.roomCorner());
                    BlockPos pos = new BlockPos(abs.getOne().intValue(), abs.getTwo().intValue(), abs.getThree().intValue());
                    if (BlockUtils.blockPos(pos).equals(BlockUtils.blockPos(lastInteract))) {
                        leverNum = name.split(" ")[0];
                        leverNumber = leverNum;
                        chestName = name;
                    }
                }
                if (category.equals("lever")) {
                    if (leverNum == null) {
                        levers.add(secret);
                    } else {
                        String[] nums = leverNum.split("/");
                        for (String num : nums) {
                            if (name.contains(num)) {
                                currentLeverPos = new BlockPos(secret.x(), secret.y(), secret.z());
                                leverName = name;
                            }
                        }
                    }
                }
            }
        }

        if (currentLeverPos != null || !levers.isEmpty()) {
            if (currentLeverPos == null && leverNum != null) {
                for (DungeonScanner.SecretWaypoint secret : levers) {
                    String name = secret.secretName();
                    String[] nums = leverNum.split("/");
                    for (String num : nums) {
                        if (name.contains(num)) {
                            currentLeverPos = new BlockPos(secret.x(), secret.y(), secret.z());
                            leverName = name;
                        }
                    }
                }
            }
            if (currentLeverPos == null) {
                ChatUtils.sendChatMessage("§cLever not found :(");
            } else {
                Triple<Double, Double, Double> abs = MapUtils.relativeToActual(currentLeverPos.getX(), currentLeverPos.getY(), currentLeverPos.getZ(), RoomDetection.roomDirection(), RoomDetection.roomCorner());
                if (SRMConfig.get().secretsInteractFullBlock) {
                    Vec3d position = new Vec3d(abs.getOne(), abs.getTwo(), abs.getThree());
                    AnotherRenderingUtil.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.get().secretsInteract, 1f, 1f, true));
                } else {
                    Vec3d position = new Vec3d(abs.getOne(), abs.getTwo(), abs.getThree());
                    AnotherRenderingUtil.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.get().secretsInteract, 1f, 1f, true));
                }

                Text text = Text.literal(getColorCode(SRMConfig.get().interactWaypointColor) + "Locked chest lever");
                Vec3d position = new Vec3d(abs.getOne(), abs.getTwo(), abs.getThree());
                AnotherRenderingUtil.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().interactsTextSize));

                if (first) {
                    removeBannerTime = System.currentTimeMillis() + 5000;
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000);
                            removeBannerTime = null;
                        } catch (InterruptedException ignored) {
                        }
                    }).start();
                    first = false;
                }
            }
        } else {
            ChatUtils.sendChatMessage("§cLever not found :(");
        }
    }

    public static List<DungeonScanner.SecretWaypoint> getSecrets() {
        Main.checkRoomData();
        if (Main.currentRoom == null || Main.currentRoom.name == null) return null;

        if (secrets == null) {
            String roomName = Main.currentRoom.name;
            secrets = DungeonScanner.ROOMS_WAYPOINTS.get(roomName);

            if (secrets == null) {
                for (String key : DungeonScanner.ROOMS_WAYPOINTS.keySet()) {
                    if (key.equalsIgnoreCase(roomName)) {
                        secrets = DungeonScanner.ROOMS_WAYPOINTS.get(key);
                        break;
                    }
                }
            }
        }
        return secrets;
    }

    public static void resetValues() {
        renderLever = false;
        currentLeverPos = null;
        removeBannerTime = null;
        first = true;
        chestName = null;
        leverName = null;
        leverNumber = null;
        secrets = null;
    }
}
