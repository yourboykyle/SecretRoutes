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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.dungeons.SecretUtils;
import xyz.yourboykyle.secretroutes.utils.*;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class Debug {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(Debug::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal("srmdebug")
                .then(literal("lever")
                        .executes(Debug::executeLever))
                .then(literal("pos")
                        .executes(Debug::executePos))
                .then(literal("cr")
                        .executes(Debug::executeCr)
                        .then(literal("f")
                                .executes(ctx -> executeCrDirection(ctx, true)))
                        .then(literal("b")
                                .executes(ctx -> executeCrDirection(ctx, false))))
                .then(literal("apicall")
                        .executes(Debug::executeApiCall))
                .then(literal("var")
                        .then(argument("field", StringArgumentType.word())
                                .suggests(Debug::suggestFields)
                                .executes(Debug::executeVarGet)
                                .then(argument("value", StringArgumentType.word())
                                        .executes(Debug::executeVarSet)))));
    }

    private static int executeLever(CommandContext<FabricClientCommandSource> context) {
        sendChatMessage("Relative :" + BlockUtils.blockPos(SecretUtils.currentLeverPos));
        BlockPos abs = RoomRotationUtils.relativeToActual(SecretUtils.currentLeverPos, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());
        sendChatMessage("Abs: " + BlockUtils.blockPos(abs));
        sendChatMessage("Chest: " + SecretUtils.chestName);
        sendChatMessage("Lever: " + SecretUtils.leverName);
        sendChatMessage("Num: " + SecretUtils.leverNumber);
        return 1;
    }

    private static int executePos(CommandContext<FabricClientCommandSource> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            context.getSource().sendError(Component.literal("Player not found"));
            return 0;
        }

        BlockPos pos = player.blockPosition();
        context.getSource().sendFeedback(Component.literal("Position: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));

        return 1;
    }

    private static int executeCr(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(
                Component.literal("Current index: " + xyz.yourboykyle.secretroutes.Main.currentRoom.closest.getTwo())
                        .withStyle(ChatFormatting.AQUA)
        );
        return 1;
    }

    private static int executeCrDirection(CommandContext<FabricClientCommandSource> context, boolean forward) {
        try {
            int currentIndex = xyz.yourboykyle.secretroutes.Main.currentRoom.closest.getTwo();
            int newIndex = forward ? currentIndex + 1 : currentIndex - 1;

            if (newIndex >= 0 && newIndex < xyz.yourboykyle.secretroutes.Main.currentRoom.arrays.size()) {
                xyz.yourboykyle.secretroutes.Main.currentRoom.currentSecretRoute =
                        xyz.yourboykyle.secretroutes.Main.currentRoom.arrays.get(newIndex);
                context.getSource().sendFeedback(
                        Component.literal("Changed to index: " + newIndex).withStyle(ChatFormatting.GREEN)
                );
            } else {
                context.getSource().sendError(Component.literal("Index out of bounds"));
            }
        } catch (Exception e) {
            context.getSource().sendError(Component.literal("Error changing route: " + e.getMessage()));
            LogUtils.error(e);
        }
        return 1;
    }

    private static int executeApiCall(CommandContext<FabricClientCommandSource> context) {
        try {
            APIUtils.addMember();
            context.getSource().sendFeedback(Component.literal("API call executed").withStyle(ChatFormatting.GREEN));
        } catch (Exception e) {
            context.getSource().sendError(Component.literal("API call failed: " + e.getMessage()));
            LogUtils.error(e);
        }
        return 1;
    }

    private static int executeVarGet(CommandContext<FabricClientCommandSource> context) {
        String fieldName = StringArgumentType.getString(context, "field");
        try {
            Field field = SRMConfig.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object currentValue = field.get(SRMConfig.get());
            context.getSource().sendFeedback(
                    Component.literal(fieldName + ": " + currentValue).withStyle(ChatFormatting.AQUA)
            );
        } catch (NoSuchFieldException e) {
            context.getSource().sendError(Component.literal("Invalid field: " + fieldName));
        } catch (IllegalAccessException e) {
            context.getSource().sendError(Component.literal("Illegal access (most likely private)"));
            LogUtils.error(e);
        } catch (Exception e) {
            context.getSource().sendError(Component.literal("Error accessing field: " + e.getMessage()));
            LogUtils.error(e);
        }
        return 1;
    }

    private static int executeVarSet(CommandContext<FabricClientCommandSource> context) {
        String fieldName = StringArgumentType.getString(context, "field");
        String value = StringArgumentType.getString(context, "value");

        try {
            Field field = SRMConfig.class.getDeclaredField(fieldName);
            String type = field.getAnnotatedType().getType().getTypeName();
            field.setAccessible(true);

            Object currentValue = field.get(SRMConfig.get());

            switch (type) {
                case "int":
                    field.set(SRMConfig.get(), Integer.valueOf(value));
                    break;
                case "float":
                    field.set(SRMConfig.get(), Float.valueOf(value));
                    break;
                case "boolean":
                    field.set(SRMConfig.get(), Boolean.valueOf(value));
                    break;
                case "double":
                    field.set(SRMConfig.get(), Double.valueOf(value));
                    break;
                case "java.lang.String":
                    field.set(SRMConfig.get(), value);
                    break;
                default:
                    context.getSource().sendError(Component.literal("Unsupported type: " + type));
                    return 0;
            }

            SRMConfig.HANDLER.save();

            context.getSource().sendFeedback(
                    Component.literal("Changed [" + fieldName + "] from " + currentValue + " to " + value)
                            .withStyle(ChatFormatting.AQUA)
            );
        } catch (NoSuchFieldException e) {
            context.getSource().sendError(Component.literal("Invalid field: " + fieldName));
        } catch (IllegalAccessException e) {
            context.getSource().sendError(Component.literal("Illegal access (most likely private)"));
            LogUtils.error(e);
        } catch (NumberFormatException e) {
            context.getSource().sendError(Component.literal("Wrong type - cannot parse value"));
            LogUtils.error(e);
        } catch (Exception e) {
            context.getSource().sendError(Component.literal("Something went wrong: " + e.getMessage()));
            LogUtils.error(e);
        }
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestFields(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        try {
            Field[] fields = SRMConfig.class.getDeclaredFields();
            for (Field field : fields) {
                builder.suggest(field.getName());
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
        return builder.buildFuture();
    }
}
//#endif
