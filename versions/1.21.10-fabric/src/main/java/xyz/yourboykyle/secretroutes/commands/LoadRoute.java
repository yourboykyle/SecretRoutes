/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2024 yourboykyle & R-aMcC
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

package xyz.yourboykyle.secretroutes.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.RoomDirectionUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class LoadRoute {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(LoadRoute::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("loadroute")
                .executes(LoadRoute::executeCommand));
    }

    private static int executeCommand(CommandContext<FabricClientCommandSource> context) {
        // Load the route from Downloads folder
        String filePath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "routes.json";

        try {
            Gson gson = new GsonBuilder().create();
            FileReader reader = new FileReader(filePath);

            gson.fromJson(reader, JsonObject.class);
            reader.close();

            Main.currentRoom = new Room(RoomDirectionUtils.roomName(), filePath);

            context.getSource().sendFeedback(
                    Text.literal("Loaded route for room: " + RoomDirectionUtils.roomName()).formatted(Formatting.GREEN)
            );

        } catch (IOException e) {
            context.getSource().sendError(
                    Text.literal("Failed to load route: " + e.getMessage())
            );
            LogUtils.error(e);
        }

        return 1;
    }
}
