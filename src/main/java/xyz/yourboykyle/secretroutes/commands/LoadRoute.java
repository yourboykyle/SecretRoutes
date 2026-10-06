//#if FABRIC
/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2024 yourboykyle & R-aMcC & christechs
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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.dungeons.Room;
import xyz.yourboykyle.secretroutes.utils.RoomDirectionUtils;
import xyz.yourboykyle.secretroutes.utils.RouteFileCache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class LoadRoute {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(LoadRoute::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal("loadroute")
                .executes(LoadRoute::executeCommand));
    }

    private static int executeCommand(CommandContext<FabricClientCommandSource> context) {
        // Load the route from Downloads folder
        String filePath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "routes.json";

        try {
            String roomName = RoomDirectionUtils.roomName();
            Path routeFile = Path.of(filePath);
            RouteFileCache.SHARED.invalidate(routeFile);
            RouteFileCache.SHARED.roomData(routeFile, roomName);
            Room replacement = new Room(roomName, filePath);
            replacement.inheritPersonalBestVisit(Main.currentRoom);
            Main.currentRoom = replacement;

            context.getSource().sendFeedback(
                    Component.literal("Loaded route for room: " + RoomDirectionUtils.roomName()).withStyle(ChatFormatting.GREEN)
            );

        } catch (IOException e) {
            context.getSource().sendError(
                    Component.literal("Failed to load route: " + e.getMessage())
            );
            LogUtils.error(e);
        }

        return 1;
    }
}
//#endif
