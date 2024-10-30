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



package xyz.yourboykyle.secretroutes.events;

import scala.runtime.BooleanRef;
import xyz.yourboykyle.secretroutes.commands.SRM;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.Utils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.utils.BlockUtils;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.SecretUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendVerboseMessage;

public class OnChatReceive {
    Long lastSent = System.currentTimeMillis();
    private static String[] sections = null;
    private static String unformated = "";
    private static boolean allFound = false;
    private static HashMap<String, String> msgMap = new HashMap<>();


    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onChatReceive(ClientChatReceivedEvent e) {
        Utils.checkForSkyblock();
        Utils.checkForCatacombs();
        if (!Utils.inSkyblock) {
            return;
        }
        String unformatted = e.message.getUnformattedText();
        String secrets = "";
        if (e.type == 2) {
            Matcher matcher = Pattern.compile("§7(?<roomCollectedSecrets>\\d+)/(?<roomTotalSecrets>\\d+) Secrets").matcher(unformatted);
            if (matcher.find()) {
                int roomSecrets = Integer.parseInt(matcher.group("roomTotalSecrets"));
                int secretsFound = Integer.parseInt(matcher.group("roomCollectedSecrets"));
                if (roomSecrets == secretsFound) {
                    sendVerboseMessage("§aAll secrets found!", "Actionbar");
                    allFound  = true;
                } else {
                    sendVerboseMessage("§9(" + secretsFound + "/" + roomSecrets + ")", "Actionbar");
                    allFound = false;
                }
            }
        }else{
            if(e.message.getUnformattedText().contains("That chest is locked!")){
                LogUtils.info("§aLocked chest detected!");
                new Thread(() ->{
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException ignored){}
                    SecretUtils.secretLocations.remove(BlockUtils.blockPos(SecretUtils.lastInteract));
                }).start();
                SecretUtils.renderLever = true;
            }
        }

    }
    public static boolean isAllFound() {
        return allFound;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void hideBossMessages(ClientChatReceivedEvent e) {
        if(e.type == 2){return;}
        if(SRMConfig.hideBossMessages){
            if(msgMap.isEmpty()){
                msgMap.put("§r§c[BOSS] The Watcher", "hideWatcher");
                msgMap.put("§r§c[BOSS] Bonzo", "hideBonzo");
                msgMap.put("§r§c[BOSS] Scarf", "hideScarf");
                msgMap.put("§r§c[BOSS] Professor", "hideProfessor");
                msgMap.put("§r§c[BOSS] Thorn", "hideThorn");
                msgMap.put("§r§c[BOSS] Livid", "hideLivid");
                msgMap.put("§r§c[BOSS] Sadan", "hideSadan");
                msgMap.put("§r§4[BOSS] Maxor", "hideWitherLords");
                msgMap.put("§r§4[BOSS] Storm", "hideWitherLords");
                msgMap.put("§r§4[BOSS] Goldor", "hideWitherLords");
                msgMap.put("§r§4[BOSS] Necron", "hideWitherLords");
            }
            String formated = e.message.getFormattedText();
            if(!formated.contains("BOSS")){return;}
            if(SRMConfig.bloodNotif){
                if(formated.equals("§r§c[BOSS] The Watcher§r§f: That will be enough for now.§r")){
                    sendChatMessage("KILL BLOOD");
                    OnGuiRender.spawnNotifTime = System.currentTimeMillis()+SRMConfig.bloodBannerDuration;

                }
            }
            for(Map.Entry<String, String> entry : msgMap.entrySet()){
                try{
                    Field field = SRMConfig.class.getField(entry.getValue());
                    field.setAccessible(true);
                    if(formated.startsWith(entry.getKey()) && field.getBoolean(null)){
                        e.setCanceled(true);
                    }
                }catch (NoSuchFieldException ex){
                    LogUtils.info("I (_Wyan) screwed up. No filed : "+entry.getValue());
                }catch (IllegalAccessException ex){
                    LogUtils.info("I (_Wyan) screwed up. Field not accessible: "+entry.getValue());
                }

            }
        }
    }


}
