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

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.MapUtils;
import xyz.yourboykyle.secretroutes.utils.*;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

public class Debug extends CommandBase {
    @Override
    public String getCommandName() {return "srmdebug";}

    @Override
    public String getCommandUsage(ICommandSender sender) {return "/srmdebug <option> <value>";}

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(!sender.getName().contains("_Wyan")){
            ChatUtils.sendChatMessage("§eAre you sure you want to do this?");
        }

        if(args.length != 0){
            try{
                switch (args[0]) {
                    case "lever":
                        sendChatMessage("Relative :" + BlockUtils.blockPos(SecretUtils.currentLeverPos));
                        BlockPos abs = MapUtils.relativeToActual(SecretUtils.currentLeverPos, RoomDetection.roomDirection, RoomDetection.roomCorner);
                        sendChatMessage("Abs: " + BlockUtils.blockPos(abs));
                        sendChatMessage("Chest: " + SecretUtils.chestName);
                        sendChatMessage("Lever: " + SecretUtils.leverName);
                        sendChatMessage("Num: " + SecretUtils.leverNumber);
                        break;
                    case "pos":
                        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
                        sendChatMessage("Relative: " + p.getPosition());
                        sendChatMessage("Abs: " + BlockUtils.blockPos(p.getPosition()));

                        break;
                    case "var":
                        if(args.length == 1){
                            sendChatMessage("Missing argument after \"var\"");
                        }else{
                            try {
                                Field field = Constants.class.getDeclaredField(args[1]);
                                String type = field.getAnnotatedType().getType().getTypeName();
                                field.setAccessible(true);
                                Object currentValue = field.get(null);
                                if (args.length == 2) {
                                    ChatUtils.sendChatMessage("§b" + args[1] + ": " + currentValue);
                                } else {
                                    switch (type) {
                                        case "int":
                                            field.set(null, Integer.valueOf(args[2]));
                                            break;
                                        case "float":
                                            field.set(null, Float.valueOf(args[2]));
                                            break;
                                        case "boolean":
                                            field.set(null, Boolean.valueOf(args[2]));
                                            break;
                                        case "double":
                                            field.set(null, Double.valueOf(args[2]));
                                            break;
                                        case "String":
                                            field.set(null, args[2]);
                                            break;
                                    }
                                    ChatUtils.sendChatMessage("§bChanged [" + args[1] + "] from " + currentValue + " to " + args[2]);
                                }


                            } catch (NoSuchFieldException e) {
                                sendChatMessage("§cInvalid argument: " + args[1]);
                            } catch (IllegalAccessException e) {
                                sendChatMessage("§cIllegal access (Most likely private");
                                LogUtils.error(e);
                            } catch (IllegalFormatException e) {
                                sendChatMessage("§cWrong type");
                                LogUtils.error(e);
                            } catch (Exception e) {
                                LogUtils.error(e);
                                sendChatMessage("§cSomething went wrong... Command [/srmdebug " + args[1] + " " + args[2] + "]");
                            }
                            break;
                        }

                }
            }catch(Exception e){
                LogUtils.error(e);
            }





        }
    }

    @Override
    public int getRequiredPermissionLevel() {return 0;}

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        List<String> completions = new ArrayList<>();
        List<String> basicOptions = new ArrayList<>();
        basicOptions.add("lever");
        basicOptions.add("pos");
        basicOptions.add("var");

       switch (args.length) {
           case 0:
            completions.addAll(basicOptions);
           case 1:
                completions.addAll(basicOptions);
                completions.removeIf(completion -> !(completion.toLowerCase().startsWith(args[0].toLowerCase())));
           case 2:
               try {
                   switch (args[0].toLowerCase()) {
                       case "lever":
                       case "pos":
                           break;
                       case "var":
                           //Idk why this can happen, but it does... There goes 3 hrs of my time
                           if(args.length == 1) return completions;
                           // In this one line. Right here. Checking a value after JUST CHECKING IT
                           Field[] fields = Constants.class.getDeclaredFields();

                           for (Field field : fields) {
                               if (field.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                                   completions.add(field.getName());
                               }
                           }
                   }
               }catch (Exception e){
                   ChatUtils.sendChatMessage("Error happened again");
                   e.printStackTrace();
               }
        }

        return completions;
    }
}
