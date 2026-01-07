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
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.FileUtils;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ChangeRoute {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(ChangeRoute::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        // xyz.yourboykyle.secretroutes.Main command and aliases
        dispatcher.register(literal("changeroute")
                .then(literal("list")
                        .executes(ChangeRoute::listRoutes))
                .then(literal("load")
                        .then(argument("route", StringArgumentType.greedyString())
                                .suggests(ChangeRoute::suggestRoutes)
                                .executes(ChangeRoute::loadRoute))));

        // Aliases
        dispatcher.register(literal("cr").redirect(dispatcher.register(literal("changeroute"))));
        dispatcher.register(literal("croute").redirect(dispatcher.register(literal("changeroute"))));
    }

    private static int listRoutes(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Routes:").formatted(Formatting.DARK_AQUA));
        for (String route : FileUtils.getRouteFileNames()) {
            context.getSource().sendFeedback(Text.literal(" - " + route).formatted(Formatting.AQUA));
        }
        return 1;
    }

    private static int loadRoute(CommandContext<FabricClientCommandSource> context) {
        String routeName = StringArgumentType.getString(context, "route");

        if (FileUtils.doesFileExist(Main.ROUTES_PATH + File.separator + routeName)) {
            SRMConfig.get().routesFileName = routeName;
            context.getSource().sendFeedback(
                    Text.literal("Loaded ").formatted(Formatting.DARK_GREEN)
                            .append(Text.literal(routeName).formatted(Formatting.GREEN))
                            .append(Text.literal(" as filename for custom routes").formatted(Formatting.DARK_GREEN))
            );
        } else {
            context.getSource().sendError(Text.literal("Specified file does not exist"));
        }
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestRoutes(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        for (String route : FileUtils.getRouteFileNames()) {
            builder.suggest(route);
        }
        return builder.buildFuture();
    }
}
