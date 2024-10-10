package xyz.yourboykyle.secretroutes.events;

import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.Utils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendVerboseMessage;

public class OnChatReceive {
    Long lastSent = System.currentTimeMillis();
    private static String[] sections = null;
    private static String unformated = "";
    private static boolean allFound = false;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onChatReceive(ClientChatReceivedEvent e) {
        Utils.checkForSkyblock();
        Utils.checkForCatacombs();
        if (!Utils.inSkyblock) {
            return;
        }
        String unformatted = e.message.getUnformattedText();
        String secrets = "";
        if (e.type != 2) {
            return;
        }
        sections = unformatted.split(" {5}");
        for (String section : sections) {
            if (section.contains("Secret")) {
                secrets = section;
            }
        }

        Long currentTime = System.currentTimeMillis();
        if (currentTime - lastSent > 10000) {
            lastSent = currentTime;
            sendVerboseMessage(unformatted, "Actionbar");
        }

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
    }
    public static boolean isAllFound() {
        return allFound;
    }


}
