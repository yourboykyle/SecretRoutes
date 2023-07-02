package io.github.quantizr.dungeonrooms.handlers;

import com.google.gson.JsonObject;
import io.github.quantizr.dungeonrooms.DungeonRooms;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class OpenLink {

    public static void checkForLink(String type){
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (RoomDetection.roomName.equals("undefined") || DungeonRooms.roomsJson.get(RoomDetection.roomName) == null) {
            //Unknown room
            return;
        }

        JsonObject roomJson = DungeonRooms.roomsJson.get(RoomDetection.roomName).getAsJsonObject();
        if (roomJson.get("dsg").getAsString().equals("null") && roomJson.get("sbp") == null) {
            //No channels / images
            return;
        }

        switch (type) {
            case "gui":
                //
                break;
            case "dsg":
                OpenLink.openDiscord("client");
                break;
            case "sbp":
                if (DungeonRooms.usingSBPSecrets) {
                    OpenLink.openSBPSecrets();
                } else {
                    String sbpURL = "https://discord.gg/2UjaFqfPwJ";
                    ChatComponentText sbp = new ChatComponentText(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.UNDERLINE + sbpURL);
                    sbp.setChatStyle(sbp.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, sbpURL)));
                    //SBP
                }
                break;
        }

    }

    public static void openDiscord(String type) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (RoomDetection.roomName.equals("undefined") || DungeonRooms.roomsJson.get(RoomDetection.roomName) == null) {
            //Not in a room
            return;
        }

        JsonObject roomJson = DungeonRooms.roomsJson.get(RoomDetection.roomName).getAsJsonObject();
        if (roomJson.get("dsg").getAsString().equals("null")) {
            //No channels
            return;
        }
    }

    public static void openSBPSecrets() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (RoomDetection.roomName.equals("undefined") || DungeonRooms.roomsJson.get(RoomDetection.roomName) == null) {
            //Not in a room
            return;
        }

        JsonObject roomJson = DungeonRooms.roomsJson.get(RoomDetection.roomName).getAsJsonObject();
        if (roomJson.get("sbp") == null) {
            //No SBP
            return;
        }
        String name = roomJson.get("sbp").getAsString();

        String category = roomJson.get("category").getAsString();
        switch (category) {
            case "Puzzle":
            case "Trap":
                category = "puzzles";
                break;
            case "L-shape":
                category = "L";
                break;
        }
        ClientCommandHandler.instance.executeCommand(FMLClientHandler.instance().getClientPlayerEntity(), "/secretoverride " + category + " " + name);
    }
}
