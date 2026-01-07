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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.RoomDetection;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Recording {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(Recording::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("recording")
                .executes(Recording::openGui)
                .then(literal("start")
                        .executes(Recording::executeStart))
                .then(literal("stop")
                        .executes(Recording::executeStop))
                .then(literal("export")
                        .executes(Recording::executeExport))
                .then(literal("getroom")
                        .executes(Recording::executeGetRoom))
                .then(literal("setbat")
                        .executes(Recording::executeSetBat))
                .then(literal("setexit")
                        .executes(Recording::executeSetExit))
                .then(literal("import")
                        .then(argument("filename", StringArgumentType.greedyString())
                                .executes(Recording::executeImport))));
    }

    private static int openGui(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.send(() -> client.setScreen(SRMConfig.getScreen(client.currentScreen)));
        return 1;
    }

    private static int executeStart(CommandContext<FabricClientCommandSource> context) {
        Main.routeRecording.startRecording();
        return 1;
    }

    private static int executeStop(CommandContext<FabricClientCommandSource> context) {
        Main.routeRecording.stopRecording();
        return 1;
    }

    private static int executeExport(CommandContext<FabricClientCommandSource> context) {
        Main.routeRecording.exportAllRoutes();
        return 1;
    }

    private static int executeGetRoom(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(
                Text.literal("Room Name: " + RoomDetection.roomName() +
                                ", Room Corner: " + RoomDetection.roomCorner() +
                                ", Room Direction: " + RoomDetection.roomDirection())
                        .formatted(Formatting.BLUE)
        );
        return 1;
    }

    private static int executeSetBat(CommandContext<FabricClientCommandSource> context) {
        if (Main.routeRecording.recording) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) {
                context.getSource().sendError(Text.literal("Player not found"));
                return 0;
            }

            BlockPos playerPos = player.getBlockPos();
            BlockPos targetPos = new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
            targetPos = targetPos.add(-1, 2, -1); // Block above the player, the -1 on X and Z have to be like that, trust the process

            Main.routeRecording.addWaypoint(Room.SECRET_TYPES.BAT, targetPos);
            Main.routeRecording.newSecret();
            Main.routeRecording.setRecordingMessage("Added bat secret waypoint.");
        } else {
            context.getSource().sendError(Text.literal("Route recording is not enabled. Run /recording start"));
        }
        return 1;
    }

    private static int executeSetExit(CommandContext<FabricClientCommandSource> context) {
        if (Main.routeRecording.recording) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) {
                context.getSource().sendError(Text.literal("Player not found"));
                return 0;
            }

            BlockPos playerPos = player.getBlockPos();
            BlockPos targetPos = new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
            targetPos = targetPos.add(-1, 0, -1);

            Main.routeRecording.addWaypoint(Room.SECRET_TYPES.EXITROUTE, targetPos);
            Main.routeRecording.newSecret();
            Main.routeRecording.stopRecording(); // Exiting the route, it should be stopped
            Main.routeRecording.setRecordingMessage("Added route exit waypoint & stopped recording.");
            LogUtils.info("Added route exit waypoint & stopped recording.");
        } else {
            context.getSource().sendError(Text.literal("Route recording is not enabled. Run /recording start"));
        }
        return 1;
    }

    private static int executeImport(CommandContext<FabricClientCommandSource> context) {
        String filename = StringArgumentType.getString(context, "filename");
        Main.routeRecording.importRoutes(filename);
        context.getSource().sendFeedback(
                Text.literal("Imported routes from ").formatted(Formatting.DARK_GREEN)
                        .append(Text.literal(filename).formatted(Formatting.GREEN))
        );
        return 1;
    }
}
