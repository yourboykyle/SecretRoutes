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
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import xyz.yourboykyle.secretroutes.events.OnGuiRender;
import xyz.yourboykyle.secretroutes.events.OnSkyblockerRender;
import xyz.yourboykyle.secretroutes.utils.*;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class Debug {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(Debug::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("srmdebug")
                .then(literal("lever")
                        .executes(Debug::executeLever))
                .then(literal("pos")
                        .executes(Debug::executePos))
                .then(literal("bloodtime")
                        .executes(ctx -> executeBloodtime(ctx, 3000L))
                        .then(argument("time", LongArgumentType.longArg(0))
                                .executes(ctx -> executeBloodtime(ctx, LongArgumentType.getLong(ctx, "time")))))
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
        BlockPos abs = MapUtils.relativeToActual(SecretUtils.currentLeverPos, RoomDetection.roomDirection(), RoomDetection.roomCorner());
        sendChatMessage("Abs: " + BlockUtils.blockPos(abs));
        sendChatMessage("Chest: " + SecretUtils.chestName);
        sendChatMessage("Lever: " + SecretUtils.leverName);
        sendChatMessage("Num: " + SecretUtils.leverNumber);
        return 1;
    }

    private static int executePos(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            context.getSource().sendError(Text.literal("Player not found"));
            return 0;
        }

        BlockPos pos = player.getBlockPos();
        context.getSource().sendFeedback(Text.literal("Position: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));

        // Temporary rendering debugging
        ChatUtils.sendChatMessage("World texts: " + OnSkyblockerRender.worldTexts.toString());
        ChatUtils.sendChatMessage("Outlined boxes: " + OnSkyblockerRender.outlinedBoxes.toString());

        return 1;
    }

    private static int executeBloodtime(CommandContext<FabricClientCommandSource> context, long time) {
        OnGuiRender.spawnNotifTime = System.currentTimeMillis() + time;
        context.getSource().sendFeedback(Text.literal("Blood spawn notification set for " + time + "ms from now").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeCr(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(
            Text.literal("Current index: " + xyz.yourboykyle.secretroutes.Main.currentRoom.closest.getTwo())
                .formatted(Formatting.AQUA)
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
                    Text.literal("Changed to index: " + newIndex).formatted(Formatting.GREEN)
                );
            } else {
                context.getSource().sendError(Text.literal("Index out of bounds"));
            }
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Error changing route: " + e.getMessage()));
            LogUtils.error(e);
        }
        return 1;
    }

    private static int executeApiCall(CommandContext<FabricClientCommandSource> context) {
        try {
            APIUtils.addMember();
            context.getSource().sendFeedback(Text.literal("API call executed").formatted(Formatting.GREEN));
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("API call failed: " + e.getMessage()));
            LogUtils.error(e);
        }
        return 1;
    }

    private static int executeVarGet(CommandContext<FabricClientCommandSource> context) {
        String fieldName = StringArgumentType.getString(context, "field");
        try {
            Field field = Constants.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object currentValue = field.get(null);
            context.getSource().sendFeedback(
                    Text.literal(fieldName + ": " + currentValue).formatted(Formatting.AQUA)
            );
        } catch (NoSuchFieldException e) {
            context.getSource().sendError(Text.literal("Invalid field: " + fieldName));
        } catch (IllegalAccessException e) {
            context.getSource().sendError(Text.literal("Illegal access (most likely private)"));
            LogUtils.error(e);
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Error accessing field: " + e.getMessage()));
            LogUtils.error(e);
        }
        return 1;
    }

    private static int executeVarSet(CommandContext<FabricClientCommandSource> context) {
        String fieldName = StringArgumentType.getString(context, "field");
        String value = StringArgumentType.getString(context, "value");

        try {
            Field field = Constants.class.getDeclaredField(fieldName);
            String type = field.getAnnotatedType().getType().getTypeName();
            field.setAccessible(true);
            Object currentValue = field.get(null);

            switch (type) {
                case "int":
                    field.set(null, Integer.valueOf(value));
                    break;
                case "float":
                    field.set(null, Float.valueOf(value));
                    break;
                case "boolean":
                    field.set(null, Boolean.valueOf(value));
                    break;
                case "double":
                    field.set(null, Double.valueOf(value));
                    break;
                case "java.lang.String":
                    field.set(null, value);
                    break;
                default:
                    context.getSource().sendError(Text.literal("Unsupported type: " + type));
                    return 0;
            }

            context.getSource().sendFeedback(
                    Text.literal("Changed [" + fieldName + "] from " + currentValue + " to " + value)
                            .formatted(Formatting.AQUA)
            );
        } catch (NoSuchFieldException e) {
            context.getSource().sendError(Text.literal("Invalid field: " + fieldName));
        } catch (IllegalAccessException e) {
            context.getSource().sendError(Text.literal("Illegal access (most likely private)"));
            LogUtils.error(e);
        } catch (NumberFormatException e) {
            context.getSource().sendError(Text.literal("Wrong type - cannot parse value"));
            LogUtils.error(e);
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Something went wrong: " + e.getMessage()));
            LogUtils.error(e);
        }
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestFields(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        try {
            Field[] fields = Constants.class.getDeclaredFields();
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
