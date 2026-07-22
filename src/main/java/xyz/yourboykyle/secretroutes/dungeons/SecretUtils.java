//#if FABRIC
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

package xyz.yourboykyle.secretroutes.dungeons;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.joml.Vector3d;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.dungeons.rendering.RenderTypes;
import xyz.yourboykyle.secretroutes.dungeons.rendering.RenderingBackend;
import xyz.yourboykyle.secretroutes.utils.*;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;
import xyz.yourboykyle.secretroutes.utils.multistorage.Tuple;

import java.awt.*;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SecretUtils {
    public static JsonArray secrets = null;
    public static boolean renderLever = false;
    public static BlockPos currentLeverPos = null;
    public static BlockPos lastInteract;
    public static Long removeBannerTime = null;
    public static boolean first = true;
    public static String chestName = null;
    public static String leverName = null;
    public static String leverNumber = null;
    public static ArrayList<String> secretLocations = new ArrayList<>();

    public static final Set<BlockPos> brokenBlocks = new HashSet<>();

    public static int activeEtherwarpStep = -1;
    public static int targetEtherwarpIndex = 0;

    private static String getColorCode(SRMConfig.TextColor color) {
        return color.formatting.toString();
    }

    private static boolean shouldRenderRouteStep(int stepIndex) {
        int visibleRouteSteps = Math.max(1, Math.min(5, SRMConfig.get().visibleRouteSteps));
        int currentStepIndex = Main.currentRoom.currentSecretIndex;
        return SRMConfig.get().wholeRoute || SRMConfig.get().allSteps || (stepIndex >= currentStepIndex && stepIndex < currentStepIndex + visibleRouteSteps);
    }

    private static Color colorForRouteStep(int stepIndex, Color currentColor, Color secondStepColor) {
        return stepIndex == Main.currentRoom.currentSecretIndex ? currentColor : secondStepColor;
    }

    public static void renderingCallback(JsonObject waypoints, int index2) {
        if (waypoints == null) return;
        if (!shouldRenderRouteStep(index2)) return;

        // Player to Secret Line
        if (SRMConfig.get().playerWaypointLine) {
            BlockPos nextSecret = Main.currentRoom.getSecretLocation();
            if (nextSecret != null) {
                Vector3d point = new Vector3d(nextSecret.getX() + 0.5, nextSecret.getY() + 0.5, nextSecret.getZ() + 0.5);
                Color semiTransparent = new Color(SRMConfig.get().lineColor.getRed(), SRMConfig.get().lineColor.getGreen(), SRMConfig.get().lineColor.getBlue(), 127);
                RenderingBackend.addLineFromCursor(new RenderTypes.LineFromCursor(point, semiTransparent, SRMConfig.get().width));
            }
        }

        renderWaypointCategory(
                waypoints, index2, "etherwarps", SRMConfig.get().renderEtherwarps, SRMConfig.get().etherWarp, SRMConfig.get().secondStepEtherWarp,
                SRMConfig.get().etherwarpFullBlock, SRMConfig.get().etherwarpsTextToggle, SRMConfig.get().etherwarpNumberingToggle, SRMConfig.get().etherwarpsWaypointColor, "etherwarp", SRMConfig.get().etherwarpsTextSize, true
        );

        renderWaypointCategory(
                waypoints, index2, "mines", SRMConfig.get().renderMines, SRMConfig.get().mine, SRMConfig.get().secondStepMine,
                SRMConfig.get().mineFullBlock, SRMConfig.get().minesTextToggle, !SRMConfig.get().minesEnumToggle, SRMConfig.get().minesWaypointColor, "mine", SRMConfig.get().minesTextSize, false
        );

        renderWaypointCategory(
                waypoints, index2, "interacts", SRMConfig.get().renderInteracts, SRMConfig.get().interacts, SRMConfig.get().secondStepInteracts,
                SRMConfig.get().interactsFullBlock, SRMConfig.get().interactsTextToggle, !SRMConfig.get().interactsEnumToggle, SRMConfig.get().interactWaypointColor, "interact", SRMConfig.get().interactsTextSize, false
        );

        renderWaypointCategory(
                waypoints, index2, "tnts", SRMConfig.get().renderSuperboom, SRMConfig.get().superbooms, SRMConfig.get().secondStepSuperbooms,
                SRMConfig.get().superboomsFullBlock, SRMConfig.get().superboomsTextToggle, !SRMConfig.get().superboomsEnumToggle, SRMConfig.get().superboomsWaypointColor, "superboom", SRMConfig.get().superboomsTextSize, false
        );

        // Render Normal Lines
        if (waypoints.has("locations") && SRMConfig.get().lineType == SRMConfig.LineType.LINES) {
            renderConnectingLines(waypoints.getAsJsonArray("locations"));
        }

        // Render Ender Pearls
        if (waypoints.has("enderpearls") && SRMConfig.get().renderEnderpearls) {
            renderEnderPearls(waypoints, index2);
        }

        // Render Current Secret Target
        if (waypoints.has("secret")) {
            renderCurrentSecret(waypoints.getAsJsonObject("secret"), index2);
        }

        // Render Start/Exit Labels
        renderStartAndExitLabels(waypoints, index2);
    }

    private static void renderWaypointCategory(JsonObject waypoints, int stepIndex, String jsonKey, boolean isEnabled, Color primaryColor, Color secondaryColor, boolean isFullBlock, boolean textToggle, boolean showNumbering, SRMConfig.TextColor textColor, String textPrefix, float textSize, boolean isEtherwarp) {
        if (!isEnabled || !waypoints.has(jsonKey)) return;

        JsonArray locations = waypoints.getAsJsonArray(jsonKey);
        int counter = 1;
        Color boxColor = colorForRouteStep(stepIndex, primaryColor, secondaryColor);

        boolean isActiveStep = (stepIndex == Main.currentRoom.currentSecretIndex);

        if (isEtherwarp && isActiveStep) {
            if (activeEtherwarpStep != stepIndex) {
                activeEtherwarpStep = stepIndex;
                targetEtherwarpIndex = 0;
            }
        }

        for (int i = 0; i < locations.size(); i++) {
            JsonElement element = locations.get(i);
            JsonArray loc = element.getAsJsonArray();
            int currentNum = counter++;

            BlockPos pos = RoomRotationUtils.relativeToActual(
                    new BlockPos(loc.get(0).getAsInt(), loc.get(1).getAsInt(), loc.get(2).getAsInt()),
                    RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner()
            );

            if (brokenBlocks.contains(pos)) continue;

            if (jsonKey.equals("mines") && Minecraft.getInstance().level != null) {
                if (Minecraft.getInstance().level.getBlockState(pos).isAir()) {
                    continue;
                }
            }

            Vector3d position = new Vector3d(pos.getX(), pos.getY(), pos.getZ());

            if (isEtherwarp && isActiveStep && !SRMConfig.get().wholeRoute && SRMConfig.get().playerToEtherwarp) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    if (i == targetEtherwarpIndex) {
                        double dx = player.getX() - (pos.getX() + 0.5);
                        double dy = player.getY() - (pos.getY() + 1.0);
                        double dz = player.getZ() - (pos.getZ() + 0.5);

                        if ((dx * dx + dy * dy + dz * dz) <= 2.0) {
                            targetEtherwarpIndex++;
                        }
                    }

                    if (i == targetEtherwarpIndex) {
                        Vector3d point = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                        RenderingBackend.addLineFromCursor(new RenderTypes.LineFromCursor(point, new Color(0, 255, 255), SRMConfig.get().width));
                    }
                }
            }

            String textContent = null;
            if (textToggle) {
                textContent = showNumbering ? textPrefix + " " + currentNum : textPrefix;
            }

            submitBoxAndText(position, boxColor, isFullBlock, textToggle, textColor, textContent, textSize, false);
        }
    }

    private static void renderConnectingLines(JsonArray lineLocations) {
        if (!SRMConfig.get().modEnabled) return;
        List<Vector3d> linePoints = new ArrayList<>();

        for (JsonElement element : lineLocations) {
            JsonArray loc = element.getAsJsonArray();
            Triple<Double, Double, Double> linePos = RoomRotationUtils.relativeToActual(
                    loc.get(0).getAsDouble(), loc.get(1).getAsDouble(), loc.get(2).getAsDouble(),
                    RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner()
            );
            linePoints.add(new Vector3d(linePos.getOne() + 0.5, linePos.getTwo() + 0.5, linePos.getThree() + 0.5));
        }

        RenderingBackend.addLinesFromPoints(linePoints.toArray(new Vector3d[0]), SRMConfig.get().lineColor, SRMConfig.get().width, SRMConfig.get().renderLinesThroughWalls);
    }

    private static void renderEnderPearls(JsonObject waypoints, int stepIndex) {
        JsonArray pearlLocations = waypoints.getAsJsonArray("enderpearls");
        JsonArray enderpearlAnglesArray = waypoints.getAsJsonArray("enderpearlangles");

        int index = 0;
        Color enderpearlColor = colorForRouteStep(stepIndex, SRMConfig.get().enderpearls, SRMConfig.get().secondStepEnderpearls);

        for (JsonElement element : pearlLocations) {
            JsonArray loc = element.getAsJsonArray();
            JsonArray angleObj = enderpearlAnglesArray.get(index).getAsJsonArray();

            double posX = loc.get(0).getAsDouble();
            double posY = loc.get(1).getAsDouble();
            double posZ = loc.get(2).getAsDouble();

            Triple<Double, Double, Double> positions = RoomRotationUtils.relativeToActual(posX, posY, posZ, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());
            posX = positions.getOne() - 0.25;
            posY = positions.getTwo();
            posZ = positions.getThree() - 0.25;

            Vector3d boxPos = new Vector3d(posX, posY, posZ);

            String textContent = SRMConfig.get().enderpearlEnumToggle ? "ender pearl" : "ender pearl " + (index + 1);

            submitBoxAndText(boxPos, enderpearlColor, SRMConfig.get().enderpearlFullBlock,
                    SRMConfig.get().enderpearlTextToggle, SRMConfig.get().enderpearlWaypointColor, textContent, SRMConfig.get().enderpearlTextSize, false);

            double pitch = angleObj.get(0).getAsDouble();
            double yaw = RotationUtils.relativeToActualYaw(angleObj.get(1).getAsFloat(), RoomDirectionUtils.roomDirection()) + 90;
            double yawRad = Math.toRadians(yaw);
            double pitchRad = Math.toRadians(pitch);

            double length = 10.0D;
            double x = -Math.sin(yawRad) * Math.cos(pitchRad);
            double y = -Math.sin(pitchRad);
            double z = Math.cos(yawRad) * Math.cos(pitchRad);

            double sideLength = Math.sqrt(x * x + y * y + z * z);
            x /= sideLength; y /= sideLength; z /= sideLength;

            Vector3d start = new Vector3d(posX + 0.25F, posY + 1.62F, posZ + 0.25F);
            Vector3d end = new Vector3d(posX + x * length + 0.25, posY + y * length + 1.62, posZ + z * length + 0.25);

            RenderingBackend.addLine(new RenderTypes.Line(start, end, SRMConfig.get().pearlLineColor, SRMConfig.get().pearlLineWidth, true));
            index++;
        }
    }

    private static void renderCurrentSecret(JsonObject secret, int stepIndex) {
        String type = secret.get("type").getAsString();
        JsonArray loc = secret.get("location").getAsJsonArray();
        BlockPos pos = RoomRotationUtils.relativeToActual(new BlockPos(loc.get(0).getAsInt(), loc.get(1).getAsInt(), loc.get(2).getAsInt()), RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());
        Vector3d position = new Vector3d(pos.getX(), pos.getY(), pos.getZ());

        switch (type) {
            case "interact":
                if (SRMConfig.get().renderSecretIteract) {
                    Color c = colorForRouteStep(stepIndex, SRMConfig.get().secretsInteract, SRMConfig.get().secondStepSecretsInteract);
                    submitBoxAndText(position, c, SRMConfig.get().secretsInteractFullBlock, SRMConfig.get().interactTextToggle, SRMConfig.get().interactWaypointColor, "Interact", SRMConfig.get().interactTextSize, true);
                }
                break;
            case "item":
                if (SRMConfig.get().renderSecretsItem) {
                    Color c = colorForRouteStep(stepIndex, SRMConfig.get().secretsItem, SRMConfig.get().secondStepSecretsItem);
                    submitBoxAndText(position, c, SRMConfig.get().secretsItemFullBlock, SRMConfig.get().itemTextToggle, SRMConfig.get().itemWaypointColor, "Item", SRMConfig.get().itemTextSize, true);
                }
                break;
            case "bat":
                if (SRMConfig.get().renderSecretBat) {
                    Color c = colorForRouteStep(stepIndex, SRMConfig.get().secretsBat, SRMConfig.get().secondStepSecretsBat);
                    submitBoxAndText(position, c, SRMConfig.get().secretsBatFullBlock, SRMConfig.get().batTextToggle, SRMConfig.get().batWaypointColor, "Bat", SRMConfig.get().batTextSize, true);
                }
                break;
        }
    }

    private static void renderStartAndExitLabels(JsonObject waypoints, int index2) {
        if (!waypoints.has("locations") || waypoints.getAsJsonArray("locations").isEmpty()) return;

        if (index2 == 0 && SRMConfig.get().startTextToggle) {
            JsonArray startCoords = waypoints.getAsJsonArray("locations").get(0).getAsJsonArray();
            BlockPos pos = RoomRotationUtils.relativeToActual(new BlockPos(startCoords.get(0).getAsInt(), startCoords.get(1).getAsInt(), startCoords.get(2).getAsInt()), RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());
            RenderingBackend.addWorldText(new RenderTypes.WorldText(Component.literal(getColorCode(SRMConfig.get().startWaypointColor) + "Start"), new Vector3d(pos.getX(), pos.getY(), pos.getZ()), true, SRMConfig.get().startTextSize));
        }

        if (index2 == Main.currentRoom.currentSecretRoute.getAsJsonArray().size() - 1 && SRMConfig.get().exitTextToggle) {
            JsonObject secret = waypoints.getAsJsonObject("secret");
            if (secret.get("type").getAsString().equals("exitroute")) {
                JsonArray loc = secret.getAsJsonArray("location");
                BlockPos pos = RoomRotationUtils.relativeToActual(new BlockPos(loc.get(0).getAsInt(), loc.get(1).getAsInt(), loc.get(2).getAsInt()), RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());
                RenderingBackend.addWorldText(new RenderTypes.WorldText(Component.literal(getColorCode(SRMConfig.get().exitWaypointColor) + "Exit"), new Vector3d(pos.getX(), pos.getY(), pos.getZ()), true, SRMConfig.get().exitTextSize));
            }
        }
    }

    public static void renderSecrets() {
        secrets = getSecrets();
        if (secrets == null) return;

        for (JsonElement secret : secrets) {
            JsonObject secretInfos = secret.getAsJsonObject();
            String name = secretInfos.get("secretName").getAsString();

            if (!name.contains("Chest") && !name.contains("Bat") && !name.contains("Wither Essence") && !name.contains("Lever") && !name.contains("Item")) continue;

            int xPos = secretInfos.get("x").getAsInt();
            int yPos = secretInfos.get("y").getAsInt();
            int zPos = secretInfos.get("z").getAsInt();
            if (secretLocations.contains(BlockUtils.blockPos(new BlockPos(xPos, yPos, zPos)))) continue;

            Triple<Double, Double, Double> abs = RoomRotationUtils.relativeToActual(xPos, yPos, zPos, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());
            Vector3d boxPos = new Vector3d(abs.getOne(), abs.getTwo(), abs.getThree());

            if (name.contains("Chest") || name.contains("Wither Essence")) {
                submitBoxAndText(boxPos, SRMConfig.get().secretsInteract, false, SRMConfig.get().interactTextToggle, SRMConfig.get().interactWaypointColor, "Interact", SRMConfig.get().interactTextSize, true);
            } else if (name.contains("Bat")) {
                submitBoxAndText(boxPos, SRMConfig.get().secretsBat, false, SRMConfig.get().batTextToggle, SRMConfig.get().batWaypointColor, "Bat", SRMConfig.get().batTextSize, true);
            } else if (name.contains("Lever")) {
                submitBoxAndText(boxPos, SRMConfig.get().interacts, false, SRMConfig.get().interactsTextToggle, SRMConfig.get().interactWaypointColor, "Interact", SRMConfig.get().interactsTextSize, true);
            } else if (name.contains("Item")) {
                submitBoxAndText(boxPos, SRMConfig.get().secretsItem, false, SRMConfig.get().itemTextToggle, SRMConfig.get().itemWaypointColor, "Item", SRMConfig.get().itemTextSize, true);
            }
        }
    }

    public static void renderLever() {
        ArrayList<JsonElement> levers = new ArrayList<>();
        JsonArray csr = getSecrets();
        String leverNum = null;
        if (csr == null) {
            SecretUtils.renderLever = false;
        }
        if (currentLeverPos == null && csr != null) {
            for (JsonElement secret : csr) {
                JsonObject secretInfos = secret.getAsJsonObject();
                String name = secretInfos.get("secretName").getAsString();
                String category = secretInfos.get("category").getAsString();
                if (category.equals("chest") && leverNum == null) {
                    int x = secretInfos.get("x").getAsInt();
                    int y = secretInfos.get("y").getAsInt();
                    int z = secretInfos.get("z").getAsInt();

                    Triple<Double, Double, Double> abs = RoomRotationUtils.relativeToActual(x, y, z, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());
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
                                currentLeverPos = new BlockPos(secretInfos.get("x").getAsInt(), secretInfos.get("y").getAsInt(), secretInfos.get("z").getAsInt());
                                leverName = name;
                            }
                        }

                    }
                }

            }
        }


        if (currentLeverPos != null || !levers.isEmpty()) {
            if (currentLeverPos == null && leverNum != null) {
                for (JsonElement secret : levers) {
                    JsonObject secretInfos = secret.getAsJsonObject();
                    String name = secretInfos.get("secretName").getAsString();
                    String[] nums = leverNum.split("/");
                    for (String num : nums) {
                        if (name.contains(num)) {
                            currentLeverPos = new BlockPos(secretInfos.get("x").getAsInt(), secretInfos.get("y").getAsInt(), secretInfos.get("z").getAsInt());
                            leverName = name;
                        }
                    }
                }
            }
            if (currentLeverPos == null) {
                ChatUtils.sendChatMessage("§cLever not found :(");
            } else {
                Triple<Double, Double, Double> abs = RoomRotationUtils.relativeToActual(currentLeverPos.getX(), currentLeverPos.getY(), currentLeverPos.getZ(), RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());
                if (SRMConfig.get().secretsInteractFullBlock) {
                    Vector3d position = new Vector3d(abs.getOne(), abs.getTwo(), abs.getThree());
                    RenderingBackend.addFilledBox(new RenderTypes.FilledBox(position, SRMConfig.get().secretsInteract, 1f, 1f, SRMConfig.get().renderLinesThroughWalls));
                } else {
                    Vector3d position = new Vector3d(abs.getOne(), abs.getTwo(), abs.getThree());
                    RenderingBackend.addOutlinedBox(new RenderTypes.OutlinedBox(position, SRMConfig.get().secretsInteract, 1f, 1f, SRMConfig.get().renderLinesThroughWalls));
                }

                if (SRMConfig.get().interactsTextToggle) {
                    Component text = Component.literal(getColorCode(SRMConfig.get().interactsWaypointColor) + "Locked chest lever");
                    Vector3d position = new Vector3d(abs.getOne(), abs.getTwo(), abs.getThree());
                    RenderingBackend.addWorldText(new RenderTypes.WorldText(text, position, true, SRMConfig.get().interactsTextSize));
                }

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

    private static void submitBoxAndText(Vector3d pos, Color boxColor, boolean isFull, boolean textToggle, SRMConfig.TextColor textColor, String textContent, float textSize, boolean shiftTextUp) {
        if (isFull) RenderingBackend.addFilledBox(new RenderTypes.FilledBox(pos, boxColor, 1, 1, SRMConfig.get().renderLinesThroughWalls));
        else RenderingBackend.addOutlinedBox(new RenderTypes.OutlinedBox(pos, boxColor, 1, 1, SRMConfig.get().renderLinesThroughWalls));

        if (textToggle && textContent != null) {
            Vector3d textPos = shiftTextUp ? new Vector3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) : pos;
            RenderingBackend.addWorldText(new RenderTypes.WorldText(Component.literal(getColorCode(textColor) + textContent), textPos, true, textSize));
        }
    }

    public static JsonArray getSecrets() {
        if (Main.currentRoom == null || Main.currentRoom.name == null) return null;

        String roomName = Main.currentRoom.name.toLowerCase();
        if (secrets == null) {
            try (Reader reader = new InputStreamReader(Main.class.getResourceAsStream("/assets/secretroutesmod/secretlocations.json"))) {
                JsonObject object = new JsonParser().parse(reader).getAsJsonObject();
                for (String key : object.keySet()) {
                    if (key.equalsIgnoreCase(roomName)) {
                        secrets = object.getAsJsonArray(key);
                        break;
                    }
                }
            } catch (Exception e) {
                LogUtils.error(e);
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

        activeEtherwarpStep = -1;
        targetEtherwarpIndex = 0;

        brokenBlocks.clear();
    }
}
//#endif