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
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.LogUtils;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SRM {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(SRM::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        // xyz.yourboykyle.secretroutes.Main command and aliases
        dispatcher.register(literal("srm")
                .executes(SRM::executeCommand));

        // Aliases
        dispatcher.register(literal("xyz/yourboykyle/secretroutes").redirect(dispatcher.register(literal("srm"))));
        dispatcher.register(literal("secretroutesmod").redirect(dispatcher.register(literal("srm"))));
    }

    private static int executeCommand(CommandContext<FabricClientCommandSource> context) {
        LogUtils.info("Opening SRM GUI (command)...");
        MinecraftClient.getInstance().send(() ->
                MinecraftClient.getInstance().setScreen(SRMConfig.getScreen(MinecraftClient.getInstance().currentScreen))
        );
        return 1;
    }
}
