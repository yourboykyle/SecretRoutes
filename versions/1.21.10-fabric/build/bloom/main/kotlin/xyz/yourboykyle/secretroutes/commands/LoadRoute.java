//#if FORGE && MC == 1.8.9
//$$ // TODO: update this file for multi versioning (1.8.9 -> 1.21.10)
//$$ /*
//$$  * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
//$$  * Copyright 2024 yourboykyle & R-aMcC
//$$  *
//$$  * <DO NOT REMOVE THIS COPYRIGHT NOTICE>
//$$  *
//$$  * This program is free software: you can redistribute it and/or modify
//$$  * it under the terms of the GNU General Public License as published by
//$$  * the Free Software Foundation, either version 3 of the License, or
//$$  * (at your option) any later version.
//$$  *
//$$  * This program is distributed in the hope that it will be useful,
//$$  * but WITHOUT ANY WARRANTY; without even the implied warranty of
//$$  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//$$  * GNU General Public License for more details.
//$$  *
//$$  * You should have received a copy of the GNU General Public License along
//$$  * with this program.  If not, see <https://www.gnu.org/licenses/>.
//$$  */
//$$
//$$ package xyz.yourboykyle.secretroutes.commands;
//$$
//$$ import com.google.gson.Gson;
//$$ import com.google.gson.GsonBuilder;
//$$ import com.google.gson.JsonObject;
//$$ import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.command.CommandBase;
//$$ import net.minecraft.command.CommandException;
//$$ import net.minecraft.command.ICommandSender;
//$$ import net.minecraft.util.ChatComponentText;
//$$ import xyz.yourboykyle.secretroutes.Main;
//$$ import xyz.yourboykyle.secretroutes.utils.LogUtils;
//$$ import xyz.yourboykyle.secretroutes.utils.Room;
//$$
//$$ import java.io.File;
//$$ import java.io.FileReader;
//$$ import java.io.IOException;
//$$
//$$ public class LoadRoute extends CommandBase {
//$$     @Override
//$$     public String getCommandName() {
//$$         return "loadroute";
//$$     }
//$$
//$$     @Override
//$$     public String getCommandUsage(ICommandSender sender) {
//$$         return "/loadroute";
//$$     }
//$$
//$$     @Override
//$$     public void processCommand(ICommandSender sender, String[] args) throws CommandException {
//$$         // Load the route
//$$         String filePath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "routes.json";
//$$
//$$         try {
//$$             Gson gson = new GsonBuilder().create();
//$$             FileReader reader = new FileReader(filePath);
//$$
//$$             JsonObject data = gson.fromJson(reader, JsonObject.class);
//$$             Main.currentRoom = new Room(RoomDetection.roomName, filePath);
//$$             Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Loaded Route."));
//$$         } catch (IOException e) {
//$$             LogUtils.error(e);
//$$         }
//$$     }
//$$
//$$     @Override
//$$     public int getRequiredPermissionLevel() {
//$$         return 0;
//$$     }
//$$ }
//#endif
