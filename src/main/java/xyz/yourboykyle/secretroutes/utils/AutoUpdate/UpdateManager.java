package xyz.yourboykyle.secretroutes.utils.AutoUpdate;

import com.google.gson.JsonElement;
import moe.nea.libautoupdate.*;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;
import xyz.yourboykyle.secretroutes.utils.LogUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/*
 * This class is a taken from SkyHanni, translated from Kotlin to Java
 * https://github.com/hannibal002/SkyHanni/blob/beta/src/main/java/at/hannibal2/skyhanni/features/misc/update/UpdateManager.kt
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
/*
    public void injectConfigProcessor(MoulConfigProcessor<?> processor) {
        processor.registerConfigEditor(ConfigVersionDisplay.class, (option, _unused) -> new GuiOptionEditorUpdateCheck(option));
    }

     */

    public void reset() {
        updateState = UpdateState.NONE;
        _activePromise = null;
        potentialUpdate = null;
        LogUtils.info("Reset update state");
    }

    public void checkUpdate(boolean forceDownload) {
        if (updateState != UpdateState.NONE) {
            //LogUtils.info("Trying to perform update check while another update is already in progress");
            return;
        }

        LogUtils.info("Starting update check");

        setActivePromise(context.checkUpdate("full")
                .thenAcceptAsync(update -> {
                    LogUtils.info("Update check completed");

                    if (updateState != UpdateState.NONE) {
                        LogUtils.info("This appears to be the second update check. Ignoring this one");
                        return;
                    }

                    potentialUpdate = update;

                    LogUtils.info("New Update: " + update.getUpdate().getVersionNumber());
                    LogUtils.info("Current Version: " + update.getContext().getCurrentVersion());
                    if (update.isUpdateAvailable()) {
                        updateState = UpdateState.AVAILABLE;

                        if (forceDownload) {
                            LogUtils.info("forceDownload & update is available!");
                            ChatUtils.sendChatMessage(EnumChatFormatting.GREEN + "Secret Routes Mod found a new update: " + update.getUpdate().getVersionName() + ", starting to download now.");
                            queueUpdate();
                        } else {
                            LogUtils.info("update is available!");
                            ChatUtils.sendChatMessage(EnumChatFormatting.GREEN + "Secret Routes Mod found a new update: " + update.getUpdate().getVersionName());
                        }
                    } else if (forceDownload) {
                        LogUtils.info("forceDownload & update is NOT available!");
                        ChatUtils.sendChatMessage(EnumChatFormatting.GREEN + "Secret Routes Mod didn't find a new update.");
                    }
                }, MinecraftExecutor.INSTANCE)
        );
    }

    public void checkUpdate() {
        checkUpdate(false);
    }

    public void queueUpdate() {
        if (updateState != UpdateState.AVAILABLE) {
            LogUtils.info("Trying to enqueue an update while another one is already downloaded or none is present");
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
            ChatUtils.sendChatMessage("Download of update complete.");
            ChatUtils.sendChatMessage("§aThe update will be installed after your next restart.");
        }, MinecraftExecutor.INSTANCE));
    }

    private static final UpdateContext context = new UpdateContext(
            UpdateSource.githubUpdateSource("yourboykyle", "SecretRoutes"),
            UpdateTarget.deleteAndSaveInTheSameFolder(UpdateManager.class),
            new CurrentVersion() {
                private final CurrentVersion normalDelegate = CurrentVersion.ofTag(Main.VERSION);

                @Override
                public String display() {
                    if (SRMConfig.autoUpdateTesting) {
                        return "Force Outdated";
                    }
                    return normalDelegate.display();
                }

                @Override
                public boolean isOlderThan(JsonElement element) {
                    if (SRMConfig.autoUpdateTesting) {
                        return true;
                    }
                    return normalDelegate.isOlderThan(element);
                }

                @Override
                public String toString() {
                    return "ForceOutdateDelegate(" + normalDelegate + ")";
                }
            },
            Main.MODID
    );

    static {
        context.cleanup();
        //UpdateUtils.patchConnection(connection -> {
            //if (connection instanceof HttpURLConnection) {
                //ApiUtil.patchHttpsRequest((HttpURLConnection) connection);
            //}
        //});
    }

    public enum UpdateState {
        AVAILABLE,
        QUEUED,
        DOWNLOADED,
        NONE
    }
}