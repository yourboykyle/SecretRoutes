//#if FORGE == 1.8.9
// TODO: update this file for multi versioning (1.8.9 -> 1.21.10)
package xyz.yourboykyle.secretroutes.utils.autoupdate;

import com.google.gson.JsonElement;
import moe.nea.libautoupdate.*;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;
import xyz.yourboykyle.secretroutes.utils.LogUtils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * This class is a taken from SkyHanni, translated from Kotlin to Java
 * <a href="https://github.com/hannibal002/SkyHanni/blob/beta/src/main/java/at/hannibal2/skyhanni/features/misc/update/UpdateManager.kt">...</a>
 * All credit to them
 */

public class UpdateManager {
    private static CompletableFuture<?> _activePromise = null;

    public static void setActivePromise(CompletableFuture<?> value) {
        if (_activePromise != null) {
            _activePromise.cancel(true);
        }
        _activePromise = value;
    }

    private static UpdateState updateState = UpdateState.NONE;
    private static PotentialUpdate potentialUpdate = null;


    public static String getNextVersion() {
        return potentialUpdate != null ? potentialUpdate.getUpdate().getVersionNumber().getAsString() : null;
    }

    public void reset() {
        updateState = UpdateState.NONE;
        _activePromise = null;
        potentialUpdate = null;
        LogUtils.info("Reset update state");
    }

    public void checkUpdate(Boolean button) {
        if (updateState == UpdateState.DOWNLOADED) {
            LogUtils.info("The latest version has already been downloaded in this session. Please restart to apply the changes.");
            return;
        } else if (updateState == UpdateState.QUEUED) {
            LogUtils.info("Trying to perform update check while another update is already in progress");
            return;
        } else if (updateState == UpdateState.AVAILABLE) {
            LogUtils.info("This appears to be the second update check. Assuming user checking manually");
        }

        setActivePromise(context.checkUpdate("full")
                .thenAcceptAsync(update -> {
                    LogUtils.info("Update check completed");

                    potentialUpdate = update;

                    LogUtils.info("Current version: " + context.getCurrentVersion());
                    LogUtils.info("Latest version: " + update.getUpdate().getVersionNumber());


                    if (checkVersion(update) || SRMConfig.forceUpdateDEBUG) { // Dev option to test auto update: Forces an out of date version no matter what (HIDDEN THROUGH A (not very secure) DEV PASSWORD)
                        updateState = UpdateState.AVAILABLE;
                        LogUtils.info("Update available");

                        ChatUtils.sendChatMessage(EnumChatFormatting.GREEN + "Secret Routes Mod found a new update: " + update.getUpdate().getVersionName());
                        if (SRMConfig.autoDownload) {
                            LogUtils.info("Update available, autoUpdate is enabled");
                            ChatUtils.sendChatMessage(EnumChatFormatting.GREEN + "Automatically downloading new Secret Routes Mod update, since AutoDownload is true...");
                            queueUpdate();
                        } else {
                            ChatUtils.sendClickableMessage(EnumChatFormatting.GREEN + "Download at https://github.com/yourboykyle/SecretRoutes/releases/latest", "https://github.com/yourboykyle/SecretRoutes/releases/latest");
                        }
                    } else {
                        if(button){
                            ChatUtils.sendChatMessage(EnumChatFormatting.GREEN + "Secret Routes Mod is up to date!");
                        }
                        LogUtils.info("No update available.");
                    }
                }, MinecraftExecutor.INSTANCE)
        );
    }

    public void queueUpdate() {
        if (updateState != UpdateState.AVAILABLE) {
            LogUtils.info("Trying to queue an update while another one is already downloaded or none is present");
            return;
        }

        updateState = UpdateState.QUEUED;
        setActivePromise(CompletableFuture.supplyAsync((Supplier<Void>) () -> {
            LogUtils.info("Update download started");
            try {
                potentialUpdate.prepareUpdate();
            } catch (IOException e) {
                LogUtils.error(e);
            }
            return null;
        }).thenAcceptAsync(aVoid -> {
            LogUtils.info("Update download completed, setting exit hook");
            updateState = UpdateState.DOWNLOADED;
            potentialUpdate.executePreparedUpdate();
            ChatUtils.sendChatMessage("§eDownload of update complete.");
            ChatUtils.sendChatMessage("§aThe update will be installed after your next restart.");
        }));
    }

    private static final UpdateContext context = new UpdateContext(
            UpdateSource.githubUpdateSource("yourboykyle", "SecretRoutes"),
            UpdateTarget.deleteAndSaveInTheSameFolder(UpdateManager.class),
            new CurrentVersion() {
                private final CurrentVersion normalDelegate = CurrentVersion.ofTag("v" + Main.VERSION);

                @Override
                public String display() {
                    if (SRMConfig.forceUpdateDEBUG) {
                        return "Force Outdated";
                    }
                    return normalDelegate.display();
                }

                @Override
                public boolean isOlderThan(JsonElement element) {
                    if (SRMConfig.forceUpdateDEBUG) {
                        LogUtils.info("isOlderThan: force update!");
                        return true;
                    }
                    return normalDelegate.isOlderThan(element);
                }

                @Override
                public String toString() {
                    return "" + normalDelegate;
                }
            },
            Main.MODID
    );

    static {
        context.cleanup();

    }

    public enum UpdateState {
        AVAILABLE,
        QUEUED,
        DOWNLOADED,
        NONE
    }

    public boolean checkVersion(PotentialUpdate update) {
        String[] currentV = Main.VERSION.split("\\.");
        String[] nextV = update.getUpdate().getVersionName().substring(1).split("\\.");

        for (int i = 0; i < currentV.length && i < nextV.length; i++) {
            if (Integer.parseInt(currentV[i]) < Integer.parseInt(nextV[i])) {
                return true;
            } else if (Integer.parseInt(currentV[i]) > Integer.parseInt(nextV[i])) {
                return false;
            }
        }
        if (currentV.length < nextV.length) {
            return true;
        }
        return false;
    }
}
//#endif
