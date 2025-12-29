//#if FABRIC && MC == 1.21.10
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
import xyz.yourboykyle.secretroutes.utils.ConfigUtils;
import xyz.yourboykyle.secretroutes.utils.FileUtils;

import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ChangeColorProfile {
    private static boolean loadDefault = false;

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(ChangeColorProfile::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        // Main command and aliases
        dispatcher.register(literal("changecolorprofile")
                .executes(ChangeColorProfile::openGui)
                .then(literal("list")
                        .executes(ChangeColorProfile::listProfiles))
                .then(literal("load")
                        .executes(ChangeColorProfile::loadDefaultProfile)
                        .then(argument("profile", StringArgumentType.string())
                                .suggests(ChangeColorProfile::suggestProfiles)
                                .executes(ChangeColorProfile::loadProfile)))
                .then(literal("save")
                        .then(argument("profile", StringArgumentType.string())
                                .executes(ChangeColorProfile::saveProfile))));

        // Aliases
        dispatcher.register(literal("ccp").redirect(dispatcher.register(literal("changecolorprofile"))));
        dispatcher.register(literal("changeclrp").redirect(dispatcher.register(literal("changecolorprofile"))));
        dispatcher.register(literal("changecolourprofile").redirect(dispatcher.register(literal("changecolorprofile"))));
    }

    private static int openGui(CommandContext<FabricClientCommandSource> context) {
        SRMConfig.INSTANCE.openGui();
        return 1;
    }

    private static int listProfiles(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Color Profiles:").formatted(Formatting.DARK_AQUA));
        for (String profile : FileUtils.getFileNames(Main.COLOR_PROFILE_PATH)) {
            context.getSource().sendFeedback(Text.literal(" - " + profile).formatted(Formatting.AQUA));
        }
        return 1;
    }

    private static int loadDefaultProfile(CommandContext<FabricClientCommandSource> context) {
        if (!loadDefault) {
            context.getSource().sendError(Text.literal("Incorrect usage: /changecolorprofile load [profile]. Run again to load default"));
            loadDefault = true;
        } else {
            context.getSource().sendFeedback(Text.literal("Loaded default color profile").formatted(Formatting.DARK_GREEN));
            loadDefault = false;
        }
        return 1;
    }

    private static int loadProfile(CommandContext<FabricClientCommandSource> context) {
        String profile = StringArgumentType.getString(context, "profile");
        if (ConfigUtils.loadColorConfig(profile)) {
            context.getSource().sendFeedback(
                    Text.literal("Loaded ").formatted(Formatting.DARK_GREEN)
                            .append(Text.literal(profile).formatted(Formatting.GREEN))
                            .append(Text.literal(" as color profile").formatted(Formatting.DARK_GREEN))
            );
        } else {
            context.getSource().sendError(Text.literal("Failed to load color profile: " + profile));
        }
        loadDefault = false;
        return 1;
    }

    private static int saveProfile(CommandContext<FabricClientCommandSource> context) {
        String profile = StringArgumentType.getString(context, "profile");
        ConfigUtils.writeColorConfig(profile);
        context.getSource().sendFeedback(
                Text.literal("Saved color profile: ").formatted(Formatting.DARK_GREEN)
                        .append(Text.literal(profile).formatted(Formatting.GREEN))
        );
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestProfiles(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        for (String profile : FileUtils.getFileNames(Main.COLOR_PROFILE_PATH)) {
            builder.suggest(profile);
        }
        return builder.buildFuture();
    }

}
//#endif
