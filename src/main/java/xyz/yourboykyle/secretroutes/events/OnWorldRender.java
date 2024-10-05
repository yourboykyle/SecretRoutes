/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2023 yourboykyle
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

package xyz.yourboykyle.secretroutes.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.DungeonManager;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.utils.MapUtils;
import io.github.quantizr.dungeonrooms.utils.Utils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.RenderUtils;
import xyz.yourboykyle.secretroutes.utils.RotationUtils;
import xyz.yourboykyle.secretroutes.utils.SecretRoutesRenderUtils;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;
import xyz.yourboykyle.secretroutes.events.OnChatReceive;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendVerboseMessage;

public class OnWorldRender {
    private final static String verboseTAG = "Rendering";

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        try {
            // Make sure the player is actually in a dungeon
            Utils.checkForCatacombs();
            if (!Utils.inCatacombs || DungeonManager.gameStage != 2 || !SRMConfig.modEnabled) {
                return;
            }
            if(OnChatReceive.isAllFound() && !SRMConfig.renderComplete){
                return;
            }


            if(SRMConfig.allSecrets){
                JsonArray csr = Main.currentRoom.currentSecretRoute;
                if(csr != null){
                    for(int i = Main.currentRoom.currentSecretIndex; i<csr.size(); i++){
                        renderingCallback(csr.get(i).getAsJsonObject(), event, i);
                    }
                }

            }else{
                renderingCallback(Main.currentRoom.currentSecretWaypoints, event, Main.currentRoom.currentSecretIndex);
            }


        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public void renderingCallback(JsonObject currentSecretWaypoints, RenderWorldLastEvent event, int index2){
        ArrayList<BlockPos> etherwarpPositions = new ArrayList<>();
        ArrayList<BlockPos> minesPositions = new ArrayList<>();
        ArrayList<BlockPos> interactsPositions = new ArrayList<>();
        ArrayList<BlockPos> superboomsPositions = new ArrayList<>();
        ArrayList<Triple<Double, Double, Double>> enderpearlPositons = new ArrayList<>();
        ArrayList<Tuple<Float, Float>> enderpearlAngles = new ArrayList<>();

        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();


        // Render the etherwarps
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("etherwarps") != null && (!SRMConfig.allSecrets || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex)) {
            JsonArray etherwarpLocations = currentSecretWaypoints.get("etherwarps").getAsJsonArray();
            for (JsonElement etherwarpLocationElement : etherwarpLocations) {
                JsonArray etherwarpLocation = etherwarpLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(etherwarpLocation.get(0).getAsInt(), etherwarpLocation.get(1).getAsInt(), etherwarpLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

                etherwarpPositions.add(pos);
                if(SRMConfig.etherwarpFullBlock){
                    SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.etherWarp, 1, 1);
                }else{
                    SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.etherWarp, 1, 1);
                }
            }
        }

        // Render the mines
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("mines") != null && (!SRMConfig.allSecrets || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex)) {
            JsonArray mineLocations = currentSecretWaypoints.get("mines").getAsJsonArray();
            for (JsonElement mineLocationElement : mineLocations) {
                JsonArray mineLocation = mineLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(mineLocation.get(0).getAsInt(), mineLocation.get(1).getAsInt(), mineLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
                minesPositions.add(pos);
                if(SRMConfig.mineFullBlock){
                    SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.mine, 1, 1);
                }else {
                    SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.mine, 1, 1);
                }
            }
        }

        // Render the interacts
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("interacts") != null && (!SRMConfig.allSecrets || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex)) {
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
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("tnts") != null && (!SRMConfig.allSecrets || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex)) {
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
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("locations") != null && SRMConfig.lineType == 1  && (!SRMConfig.allSecrets || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex)) {
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
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("enderpearls") != null && (!SRMConfig.allSecrets || SRMConfig.allSteps || index2 == Main.currentRoom.currentSecretIndex)) {
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
                if(SRMConfig.enderPearlFullBlock){
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
                RenderUtils.drawNormalLine(posX + 0.25F, posY + 1.62F, posZ + 0.25F, newX, newY, newZ, SRMConfig.pearlLineColor, event.partialTicks, true, SRMConfig.pearlLineWidth);
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



            if (type.equals("interact")) {
                if(SRMConfig.secretsInteractFullBlock){
                    SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.secretsInteract, 1, 1);
                }else{
                    SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.secretsInteract, 1, 1);
                }
                if (SRMConfig.interactTextToggle) {
                    SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.interactWaypointColorIndex) + "Interact", SRMConfig.interactTextSize, event.partialTicks);
                }
            } else if (type.equals("item")) {
                if(SRMConfig.secretsItemFullBlock){
                    SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.secretsItem, 1, 1);
                }else{
                    SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.secretsItem, 1, 1);
                }

                if (SRMConfig.itemTextToggle) {
                    SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.itemWaypointColorIndex) + "Item", SRMConfig.itemTextSize, event.partialTicks);
                }
            } else if (type.equals("bat")) {
                if(SRMConfig.secretsInteractFullBlock){
                    SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.secretsBat, 1, 1);
                }else{
                    SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), SRMConfig.secretsBat, 1, 1);
                }
                if (SRMConfig.batTextToggle) {
                    SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.batWaypointColorIndex) + "Bat", SRMConfig.batTextSize, event.partialTicks);
                }
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
        GlStateManager.enableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

}