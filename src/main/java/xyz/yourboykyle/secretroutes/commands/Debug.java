/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2024 yourboykyle & R-aMcC
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
import java.util.IllegalFormatException;

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

        if(args.length == 0){
            return;
        }else{
            try{
                if(args[0].equals("lever")){
                    sendChatMessage("Relative :" + BlockUtils.blockPos(SecretUtils.currentLeverPos));
                    BlockPos abs = MapUtils.relativeToActual(SecretUtils.currentLeverPos, RoomDetection.roomDirection, RoomDetection.roomCorner);
                    sendChatMessage("Abs: " + BlockUtils.blockPos(abs));
                    sendChatMessage("Chest: " + SecretUtils.chestName);
                    sendChatMessage("Lever: " + SecretUtils.leverName);
                    sendChatMessage("Num: " + SecretUtils.leverNumber);
                    return;
                }else if(args[0].equals("pos")){
                     EntityPlayerSP p= Minecraft.getMinecraft().thePlayer;
                    sendChatMessage("Relative: "+p.getPosition());
                     sendChatMessage("Abs: "+ BlockUtils.blockPos(p.getPosition()));


                }
            }catch(Exception e){
                LogUtils.error(e);
            }








            try{
                Field field = Constants.class.getDeclaredField(args[0]);
                String type = field.getAnnotatedType().getType().getTypeName();
                field.setAccessible(true);
                Object currentValue = field.get(null);
                if(args.length == 1){
                    ChatUtils.sendChatMessage("§b"+args[0]+": "+currentValue);
                }else{
                    switch (type) {
                        case "int":
                            field.set(null, Integer.valueOf(args[1]));
                            break;
                        case "float":
                            field.set(null, Float.valueOf(args[1]));
                            break;
                        case "boolean":
                            field.set(null, Boolean.valueOf(args[1]));
                            break;
                        case "double":
                            field.set(null, Double.valueOf(args[1]));
                            break;
                        case "String":
                            field.set(null, args[1]);
                            break;
                    }
                    ChatUtils.sendChatMessage("§bChanged ["+args[0]+"] from "+currentValue+" to "+args[1]);
                }


            }catch(NoSuchFieldException e){
                sendChatMessage("§cInvalid argument: " + args[0]);
            }catch(IllegalAccessException e){
                sendChatMessage("§cIllegal access (Most likely private");
                LogUtils.error(e);
            }catch(IllegalFormatException e ){
             sendChatMessage("§cWrong type");
             LogUtils.error(e);
            }catch(Exception e){
                LogUtils.error(e);
                if(args.length == 1){
                    ChatUtils.sendChatMessage("§cSomething went wrong... Command [/srmdebug "+args[0]+ "]");
                }else{
                    sendChatMessage("§cSomething went wrong... Command [/srmdebug "+args[0]+ " "+ args[1]+"]");
                }
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {return 0;}
}
