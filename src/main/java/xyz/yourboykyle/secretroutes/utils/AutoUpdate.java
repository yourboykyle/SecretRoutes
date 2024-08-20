package xyz.yourboykyle.secretroutes.utils;

import com.google.gson.JsonElement;
import moe.nea.libautoupdate.*;
import xyz.yourboykyle.secretroutes.Main;

public class AutoUpdate {
    public static enum UpdateState {
        AVAILABLE,
        QUEUED,
        DOWNLOADED,
        NONE
    }

    public static UpdateState updateState = UpdateState.NONE;

    public static UpdateContext context = new UpdateContext(
            UpdateSource.githubUpdateSource("yourboykyle", "SecretRoutes"),
            UpdateTarget.deleteAndSaveInTheSameFolder(AutoUpdate.class),
            new CurrentVersion() {
                private final CurrentVersion normalDelegate = CurrentVersion.ofTag(Main.VERSION);

                @Override
                public String display() {
                    return normalDelegate.display();
                }

                @Override
                public boolean isOlderThan(JsonElement element) {
                    return normalDelegate.isOlderThan(element);
                }
            },
            Main.MODID
    );

    public AutoUpdate() {
        context.cleanup();
    }
}