package moe.nea.libautoupdate;

import lombok.Value;
import lombok.val;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Value
public class PotentialUpdate {
    /**
     * The update data of this update.
     */
    UpdateData update;
    /**
     * The context of this update.
     */
    UpdateContext context;
    /**
     * A UUID so that each update does not conflict which each other.
     */
    UUID updateUUID = UUID.randomUUID();

    /**
     * @return the directory in which update data gets stored for the post exit stage.
     */
    public File getUpdateDirectory() {
        return new File(".autoupdates", context.getIdentifier() + "/" + updateUUID);
    }

    /**
     * @return true if an update exists and has a higher version number than our current version.
     */
    public boolean isUpdateAvailable() {
        if (update == null) return false;
        return context.getCurrentVersion().isOlderThan(update.getVersionNumber());
    }

    private File getFile(String name) {
        getUpdateDirectory().mkdirs();
        return new File(getUpdateDirectory(), name);
    }

    /**
     * @return the location where the updated jar will be stored.
     */
    public File getUpdateJarStorage() {
        return getFile("next.jar");
    }

    /**
     * @return the filename of the updated jar, as specified by the {@link UpdateData#getDownload()}
     * @throws MalformedURLException if {@link #update} contains an invalid download url.
     */
    public String getFileName() throws MalformedURLException {
        val split = update.getDownloadAsURL().getPath().split("/");
        return split[split.length - 1];
    }

    /**
     * Extracts the updater jar (for the post exit stage) into the storage directory.
     */
    public void extractUpdater() throws IOException {
        val file = getFile("updater.jar");
        try (val from = getClass().getResourceAsStream("/updater.jar");
             val to = new FileOutputStream(file)) {
            UpdateUtils.connect(from, to);
        }
    }

    /**
     * Download the updated jar into the storage directory.
     */
    public void downloadUpdate() throws IOException {
        try (val from = UpdateUtils.openUrlConnection(update.getDownloadAsURL());
             val to = new FileOutputStream(getUpdateJarStorage())) {
            UpdateUtils.connect(from, to);
        }
        try (val check = new FileInputStream(getUpdateJarStorage())) {
            val updateSha = UpdateUtils.sha256sum(check);
            if (update.getSha256() != null && !update.getSha256().equalsIgnoreCase(updateSha)) {
                throw new UpdateException(
                        "Hash of downloaded file " + getUpdateJarStorage() +
                                " (" + updateSha + ") does not match expected hash of " +
                                update.getSha256());
            }
        }
    }

    /**
     * Prepare the layout of the storage directory.
     */
    public void prepareUpdate() throws IOException {
        extractUpdater();
        downloadUpdate();
    }

    /**
     * Execute the update.
     * This is done by first preparing the storage directory using {@link #prepareUpdate()} and then setting the {@link ExitHookInvoker}
     */
    public void executeUpdate() throws IOException {
        prepareUpdate();
        executePreparedUpdate();
    }

    /**
     * Execute an already prepared update.
     * Identical to {@link #executeUpdate()} if {@link #prepareUpdate()} was called beforehand.
     */
    public void executePreparedUpdate() {
        ExitHookInvoker.setExitHook(
                getContext().getIdentifier(),
                getUpdateUUID(),
                getFile("updater.jar"),
                context.getTarget().generateUpdateActions(this));
    }

    /**
     * Execute the update in another thread.
     *
     * @return a future which is completed when the update is downloaded and hooked on exit.
     */
    public CompletableFuture<Void> launchUpdate() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                executeUpdate();
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return null;
        });
    }

}
