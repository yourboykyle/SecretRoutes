package moe.nea.libautoupdate;


import lombok.NonNull;
import lombok.Value;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * The update context holds information about your application, and it's update process.
 */
@Value
public class UpdateContext {
    /**
     * The source from which to pull updates.
     */
    @NonNull UpdateSource source;
    /**
     * The update target which specifies how to install an update.
     */
    @NonNull UpdateTarget target;
    /**
     * The version of this application
     */
    @NonNull CurrentVersion currentVersion;
    /**
     * An identifier for this application, as to not collide with other applications using this update lib.
     */
    @NonNull String identifier;

    /**
     * Delete remnants of previous updates.
     */
    public void cleanup() {
        File file = new File(".autoupdates", identifier).getAbsoluteFile();
        try {
            UpdateUtils.deleteDirectory(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check for an update.
     *
     * @param updateStream the update stream to check.
     * @return a future that resolves to an update of this stream, or null.
     */
    public CompletableFuture<PotentialUpdate> checkUpdate(String updateStream) {
        return source.checkUpdate(updateStream)
                .thenApply(it -> new PotentialUpdate(it, this));
    }
}
