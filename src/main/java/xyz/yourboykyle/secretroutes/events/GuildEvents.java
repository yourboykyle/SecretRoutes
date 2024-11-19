package xyz.yourboykyle.secretroutes.events;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;

public class GuildEvents {
    private static final String[] formats = {"§a", "§b", "§c", "§d", "§e", "§f", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§0", "§k", "§l", "§m", "§n", "§o", "§r"};
    private static String[] ranks = {};
    private static final String SEARCH = "§2Guild > §a[VIP§6+§a] SRMBridge";

    public String cleanMessage(String message) {
        for (String format : formats) {
            message = message.replace(format, "");
        }
        return message;
    }


    @SubscribeEvent(priority = EventPriority.HIGH)
    public void OnChatReceived(ClientChatReceivedEvent e) {
        if (e.type == 2 || !SRMConfig.bridge) return;
        String message = e.message.getUnformattedText();
        System.out.println("Message: " + message);
        if (message.contains(SEARCH)) {
            String tmp = message.split(":")[1];
            String sec1 = tmp.substring(0, tmp.indexOf("»") - 1).replaceFirst("»", ":");
            String sec2 = tmp.substring(tmp.indexOf("»") + 1);
            ChatUtils.sendChatMessage("§2Bridge >§b" + sec1 + "§r:" + sec2);
            e.setCanceled(true);
        }
    }
}