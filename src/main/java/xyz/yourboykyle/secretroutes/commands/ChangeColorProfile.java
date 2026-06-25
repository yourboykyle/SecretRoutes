//#if FABRIC
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
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.ConfigUtils;
import xyz.yourboykyle.secretroutes.utils.FileUtils;

import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class ChangeColorProfile {
    private static boolean loadDefault = false;

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(ChangeColorProfile::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        var mainCommand = dispatcher.register(literal("changecolorprofile")
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
        dispatcher.register(literal("ccp").redirect(mainCommand));
        dispatcher.register(literal("changeclrp").redirect(mainCommand));
        dispatcher.register(literal("changecolourprofile").redirect(mainCommand));
    }

    private static int openGui(CommandContext<FabricClientCommandSource> context) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreen(SRMConfig.getScreen(client.screen)));
        return 1;
    }

    private static int listProfiles(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Component.literal("Color Profiles:").withStyle(ChatFormatting.DARK_AQUA));
        for (String profile : FileUtils.getFileNames(Main.COLOR_PROFILE_PATH)) {
            context.getSource().sendFeedback(Component.literal(" - " + profile).withStyle(ChatFormatting.AQUA));
        }
        return 1;
    }

    private static int loadDefaultProfile(CommandContext<FabricClientCommandSource> context) {
        if (!loadDefault) {
            context.getSource().sendError(Component.literal("Incorrect usage: /changecolorprofile load [profile]. Run again to load default"));
            loadDefault = true;
        } else {
            context.getSource().sendFeedback(Component.literal("Loaded default color profile").withStyle(ChatFormatting.DARK_GREEN));
            loadDefault = false;
        }
        return 1;
    }

    private static int loadProfile(CommandContext<FabricClientCommandSource> context) {
        String profile = StringArgumentType.getString(context, "profile");
        if (ConfigUtils.loadColorConfig(profile)) {
            context.getSource().sendFeedback(
                    Component.literal("Loaded ").withStyle(ChatFormatting.DARK_GREEN)
                            .append(Component.literal(profile).withStyle(ChatFormatting.GREEN))
                            .append(Component.literal(" as color profile").withStyle(ChatFormatting.DARK_GREEN))
            );
        } else {
            context.getSource().sendError(Component.literal("Failed to load color profile: " + profile));
        }
        loadDefault = false;
        return 1;
    }

    private static int saveProfile(CommandContext<FabricClientCommandSource> context) {
        String profile = StringArgumentType.getString(context, "profile");
        ConfigUtils.writeColorConfig(profile);
        context.getSource().sendFeedback(
                Component.literal("Saved color profile: ").withStyle(ChatFormatting.DARK_GREEN)
                        .append(Component.literal(profile).withStyle(ChatFormatting.GREEN))
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